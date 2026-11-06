-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-dontwarn com.android.billingclient.**
-keep class com.android.billingclient.** { *; }

# Prevent Proguard from stripping entities, models, preferences and language helpers
-keep class com.evduzeni.planner.data.entity.** { *; }
-keep class com.evduzeni.planner.data.model.** { *; }
-keep class com.evduzeni.planner.data.preferences.** { *; }
-keep class com.evduzeni.planner.util.** { *; }
