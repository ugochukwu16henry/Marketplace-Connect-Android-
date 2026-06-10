# Marketplace Connect - Android

Marketplace Connect is a native Android classified ads app built with Java.

## Features

- Add product listings with title, description, price, and category
- Browse listings in a RecyclerView
- View full listing details
- Search by title or description
- Filter by category
- Sort by newest, price low to high, or price high to low
- Local persistence with Room (SQLite)
- Rotation-safe UI state for Home filters and Add Listing form

## Tech Stack

- Java
- Android SDK
- Room (SQLite)
- RecyclerView
- Material Components
- MVVM-style screen logic with ViewModel + LiveData

## Project Structure

- `app/src/main/java/com/marketplace/connect/ui` - activities and view models
- `app/src/main/java/com/marketplace/connect/data` - repository
- `app/src/main/java/com/marketplace/connect/db` - Room database and DAO
- `app/src/main/java/com/marketplace/connect/model` - entities
- `app/src/main/res/layout` - screen and list item layouts
- `app/src/main/res/values` - strings and themes

## How to Run

### Local (Android Studio on your PC)

1. Open the project in Android Studio.
2. Let Android Studio sync Gradle dependencies.
3. Run on an emulator or physical Android device.

### Firebase Studio (browser / cloud)

[![Open in Firebase Studio](https://img.shields.io/badge/Open%20in-Firebase%20Studio-orange?logo=firebase)](https://studio.firebase.google.com/import?url=https://github.com/ugochukwu16henry/Marketplace-Connect-Android-)

**Recommended for this project — Android Studio Cloud**

1. Open [Android Studio Cloud workspace](https://studio.firebase.google.com/new/android-studio) and sign in with Google.
2. Wait for the cloud Android Studio IDE to load (SDK is pre-installed).
3. In Android Studio: **Get from VCS** → **GitHub** → clone `ugochukwu16henry/Marketplace-Connect-Android-`.
4. Open the cloned project folder and let Gradle sync finish.
5. Start an emulator from **Device Manager**, then click **Run**.

**Alternative — import repo into Firebase Studio**

1. Open the [import link](https://studio.firebase.google.com/import?url=https://github.com/ugochukwu16henry/Marketplace-Connect-Android-) and sign in.
2. Name the workspace and click **Import** (authenticate GitHub if the repo is private).
3. Gradle sync runs automatically via `.idx/dev.nix` on first open.
4. Use the built-in Android emulator preview or run `./gradlew installDebug` in the terminal.

> Enable third-party cookies in your browser if Firebase Studio does not load.

## Core App Flow

1. Launch app to Home screen.
2. Add listing using the floating action button.
3. Return to Home to see new listing.
4. Use search, category filter, and sort controls.
5. Tap a listing to open Details screen.

## Data Persistence

Listings are stored locally using Room. Data remains after app restarts.

## Known Limitations (v1)

- No authentication/login
- No cloud sync/backend API
- No image upload pipeline
- No in-app chat or payments

## Demo Checklist

- Add at least two listings
- Confirm list updates on Home
- Search for a listing keyword
- Filter by category
- Change sort option
- Open details for a listing
- Close and reopen app to confirm persistence
