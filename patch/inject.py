#!/usr/bin/env python3
"""
inject.py — patches the Delta APK with Halo launcher UI
Usage: python3 inject.py <decoded_dir> <patch_dir>
"""
import sys, os, shutil, re
import xml.etree.ElementTree as ET

ANDROID_NS = 'http://schemas.android.com/apk/res/android'
ET.register_namespace('android', ANDROID_NS)
ET.register_namespace('',        '')

def a(name):
    return f'{{{ANDROID_NS}}}{name}'

def log(msg):
    print(f'[inject] {msg}')

def patch(decoded_dir, patch_dir):
    manifest_path = os.path.join(decoded_dir, 'AndroidManifest.xml')

    # ── 1. Patch AndroidManifest.xml ──────────────────────────────────────
    log('Patching AndroidManifest.xml …')
    tree = ET.parse(manifest_path)
    root = tree.getroot()

    app_el = root.find('application')
    if app_el is None:
        sys.exit('[inject] ERROR: <application> not found in manifest')

    # Remove LAUNCHER category from every existing activity
    for activity in app_el.findall('activity'):
        for ifilter in activity.findall('intent-filter'):
            for cat in ifilter.findall('category'):
                if cat.get(a('name')) == 'android.intent.category.LAUNCHER':
                    log(f'  Removed LAUNCHER from {activity.get(a("name"), "?")}')
                    activity.remove(ifilter)
                    break  # one filter removed per activity is enough

    # Inject HaloActivity as the new LAUNCHER
    halo = ET.SubElement(app_el, 'activity')
    halo.set(a('name'),       'com.roblox.client.halo.HaloActivity')
    halo.set(a('exported'),   'true')
    halo.set(a('theme'),      '@android:style/Theme.Black.NoTitleBar.Fullscreen')
    halo.set(a('launchMode'), 'singleTop')
    halo.set(a('screenOrientation'), 'portrait')

    ifilter = ET.SubElement(halo, 'intent-filter')
    action  = ET.SubElement(ifilter, 'action')
    action.set(a('name'), 'android.intent.action.MAIN')
    cat = ET.SubElement(ifilter, 'category')
    cat.set(a('name'), 'android.intent.category.LAUNCHER')

    tree.write(manifest_path, xml_declaration=True, encoding='UTF-8')
    log('  AndroidManifest.xml written')

    # ── 2. Copy smali files ───────────────────────────────────────────────
    log('Copying smali files …')
    smali_src = os.path.join(patch_dir, 'smali')
    # Pick the primary smali dir (smali/ if exists, else smali_classes2/, etc.)
    smali_dirs = sorted([
        d for d in os.listdir(decoded_dir)
        if d.startswith('smali') and os.path.isdir(os.path.join(decoded_dir, d))
    ])
    if not smali_dirs:
        sys.exit('[inject] ERROR: no smali dir found in decoded APK')
    target_smali = os.path.join(decoded_dir, smali_dirs[0])
    log(f'  Target smali dir: {smali_dirs[0]}')

    for root_dir, dirs, files in os.walk(smali_src):
        for f in files:
            src_file = os.path.join(root_dir, f)
            rel      = os.path.relpath(src_file, smali_src)
            dst_file = os.path.join(target_smali, rel)
            os.makedirs(os.path.dirname(dst_file), exist_ok=True)
            shutil.copy2(src_file, dst_file)
            log(f'  + {rel}')

    # ── 3. Copy HTML assets ───────────────────────────────────────────────
    log('Copying HTML assets …')
    assets_src = os.path.join(patch_dir, 'assets')
    assets_dst = os.path.join(decoded_dir, 'assets')
    os.makedirs(assets_dst, exist_ok=True)

    for root_dir, dirs, files in os.walk(assets_src):
        for f in files:
            src_file = os.path.join(root_dir, f)
            rel      = os.path.relpath(src_file, assets_src)
            dst_file = os.path.join(assets_dst, rel)
            os.makedirs(os.path.dirname(dst_file), exist_ok=True)
            shutil.copy2(src_file, dst_file)
            log(f'  + assets/{rel}')

    log('Injection complete ✓')

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print(f'Usage: python3 {sys.argv[0]} <decoded_dir> <patch_dir>')
        sys.exit(1)
    patch(sys.argv[1], sys.argv[2])
