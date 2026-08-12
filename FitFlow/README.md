# FitFlow Android

Native Kotlin and Jetpack Compose application for Android 10+.

1. Open `FitFlow` in Android Studio (Ladybug or later).
2. Set the Android SDK location if prompted, sync Gradle, and run on Android 10+.
3. The official supplied Blue Running-Man artwork is included as `app/src/main/res/drawable/fitflow_logo.png` and the launcher resource `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png`.
4. For an APK, choose `Build > Build APK(s)`; for Play Store choose `Build > Generate Signed Bundle / APK > Android App Bundle`.

Before publishing, replace the test AdMob IDs, host the privacy policy and update its URL, add official store art to `play-store-assets/`, and increment version information in `app/build.gradle.kts`.

## GitHub automatic APK downloads

Upload the contents of this `FitFlow` folder to a GitHub repository, not the surrounding ZIP as a single file. The included `.github/workflows/android.yml` workflow builds a debug APK on every push to `main` or `master`.

1. On GitHub, open `Actions` and choose `Build FitFlow Android`.
2. Open the successful `Build Debug APK` run.
3. Download `FitFlow-debug-apk` from the **Artifacts** area at the bottom of the run page.

Use `Run workflow` in Actions to produce an unsigned `FitFlow-release-aab-unsigned` artifact. Sign the AAB in Android Studio with your own upload key before uploading it to Google Play. Never commit an upload keystore, its password, or production AdMob IDs into GitHub.
