# RemindMe

A local-first reminder app built with **Kotlin** and **Jetpack Compose** (Material 3, brutalist design).

Everything stays on your device — **no accounts, no cloud**.

## Features

### Time-based reminders
- One-off reminders (errands, to-dos) with quick presets: **5 / 15 / 30 MIN, 1 HR** or custom date & time.
- **Medical & health** — a single entry can hold **multiple medications**, each with name, dosage, and instructions.
- **Monthly bills** — optional amount tracking (RM).
- **Recurrence on any reminder** — once, daily, weekly, monthly, yearly, or every N days.

### Share & import
- Share any reminder as a link → the recipient imports it into RemindMe instantly.

### Vault (zero-alert reference data)
- A quiet, searchable home for everyday details you need on hand: **People**, **Home & Vehicle**, **Property**.
- No notifications — ever.

### Extra
- Dark + light theme (follows system), brutalist UI.
- Auto-delete completed reminders (optional).
- Optional "also add to todo list" for reminders.

## Tech

| Layer | Choice |
| --- | --- |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| DB | Room (SQLite) |
| Background | WorkManager + AlarmManager (exact alarms) |
| Preferences | DataStore |
| Min / target SDK | 26 / 34 |

## Build

Requires Android SDK + JDK 17.

```bash
# clean debug build
./gradlew assembleDebug

# signed release build
# (reads gitignored keystore.properties; falls back to debug signing if absent)
./gradlew assembleRelease
```

Install the APK from `app/build/outputs/apk/release/app-release.apk`.

## Testing

```bash
./gradlew testDebugUnitTest          # unit tests
./gradlew connectedDebugAndroidTest  # instrumented tests (device/emulator attached)
```

## Release history

Latest release: [v1.0](https://github.com/LuqmanKml1961/RemindMe/releases/tag/v1.0) — signed family-test build.

## License

All rights reserved.