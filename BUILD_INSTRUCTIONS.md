# Build Instructions - Anti Delete WhatsApp

Panduan lengkap untuk build dan install aplikasi Anti Delete WA.

## 📋 Requirements

### Software
- **Android Studio:** Hedgehog (2023.1.1) atau lebih baru
  - Download: https://developer.android.com/studio
- **JDK:** Version 17
- **Android SDK:** API Level 34 (Android 14)

### Device Requirements
- Android 8.0 (API 26) atau lebih tinggi
- Minimal 50MB storage

## 🔧 Setup Project

### 1. Persiapan Android Studio

```bash
# Clone atau extract proyek
cd /path/to/AntiDeleteWA

# Buka Android Studio
# File → Open → Pilih folder AntiDeleteWA
```

### 2. Gradle Sync

Setelah membuka proyek, Android Studio akan otomatis:
- Download dependencies
- Sync Gradle files
- Setup SDK

**Jika ada error:**
- File → Invalidate Caches → Restart
- Tools → SDK Manager → Install missing SDK components

### 3. Verifikasi Setup

Pastikan di `build.gradle.kts (Module: app)`:
```kotlin
android {
    compileSdk = 34
    
    defaultConfig {
        minSdk = 26
        targetSdk = 34
    }
}
```

## 🏗️ Build APK

### Method 1: Via Android Studio Menu

1. **Select Build Variant**
   - Build → Select Build Variant
   - Pilih: `debug`

2. **Build APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Tunggu proses build selesai (2-5 menit)

3. **Locate APK**
   - APK Location: `app/build/outputs/apk/debug/app-debug.apk`
   - Atau klik "locate" di notifikasi build success

### Method 2: Via Gradle Command Line

```bash
# Di root folder proyek
./gradlew assembleDebug

# Atau di Windows
gradlew.bat assembleDebug

# APK akan ada di:
# app/build/outputs/apk/debug/app-debug.apk
```

### Method 3: Build Release APK (Optional)

⚠️ **Untuk production, perlu signing key**

```bash
./gradlew assembleRelease
```

## 📱 Install ke Device

### Method 1: Via USB (Android Studio)

1. **Enable Developer Options di Android:**
   - Settings → About Phone → Tap "Build Number" 7x
   - Settings → Developer Options → Enable "USB Debugging"

2. **Connect Device:**
   - Sambungkan Android ke PC via USB
   - Izinkan USB debugging

3. **Run dari Android Studio:**
   - Click "Run" (▶️) atau Shift+F10
   - Pilih device Anda
   - App akan auto-install dan launch

### Method 2: Manual Install APK

1. **Transfer APK:**
   - Copy `app-debug.apk` ke device Android
   - Via USB, email, atau cloud storage

2. **Enable Install from Unknown Sources:**
   - Settings → Security → Install Unknown Apps
   - Pilih browser/file manager → Allow

3. **Install:**
   - Buka file manager
   - Tap file APK
   - Tap "Install"
   - Tap "Open" setelah selesai

### Method 3: Via ADB Command

```bash
# Install via ADB
adb install app/build/outputs/apk/debug/app-debug.apk

# Atau force reinstall
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch app setelah install
adb shell am start -n com.private.antideletewa/.ui.MainActivity
```

## ⚙️ First Run Setup

### 1. Enable Notification Access

Saat pertama kali membuka app:

1. Dialog akan muncul: "Notification Access Required"
2. Tap **"Open Settings"**
3. Scroll cari **"Anti Delete WA"**
4. Toggle **ON**
5. Tap **"Allow"** di dialog konfirmasi
6. Kembali ke app

### 2. Verify Setup

1. Buka app
2. Pastikan tidak ada peringatan
3. Coba kirim pesan WhatsApp ke diri sendiri (dari device lain)
4. Cek apakah pesan muncul di app

## 🧪 Testing

### Test Notification Capture

1. **Kirim test message:**
   - Dari device lain, kirim pesan WA ke device utama
   - Pastikan ini **PRIVATE CHAT** (bukan group)

2. **Check app:**
   - Buka Anti Delete WA
   - Pesan harus muncul dalam list
   - Format: Nama sender + isi pesan + timestamp

3. **Test delete message:**
   - Hapus pesan di WhatsApp (Delete for everyone)
   - Buka Anti Delete WA
   - Pesan tetap ada ✓

### Expected Behavior

✅ **Disimpan:**
- Private chat 1-to-1
- Text messages
- Pesan dengan notifikasi

❌ **Diabaikan:**
- Group chat messages
- Pesan dengan ":" di awal (format group)
- Pesan yang sudah dihapus sebelum notif muncul
- Media (foto/video/voice)
- Status updates

## 🔍 Troubleshooting

### Build Errors

**Error: SDK not found**
```bash
Solution:
- Tools → SDK Manager
- Install Android SDK 34
- Restart Android Studio
```

**Error: Gradle sync failed**
```bash
Solution:
- File → Invalidate Caches → Restart
- Delete .gradle folder
- ./gradlew clean
```

**Error: Kotlin version mismatch**
```bash
Solution:
- Update Kotlin plugin di Android Studio
- Sync gradle
```

### Runtime Issues

**App crashes on launch**
- Check Logcat for errors
- Verify minSdk = 26
- Clean & Rebuild project

**Notification not captured**
- Verify notification access is ON
- Check if WA notification is enabled
- Restart both apps

**No messages showing**
- Send test private chat message
- Check if notification appeared
- Verify database in Device File Explorer

## 📊 APK Info

**Debug APK:**
- Size: ~5-8 MB
- Package: `com.private.antideletewa`
- Version: 1.0
- Min Android: 8.0 (API 26)

**Release APK (if built):**
- Requires signing key
- Optimized & minified
- Size: ~3-5 MB

## 🎯 Build Variants

```kotlin
debug {
    applicationIdSuffix = ".debug"
    debuggable = true
    minifyEnabled = false
}

release {
    minifyEnabled = true
    proguardFiles = [...]
    signingConfig = signingConfigs.release
}
```

## 📝 Notes

- **Development:** Gunakan `debug` build variant
- **Distribution:** Gunakan `release` + signing
- **Testing:** Device fisik lebih baik daripada emulator
- **Permissions:** Notification access WAJIB

## 🆘 Support

Jika ada masalah:
1. Check Logcat di Android Studio
2. Verify semua dependencies terinstall
3. Clean & Rebuild project
4. Invalidate caches & restart

---

**Happy Building! 🚀**
