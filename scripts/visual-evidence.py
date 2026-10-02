# -*- coding: utf-8 -*-
"""界面逐页取证：把「管理端到底长什么样」变成可复跑、可断言的证据。

它做的事（不需要 Docker，只需要本机有 MySQL / Redis / JDK8 / Chrome 或 Edge）：

  1. 建一个**空库**并执行 `database/001_schema.sql` → `002_seed_system.sql` → `003_demo_data.sql`；
  2. 用 `backend/target/jianxue.jar`（没有就先 `mvn -o package -DskipTests`）以 community profile 起真后端；
  3. 起一个本地静态站托管 `admin-web/dist`（**与 Docker 镜像里打包的是同一份产物**），
     并把 `/api/` 反代到后端 —— 与 `deploy/nginx/default.conf` 的 `^~ /api/` 口径一致；
  4. 用 headless Chrome/Edge 逐页打开：先取 `/getRouters` 得到**真实菜单**，再一页一页截图 + 取渲染后的 DOM；
  5. 逐页断言：侧边栏出现该菜单标题、页面不是登录页、不是「未知路由/无权限」错误页、像素不是空白；
     另外把「演示数据真的渲染出来了」也记下来（学员管理页能找到 003 里的学员姓名）；
  6. 三组负向对照：不带令牌 → 登录页；不存在的路由 → 错误页；`sys_menu.mec_type` 为空的菜单 → 错误页
     （这类菜单不会被任何内置角色看到，见下面注释）。

为什么不用「页面不空白」当判据：空白页也能过。这里用的是**内容断言**（菜单标题 + 演示数据实体名），
并且每条判据都对坏样本自检过（`assert_judge`）。

用法：
    set -a && source scripts/env.local && set +a
    python scripts/visual-evidence.py                 # 结果写到临时目录并打印路径
    python scripts/visual-evidence.py --out-dir ./shots --only student
    python scripts/visual-evidence.py --readme-shots --out-dir ./shots   # 顺手重算 README 图

依赖：`pymysql`、`Pillow`；后端要能连上 `MYSQL_*` / `REDIS_*`（同 README「方式 B：本地直接跑」）。
"""
import argparse
import hashlib
import http.client
import io
import json
import os
import re
import secrets
import shutil
import socket
import subprocess
import sys
import tempfile
import threading
import time
import urllib.error
import urllib.parse
import urllib.request
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

HERE = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.dirname(HERE)
DBDIR = os.path.join(REPO, 'database')
DIST = os.path.join(REPO, 'admin-web', 'dist')
JAR = os.path.join(REPO, 'backend', 'target', 'jianxue.jar')
BACKEND = os.path.join(REPO, 'backend')
ADMIN_USER = 'admin'
TOKEN_COOKIE = 'jianxue-Admin-Token'
DEMO_STUDENTS = ['李昊然', '刘星辰', '张梓萱']          # 003_demo_data.sql 里的学员
# README 里展示的页面：(页面标题, 输出文件名)。'登录页' 用未登录那一张（它不是一个菜单路由）。
README_PICKS = [
    ('登录页', '01-login.jpg'),
    ('首页', '02-dashboard.jpg'),
    ('学员管理', '03-students.jpg'),
    ('班级信息', '04-classes.jpg'),
    ('报读情况', '05-enrollment.jpg'),
    ('课表管理', '06-curriculum.jpg'),
    ('班级明细报表', '07-class-report.jpg'),
    ('学生课次统计', '08-coursetime-report.jpg'),
]
LOGIN_MARKERS = ('请输入登录账号', '记住密码', '登 录')   # 登录页的真实文案
ERROR_MARKERS = ('此网页超出了您的权限', '对不起，您没有权限进入此页面', '返回我的页面')
CHROME_CANDIDATES = [
    os.environ.get('CHROME_PATH', ''),
    'C:/Program Files/Google/Chrome/Application/chrome.exe',
    'C:/Program Files (x86)/Google/Chrome/Application/chrome.exe',
    'C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe',
    'C:/Program Files/Microsoft/Edge/Application/msedge.exe',
    '/usr/bin/google-chrome', '/usr/bin/chromium', '/usr/bin/chromium-browser',
    '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome',
]

STEPS = []
BACKEND_PROC = {'proc': None, 'log': None}
HTTPD = {'srv': None}


# ----------------------------------------------------------------- 小工具
def step(sid, name, ok, detail, data=None):
    rec = {'id': sid, 'name': name, 'result': 'PASS' if ok else 'FAIL',
           'evidence_tier': 'L4 行为断言+前后值', 'detail': detail}
    if data:
        rec['data'] = data
    STEPS.append(rec)
    print('%-4s %-6s %-34s %s' % ('PASS' if ok else 'FAIL', sid, name, detail))
    return ok


def assert_judge(name, predicate, good, bad):
    """判据自检：同一条判据必须对好样本判真、对坏样本判假，否则判据恒真无效。"""
    assert predicate(good), '%s：对好样本判为假' % name
    assert not predicate(bad), '%s：对坏样本判为真（判据无效）' % name


def db_conn(database=None):
    import pymysql
    kw = dict(host=os.environ['MYSQL_HOST'], port=int(os.environ.get('MYSQL_PORT', 3306)),
              user=os.environ['MYSQL_USER'], password=os.environ['MYSQL_PASSWORD'],
              charset='utf8mb4', autocommit=True)
    if database:
        kw['database'] = database
    return pymysql.connect(**kw)


def q(sql_text, args=None, database=None):
    conn = db_conn(database)
    try:
        with conn.cursor() as cur:
            cur.execute(sql_text, args)
            return cur.fetchall()
    finally:
        conn.close()


def split_statements(text):
    """按 `;` 切分 SQL，跳过注释与字符串/反引号内的分号。

    不要图省事写 `text.split(';')`：`001_schema.sql` 里既有注释、也有带中文分号的字符串，
    切错会让建库「看起来跑了」但实际失败一堆语句（本脚本第一版就踩了，34 条语句失败）。
    """
    stmts, buf = [], []
    i, n = 0, len(text)
    quote = None
    while i < n:
        ch = text[i]
        nxt = text[i + 1] if i + 1 < n else ''
        if quote:
            buf.append(ch)
            if ch == '\\' and quote in ("'", '"'):
                if nxt:
                    buf.append(nxt)
                    i += 2
                    continue
            elif ch == quote:
                quote = None
            i += 1
            continue
        if ch in ("'", '"', '`'):
            quote = ch
            buf.append(ch)
            i += 1
            continue
        if ch == '-' and nxt == '-':
            while i < n and text[i] != '\n':
                i += 1
            continue
        if ch == '#':
            while i < n and text[i] != '\n':
                i += 1
            continue
        if ch == '/' and nxt == '*':
            i += 2
            while i < n - 1 and not (text[i] == '*' and text[i + 1] == '/'):
                i += 1
            i += 2
            continue
        if ch == ';':
            s = ''.join(buf).strip()
            if s:
                stmts.append(s)
            buf = []
            i += 1
            continue
        buf.append(ch)
        i += 1
    tail = ''.join(buf).strip()
    if tail:
        stmts.append(tail)
    return stmts


def exec_sql_file(path, db):
    """整文件在**单连接**里执行 —— 文件头的 `SET FOREIGN_KEY_CHECKS=0` 是会话级的，
    逐条各开一个连接会让它静默失效（QRTZ 那几张互相引用的表就会少建）。"""
    text = io.open(path, encoding='utf-8').read()
    conn = db_conn(db)
    done = failed = 0
    errors = []
    try:
        with conn.cursor() as cur:
            for s in split_statements(text):
                try:
                    cur.execute(s)
                    done += 1
                except Exception as exc:                              # noqa: BLE001
                    failed += 1
                    errors.append('%s :: %s' % (str(exc)[:120], s[:60]))
    finally:
        conn.close()
    return {'executed': done, 'failed': failed, 'errors': errors[:3]}


def api(base, path, method='GET', payload=None, token=None, timeout=40):
    data = json.dumps(payload).encode() if payload is not None else None
    headers = {'Content-Type': 'application/json'}
    if token:
        headers['Authorization'] = 'Bearer ' + token
    opener = urllib.request.build_opener(urllib.request.ProxyHandler({}))   # 本机直连，不吃环境代理
    req = urllib.request.Request(base + path, data=data, headers=headers, method=method)
    try:
        with opener.open(req, timeout=timeout) as r:
            return r.status, r.read().decode('utf-8', 'replace')
    except urllib.error.HTTPError as exc:
        return exc.code, exc.read().decode('utf-8', 'replace')
    except Exception as exc:                                          # noqa: BLE001
        return 0, 'CONNECTION_ERROR: %s' % str(exc)[:160]


def api_json(base, path, method='GET', payload=None, token=None):
    st, text = api(base, path, method, payload, token)
    try:
        return st, json.loads(text)
    except ValueError:
        return st, {}


def port_open(port):
    s = socket.socket()
    s.settimeout(0.6)
    try:
        s.connect(('127.0.0.1', port))
        return True
    except Exception:                                                 # noqa: BLE001
        return False
    finally:
        s.close()


def kill_port(port):
    if os.name != 'nt':
        subprocess.run(['bash', '-c', 'lsof -ti tcp:%d | xargs -r kill -9' % port], check=False)
        return
    out = subprocess.run(['netstat', '-ano', '-p', 'TCP'], capture_output=True, check=False).stdout
    for line in out.decode('utf-8', 'replace').splitlines():
        parts = line.split()
        if len(parts) >= 5 and parts[3] == 'LISTENING' and parts[1].endswith(':%d' % port):
            subprocess.run(['taskkill', '/F', '/T', '/PID', parts[4]], check=False,
                           stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)


def read_text(path):
    raw = open(path, 'rb').read()
    for enc in ('utf-8', 'gbk'):
        try:
            return raw.decode(enc)
        except UnicodeDecodeError:
            continue
    return raw.decode('utf-8', 'replace')


def find_chrome():
    for p in CHROME_CANDIDATES:
        if p and os.path.exists(p):
            return p
    return None


def find_java():
    if os.environ.get('JAVA_HOME'):
        exe = os.path.join(os.environ['JAVA_HOME'], 'bin',
                           'java.exe' if os.name == 'nt' else 'java')
        if os.path.exists(exe):
            return exe
    return shutil.which('java') or 'java'


def dom_text(html):
    t = re.sub(r'(?is)<script.*?</script>', ' ', html)
    t = re.sub(r'(?is)<style.*?</style>', ' ', t)
    t = re.sub(r'(?s)<[^>]+>', ' ', t)
    return re.sub(r'\s+', ' ', t)


def is_error_page(text):
    return any(m in text for m in ERROR_MARKERS)


def audit_png(path):
    from PIL import Image
    im = Image.open(path).convert('RGB')
    w, h = im.size
    small = im.resize((max(1, w // 4), max(1, h // 4)))
    colors = small.getcolors(maxcolors=1 << 24) or []
    total = small.size[0] * small.size[1]
    top = max((count for count, _ in colors), default=0)
    return {'size': '%dx%d' % (w, h), 'bytes': os.path.getsize(path),
            'unique_colors_1_4': len(colors), 'dominant_ratio': round(top / float(total), 4)}


def contact_sheet(items, out_path, cols=3, cell=(480, 300)):
    from PIL import Image, ImageDraw, ImageFont
    font = None
    for fp in ('C:/Windows/Fonts/msyh.ttc', 'C:/Windows/Fonts/simhei.ttf',
               '/System/Library/Fonts/PingFang.ttc',
               '/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc'):
        if os.path.exists(fp):
            try:
                font = ImageFont.truetype(fp, 16)
                break
            except Exception:                                         # noqa: BLE001
                font = None
    rows = (len(items) + cols - 1) // cols
    pad, label_h = 12, 26
    sheet = Image.new('RGB', (cols * (cell[0] + pad) + pad,
                              rows * (cell[1] + label_h + pad) + pad), (247, 245, 240))
    d = ImageDraw.Draw(sheet)
    for i, (title, png) in enumerate(items):
        r, c = divmod(i, cols)
        x = pad + c * (cell[0] + pad)
        y = pad + r * (cell[1] + label_h + pad)
        d.text((x, y), title, fill=(41, 45, 50), font=font)
        try:
            sheet.paste(Image.open(png).convert('RGB').resize(cell), (x, y + label_h))
        except Exception:                                             # noqa: BLE001
            d.rectangle([x, y + label_h, x + cell[0], y + label_h + cell[1]], fill=(220, 220, 220))
        d.rectangle([x, y + label_h, x + cell[0], y + label_h + cell[1]], outline=(120, 120, 120))
    sheet.save(out_path, quality=88)
    return out_path


# ----------------------------------------------------------------- 后端与静态站
def start_backend(port, db, admin_password, upload_root, timeout=240):
    log_path = os.path.join(upload_root, '..', 'backend.log')
    if port_open(port):
        kill_port(port)
        time.sleep(3)
    env = dict(os.environ)
    env.update({'INITIAL_ADMIN_PASSWORD': admin_password,
                'UPLOAD_ROOT': upload_root.replace('\\', '/') + '/',
                # **必须把后端指到本脚本刚建的那个库**：否则后端会去连 MYSQL_DATABASE 里那个库，
                # 在别人已经初始化过的库上跑，启动日志会说「已完成初始化，跳过口令写入」，
                # 于是登录用 INITIAL_ADMIN_PASSWORD 必然 Bad credentials（踩过）。
                'MYSQL_DATABASE': db})
    args = [find_java(), '-Dfile.encoding=UTF-8', '-jar', JAR,
            '--server.port=%d' % port,
            '--spring.redis.database=%s' % os.environ.get('REDIS_DB', '12'),
            '--spring.redis.cache_name=%s' % os.environ.get('REDIS_CACHE_NAME', 'yunduo_community'),
            '--spring.datasource.druid.stat-view-servlet.enabled=false']
    log = open(log_path, 'w', encoding='utf-8', errors='replace')
    proc = subprocess.Popen(args, cwd=BACKEND, env=env, stdout=log, stderr=subprocess.STDOUT)
    BACKEND_PROC.update(proc=proc, log=log)
    t0 = time.time()
    while time.time() - t0 < timeout:
        if proc.poll() is not None:
            return False, '进程退出 rc=%s（日志 %s）' % (proc.returncode, log_path)
        if os.path.exists(log_path) and 'Started ' in read_text(log_path):
            return True, log_path
        time.sleep(2)
    return False, '超时未见 Started（日志 %s）' % log_path


def stop_backend(port):
    proc = BACKEND_PROC.get('proc')
    if proc is not None and proc.poll() is None:
        if os.name == 'nt':
            subprocess.run(['taskkill', '/F', '/T', '/PID', str(proc.pid)], check=False,
                           stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        else:
            proc.terminate()
    time.sleep(2)
    kill_port(port)
    if BACKEND_PROC.get('log'):
        BACKEND_PROC['log'].close()
    BACKEND_PROC.update(proc=None, log=None)


SEED_HTML = """<!DOCTYPE html><html><head><meta charset="utf-8"><title>seed</title></head>
<body><script>
(function () {
  var q = new URLSearchParams(location.search);
  var t = q.get('token');
  if (t) { document.cookie = '%s=' + t + '; path=/; SameSite=Strict'; }
  location.replace(q.get('next') || '/');
})();
</script></body></html>""" % TOKEN_COOKIE


def make_handler(backend_port, dist):
    class Handler(BaseHTTPRequestHandler):
        protocol_version = 'HTTP/1.1'

        def log_message(self, *a):
            pass

        def _send(self, code, ctype, payload):
            self.send_response(code)
            self.send_header('Content-Type', ctype)
            self.send_header('Content-Length', str(len(payload)))
            self.end_headers()
            try:
                self.wfile.write(payload)
            except BrokenPipeError:
                pass

        def do_GET(self):
            path = urllib.parse.urlparse(self.path).path
            if path.startswith('/api/'):
                return self._proxy('GET')
            if path == '/__seed':
                return self._send(200, 'text/html; charset=utf-8', SEED_HTML.encode('utf-8'))
            rel = path.lstrip('/') or 'index.html'
            full = os.path.normpath(os.path.join(dist, rel.replace('/', os.sep)))
            if not full.startswith(os.path.normpath(dist)) or not os.path.isfile(full):
                return self._send(404, 'text/html; charset=utf-8', b'<h1>404</h1>not found')
            ctype = {'.html': 'text/html; charset=utf-8', '.js': 'application/javascript',
                     '.css': 'text/css', '.png': 'image/png', '.jpg': 'image/jpeg',
                     '.gif': 'image/gif', '.svg': 'image/svg+xml', '.ico': 'image/x-icon',
                     '.woff': 'font/woff', '.woff2': 'font/woff2', '.ttf': 'font/ttf'}.get(
                os.path.splitext(full)[1].lower(), 'application/octet-stream')
            return self._send(200, ctype, open(full, 'rb').read())

        def do_POST(self):
            return self._proxy('POST') if self.path.startswith('/api/') \
                else self._send(404, 'text/plain', b'no')

        def _proxy(self, method):
            """`/api/xxx` → 后端 `/xxx`，与 deploy/nginx/default.conf 的 proxy_pass 一致。"""
            target = self.path[len('/api'):] or '/'
            length = int(self.headers.get('Content-Length') or 0)
            payload = self.rfile.read(length) if length else None
            headers = {h: self.headers[h] for h in ('Authorization', 'Content-Type', 'Accept')
                       if self.headers.get(h)}
            conn = http.client.HTTPConnection('127.0.0.1', backend_port, timeout=60)
            try:
                conn.request(method, target, body=payload, headers=headers)
                resp = conn.getresponse()
                self._send(resp.status, resp.getheader('Content-Type') or 'application/json',
                           resp.read())
            except Exception as exc:                                  # noqa: BLE001
                self._send(502, 'application/json',
                           json.dumps({'code': 502, 'msg': str(exc)}).encode())
            finally:
                conn.close()

    return Handler


def start_web(port, backend_port):
    if port_open(port):
        kill_port(port)
        time.sleep(1)
    srv = ThreadingHTTPServer(('127.0.0.1', port), make_handler(backend_port, DIST))
    threading.Thread(target=srv.serve_forever, daemon=True).start()
    HTTPD['srv'] = srv
    time.sleep(0.5)
    return port_open(port)


def stop_web():
    if HTTPD.get('srv'):
        HTTPD['srv'].shutdown()
        HTTPD['srv'].server_close()
    HTTPD['srv'] = None


# ----------------------------------------------------------------- 浏览器
def chrome_capture(chrome, url, png_path, out_dir, timeout=90, budget=25000,
                   size=(1440, 900)):
    profile = os.path.join(out_dir, '_profile')
    common = [chrome, '--headless=new', '--disable-gpu', '--no-first-run',
              '--no-default-browser-check', '--hide-scrollbars', '--mute-audio',
              '--disable-extensions', '--no-proxy-server',
              '--window-size=%d,%d' % size,
              '--virtual-time-budget=%d' % budget,
              '--user-data-dir=' + profile]
    shot = subprocess.run(common + ['--screenshot=' + png_path, url],
                          capture_output=True, timeout=timeout)
    dom = subprocess.run(common + ['--dump-dom', url], capture_output=True, timeout=timeout)
    text = dom.stdout.decode('utf-8', 'replace')
    with io.open(png_path.replace('.png', '.dom.html'), 'w',
                 encoding='utf-8', newline='\n') as fh:
        fh.write(text)
    return shot.returncode, text


def derive_routes(base, token):
    st, obj = api_json(base, '/getRouters', token=token)
    rows = obj.get('data') or []

    def join(parent, child):
        parent = (parent or '').strip('/')
        child = (child or '').strip('/')
        if not parent:
            return '/' + child
        return '/' + parent + ('/' + child if child else '')

    out = []
    for top in rows:
        kids = top.get('children') or []
        if kids:
            for c in kids:
                out.append((join(top.get('path'), c.get('path')),
                            (c.get('meta') or {}).get('title') or c.get('name') or ''))
        else:
            out.append((join('', top.get('path')),
                        (top.get('meta') or {}).get('title') or top.get('name') or ''))
    return st, out


def unreachable_menu_paths(db_name):
    """`mec_type` 为空（NULL）的目录菜单：菜单查询用 `mec_type = ?` 等值过滤，NULL 永不命中。"""
    rows = q("SELECT menu_id, parent_id, menu_name, path, component FROM sys_menu "
             "WHERE menu_type IN ('M','C') AND mec_type IS NULL AND status = '0' "
             "AND visible = '0' ORDER BY menu_id", database=db_name)
    allrows = {r[0]: r for r in q('SELECT menu_id, parent_id, menu_name, path FROM sys_menu',
                                 database=db_name)}
    out = []
    for mid, parent, name, path, comp in rows:
        chain, cur, guard = [], parent, 0
        while cur and guard < 10:
            row = allrows.get(cur)
            if not row:
                break
            chain.append(row[3])
            cur = row[1]
            guard += 1
        parts = [p.strip('/') for p in reversed(chain) if p] + [str(path or '').strip('/')]
        out.append({'menu_id': mid, 'title': name, 'path': '/' + '/'.join(parts),
                    'component': comp})
    return out


def write_readme_shots(pages, login_png, out_dir, width=1200, quality=90):
    """从原图统一缩放，生成 README 里用的那 8 张（`docs/screenshots/`）。

    统一缩放而不是直接拷原图的理由：原图是 1440x900 PNG（登录页那张单张 447 KB），
    8 张就有几 MB；缩到宽 1200 + JPEG q90（4:4:4，避免中文发虚）后合计不到 1 MB。
    整个过程是确定性的：同一批原图重跑得到同样的字节与 sha256。
    """
    from PIL import Image
    dst = os.path.join(REPO, 'docs', 'screenshots')
    os.makedirs(dst, exist_ok=True)
    made = []
    for title, fname in README_PICKS:
        if title == '登录页':
            src = login_png
        else:
            rec = next((p for p in pages if p['title'].strip() == title), None)
            src = os.path.join(out_dir, 'screenshots', rec['png']) if rec else None
        if not src or not os.path.exists(src):
            print('   !! 缺页面，跳过：%s' % title)
            continue
        im = Image.open(src).convert('RGB')
        w, h = im.size
        im = im.resize((width, int(round(h * width / float(w)))), Image.LANCZOS)
        out = os.path.join(dst, fname)
        im.save(out, 'JPEG', quality=quality, subsampling=0, optimize=True)
        raw = open(out, 'rb').read()
        made.append({'file': 'docs/screenshots/' + fname, 'from': os.path.basename(src),
                     'bytes': len(raw), 'sha256': hashlib.sha256(raw).hexdigest(),
                     'pixels': '%dx%d' % im.size})
        print('   %-24s %8d B  %s' % (fname, len(raw), hashlib.sha256(raw).hexdigest()[:16]))
    return made


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--out-dir', default=os.path.join(tempfile.gettempdir(), 'yunduo-ui-evidence'))
    ap.add_argument('--db', default=os.environ.get('VISUAL_DB', 'yunduo_community_visual'))
    ap.add_argument('--port', type=int, default=8199)
    ap.add_argument('--web-port', type=int, default=9101)
    ap.add_argument('--only', default=None, help='只跑路径里含该子串的页面')
    ap.add_argument('--keep', action='store_true', help='跑完不关后端/静态站（排错用）')
    ap.add_argument('--readme-shots', action='store_true',
                    help='顺手生成 README 用的 8 张图到 docs/screenshots/（可复算）')
    args = ap.parse_args()
    need = ('MYSQL_HOST', 'MYSQL_USER', 'MYSQL_PASSWORD')
    missing = [k for k in need if not os.environ.get(k)]
    if missing:
        sys.exit('缺少环境变量 %s（参考 scripts/env.local.example）' % missing)

    t0 = time.time()
    base = 'http://127.0.0.1:%d' % args.port
    web = 'http://127.0.0.1:%d' % args.web_port
    out_dir = os.path.abspath(args.out_dir)
    raw_dir = os.path.join(out_dir, 'screenshots')
    os.makedirs(raw_dir, exist_ok=True)
    chrome = find_chrome()

    # ---- 环境准备
    for f in ('001_schema.sql', '002_seed_system.sql', '003_demo_data.sql'):
        if not os.path.exists(os.path.join(DBDIR, f)):
            sys.exit('缺少 database/%s' % f)
    q('DROP DATABASE IF EXISTS `%s`' % args.db)
    q('CREATE DATABASE `%s` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci' % args.db)
    setup = {f: exec_sql_file(os.path.join(DBDIR, f), args.db)
             for f in ('001_schema.sql', '002_seed_system.sql', '003_demo_data.sql')}
    setup_failed = sum(v['failed'] for v in setup.values())
    if not os.path.exists(JAR):
        print('未找到 %s，先执行 mvn -o package -DskipTests ...' % os.path.relpath(JAR, REPO))
        subprocess.run(['mvn', '-o', '-q', 'clean', 'package', '-DskipTests'], cwd=BACKEND,
                       check=False)
    ok0 = chrome is not None and os.path.exists(JAR) \
        and os.path.isfile(os.path.join(DIST, 'index.html')) and setup_failed == 0
    step('V00', '环境准备（空库 001+002+003 / 产物 / 浏览器）', ok0,
         '建库语句失败=%d；dist/index.html=%s；jar=%s；浏览器=%s'
         % (setup_failed, os.path.isfile(os.path.join(DIST, 'index.html')),
            os.path.exists(JAR), os.path.basename(chrome) if chrome else '缺失'),
         {'db': args.db, 'setup': setup, 'chrome': chrome})
    if not ok0:
        return finalize(args, t0, out_dir)

    admin_pw = secrets.token_urlsafe(15) + 'Aa1'
    started, info = start_backend(args.port, args.db, admin_pw, os.path.join(out_dir, 'upload'))
    if not started:
        step('V00b', '启动后端', False, info)
        return finalize(args, t0, out_dir)

    st, obj = api_json(base, '/login', 'POST', {'username': ADMIN_USER, 'password': admin_pw})
    token = obj.get('token')
    if not (st == 200 and obj.get('code') == 200 and token):
        step('V01', '登录取令牌', False, 'HTTP %s code=%s' % (st, obj.get('code')))
        stop_backend(args.port)
        return finalize(args, t0, out_dir)
    step('V01', '登录取令牌（机构管理员）', True,
         'POST /login → HTTP %s code=%s token 长度=%d' % (st, obj.get('code'), len(token)),
         {'token_len': len(token)})

    st_r, routes_mec = derive_routes(base, token)
    token_mec = token
    step('V02', '菜单路由（机构视图）', st_r == 200 and len(routes_mec) >= 1,
         '顶层 %d 个页面：%s' % (len(routes_mec), '、'.join(t for _, t in routes_mec)),
         {'routes': [{'path': p, 'title': t} for p, t in routes_mec]})

    # 第二个会话：`/base/mechanism/into/{id}` 是**原地刷新同一个令牌**，不能拿同一个令牌既拍机构视图又拍校区视图
    st2, obj2 = api_json(base, '/login', 'POST', {'username': ADMIN_USER, 'password': admin_pw})
    token_campus = obj2.get('token')
    st_in, into = api_json(base, '/base/mechanism/into/1', 'POST', None, token_campus)
    st_r2, routes_campus = derive_routes(base, token_campus)
    ok02 = bool(token_campus) and into.get('code') == 200 and len(routes_campus) > len(routes_mec)
    step('V02b', '进入校区 → 菜单切换为校区视图', ok02,
         'POST /base/mechanism/into/1 → code=%s；页面 %d → %d'
         % (into.get('code'), len(routes_mec), len(routes_campus)),
         {'routes': [{'path': p, 'title': t} for p, t in routes_campus]})

    if not start_web(args.web_port, args.port):
        step('V03', '静态站 + /api 反代', False, '静态服务器未起来')
        stop_backend(args.port)
        return finalize(args, t0, out_dir)
    opener = urllib.request.build_opener(urllib.request.ProxyHandler({}))
    st_api = 0
    try:
        with opener.open(urllib.request.Request(
                web + '/api/getInfo', headers={'Authorization': 'Bearer ' + token}), timeout=20) as r:
            st_api = r.status
    except urllib.error.HTTPError as exc:
        st_api = exc.code
    except Exception:                                                 # noqa: BLE001
        st_api = 0
    step('V03', '静态站 + /api 反代（与 nginx 同口径）', st_api == 200,
         'GET %s/api/getInfo → HTTP %s' % (web, st_api), {'web_port': args.web_port})

    def seed_url(tok, rel):
        return (web + '/__seed?token=' + urllib.parse.quote(tok) + '&next='
                + urllib.parse.quote('/#' + rel, safe=''))

    def shot(tok, rel, name):
        png = os.path.join(raw_dir, name)
        rc, html = chrome_capture(chrome, seed_url(tok, rel), png, out_dir)
        return png, rc, html

    # ---- 未登录负向对照
    login_png, rc, html_login = shot('', '/', '00-login.png')
    step('V04', '负向对照：未带令牌 → 登录页',
         rc == 0 and any(m in dom_text(html_login) for m in LOGIN_MARKERS),
         'headless 退出码=%s；命中登录页文案=%s'
         % (rc, [m for m in LOGIN_MARKERS if m in dom_text(html_login)]),
         {'png': os.path.relpath(login_png, out_dir)})

    # ---- 逐页取证
    targets = [('机构视图', p, t, token_mec) for p, t in routes_mec] + \
              [('校区视图', p, t, token_campus) for p, t in routes_campus]
    if args.only:
        targets = [x for x in targets if args.only.lower() in x[1].lower()]
    pages = []
    for view, path, title, tok in targets:
        name = '%02d-%s.png' % (len(pages) + 1,
                                re.sub(r'[^\w\u4e00-\u9fff-]+', '_', (title or path).strip())[:40])
        png, rc, html = shot(tok, path, name)
        txt = dom_text(html)
        pixel = audit_png(png)
        rec = {'view': view, 'path': path, 'title': title, 'png': name, 'rc': rc,
               'dom_text_len': len(txt),
               'on_login_page': any(m in txt for m in LOGIN_MARKERS),
               'on_error_page': is_error_page(txt),
               'title_in_dom': (title or '').strip() in txt,
               'demo_entities_in_dom': [s for s in DEMO_STUDENTS if s in txt],
               'pixel': pixel,
               'sha256': hashlib.sha256(open(png, 'rb').read()).hexdigest()}
        rec['result'] = 'PASS' if (rc == 0 and not rec['on_login_page'] and not rec['on_error_page']
                                   and rec['title_in_dom'] and len(txt) >= 100
                                   and pixel['unique_colors_1_4'] > 60
                                   and pixel['dominant_ratio'] < 0.97) else 'FAIL'
        pages.append(rec)
        print('   %-4s %-8s %-24s %-20s dom=%5d colors=%4d %s'
              % (rec['result'], view, path, title, len(txt), pixel['unique_colors_1_4'],
                 ('演示数据=%s' % '/'.join(rec['demo_entities_in_dom']))
                 if rec['demo_entities_in_dom'] else ''))
    passed = sum(1 for r in pages if r['result'] == 'PASS')
    step('V05', '逐页取证（菜单标题 + 演示数据 + 像素）', passed == len(pages) and passed > 0,
         '%d/%d 页 PASS；%d 页 DOM 里出现演示数据实体名'
         % (passed, len(pages), sum(1 for r in pages if r['demo_entities_in_dom'])),
         {'pages': pages})

    # ---- 不存在的路由
    nf_png, _rc, html_nf = shot(token_campus, '/this-route-does-not-exist', '99-notfound.png')
    step('V06', '负向对照：不存在的路由 → 错误页', is_error_page(dom_text(html_nf)),
         '命中错误页文案=%s' % is_error_page(dom_text(html_nf)))

    # ---- mec_type 为空的菜单
    unreachable = unreachable_menu_paths(args.db)
    null_ids = [u['menu_id'] for u in unreachable]
    role_facts = {}
    for rid, ids in q('SELECT role_id, GROUP_CONCAT(menu_id ORDER BY menu_id) FROM sys_role_menu '
                      'GROUP BY role_id', database=args.db):
        lst = [int(x) for x in str(ids).split(',')]
        role_facts[rid] = {'mapped_total': len(lst),
                           'mapped_null_menus': [i for i in lst if i in null_ids]}
    confirmed = []
    for u in unreachable:
        nm = '98-unreachable-%s.png' % re.sub(r'[^\w-]+', '_', u['title'])[:20]
        png, _rc, html = shot(token_campus, u['path'], nm)
        confirmed.append(dict(u, png=nm, lands_on_error_page=is_error_page(dom_text(html))))
    leaked = [u for u in unreachable
              if u['path'] in {p for p, _ in routes_mec} | {p for p, _ in routes_campus}]
    step('V07', 'mec_type 为空的菜单在内置角色下不可达',
         bool(unreachable) and not leaked and all(c['lands_on_error_page'] for c in confirmed),
         '%d 个菜单（%s）不在任何菜单里，直接访问都落到错误页；role_menu 映射情况：%s'
         % (len(unreachable), '、'.join(u['title'] for u in unreachable),
            {k: v['mapped_null_menus'] for k, v in sorted(role_facts.items())}),
         {'unreachable_menus': unreachable, 'confirmed_error_page': confirmed,
          'leaked_into_routes': leaked, 'role_menu_facts': role_facts})

    # ---- 判据自检
    assert_judge('菜单标题', lambda s: '学员管理' in s, '学员管理 学员列表', '随便什么文本')
    assert_judge('演示数据', lambda s: any(stu in s for stu in DEMO_STUDENTS),
                 '李昊然 剩余课时 9', '没有这个学员')
    assert_judge('登录页', lambda s: any(m in s for m in LOGIN_MARKERS),
                 '请输入登录账号/注册手机号码 记住密码 登 录', '退出登录 学员管理')
    assert_judge('错误页', is_error_page, ERROR_MARKERS[0], '学员管理 共 10 条')

    # ---- 联系表
    items = [('00 未登录（负向对照）', login_png)] + \
            [('%02d [%s] %s %s' % (i + 1, r['view'], r['path'], r['title']),
              os.path.join(raw_dir, r['png'])) for i, r in enumerate(pages)] + \
            [('91 不存在路由（错误页）', nf_png)] + \
            [('90 不可达菜单 %s' % c['title'], os.path.join(raw_dir, c['png'])) for c in confirmed]
    sheet = os.path.join(out_dir, 'contact-sheet.jpg')
    contact_sheet(items, sheet)
    step('V08', '生成联系表（人眼复核）', os.path.exists(sheet),
         '%s（%d 格）' % (os.path.relpath(sheet, out_dir), len(items)))

    if args.readme_shots:
        made = write_readme_shots(pages, login_png, out_dir)
        step('V09', '生成 README 截图（docs/screenshots）', len(made) == len(README_PICKS),
             '%d/%d 张已写入 docs/screenshots/（宽 1200，JPEG q=90 4:4:4）'
             % (len(made), len(README_PICKS)), {'shots': made})

    if not args.keep:
        stop_web()
        stop_backend(args.port)
    return finalize(args, t0, out_dir)


def finalize(args, t0, out_dir):
    summary = {}
    for s in STEPS:
        summary[s['result']] = summary.get(s['result'], 0) + 1
    rec = {'what': '管理端界面逐页取证', 'db': args.db, 'out_dir': out_dir,
           'started_at': time.strftime('%Y-%m-%dT%H:%M:%S', time.localtime(t0)),
           'finished_at': time.strftime('%Y-%m-%dT%H:%M:%S'),
           'seconds': round(time.time() - t0, 1), 'steps': STEPS, 'summary': summary,
           'evidence_tier': 'L4（内容断言 + 前后值）；L5「容器内取证」需要 Docker，未覆盖',
           'result': 'PASS' if summary.get('FAIL', 0) == 0 and summary.get('PASS', 0) > 0
           else 'FAIL'}
    out = os.path.join(out_dir, 'visual-evidence.json')
    with io.open(out, 'w', encoding='utf-8', newline='\n') as fh:
        json.dump(rec, fh, ensure_ascii=False, indent=1, default=str)
        fh.write('\n')
    print('\n== 结果：%s ==  %s\n   证据目录：%s' % (rec['result'], summary, out_dir))
    return 0 if rec['result'] == 'PASS' else 1


if __name__ == '__main__':
    sys.exit(main())
