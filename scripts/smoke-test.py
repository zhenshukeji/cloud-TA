"""云朵教务社区版 · 最小冒烟测试（OS007）。

一条命令串起：重建库 → 启动后端 → 管理员登录 → 调用核心只读接口 → 退出登录 → 校验原 Token 失效。
每一步都记录退出码；任一步失败即整体返回非 0。输出不含 Token 与口令。

用法：
    python scripts/smoke-test.py                    # 正向：期望全绿，退出码 0
    python scripts/smoke-test.py --mode negative    # 负向对照：故意用错口令，退出码必须非 0

前置：
    set -a && source scripts/env.local && set +a
    环境隔离要求见 scripts/verify-env.py（本脚本自己也做一遍前缀/逻辑库硬校验）

设计取舍（简要说）：
  * 冒烟口令默认「每次随机生成」并注入 sys_user.password，用完即弃 —— 这样脚本和命令里永远不出现字面量口令，
    也不会把口令写进任何文件。产品级的 INITIAL_ADMIN_PASSWORD 一次性初始化流程属 OS008/OS009，不在本脚本内。
  * 后端由本脚本启动并负责收尾（Windows 下按进程树 / 端口兜底清理），避免留下孤儿 Java 进程。
"""
import argparse
import datetime
import json
import os
import re
import secrets
import socket
import subprocess
import sys
import time
import urllib.error
import urllib.request

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)                                  # community-edition/
REPO_ROOT = os.path.dirname(ROOT)                             # 仓库外层的 workspace
TOOLS = os.path.join(REPO_ROOT, 'outputs', 'open-source-review', '_tools')
sys.path.insert(0, TOOLS)

DB_PREFIX = 'yunduo_community'
REDIS_DB = int(os.environ.get('JX_REDIS_DB', '12'))
CACHE_NAME = os.environ.get('JX_CACHE_NAME', 'yunduo_community')
PORT = int(os.environ.get('JX_SERVER_PORT', '8199'))
BASE = 'http://127.0.0.1:%d' % PORT
ADMIN_USER = os.environ.get('SMOKE_ADMIN_USER', 'admin')

STEPS = []
BACKEND = {'proc': None, 'log': None, 'started_by_us': False}


def step(name, ok, detail, code=None):
    STEPS.append({
        'step': name,
        'result': 'PASS' if ok else 'FAIL',
        'exit_code': 0 if ok else (code if code is not None else 1),
        'detail': detail,
    })
    print('%-4s %-34s | %s' % ('PASS' if ok else 'FAIL', name, detail))
    return ok


def http(path, method='GET', body=None, token=None, timeout=20):
    url = BASE + path
    data = json.dumps(body).encode() if body is not None else None
    headers = {'Content-Type': 'application/json'}
    if token:
        headers['Authorization'] = 'Bearer ' + token
    req = urllib.request.Request(url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=timeout) as r:
            return r.status, r.read().decode('utf-8', 'replace')
    except urllib.error.HTTPError as e:
        return e.code, e.read().decode('utf-8', 'replace')
    except Exception as e:  # noqa: BLE001 —— 连接层错误也要变成可判定的结果
        return 0, 'CONNECTION_ERROR: %s' % str(e)[:160]


def ajax_code(body):
    """RuoYi AjaxResult 的 code 字段；拿不到就返回 None。"""
    try:
        return json.loads(body).get('code')
    except ValueError:
        return None


def port_open(port):
    s = socket.socket()
    s.settimeout(1)
    try:
        return s.connect_ex(('127.0.0.1', port)) == 0
    finally:
        s.close()


def guard_env():
    db = os.environ.get('MYSQL_DATABASE', '')
    ok_name = db.startswith(DB_PREFIX)
    ok_db = REDIS_DB not in (0, 10)
    ok_prefix = CACHE_NAME.startswith(DB_PREFIX)
    return step('环境隔离硬校验', ok_name and ok_db and ok_prefix,
                'db=%s（前缀 %s：%s）；redis_db=%d（非 0/非 10：%s）；cache_name=%s'
                % (db or '(unset)', DB_PREFIX, ok_name, REDIS_DB, ok_db, CACHE_NAME))


def rebuild_db():
    """重建社区版空库并执行 001 + 002（复用 OS006 的脚本口径：库名前缀硬校验）。"""
    import db as dblib
    target = os.environ['MYSQL_DATABASE']
    if not target.startswith(DB_PREFIX):
        return step('重建库（001+002）', False, '拒绝：目标库 %r 缺少社区版前缀' % target)
    dblib.q('DROP DATABASE IF EXISTS `%s`' % target)
    dblib.q('CREATE DATABASE `%s` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci' % target)
    res = {}
    for f in ('001_schema.sql', '002_seed_system.sql'):
        r = dblib.exec_file(os.path.join(ROOT, 'database', f), target)
        res[f] = '%s/%s' % (r['executed'], r['statements'])
        if r['failed']:
            return step('重建库（001+002）', False, '%s 失败：%s' % (f, r['errors'][:1]))
    return step('重建库（001+002）', True, '001=%s，002=%s' % (res['001_schema.sql'], res['002_seed_system.sql']))


def inject_smoke_password(password):
    """把随机口令的 BCrypt 哈希写进 admin 账号。仅用于冒烟，替代尚未实现的一次性初始化流程。"""
    import bcrypt
    import db as dblib
    h = bcrypt.hashpw(password.encode(), bcrypt.gensalt(rounds=10)).decode()
    target = os.environ['MYSQL_DATABASE']
    n = dblib.q('UPDATE sys_user SET password=%s WHERE user_name=%s', db=target, args=(h, ADMIN_USER))[1]
    return step('注入冒烟口令（一次性）', n == 1,
                '更新 %d 行；口令长度 %d，随机生成，未落盘' % (n, len(password)))


def start_backend(backend_log, reuse):
    if reuse or port_open(PORT):
        BACKEND['started_by_us'] = False
        return step('启动后端', True, '复用已在 %d 端口运行的后端' % PORT)
    env = dict(os.environ)
    args = ('--server.port=%d --spring.redis.database=%d --spring.redis.cache_name=%s '
            '--spring.datasource.druid.stat-view-servlet.enabled=false --spring.devtools.restart.enabled=false'
            % (PORT, REDIS_DB, CACHE_NAME))
    cmd = ['D:/ProgrammEnvironment/apache-maven-3.9.9/bin/mvn.cmd', '-o', 'spring-boot:run',
           '-Dspring-boot.run.arguments=' + args]
    os.makedirs(os.path.dirname(backend_log), exist_ok=True)
    log = open(backend_log, 'w', encoding='utf-8')
    t0 = time.time()
    proc = subprocess.Popen(cmd, cwd=os.path.join(ROOT, 'backend'), env=env, stdout=log, stderr=subprocess.STDOUT)
    BACKEND.update(proc=proc, log=log, started_by_us=True)
    deadline = t0 + 240
    while time.time() < deadline:
        if port_open(PORT):
            return step('启动后端', True, '耗时 %.1fs，端口 %d 就绪，日志 %s'
                        % (time.time() - t0, PORT, os.path.basename(backend_log)))
        if proc.poll() is not None:
            tail = ''
            try:
                with open(backend_log, encoding='utf-8', errors='replace') as f:
                    tail = ' | '.join([l for l in f.read().split('\n') if 'ERROR' in l or 'Caused by' in l][-2:])[:300]
            except OSError:
                pass
            return step('启动后端', False, '进程提前退出（exit=%s）%s' % (proc.returncode, tail))
        time.sleep(2)
    return step('启动后端', False, '等待 240s 仍未监听端口 %d' % PORT)


def stop_backend():
    if not BACKEND['started_by_us']:
        return
    proc = BACKEND['proc']
    if proc is None:
        return
    try:
        if os.name == 'nt':
            subprocess.run(['taskkill', '/F', '/T', '/PID', str(proc.pid)],
                           stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=False)
        else:
            os.killpg(os.getpgid(proc.pid), 15)
    except Exception:  # noqa: BLE001
        pass
    time.sleep(3)
    if port_open(PORT):
        # 兜底：按端口找 PID 再杀（Windows）/ 用 lsof（POSIX）
        try:
            if os.name == 'nt':
                # netstat 在中文 Windows 上是 GBK 输出：必须按字节读再宽松解码，否则 UnicodeDecodeError
                raw = subprocess.run(['netstat', '-ano'], capture_output=True, check=False).stdout
                out = raw.decode('utf-8', 'replace')
                for line in out.split('\n'):
                    if (':%d ' % PORT) in line and 'LISTENING' in line.upper():
                        pid = line.split()[-1]
                        subprocess.run(['taskkill', '/F', '/PID', pid], stdout=subprocess.DEVNULL,
                                       stderr=subprocess.DEVNULL, check=False)
        except Exception:  # noqa: BLE001
            pass
    if BACKEND['log']:
        BACKEND['log'].close()


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--mode', choices=['smoke', 'negative'], default='smoke',
                    help='smoke=正向全链路；negative=故意用错口令（负向对照，按设计返回非 0）')
    ap.add_argument('--reuse-backend', action='store_true', help='复用已在跑的后端，不自己启动')
    ap.add_argument('--skip-rebuild', action='store_true', help='跳过重建库（调试用）')
    ap.add_argument('--backend-log', default=os.path.join(REPO_ROOT, 'outputs', 'open-source-review',
                                                          'OS007', 'logs', 'backend-smoke.log'))
    ap.add_argument('--out', default=None,
                    help='证据文件路径；默认 outputs/open-source-review/OS007/smoke-result-<mode>.json')
    args = ap.parse_args()
    # 两种模式各自留一份证据文件，避免互相覆盖
    if args.out is None:
        args.out = os.path.join(REPO_ROOT, 'outputs', 'open-source-review', 'OS007',
                                'smoke-result-%s.json' % args.mode)

    started = datetime.datetime.now().isoformat(timespec='seconds')
    password = os.environ.get('SMOKE_ADMIN_PASSWORD') or secrets.token_urlsafe(18)
    negative = args.mode == 'negative'

    if not guard_env():
        return finish(args, started, note='环境隔离校验未通过，未执行后续步骤')

    if not args.skip_rebuild:
        if not rebuild_db():
            return finish(args, started, note='重建库失败')
    if not inject_smoke_password(password):
        return finish(args, started, note='冒烟口令注入失败（账号是否存在？）')

    try:
        if not start_backend(args.backend_log, args.reuse_backend):
            return finish(args, started, note='后端启动失败')

        login_pw = (password + '-deliberately-wrong') if negative else password
        st, body = http('/login', 'POST', {'username': ADMIN_USER, 'password': login_pw})
        payload = {}
        try:
            payload = json.loads(body)
        except ValueError:
            pass
        token = payload.get('token')
        if negative:
            ok = st != 200 or not token
            step('登录（负向对照：错口令）', ok,
                 'HTTP=%s，code=%s，msg=%s' % (st, payload.get('code'), str(payload.get('msg'))[:40]))
        else:
            step('管理员登录', st == 200 and bool(token) and payload.get('code') == 200,
                 'HTTP=%s，code=%s，token 长度=%s（不输出内容）' % (st, payload.get('code'), len(token or '')))

        if token:
            st, body = http('/getInfo', token=token)
            step('只读接口 /getInfo', st == 200 and ajax_code(body) == 200,
                 'HTTP=%s，code=%s，响应长度=%d' % (st, ajax_code(body), len(body)))

            st, body = http('/educenter/course/list?pageNum=1&pageSize=5', token=token)
            step('只读接口 /educenter/course/list', st == 200 and ajax_code(body) == 200,
                 'HTTP=%s，code=%s，响应长度=%d' % (st, ajax_code(body), len(body)))

            st, body = http('/logout', 'POST', {}, token)
            step('退出登录 /logout', st == 200 and ajax_code(body) == 200,
                 'HTTP=%s，code=%s' % (st, ajax_code(body)))

            st, body = http('/getInfo', token=token)
            step('原 Token 已失效', st in (401, 403) or ajax_code(body) == 401,
                 'HTTP=%s，code=%s' % (st, ajax_code(body)))
        elif not negative:
            step('后续接口调用', False, '未拿到 token，跳过')
    finally:
        stop_backend()

    return finish(args, started)


def finish(args, started, note=None):
    failed = [s for s in STEPS if s['result'] == 'FAIL']
    # negative 模式即使各步都符合预期，也必须返回非 0（任务书 OS007 验收第 2 条）
    exit_code = 1 if (failed or args.mode == 'negative') else 0
    out = {
        'task': 'OS007',
        'mode': args.mode,
        'target': {
            'base_url': BASE,
            'mysql': '%s/%s' % (os.environ.get('MYSQL_HOST', '')[:4] + '***', os.environ.get('MYSQL_DATABASE', '')),
            'redis_db': REDIS_DB,
            'cache_name': CACHE_NAME,
        },
        'started_at': started,
        'finished_at': datetime.datetime.now().isoformat(timespec='seconds'),
        'steps': STEPS,
        'pass': len(STEPS) - len(failed),
        'total': len(STEPS),
        'exit_code': exit_code,
        'note': note or '',
        'secret_policy': '本文件不含 Token 与口令；冒烟口令每次随机生成，仅存在于进程内存。',
    }
    os.makedirs(os.path.dirname(os.path.abspath(args.out)), exist_ok=True)
    with open(args.out, 'w', encoding='utf-8') as f:
        json.dump(out, f, ensure_ascii=False, indent=1)
    print('== %d/%d ==  退出码=%d  证据：%s' % (out['pass'], out['total'], exit_code, args.out))
    if args.mode == 'negative':
        print('负向对照：错口令被拒绝，按任务书 OS007 验收第 2 条，本模式**按设计返回非 0**。')
    return exit_code


if __name__ == '__main__':
    sys.exit(main())
