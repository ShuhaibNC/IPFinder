# IPF - IP Finder

IPF (IP Finder) is a lightweight Android application that displays the IPv4 address of `ap0` or `wlan0` interfaces **without requiring a WiFi connection**.

## 📱 Features

- Shows local IPv4 address of `ap0` (Hotspot) and `wlan0` (WiFi) interfaces.
- Does **not require** an active internet or WiFi connection.
- Clean, simple UI: big readable IP, interface name, no clutter.
- Copy IP address with a single tap (on the IP itself or the Copy button).
- Built-in Refresh button to update IP info.
- Simple, fast, and effective!

## 🔧 Use Case

This app is especially useful for developers, testers, or tech enthusiasts who:
- Run servers on Android (like HTTP file servers).
- Need local IP for ADB over WiFi or similar.
- Want to debug or view local network interfaces quickly.

## ⚙️ How It Works

The app queries available `NetworkInterface`s, then checks for `wlan0` or `ap0`. If an active IPv4 address is present, it displays it.

```java
if (networkInterface.getName().equalsIgnoreCase("wlan0") ||
    networkInterface.getName().equalsIgnoreCase("ap0")) {
    // Fetch and return the IPv4 address
}
```

If no IP is available, it shows `127.0.0.1` with a warning.

## 📦 AIDE Support

You can directly open this project in [AIDE](https://play.google.com/store/apps/details?id=com.aide.ui), the Android IDE. Just:
1. Install AIDE.
2. Clone this repo.
3. Open `MainActivity.java` and run the app.

## 🖥️ For Termux Users

If you're using **Termux**, you probably don’t need this app. Just run:

```bash
ifconfig
```

## 🛠️ Build Requirements

- **Android SDK** (API level 16+ recommended)
- AIDE or Android Studio
- No extra libraries or permissions required

## 🚫 Limitations

- Might not work on all devices or ROMs if the system restricts access to network interfaces.
- Only shows IP for `ap0` or `wlan0`, not other interfaces.

## 📃 License

This project is open-source. Do whatever you want with it. Attribution appreciated but not required.


