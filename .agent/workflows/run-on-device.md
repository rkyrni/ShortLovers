---
description: Android Device Run + Debug (ADB/Gradle)
---

# Run & Debug an Android App (Kotlin + Jetpack Compose) on Devices

## 0) Prerequisites & Device Selection
1. Confirm the device is detected:
   ```bash
   adb devices -l
````

2. If the status is `unauthorized` / `offline`:

   ```bash
   adb kill-server
   adb start-server
   adb devices -l
   ```
3. If multiple devices are connected, select one:

   * Option A (per-command):

     ```bash
     adb -s <serial> <command>
     ```
   * Option B (set default for the terminal session):

     ```bash
     export ANDROID_SERIAL=<serial>
     ```

---

## 1) Build the Debug APK

1. Build with full error details:

   ```bash
   ./gradlew :app:assembleDebug --stacktrace
   ```

---

## 2) Install / Reinstall to the Device

1. Install via Gradle:

   ```bash
   ./gradlew :app:installDebug --stacktrace
   ```
2. If install fails due to version/signature issues, do a clean reinstall:

   ```bash
   adb uninstall your.package.name
   ./gradlew :app:installDebug
   ```

---

## 3) Launch the App

1. Fastest way (one monkey event):

   ```bash
   adb shell monkey -p your.package.name -c android.intent.category.LAUNCHER 1
   ```
2. Alternative: start a specific Activity:

   ```bash
   adb shell am start -n your.package.name/.MainActivity
   ```

---

## 4) Monitor Logs for Runtime Debugging (Crash/Exceptions)

1. Clear logs (optional):

   ```bash
   adb logcat -c
   ```
2. Stream logs only for the app process:

   ```bash
   adb logcat --pid=$(adb shell pidof -s your.package.name)
   ```
3. Save logs to a file:

   ```bash
   adb logcat -d > logcat.txt
   ```

---

## 5) Crash Debug Loop (Main Loop)

1. Launch the app (Section 3).
2. Reproduce the crash.
3. Extract the stacktrace from logcat:

   * Find `FATAL EXCEPTION`
   * Use the deepest `Caused by:` as the root cause
4. Fix the code.
5. Repeat:

   ```bash
   ./gradlew :app:installDebug
   adb shell monkey -p your.package.name -c android.intent.category.LAUNCHER 1
   adb logcat --pid=$(adb shell pidof -s your.package.name)
   ```

---

## 6) Debug ANR / Freeze (UI Hangs)

1. Check for ANRs:

   ```bash
   adb shell dumpsys activity anr
   ```
2. Capture a full report:

   ```bash
   adb bugreport bugreport.zip
   ```

---

## 7) Reset App State (For Consistent Repro)

1. Force-stop the app:

   ```bash
   adb shell am force-stop your.package.name
   ```
2. Clear app data (reset state):

   ```bash
   adb shell pm clear your.package.name
   ```

---

## 8) (Optional) Terminal Debugger Attach (JDB)

1. Set the app to wait for a debugger:

   ```bash
   adb shell am set-debug-app -w your.package.name
   ```
2. Launch the app:

   ```bash
   adb shell monkey -p your.package.name -c android.intent.category.LAUNCHER 1
   ```
3. List JDWP processes:

   ```bash
   adb jdwp
   ```
4. Forward a port and attach:

   ```bash
   adb forward tcp:8700 jdwp:<jdwp_id>
   jdb -attach localhost:8700
   ```

---

## 9) (Optional) Performance/Jank Trace (Perfetto)

1. Record a short trace:

   ```bash
   adb shell perfetto -o /data/misc/perfetto-traces/trace.perfetto-trace -t 10s sched freq idle am wm gfx view binder_driver
   ```
2. Pull the trace to your machine:

   ```bash
   adb pull /data/misc/perfetto-traces/trace.perfetto-trace
   ```

```
