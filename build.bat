@echo off
echo Building Cafe POS APK...
gradlew assembleDebug
if %errorlevel% equ 0 (
    echo Build successful! APK location: app\build\outputs\apk\debug\app-debug.apk
) else (
    echo Build failed!
)
pause