# Practical 5 verification

Verified on 21 October 2026 (Asia/Shanghai). Device: Pixel 6 emulator, Android 15 / API 35. App: com.example.practicalfive, version 1.0, Java/XML.

## Results

| Check | Result |
|---|---|
| Debug APK build (`assembleDebug`) | Passed |
| JVM tests (`testDebugUnitTest`) | 4 tests, 0 failures, 0 errors |
| Emulator tests (`connectedDebugAndroidTest`) | 6 tests, 0 failures, 0 errors |
| Android Lint (`lintDebug`) | Passed; no blocking errors |
| Main page and city screenshots | Captured from the running app |
| Four official website URLs | Each returned HTTP 200 over HTTPS from the development machine |

## Automated coverage

1. Supported city names, uppercase/lowercase/mixed case, surrounding spaces, null, empty, whitespace-only, and unsupported input. Locale independence is checked with a Turkish device-locale simulation in the JVM test.
2. Keyboard submission and navigation through the actual MainActivity. The test waits for the destination activity to resume before checking its views.
3. Correct titles, supplied images (render comparison), summary-array entries, 80–120 word counts, title backgrounds, information text colours, and button tints for all four cities.
4. Detail activity recreation retains the selected city's content and keeps the website button visible.
5. Missing, blank, and unsupported Intent extras close the detail activity safely.
6. Each website button emits an implicit ACTION_VIEW intent with the correct HTTPS URL. An instrumentation monitor intercepts these intents for deterministic URL assertions.
7. A simulated ActivityNotFoundException exercises the missing-browser catch path; the detail screen remains usable.

## Website checks and limits

| City | URL | HTTPS check |
|---|---|---|
| Beijing | https://english.beijing.gov.cn/ | 200 |
| Shanghai | https://english.shanghai.gov.cn/ | 200 |
| Chongqing | https://english.cq.gov.cn/ | 200 |
| Tianjin | https://en.tj.gov.cn/ | 200 |

A real tap on Beijing's Visit site launched the emulator's Chrome application. Chrome was at its first-run Terms of Service screen, so no browser onboarding was accepted and full website rendering inside that browser was not claimed. Exact URLs were independently verified by device intent tests and live HTTPS requests. On a device whose browser is already configured, the implicit intent opens the corresponding website normally.

## Resolved verification issues

- Windows non-ASCII checkout paths caused the test worker to fail to find compiled classes. Generated output is routed to an ASCII Gradle cache subdirectory for those checkouts; JVM tests then passed through the standard Gradle task.
- An initial UI assertion raced an activity transition. It now waits for the destination lifecycle RESUMED event.
- Local SDK property escaping, input autofill/accessibility attributes, icon variants, and legacy/current backup declarations were addressed during Lint review.
- A transient Google Maven TLS download failure succeeded on retry without weakening certificate checks.

The screenshot device was tested in portrait. Activity recreation is automated; no claim is made for exhaustive device, OS, or landscape testing.
