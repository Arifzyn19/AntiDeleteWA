# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Room entities
-keep class com.private.antideletewa.data.model.** { *; }
-keep class com.private.antideletewa.data.dao.** { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep NotificationListenerService
-keep class com.private.antideletewa.service.** { *; }
