# Add project specific ProGuard rules here.

# Preserve line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Firebase
-keep class com.google.firebase.** { *; }

# Kotlin Serialization
-keepattributes *Annotation*,InnerClasses,Signature
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepnames class kotlinx.serialization.serializer.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializer <fields>;
}

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Dao
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}
# Keep DAO implementation and entity classes that Room uses
-keep class * implements androidx.room.RoomDatabase
-keep class * implements androidx.room.Dao
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}

# Vico Charts
-keep class com.patrykandpatrick.vico.** { *; }
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
