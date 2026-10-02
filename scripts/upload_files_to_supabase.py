"""
One-time move of the local uploads/ folder into Supabase Storage, and update of the file URLs in the Supabase database.

- uploads/profiles/**  -> public bucket  (profile + success-story photos, served from Supabase CDN)
- uploads/documents/** -> private bucket (Aadhaar etc., served through the backend with signed links)

Reads SUPABASE_URL / SUPABASE_SERVICE_KEY from ../supabase-local.properties and the DB connection from ../.env.supabase.
Safe to re-run: uploads overwrite, and only URLs that still point to /files/profiles/ are rewritten.

    python scripts/upload_files_to_supabase.py
"""
import mimetypes
import os
import sys

import psycopg
import requests

from copy_mysql_to_supabase import supabase_dsn

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.join(HERE, '..')
UPLOADS = os.path.join(ROOT, 'uploads')
PUBLIC_BUCKET = 'public-files'
PRIVATE_BUCKET = 'private-documents'
# Columns holding photo URLs that should point straight at the public bucket
PHOTO_COLUMNS = [('profile_photos', 'photo_url'), ('profile_photos', 'thumbnail_url'), ('success_stories', 'photo_url')]


def settings():
    values = {}
    for line in open(os.path.join(ROOT, 'supabase-local.properties'), encoding='utf-8'):
        if '=' in line and not line.lstrip().startswith('#'):
            k, v = line.split('=', 1)
            values[k.strip()] = v.strip()
    url, key = values.get('SUPABASE_URL', '').rstrip('/'), values.get('SUPABASE_SERVICE_KEY', '')
    if not url or not key:
        sys.exit('Add SUPABASE_URL and SUPABASE_SERVICE_KEY to supabase-local.properties first.')
    return url, key


def main():
    url, key = settings()
    s = requests.Session()
    s.headers.update({'apikey': key, 'Authorization': f'Bearer {key}'})

    for bucket, public in [(PUBLIC_BUCKET, True), (PRIVATE_BUCKET, False)]:
        r = s.post(f'{url}/storage/v1/bucket', json={'id': bucket, 'name': bucket, 'public': public})
        if r.status_code not in (200, 201) and 'already exists' not in r.text.lower() and r.status_code != 409:
            sys.exit(f'Could not create bucket {bucket}: {r.status_code} {r.text}')
        print(f'bucket {bucket}: ready (public={public})')

    uploaded = set()
    for folder, bucket in [('profiles', PUBLIC_BUCKET), ('documents', PRIVATE_BUCKET)]:
        base = os.path.join(UPLOADS, folder)
        for dirpath, _, files in os.walk(base):
            for name in files:
                path = os.path.join(dirpath, name)
                storage_key = os.path.relpath(path, UPLOADS).replace('\\', '/')
                ctype = mimetypes.guess_type(name)[0] or 'application/octet-stream'
                with open(path, 'rb') as f:
                    r = s.post(f'{url}/storage/v1/object/{bucket}/{storage_key}', data=f,
                               headers={'Content-Type': ctype, 'x-upsert': 'true'})
                if r.status_code not in (200, 201):
                    sys.exit(f'Upload failed for {storage_key}: {r.status_code} {r.text}')
                uploaded.add(storage_key)
        print(f'{folder}: {sum(1 for k in uploaded if k.startswith(folder + "/"))} files uploaded to {bucket}')

    public_prefix = f'{url}/storage/v1/object/public/{PUBLIC_BUCKET}/'
    missing = []
    with psycopg.connect(**supabase_dsn()) as db:
        for table, column in PHOTO_COLUMNS:
            rows = db.execute(f"SELECT id, {column} FROM {table} WHERE {column} LIKE '/files/profiles/%%'").fetchall()
            changed = 0
            for row_id, value in rows:
                storage_key = value[len('/files/'):]
                if storage_key in uploaded:
                    db.execute(f'UPDATE {table} SET {column} = %s WHERE id = %s', (public_prefix + storage_key, row_id))
                    changed += 1
                else:
                    missing.append(f'{table}.{column} id={row_id}: {value}')
            print(f'{table}.{column}: {changed} URLs now point to Supabase Storage')
        db.commit()

    if missing:
        print('\nReferenced in the database but not found in uploads/ (left unchanged):')
        for m in missing:
            print('  ' + m)
    print('\nDone.')


if __name__ == '__main__':
    main()
