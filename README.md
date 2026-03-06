# Cafe POS System

A complete Android Point of Sale system for cafes and restaurants.

## Features
- Food ordering with quantity selection
- Invoice generation with preview
- Bluetooth thermal printer support
- Food menu management (add/edit/delete items)
- Sales history and daily reports
- Local database storage

## Setup
1. Open project in Android Studio
2. Sync Gradle files
3. Build and run on Android device/emulator

## Bluetooth Printing
- Pair thermal printer via Bluetooth
- Printer name should contain "printer" or "pos"
- Supports standard ESC/POS commands

## Database
- Uses Room database for local storage
- Automatic sample data on first run
- Stores food items and sales history

## Requirements
- Android 5.0+ (API 21)
- Bluetooth capability for printing