# Build & Test Notes

## Toolchains
- JDK 17 (gradle toolchain picks)
- Kotlin 1.9.24
- Compose BOM 2024.10.00
- AGP 8.5.2

## Permissions to test manually
1. Notifications
2. Camera
3. Record Audio
4. Fine Location
5. Send SMS
6. Call Phone
7. Allow "display over other apps" if you enable overlay prompts (not default in MVP).

## adb scripts

### Show power menu hint path
adb shell am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS --es reason globalactions

### Simulate shutdown
adb shell am broadcast -a android.intent.action.ACTION_SHUTDOWN

### Reboot to test BootReceiver flush
adb reboot

## Voice Enrollment Tips
- Quiet room; hold mic 15–20 cm from mouth.
- Read displayed short numeric phrases (nonce).

## Delay Ladder
- After 3rd failed auth: 2m shadow → 5m → 10m → 15m → SMS(+call if opted-in).
- Owner authenticates at any time → abort & purge pending evidence (24h retention default).

## Logs
- App-private, encrypted JSONL at: `files/logs/events.log.enc`.
- Export from Settings -> Logs -> Export (shares decrypted temp copy).
