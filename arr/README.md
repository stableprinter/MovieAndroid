# Flutter module (AAR path)

Place your Flutter module here so the Android app can render it via the Flutter engine.

## Setup

1. From the project root, create a Flutter module with org `com.movie`:
   ```bash
   flutter create -t module --org com.movie arr
   ```

2. Build the AAR:
   ```bash
   cd arr && flutter build aar
   ```

3. Build and run the Android app. The Flutter UI will render in the fragment above the bottom navigator.

If you used a different org or module name, update the dependency in `app/build.gradle.kts` to match (e.g. `com.example:your_module:flutter_release:1.0`).
