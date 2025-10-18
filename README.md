# 🔒 Android Anti-Theft — Anti-Shutdown (MVP)

**Offline-first, non-root Android security layer that reacts to shutdown or theft attempts.**  
Built with **Kotlin + Jetpack Compose**, entirely **on-device**, using only **free APIs**.  
Detects power-menu/shutdown triggers, authenticates the owner, captures front-camera + GPS evidence, encrypts it locally, and escalates via SMS or optional call (e.g., **IR 110**).

---

## 🚀 Features

- 🧠 **Offline authentication chain:** Voice → Biometric → PIN  
- 🎙️ **Voice enrollment** with local MFCC embedding & cosine matching  
- 📸 **Evidence capture:** Front camera (CameraX) + GPS snapshot  
- 🔐 **AES-GCM encryption** via Android Keystore  
- ⏱️ **Delay ladder FSM:** 2m → 5m → 10m → 15m escalation  
- 📱 **Fixed encrypted contact list** (1–3 numbers)  
- ✉️ **SMS + optional emergency call** escalation (e.g. 110 for Iran)  
- 🔁 **Boot receiver:** resumes foreground service after reboot  
- 🧾 **Encrypted local logging**  
- 🧩 **Fully local build:** No paid or cloud dependencies  

---

## 🧱 Tech Stack

| Layer | Tools / Libraries |
|-------|--------------------|
| Language | Kotlin 1.9.x |
| UI | Jetpack Compose + Material 3 |
| Core | AndroidX Lifecycle, WorkManager |
| Hardware | CameraX, Biometric, Location Services |
| Crypto | AES-GCM + Android Keystore |
| Storage | Encrypted SharedPrefs + Files |
| Build | Gradle (AGP 8.5), CI via GitHub Actions |
| Target | `minSdk=26` → `targetSdk=34` |

---

## ⚙️ Quick Start

### 1️⃣ Clone or Download
```bash
git clone https://github.com/yourusername/antitheft-mvp.git
cd antitheft-mvp
````

### 2️⃣ Install Android SDK & Build

Requires **JDK 17+** and **Android SDK 34**.

```bash
./gradlew :app:assembleDebug
```

APK output:

```
app/build/outputs/apk/debug/app-debug.apk
```

### 3️⃣ Install on Device

Connect a real Android phone via USB:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Launch **AntiTheft** from the launcher.

---

## 📲 First Run

1. **Grant permissions**: Camera, Mic, Location, SMS, Phone, Notifications
2. **Add fixed contacts** → encrypted local storage
3. **Set emergency number** (defaults to **110**)
4. **Enroll voice (5 phrases)** + PIN + optional biometric
5. **Start protection** → persistent foreground service

---

## 🔁 Test Triggers

| Event                   | ADB Command                                                                                      |
| ----------------------- | ------------------------------------------------------------------------------------------------ |
| Simulate Power Menu     | `adb shell am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS --es reason globalactions` |
| Simulate Shutdown       | `adb shell am broadcast -a android.intent.action.ACTION_SHUTDOWN`                                |
| Reboot for BootReceiver | `adb reboot`                                                                                     |

---

## ⏳ Delay & Escalation Flow

```
[3rd Failed Auth] 
   ↓ (Shadow Mode) 2 min
   ↓ 5 min
   ↓ 10 min
   ↓ 15 min
   └─> SMS to contacts
        └─> Optional call (e.g., 110)
```

---

## 🧪 Testing

```bash
./gradlew test
```

Instrumented test (optional, device required):

```bash
./gradlew connectedDebugAndroidTest
```

---

## 📂 Logs & Evidence

| Path                        | Description                      |
| --------------------------- | -------------------------------- |
| `files/evidence/`           | Encrypted JPEGs (`.enc`)         |
| `files/logs/events.log.enc` | Encrypted JSON log lines         |
| `settings.xml`              | Fixed contacts + flags (AES-GCM) |

All artifacts are stored locally — **no cloud upload** in MVP.

---

## ⚠️ Limitations

* Android does **not allow apps to block real shutdown**.
  → Interception is **best-effort**, using `ACTION_CLOSE_SYSTEM_DIALOGS` and `ACTION_SHUTDOWN`.
* OEMs may throttle background tasks; service runs **foreground** for reliability.
* SMS & CALL require **active SIM** and **user consent**.
* Auto-call emergency services only after explicit opt-in.
* Voice recognition is basic (cosine over MFCC). Not biometric-grade.

---

## 🧑‍💻 Development Notes

* Source organized under `app/src/main/java/com/ragr/antitheft/`
* Each subsystem modularized:

  * `auth/` → voice, PIN, biometric
  * `crypto/` → AES-GCM store
  * `capture/` → camera, GPS
  * `comms/` → SMS & call
  * `fsm/` → delay state machine
  * `service/` → intercept + notification

---

## 🧩 Roadmap

* [ ] Stealth background mode (Play-safe variant)
* [ ] Real-time owner challenge overlay
* [ ] Cloud relay (self-hosted FastAPI optional)
* [ ] Remote wipe / secure unlock commands
* [ ] Full Play Store release compliance pass

---

## 📜 License

**Apache-2.0** — free for personal and research use.
Please comply with regional laws when testing auto-dial / SMS features.

---

## 🧠 Credits

Designed & built by **ragr** — Senior Mobile Engineer & Product Architect.
Focused on privacy, local computation, and resilient offline systems.

> *"Security should defend the owner, not betray their data."*
