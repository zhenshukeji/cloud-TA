# -*- coding: utf-8 -*-
"""社区版数据库逻辑备份脚本（OS024，PRD C14）。

不依赖 mysqldump：纯 pymysql 实现「SHOW CREATE TABLE + 全量 INSERT」的逻辑备份，
输出单份可直接在空库上回放的 SQL 文件。适用于没有 mysqldump 客户端的环境；
有 mysqldump 的环境仍推荐 OPERATIONS.md 的 mysqldump 命令。

凭据从环境变量读取（与 scripts/env.local.example 同一口径），禁止写入命令行或文件：
  MYSQL_HOST / MYSQL_PORT / MYSQL_USER / MYSQL_PASSWORD / MYSQL_DATABASE

用法：
  python scripts/backup-db.py --out backup-20260928.sql
  python scripts/backup-db.py --out backup.sql --database yunduo_community
"""
import argparse
import datetime
import os
import sys

import pymysql


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--out', required=True, help='输出 SQL 文件路径')
    ap.add_argument('--database', default=os.environ.get('MYSQL_DATABASE', ''),
                    help='要备份的库（默认取 MYSQL_DATABASE）')
    args = ap.parse_args()

    db = args.database
    if not db or not db.startswith('yunduo_community'):
        print('FAIL: 拒绝备份 —— 库名必须存在且以 yunduo_community 开头（防止误备其他库）')
        sys.exit(2)

    conn = pymysql.connect(
        host=os.environ.get('MYSQL_HOST', '127.0.0.1'),
        port=int(os.environ.get('MYSQL_PORT', 3306)),
        user=os.environ.get('MYSQL_USER', 'root'),
        password=os.environ.get('MYSQL_PASSWORD', ''),
        database=db, charset='utf8mb4',
    )

    tables = []
    with conn.cursor() as cur:
        cur.execute("SELECT table_name FROM information_schema.tables "
                    "WHERE table_schema = %s AND table_type = 'BASE TABLE' ORDER BY table_name", (db,))
        tables = [r[0] for r in cur.fetchall()]

    written = 0
    with open(args.out, 'w', encoding='utf-8', newline='\n') as f:
        f.write('-- 云朵教务社区版逻辑备份\n')
        f.write('-- 库: %s    时间: %s\n' % (db, datetime.datetime.now().astimezone().isoformat(timespec='seconds')))
        f.write('-- 表: %d 张（含结构 + 数据；口令列是 BCrypt 哈希，属备份应有内容）\n' % len(tables))
        f.write('SET NAMES utf8mb4;\nSET FOREIGN_KEY_CHECKS = 0;\n')
        for t in tables:
            with conn.cursor() as cur:
                cur.execute('SHOW CREATE TABLE `%s`' % t)
                create_sql = cur.fetchone()[1]
                f.write('\nDROP TABLE IF EXISTS `%s`;\n%s;\n' % (t, create_sql))
                cur.execute('SELECT * FROM `%s`' % t)
                cols = [d[0] for d in cur.description]
                rows = cur.fetchall()
                # 排除生成列（如 bd_class_record.active_slot / bd_course_time.active_account）：
                # MySQL 8 拒绝向 STORED GENERATED 列显式写值（错误 3105），回放会整表失败
                with conn.cursor() as gcur:
                    gcur.execute("SELECT column_name FROM information_schema.columns "
                                 "WHERE table_schema = %s AND table_name = %s AND extra LIKE '%%GENERATED%%'", (db, t))
                    generated = {r[0] for r in gcur.fetchall()}
                keep = [i for i, c in enumerate(cols) if c not in generated]
                cols = [cols[i] for i in keep]
                rows = [[row[i] for i in keep] for row in rows]
                col_list = ', '.join('`%s`' % c for c in cols)
                for i in range(0, len(rows), 100):
                    chunk = rows[i:i + 100]
                    values = ',\n'.join(
                        '(' + ', '.join(conn.escape(v) if v is not None else 'NULL' for v in row) + ')'
                        for row in chunk)
                    f.write('INSERT INTO `%s` (%s) VALUES\n%s;\n' % (t, col_list, values))
                    written += len(chunk)
        f.write('\nSET FOREIGN_KEY_CHECKS = 1;\n')
    conn.close()
    size = os.path.getsize(args.out)
    print('OK: 备份 %s —— %d 张表 / %d 行数据 / %d 字节 -> %s' % (db, len(tables), written, size, args.out))


if __name__ == '__main__':
    main()
