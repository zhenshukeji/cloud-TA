# -*- coding: utf-8 -*-
"""云朵教务社区版 · 为演示账号设置口令（OS013 配套脚本）。

背景：`database/003_demo_data.sql` 只写数据、不写任何口令 —— 演示账号的
`sys_user.password` 保持空串，与 `002_seed_system.sql` 的管理员口径一致。
需要以演示账号登录时，用本脚本从环境变量读入口令并写入 BCrypt 哈希。

为什么不让 SQL 直接写口令：仓库里出现任何「默认口令」哈希，都会被当成
默认凭据（OS011 已明确「无真实默认密码、未设置即中止」）。因此口令只在
运行时从环境变量注入，不落盘、不进源码、不进日志。

用法（**必须用带 bcrypt 的解释器**，见 PITFALLS §25.10）：
    set -a && source scripts/env.local && set +a
    export DEMO_PASSWORD='<你自己的演示口令>'
    python scripts/set-demo-passwords.py            # 全部演示账号同一口令
    python scripts/set-demo-passwords.py --dry-run  # 只检查强度，不写库

强度规则（与 CommunityStartupGuard / CommunityInitializer 同口径）：
  长度 ≥ 10；至少含小写 / 大写 / 数字 / 符号中的 2 类；不等于 admin；
  不在弱口令清单内。
"""
import argparse
import os
import re
import sys

try:
    import bcrypt
except ImportError:
    sys.exit('缺少 bcrypt：请用托管解释器运行本脚本（见 PITFALLS §25.10）')

sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..',
                                'outputs', 'open-source-review', '_tools'))
sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '_tools'))
try:
    import pymysql
except ImportError:
    pymysql = None

WEAK = {'admin', 'administrator', 'password', '123456', '12345678', '123456789',
        'qwerty', 'abc123', 'admin123', 'root', 'test', 'demo', 'change-me',
        'community', 'yunduo'}


def strength_error(password):
    """返回不达标原因；达标返回 None。"""
    if password is None or password == '':
        return '未提供 DEMO_PASSWORD'
    if len(password) < 10:
        return '长度 %d < 10' % len(password)
    classes = 0
    for pat in (r'[a-z]', r'[A-Z]', r'\d', r'[^A-Za-z0-9]'):
        if re.search(pat, password):
            classes += 1
    if classes < 2:
        return '字符类别只有 %d 类（要求 ≥ 2）' % classes
    if password.lower() in WEAK:
        return '命中弱口令清单'
    return None


def connect():
    return pymysql.connect(host=os.environ['MYSQL_HOST'], port=int(os.environ['MYSQL_PORT']),
                           user=os.environ['MYSQL_USER'], password=os.environ['MYSQL_PASSWORD'],
                           database=os.environ['MYSQL_DATABASE'], charset='utf8mb4',
                           autocommit=True, connect_timeout=8)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--dry-run', action='store_true', help='只做强度检查，不写库')
    args = ap.parse_args()

    for key in ('MYSQL_HOST', 'MYSQL_PORT', 'MYSQL_DATABASE', 'MYSQL_USER', 'MYSQL_PASSWORD'):
        if not os.environ.get(key):
            sys.exit('缺少环境变量 %s：请先 source scripts/env.local' % key)

    dbname = os.environ['MYSQL_DATABASE']
    if not dbname.startswith('yunduo_community'):
        sys.exit('拒绝执行：MYSQL_DATABASE=%s 不是社区版库（要求前缀 yunduo_community）' % dbname)

    password = os.environ.get('DEMO_PASSWORD')
    err = strength_error(password)
    if err:
        sys.exit('拒绝执行：演示口令不达标（%s）' % err)

    if pymysql is None:
        sys.exit('缺少 pymysql：请用托管解释器运行本脚本')

    conn = connect()
    try:
        with conn.cursor() as cur:
            cur.execute('SELECT user_id, user_name, nick_name FROM sys_user '
                        'WHERE user_id BETWEEN 1 AND 5 ORDER BY user_id')
            users = list(cur.fetchall())
            if not users:
                sys.exit('库里没有演示账号：请先执行 002_seed_system.sql 与 003_demo_data.sql')
            if args.dry_run:
                print('--dry-run：口令强度通过；将影响 %d 个账号：%s'
                      % (len(users), [u[1] for u in users]))
                return 0
            hashed = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
            for user_id, _, _ in users:
                cur.execute('UPDATE sys_user SET password = %s WHERE user_id = %s', (hashed, user_id))
        print('已为 %d 个演示账号设置口令（口令不落盘、不回显）：%s'
              % (len(users), [u[1] for u in users]))
        print('提示：正式部署前请更换口令，或按 003_demo_data.sql 的说明重建演示数据。')
    finally:
        conn.close()
    return 0


if __name__ == '__main__':
    sys.exit(main())
