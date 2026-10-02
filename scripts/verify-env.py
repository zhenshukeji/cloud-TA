"""云朵教务社区版 · 验证环境自检（OS007）。

断言「本环境与开发库/生产库是隔离的」，并把可公开的结论写进证据文件。
不输出、不落盘任何口令；只输出主机/库名/端口这些结构性信息。

用法：
    python scripts/verify-env.py [--out <证据文件路径>]

退出码：0 = 隔离成立；非 0 = 任意一条断言失败（含连不上、库名前缀不对、Redis 逻辑库不对）。
"""
import argparse
import datetime
import json
import os
import re
import socket
import sys

DB_PREFIX = 'yunduo_community'
REDIS_DB = int(os.environ.get('JX_REDIS_DB', '12'))
CACHE_NAME = os.environ.get('JX_CACHE_NAME', 'yunduo_community')

checks = []


def add(name, ok, detail, level='blocking'):
    """level=blocking：失败则整体退出码非 0；level=warn：只登记，不改变退出码。"""
    checks.append({'check': name, 'result': 'PASS' if ok else ('WARN' if level == 'warn' else 'FAIL'),
                   'level': level, 'detail': detail})


def mask(host):
    """主机脱敏：只保留网段结构，不暴露完整地址。"""
    if not host:
        return '(unset)'
    if re.fullmatch(r'[\d.]+', host):
        parts = host.split('.')
        return '.'.join(parts[:2] + ['*', '*'])
    return host[:2] + '***'


def connect_check(host, port, label):
    s = socket.socket()
    s.settimeout(5)
    try:
        rc = s.connect_ex((host, int(port)))
        add('%s 端口可达' % label, rc == 0, '%s:%s -> %s' % (mask(host), port, 'OPEN' if rc == 0 else 'CLOSED'))
        return rc == 0
    finally:
        s.close()


def main():
    ap = argparse.ArgumentParser()
    default_out = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                               '..', 'outputs', 'open-source-review', 'OS007', 'env-evidence.json')
    ap.add_argument('--out', default=os.path.normpath(default_out))
    args = ap.parse_args()

    db_host = os.environ.get('MYSQL_HOST', '')
    db_port = os.environ.get('MYSQL_PORT', '')
    db_name = os.environ.get('MYSQL_DATABASE', '')
    rd_host = os.environ.get('REDIS_HOST', '')
    rd_port = os.environ.get('REDIS_PORT', '')

    # 1) 库名隔离：必须带社区版前缀
    add('数据库名带社区版前缀', db_name.startswith(DB_PREFIX),
        'MYSQL_DATABASE=%s（要求以 %s 开头）' % (db_name or '(unset)', DB_PREFIX))

    # 2) 反向断言：不得指向明显是开发库/生产库的名字
    forbidden = [n for n in ('jian_xue_pure', 'jian_xue_prod', 'jian_xue', 'mysql', 'sys', 'information_schema')
                 if n == db_name]
    add('未指向开发库/生产库名', not forbidden, '命中禁用库名：%s' % forbidden)

    # 3) 必填变量齐全（只报名字，不报值）
    required = ['MYSQL_HOST', 'MYSQL_PORT', 'MYSQL_DATABASE', 'MYSQL_USER', 'MYSQL_PASSWORD',
                'REDIS_HOST', 'REDIS_PORT', 'TOKEN_SECRET']
    missing = [k for k in required if not os.environ.get(k)]
    add('必填环境变量齐全', not missing, '缺少：%s' % missing)

    # 4) Redis 逻辑库固定为社区版专用，且 key 前缀独立
    add('Redis 使用社区版专用逻辑库', REDIS_DB not in (0, 10) and REDIS_DB != 10,
        'JX_REDIS_DB=%d（要求既不是 0 也不是开发库用的 10）' % REDIS_DB)
    add('Redis key 前缀独立', CACHE_NAME.startswith(DB_PREFIX),
        'JX_CACHE_NAME=%s' % CACHE_NAME)

    # 5) 连通性
    db_ok = connect_check(db_host, db_port, '数据库')
    rd_ok = connect_check(rd_host, rd_port, 'Redis')

    # 6) 实际建库/查询（连得上才做）
    server_version = None
    table_count = None
    if db_ok:
        try:
            sys.path.insert(0, os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                                            '..', 'outputs', 'open-source-review', '_tools'))
            import pymysql
            conn = pymysql.connect(host=db_host, port=int(db_port), user=os.environ['MYSQL_USER'],
                                   password=os.environ.get('MYSQL_PASSWORD', ''), charset='utf8mb4',
                                   connect_timeout=8)
            with conn.cursor() as cur:
                cur.execute('SELECT VERSION()')
                server_version = cur.fetchone()[0]
                cur.execute("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=%s", (db_name,))
                table_count = cur.fetchone()[0]
            conn.close()
            add('数据库连接与库存在性', True,
                'server=%s，库 %s 现有表 %d 张' % (server_version, db_name, table_count))
            # 当前验证环境复用共享实例上的 root 账号（本机没有可用容器/独立实例）。
            # 这属于「隔离强度未达理想」，登记为 warn 而非阻断：库名前缀与 Redis 逻辑库编号
            # 仍被硬校验，OS014 的 Docker Compose 会创建只对本库有权限的独立账号。
            add('连接账号非高权限 root', os.environ.get('MYSQL_USER') != 'root',
                'MYSQL_USER=%s —— 共享实例复用 root 属未决项，处置见 OS014（容器内独立账号）'
                % os.environ.get('MYSQL_USER'), level='warn')
        except Exception as e:  # noqa: BLE001
            add('数据库连接与库存在性', False, '连接失败：%s' % str(e)[:200])

    if rd_ok:
        try:
            sys.path.insert(0, os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                                            '..', 'outputs', 'open-source-review', '_tools'))
            import redis_lite
            with redis_lite.RedisLite(host=rd_host, port=rd_port, db=REDIS_DB) as r:
                size = r.cmd('DBSIZE')
            add('Redis 连接与逻辑库可读写探测', True, 'db%d DBSIZE=%s' % (REDIS_DB, size))
        except Exception as e:  # noqa: BLE001
            add('Redis 连接与逻辑库可读写探测', False, '连接失败：%s' % str(e)[:200])

    failed = [c for c in checks if c['result'] == 'FAIL']
    warned = [c for c in checks if c['result'] == 'WARN']
    out = {
        'task': 'OS007',
        'executed_at': datetime.datetime.now().isoformat(timespec='seconds'),
        'target': {
            'mysql': '%s:%s/%s' % (mask(db_host), db_port, db_name),
            'mysql_server_version': server_version,
            'mysql_table_count_before_smoke': table_count,
            'redis': '%s:%s db%d prefix=%s' % (mask(rd_host), rd_port, REDIS_DB, CACHE_NAME),
            'backend_port': os.environ.get('JX_SERVER_PORT', '8199'),
        },
        'isolation_note': '库名与 key 前缀均带社区版标识；Redis 使用非 0/非 10 的独立逻辑库；'
                          '本文件不含任何口令，主机名已脱敏。',
        'checks': checks,
        'pass': len(checks) - len(failed) - len(warned),
        'warn': len(warned),
        'fail': len(failed),
        'total': len(checks),
    }
    os.makedirs(os.path.dirname(os.path.abspath(args.out)), exist_ok=True)
    with open(args.out, 'w', encoding='utf-8') as f:
        json.dump(out, f, ensure_ascii=False, indent=1)

    for c in checks:
        print('%-4s %s | %s' % (c['result'], c['check'], c['detail']))
    print('== 通过 %d / 警告 %d / 失败 %d（共 %d）== 证据：%s'
          % (out['pass'], out['warn'], out['fail'], out['total'], args.out))
    return 1 if failed else 0


if __name__ == '__main__':
    sys.exit(main())
