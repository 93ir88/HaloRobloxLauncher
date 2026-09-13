# ☠️🥀 Halo Roblox Launcher

Patches the Delta APK (`com.roblox.client`) to use Halo as its launcher UI.  
Package name stays **untouched** — Roblox engine runs underneath.

## How it works

```
GitHub Actions
  ↓ downloads Delta-2.736 APK
  ↓ apktool decode
  ↓ inject.py:
      • strips LAUNCHER intent from original activity
      • injects HaloActivity.smali + HaloBridge.smali
      • copies patch/assets/halo/index.html into assets
      • patches AndroidManifest.xml
  ↓ apktool rebuild
  ↓ zipalign + apksigner
  ↓ upload artifact / GitHub Release
```

## Repo structure

```
patch/
  smali/com/roblox/client/halo/
    HaloActivity.smali   ← WebView activity (the launcher shell)
    HaloBridge.smali     ← JS bridge: Android.launchGame(placeId)
  assets/halo/
    index.html           ← Full Halo launcher UI (HTML/CSS/JS)
  inject.py              ← Orchestrates the patch
.github/workflows/
  build.yml              ← Debug build on push
  release.yml            ← Signed release on tag push
```

## Build (debug)

Push to `main` or `dev` — Actions builds automatically.  
Download the APK from the workflow's Artifacts tab.

## Release

```bash
git tag v1.0.0
git push origin v1.0.0
```

Actions builds, signs, and publishes a GitHub Release.

## Secrets (for release signing)

| Secret | Value |
|---|---|
| `SIGNING_KEY_BASE64` | `base64 -w0 release.keystore` |
| `KEY_ALIAS` | your alias |
| `KEYSTORE_PASSWORD` | store password |
| `KEY_PASSWORD` | key password |

> ☠️ Package name `com.roblox.client` is never modified.
