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

1. Open the project in Android Studio.
2. Let Android Studio sync Gradle dependencies.
3. Run on an emulator or physical Android device.

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
