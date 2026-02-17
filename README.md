# Anti Delete WhatsApp - Private Edition

Aplikasi Android untuk menyimpan pesan WhatsApp dari notifikasi. Jika pesan dihapus di WhatsApp (Delete for everyone), pesan tetap tersimpan di aplikasi ini.

## 🎯 Fitur Utama

- ✅ Auto-save pesan WhatsApp dari notifikasi
- ✅ **HANYA Private Chat (1-to-1)** - Group chat diabaikan
- ✅ Data tetap ada walau pesan dihapus di WhatsApp
- ✅ Dark theme modern dengan UI minimalis
- ✅ Tanpa root, tanpa mod WhatsApp
- ✅ Aman secara sistem Android

## 🛠️ Teknologi

- **Bahasa:** Kotlin
- **Minimum SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)
- **Arsitektur:** MVVM (Model-View-ViewModel)
- **Database:** Room Persistence Library
- **UI:** XML dengan Material Design
- **Service:** NotificationListenerService

## 📱 Cara Kerja

1. **Menangkap Notifikasi:** Aplikasi menggunakan `NotificationListenerService` untuk menangkap notifikasi WhatsApp
2. **Filter Group Chat:** Sistem otomatis mendeteksi dan mengabaikan group chat
3. **Simpan ke Database:** Pesan private chat disimpan ke Room Database
4. **Tampilkan di UI:** Pesan ditampilkan dalam RecyclerView dengan desain modern

## 🔐 Logika Filter

Aplikasi hanya menyimpan pesan jika memenuhi kriteria:

1. ✅ Dari package `com.whatsapp`
2. ✅ Bukan group conversation (`android.isGroupConversation == false`)
3. ✅ Text tidak mengandung ":" (indikasi group message format)
4. ✅ Tidak mengandung kata "deleted" atau "dihapus"
5. ✅ Bukan notifikasi sistem WhatsApp

## 🎨 Design Specifications

### Warna
- **Background:** Hitam (#000000) & Dark charcoal (#121212)
- **Primary:** Hijau solid (#1DB954)
- **Text Primary:** Putih (#FFFFFF)
- **Text Secondary:** Abu-abu terang (#B3B3B3)

### Style
- Dark mode only (NO GRADIENT)
- Flat modern UI
- Rounded corners (8-12dp)
- Minimalis & clean spacing

## 📂 Struktur Proyek

```
AntiDeleteWA/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/private/antideletewa/
│   │       │   ├── data/
│   │       │   │   ├── model/
│   │       │   │   │   └── MessageEntity.kt
│   │       │   │   ├── dao/
│   │       │   │   │   └── MessageDao.kt
│   │       │   │   ├── database/
│   │       │   │   │   └── AppDatabase.kt
│   │       │   │   └── repository/
│   │       │   │       └── MessageRepository.kt
│   │       │   ├── service/
│   │       │   │   └── WhatsAppNotificationListener.kt
│   │       │   └── ui/
│   │       │       ├── MainActivity.kt
│   │       │       ├── adapter/
│   │       │       │   └── MessageAdapter.kt
│   │       │       └── viewmodel/
│   │       │           └── MainViewModel.kt
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   ├── activity_main.xml
│   │       │   │   ├── item_message.xml
│   │       │   │   └── empty_state.xml
│   │       │   ├── drawable/
│   │       │   ├── values/
│   │       │   │   ├── colors.xml
│   │       │   │   ├── themes.xml
│   │       │   │   └── strings.xml
│   │       │   └── menu/
│   │       │       └── main_menu.xml
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 🚀 Setup & Build

### Prerequisites
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17
- Android SDK 34

### Langkah Build

1. **Clone/Download Proyek**
   ```bash
   cd AntiDeleteWA
   ```

2. **Open di Android Studio**
   - File → Open → Pilih folder `AntiDeleteWA`
   - Tunggu Gradle sync selesai

3. **Build APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - APK akan tersimpan di: `app/build/outputs/apk/debug/app-debug.apk`

4. **Install APK**
   - Transfer APK ke device Android
   - Install APK (pastikan "Install from Unknown Sources" aktif)

## ⚙️ Cara Menggunakan

1. **Install Aplikasi**
   - Install APK ke device Android Anda

2. **Aktifkan Notification Access**
   - Buka aplikasi
   - Akan muncul dialog untuk mengaktifkan notification access
   - Tap "Open Settings"
   - Cari dan aktifkan "Anti Delete WA"

3. **Mulai Menggunakan**
   - Setelah notification access aktif, aplikasi akan otomatis menyimpan pesan WhatsApp
   - Buka aplikasi untuk melihat pesan yang tersimpan
   - Pesan akan tetap ada walau dihapus di WhatsApp

## 📋 Fitur Menu

- **Clear All:** Hapus semua pesan tersimpan
- **Notification Access:** Buka pengaturan notification access

## ⚠️ Batasan & Catatan Penting

1. **Hanya Private Chat:** Group chat tidak disimpan
2. **Berbasis Notifikasi:** Hanya pesan yang memunculkan notifikasi yang akan tersimpan
3. **Tidak Ada Media:** Hanya text message, tidak menyimpan foto/video/voice
4. **Permission Required:** Memerlukan Notification Access permission
5. **Legal Use:** Gunakan hanya untuk keperluan pribadi dan legal

## 🔒 Keamanan & Privacy

- ✅ Tidak mengakses database WhatsApp
- ✅ Tidak memodifikasi aplikasi WhatsApp
- ✅ Hanya membaca notifikasi (fitur resmi Android)
- ✅ Data tersimpan lokal di device
- ✅ Tidak ada koneksi internet/server

## 🐛 Troubleshooting

### Pesan Tidak Tersimpan?
1. Pastikan Notification Access sudah aktif
2. Restart aplikasi dan coba lagi
3. Cek apakah pesan adalah private chat (bukan group)

### Notification Access Tidak Muncul?
1. Buka Settings → Apps → Anti Delete WA
2. Tap "Notifications"
3. Aktifkan notification access secara manual

## 📝 License

Proyek ini untuk keperluan **PRIVATE** dan **EDUCATIONAL** saja.
Tidak untuk dipublikasikan di Play Store.

## 👨‍💻 Developer Notes

Aplikasi ini dibuat dengan:
- Clean Architecture (MVVM)
- Repository Pattern
- Kotlin Coroutines
- LiveData & ViewModel
- Room Database
- Material Design 3

---

**⚠️ DISCLAIMER:**
Aplikasi ini dibuat untuk keperluan pribadi. Pengguna bertanggung jawab penuh atas penggunaan aplikasi ini. Developer tidak bertanggung jawab atas penyalahgunaan aplikasi.
