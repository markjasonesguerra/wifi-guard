# 🛡️ WiFi Guard

[![Release](https://img.shields.io/github/v/release/markjasonesguerra/wifi-guard)](https://github.com/markjasonesguerra/wifi-guard/releases)
[![Stars](https://img.shields.io/github/stars/markjasonesguerra/wifi-guard?style=social)](https://github.com/markjasonesguerra/wifi-guard/stargazers)
[![License](https://img.shields.io/github/license/markjasonesguerra/wifi-guard)](https://github.com/markjasonesguerra/wifi-guard/blob/main/LICENSE)
[![Issues](https://img.shields.io/github/issues/markjasonesguerra/wifi-guard)](https://github.com/markjasonesguerra/wifi-guard/issues)
[![Build Status](https://img.shields.io/github/actions/workflow/status/markjasonesguerra/wifi-guard/ci.yml?branch=main)](https://github.com/markjasonesguerra/wifi-guard/actions)

WiFi Guard is a lightweight Android utility that monitors Wi‑Fi and real internet connectivity. It detects "connected but no internet" situations, logs state changes, and notifies you when the connection drops.

---

## ✨ Key Features

- Active internet monitoring (detects ISP/DNS/captive-portal issues)
- Background service with low battery impact
- Instant notifications on connectivity loss
- Smart logging of state changes only
- Uptime / downtime tracking per session

---

## 🚀 Quick Start

### Clone

```bash
git clone https://github.com/markjasonesguerra/wifi-guard.git
cd wifi-guard
```

### Open in Android Studio

- Open the project folder in Android Studio and let Gradle sync.
- Run on a device or emulator (Debug configuration).

### Build from command line

```bash
./gradlew assembleDebug
```

---

## 🛠️ Development

- Project uses Gradle (Kotlin DSL). App module is in `app/`.
- Run unit tests with `./gradlew test` and instrumentation tests with `./gradlew connectedAndroidTest`.

---

## 📸 Screenshots

_(Add screenshots here: a screenshot/ folder and image links make the README more approachable.)_

---

## 🤝 Contributing

- Found a bug or have a feature idea? Please open an issue.
- Pull requests welcome — follow standard Gradle/Android style and include a brief description of changes.

---

## 📦 License

This project is licensed under the terms in the `LICENSE` file.

---

## ⚠️ Notes for maintainers

- Badges above are wired to `markjasonesguerra/wifi-guard` (used the repository remote URL found in the repo). If you want different badge targets (e.g., a GitHub organization or different branch), tell me and I'll adjust them.

---

If you'd like, I can also add a feature table, badges for downloads, or a nicer hero image — what would you like included next?
