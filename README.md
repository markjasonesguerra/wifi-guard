# 🛡️ WiFi Guard

**WiFi Guard** is a lightweight Android background utility app that actively monitors your Wi-Fi and real internet connectivity.

Unlike standard Android indicators that only show if you are connected to a router, WiFi Guard verifies whether the internet is actually reachable (detecting “Connected, but no internet” situations). It logs connection changes, and alerts you instantly when your internet drops.

---

## ✨ Features

- **Active Internet Monitoring:** Detects real internet availability (ISP drops, DNS issues, captive portals), not just Wi-Fi connection.
- **Background Service:** Continues monitoring even when the app is closed or the screen is off.
- **Instant Alerts:** Sends a notification and vibration when internet access is lost.
- **Smart Logging:** Logs only when the connection state changes (Connected ↔ Disconnected), avoiding spam logs.
- **Uptime / Downtime Stats:** Tracks how long your connection stays online vs offline during a session.
- **Lightweight & Battery-Friendly:** Uses periodic checks with minimal overhead.

---

## 🚀 How to Install & Test

### Option 1: Install via Android Studio (Developers)

1. Clone the repository:
   ```bash
   git clone [https://github.com/markjasonesguerra/wifi-guard.git](https://github.com/markjasonesguerra/wifi-guard.git)
