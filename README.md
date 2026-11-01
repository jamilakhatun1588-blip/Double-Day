# Double Day Android app

A tiny native Android app for the 60-real-day / 120-virtual-day exam challenge.

## Schedule used
- Challenge starts: 1 November 2026, 5:00 AM (phone local time)
- Virtual Day 1: 5:00 AM–1:30 PM
- Virtual Day 2: 1:30 PM–10:00 PM
- 10:00 PM–5:00 AM: overnight rest window
- Challenge horizon: 60 real calendar days (ends 31 December 2026 at 5:00 AM)
- No internet permission, account, ads, or backend required.

## Build an installable APK (Windows)
1. Install Android Studio from https://developer.android.com/studio
2. Extract this ZIP.
3. In Android Studio choose **Open** and select the extracted `DoubleDayAndroid` folder.
4. Allow Gradle sync to finish. If prompted, install the suggested Android SDK.
5. Choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. When the build finishes, click **locate**. The APK is normally at `app/build/outputs/apk/debug/app-debug.apk`.
7. Transfer the APK to your Android phone, open it, and allow installation from that source if Android asks.

## Notes
- This is source code, not a precompiled APK. A build requires Android Studio and the Android SDK.
- The app reads your phone's local date/time and refreshes every 30 seconds while open.
- Study-window mode: virtual days run 5:00 AM–1:30 PM and 1:30 PM–10:00 PM; the 10 PM–5 AM rest window does not advance progress.
- The app follows your phone's local date/time and refreshes every 30 seconds while open; it does not alter your phone's real date.
