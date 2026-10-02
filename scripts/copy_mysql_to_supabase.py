"""
Copy every row from the local MySQL database into Supabase (PostgreSQL).

Run AFTER the backend has started once with the `supabase` profile, so Flyway has created the tables.
Reads the Supabase connection string from ../.env.supabase (one line: postgresql://user:password@host:port/db).
Safe to re-run: it empties the Supabase tables first, then copies everything again with the same IDs.

    pip install pymysql "psycopg[binary]"
    python scripts/copy_mysql_to_supabase.py
"""
import os
import sys
from urllib.parse import urlparse, unquote

import psycopg
import pymysql

HERE = os.path.dirname(os.path.abspath(__file__))
ENV_FILE = os.path.join(HERE, '..', '.env.supabase')

MYSQL = dict(host='localhost', port=3306, user='root', password='root', database='buddhist_matrimony')


def supabase_dsn():
    raw = open(ENV_FILE, encoding='utf-8').read().strip().splitlines()[0].strip()
    if raw.startswith('jdbc:'):
        raw = raw[len('jdbc:'):]
    u = urlparse(raw)
    return dict(host=u.hostname, port=u.port or 5432, user=unquote(u.username), password=unquote(u.password),
                dbname=(u.path or '/postgres').lstrip('/') or 'postgres', sslmode='require')


def tables_in_fk_order(pg):
    """Parents before children, so foreign keys are satisfied while inserting."""
    tables = [r[0] for r in pg.execute(
        "select table_name from information_schema.tables where table_schema='public' and table_type='BASE TABLE'"
        " and table_name <> 'flyway_schema_history'")]
    deps = {t: set() for t in tables}
    for child, parent in pg.execute(
            "select tc.table_name, ccu.table_name from information_schema.table_constraints tc"
            " join information_schema.constraint_column_usage ccu on ccu.constraint_name = tc.constraint_name"
            " where tc.constraint_type = 'FOREIGN KEY' and tc.table_schema = 'public'"):
        if child != parent and child in deps:
            deps[child].add(parent)
    ordered, done = [], set()
    while len(ordered) < len(tables):
        ready = [t for t in tables if t not in done and deps[t] <= done]
        if not ready:
            raise RuntimeError(f'Circular foreign keys between: {set(tables) - done}')
        for t in sorted(ready):
            ordered.append(t)
            done.add(t)
    return ordered


def main():
    if not os.path.exists(ENV_FILE):
        sys.exit(f'Missing {ENV_FILE} - put the Supabase Session pooler connection string in it.')

    my = pymysql.connect(**MYSQL, charset='utf8mb4')
    with my.cursor() as c:
        # The app stores timestamps in UTC; read them back unchanged.
        c.execute("SET time_zone = '+00:00'")

    with psycopg.connect(**supabase_dsn(), autocommit=False) as pg:
        tables = tables_in_fk_order(pg)
        print(f'{len(tables)} tables to copy')

        # Empty Supabase first (removes Flyway's default seed rows) so the copy is exact and re-runnable.
        pg.execute('TRUNCATE ' + ', '.join(f'"{t}"' for t in tables) + ' RESTART IDENTITY CASCADE')

        report = []
        for table in tables:
            pg_cols = {name: dtype for name, dtype in pg.execute(
                "select column_name, data_type from information_schema.columns where table_schema='public' and table_name=%s",
                (table,))}
            with my.cursor() as c:
                c.execute(f'SHOW COLUMNS FROM `{table}`')
                my_cols = [r[0] for r in c.fetchall()]
            cols = [col for col in my_cols if col in pg_cols]
            missing = sorted(set(pg_cols) - set(cols))

            order = ' ORDER BY id' if 'id' in cols else ''
            with my.cursor() as c:
                c.execute(f'SELECT {", ".join(f"`{x}`" for x in cols)} FROM `{table}`{order}')
                rows = c.fetchall()

            bool_idx = [i for i, col in enumerate(cols) if pg_cols[col] == 'boolean']
            converted = []
            for row in rows:
                row = list(row)
                for i in bool_idx:
                    if row[i] is not None:
                        row[i] = bool(row[i])
                converted.append(row)

            if converted:
                placeholders = ', '.join(['%s'] * len(cols))
                col_list = ', '.join(f'"{x}"' for x in cols)
                with pg.cursor() as cur:
                    cur.executemany(f'INSERT INTO "{table}" ({col_list}) OVERRIDING SYSTEM VALUE VALUES ({placeholders})', converted)

            if 'id' in cols:
                # Next new row continues after the highest copied ID.
                pg.execute(f"""SELECT setval(pg_get_serial_sequence('"{table}"', 'id'),
                               COALESCE((SELECT MAX(id) FROM "{table}"), 0) + 1, false)""")

            pg_count = pg.execute(f'SELECT COUNT(*) FROM "{table}"').fetchone()[0]
            report.append((table, len(rows), pg_count, missing))

        mismatches = [r for r in report if r[1] != r[2]]
        if mismatches:
            pg.rollback()
            sys.exit(f'Row counts do not match, nothing was saved: {mismatches}')
        pg.commit()

    print(f'{"table":45} {"mysql":>6} {"supabase":>8}')
    for table, my_n, pg_n, missing in report:
        note = f'  (columns only in Supabase: {missing})' if missing else ''
        print(f'{table:45} {my_n:>6} {pg_n:>8}{note}')
    print(f'\nDone: {sum(r[1] for r in report)} rows copied, all counts match.')


if __name__ == '__main__':
    main()
