# Technical Architecture - Anti Delete WhatsApp

Dokumentasi arsitektur dan implementasi teknis aplikasi.

## 🏗️ Architecture Overview

Aplikasi ini menggunakan **MVVM (Model-View-ViewModel)** architecture dengan **Repository Pattern**.

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌──────────────┐         ┌─────────────────────────┐  │
│  │  MainActivity │ ◄─────► │   MainViewModel         │  │
│  │  (View)       │         │   (ViewModel)           │  │
│  └──────────────┘         └─────────────────────────┘  │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────┐
│                    Domain Layer                          │
│              ┌─────────────────────────┐                │
│              │  MessageRepository      │                │
│              │  (Business Logic)       │                │
│              └─────────────────────────┘                │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────┐
│                     Data Layer                           │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────┐  │
│  │ MessageDao   │◄───│ AppDatabase  │───►│  Room DB │  │
│  │ (Interface)  │    │ (SQLite)     │    │          │  │
│  └──────────────┘    └──────────────┘    └──────────┘  │
└─────────────────────────────────────────────────────────┘
                         ▲
                         │
┌────────────────────────┴────────────────────────────────┐
│                   Service Layer                          │
│         ┌──────────────────────────────────────┐        │
│         │ WhatsAppNotificationListener         │        │
│         │ (NotificationListenerService)        │        │
│         └──────────────────────────────────────┘        │
└─────────────────────────────────────────────────────────┘
```

## 📦 Core Components

### 1. Service Layer

#### WhatsAppNotificationListener
**Purpose:** Menangkap notifikasi WhatsApp secara real-time

**Key Methods:**
```kotlin
onNotificationPosted(sbn: StatusBarNotification)
  ├─► processWhatsAppNotification()
  │    ├─► Filter: isGroupConversation
  │    ├─► Filter: contains ":"
  │    ├─► Filter: contains "deleted"
  │    └─► saveMessage()
  └─► Insert to Database
```

**Filters Applied:**
1. ✅ Package name == "com.whatsapp"
2. ✅ NOT group conversation
3. ✅ Text NOT contains ":"
4. ✅ Text NOT contains "deleted/dihapus"
5. ✅ NOT system notification

### 2. Data Layer

#### MessageEntity
```kotlin
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: Long,
    val sender: String,        // Nama kontak
    val message: String,       // Isi pesan
    val timestamp: Long,       // Unix timestamp
    val packageName: String    // Always "com.whatsapp"
)
```

#### MessageDao
**Database Operations:**
- `insertMessage()` - Insert pesan baru
- `getAllMessages()` - Get semua pesan (DESC by timestamp)
- `getMessagesBySender()` - Filter by sender
- `searchMessages()` - Full-text search
- `deleteMessagesBySender()` - Hapus by sender
- `deleteAllMessages()` - Clear all

#### AppDatabase
- **Database Name:** `antidelete_database`
- **Version:** 1
- **Migration:** Destructive (untuk development)

### 3. Domain Layer

#### MessageRepository
**Purpose:** Abstraction layer antara ViewModel dan Data source

**Responsibilities:**
- Provide LiveData streams
- Execute suspend functions
- Handle business logic (jika ada)

### 4. Presentation Layer

#### MainViewModel
**Purpose:** UI state management

**LiveData Exposed:**
```kotlin
val allMessages: LiveData<List<MessageEntity>>
```

**Operations:**
- `insertMessage()`
- `deleteMessagesBySender()`
- `deleteAllMessages()`
- `searchMessages()`

#### MainActivity
**Responsibilities:**
- Setup RecyclerView
- Observe ViewModel LiveData
- Handle user interactions
- Check notification permission
- Show empty state

## 🔄 Data Flow

### Message Capture Flow

```
1. WhatsApp Notification
   ↓
2. NotificationListenerService.onNotificationPosted()
   ↓
3. Extract: EXTRA_TITLE, EXTRA_TEXT
   ↓
4. Apply Filters (Group detection, Delete detection)
   ↓
5. Create MessageEntity
   ↓
6. Database.insertMessage() via Coroutine
   ↓
7. LiveData automatically updates
   ↓
8. RecyclerView refreshes UI
```

### UI Update Flow

```
User Opens App
   ↓
MainActivity.onCreate()
   ↓
setupRecyclerView()
   ↓
viewModel.allMessages.observe()
   ↓
adapter.submitList(messages)
   ↓
RecyclerView displays data
```

## 🧩 Key Design Decisions

### 1. Why NotificationListenerService?
**Pros:**
- No root required
- Official Android API
- Reliable notification access
- Works in background

**Cons:**
- Requires user permission
- Only captures notifications (not silent messages)
- Cannot access past messages

**Alternative Considered:**
- ❌ AccessibilityService - Too intrusive
- ❌ Database access - Requires root
- ❌ WhatsApp API - Not available for this use case

### 2. Why Room Database?
**Pros:**
- Type-safe queries
- Compile-time verification
- LiveData integration
- Migration support

**Alternative Considered:**
- ❌ SharedPreferences - Not suitable for large data
- ❌ Raw SQLite - More boilerplate code
- ❌ Firebase - Overkill for local-only data

### 3. Why MVVM?
**Pros:**
- Separation of concerns
- Testable business logic
- Lifecycle-aware
- Android recommended

### 4. Group Chat Detection Logic

**Method 1: isGroupConversation Flag**
```kotlin
val isGroup = extras.getBoolean("android.isGroupConversation", false)
```

**Method 2: Text Pattern Analysis**
```kotlin
// Group format: "Nama: pesan"
if (text.contains(":")) {
    // Likely group message
}
```

**Why Both?**
- Double validation for accuracy
- Some WhatsApp versions may vary
- Ensures maximum reliability

## 🔐 Security Considerations

### Data Privacy
1. **Local Storage Only**
   - No network transmission
   - No cloud backup
   - All data stays on device

2. **No External Access**
   - Database not exported
   - No content provider
   - Private to app only

3. **Minimal Permissions**
   - Only BIND_NOTIFICATION_LISTENER_SERVICE
   - No storage, camera, contacts access

### Thread Safety
- **Database:** Room guarantees thread-safety
- **Service:** Uses Coroutines with SupervisorJob
- **ViewModel:** ViewModelScope for automatic cleanup

## 🎨 UI/UX Architecture

### View Hierarchy
```
MainActivity
  ├─► Toolbar (MaterialToolbar)
  ├─► RecyclerView (LinearLayoutManager)
  │    └─► MessageAdapter
  │         └─► MessageViewHolder
  │              ├─► senderText (Bold White)
  │              ├─► messageText (Gray)
  │              └─► timeText (Small Gray)
  └─► EmptyStateLayout
       ├─► Icon (Message Bubble)
       ├─► Title ("No Messages Yet")
       └─► Description
```

### RecyclerView Optimization
- **DiffUtil:** Efficient updates
- **ViewHolder Pattern:** View recycling
- **setHasFixedSize(true):** Performance optimization
- **ListAdapter:** Built-in async handling

## 📊 Performance Considerations

### Database Optimization
```kotlin
// Use indices for frequent queries
@Entity(
    indices = [Index(value = ["sender", "timestamp"])]
)
```

### Memory Management
- ViewModel survives configuration changes
- LiveData prevents memory leaks
- Coroutines automatically cancelled

### Background Processing
```kotlin
// Service uses IO dispatcher
serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
```

## 🧪 Testing Strategy

### Unit Tests (Recommended)
- **ViewModel:** Test business logic
- **Repository:** Mock DAO operations
- **Filters:** Test group detection logic

### Integration Tests
- **Database:** Room provides in-memory DB for tests
- **Service:** Test notification processing

### UI Tests
- **Espresso:** Test RecyclerView interactions
- **Test empty state visibility**

## 🔧 Configuration

### Build Variants
```kotlin
debug {
    applicationIdSuffix = ".debug"
    versionNameSuffix = "-DEBUG"
}

release {
    minifyEnabled = true
    shrinkResources = true
}
```

### ProGuard Rules
```proguard
# Keep Room entities
-keep class com.private.antideletewa.data.model.** { *; }

# Keep NotificationListenerService
-keep class com.private.antideletewa.service.** { *; }
```

## 📈 Future Enhancements

### Potential Features
1. **Export Data** - Export messages to CSV/JSON
2. **Backup/Restore** - Local backup functionality
3. **Search Enhancement** - Full-text search with highlights
4. **Statistics** - Message count per contact
5. **Dark/Light Theme** - Theme toggle (currently dark-only)
6. **Media Support** - Save image thumbnails (if possible)
7. **Encryption** - Encrypt database with password

### Technical Improvements
1. **Pagination** - Paging 3 library for large datasets
2. **Work Manager** - Scheduled cleanup of old messages
3. **Navigation Component** - Detail screen per contact
4. **Hilt/Dagger** - Dependency injection
5. **Jetpack Compose** - Modern UI (future migration)

## 🐛 Known Limitations

1. **Notification Dependency**
   - Only captures messages that trigger notifications
   - Silent messages not captured

2. **Text Only**
   - No support for media (images, videos, voice notes)
   - Notification only contains text preview

3. **No Historical Data**
   - Cannot access messages sent before app installation
   - Not a WhatsApp backup solution

4. **Group Chat Exclusion**
   - By design, group messages not saved
   - Cannot differentiate group vs broadcast

## 📚 Dependencies

```kotlin
// Core
androidx.core:core-ktx:1.12.0
androidx.appcompat:appcompat:1.6.1

// UI
com.google.android.material:material:1.11.0
androidx.constraintlayout:constraintlayout:2.1.4
androidx.recyclerview:recyclerview:1.3.2

// Architecture
androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0
androidx.lifecycle:lifecycle-livedata-ktx:2.7.0

// Database
androidx.room:room-runtime:2.6.1
androidx.room:room-ktx:2.6.1
androidx.room:room-compiler:2.6.1 (kapt)

// Async
kotlinx-coroutines-android:1.7.3
```

---

**Architecture Design:** Clean, Maintainable, Scalable
**Focus:** Security, Privacy, Performance
