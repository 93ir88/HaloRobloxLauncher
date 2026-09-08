# ☠️🥀 Halo Roblox Launcher

Material You Android launcher for Roblox — animated, clean, dark.

## Features
- Material You dynamic color (Android 12+)
- Animated splash screen
- Download & install Roblox directly from Delta
- Live download progress bar
- Installed version detection
- GitHub Actions CI/CD — auto-release on tag push

## Build

```bash
./gradlew assembleDebug
```

## Release

```bash
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions handles the rest — builds, signs, uploads APK as a GitHub Release.

## Secrets required (Settings → Secrets → Actions)

| Secret | Value |
|---|---|
| `SIGNING_KEY_BASE64` | `base64 -w 0 halo.keystore` |
| `KEY_ALIAS` | your alias |
| `KEYSTORE_PASSWORD` | your password |
| `KEY_PASSWORD` | your key password |

## Generate keystore

```bash
keytool -genkey -v -keystore halo.keystore \
  -alias halo -keyalg RSA -keysize 2048 -validity 10000

base64 -w 0 halo.keystore
```

---

Powered by Delta ☠️
