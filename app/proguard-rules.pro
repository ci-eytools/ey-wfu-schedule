# ---------- Kotlin 支持 ----------
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Kotlin 协程
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler { *; }
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory { *; }

# ---------- AndroidX / Jetpack ----------
# 基本组件
-keep class androidx.lifecycle.** { *; }
-keep class androidx.compose.** { *; }
-keep class androidx.navigation.** { *; }
-keep class androidx.activity.** { *; }
-keep class androidx.fragment.** { *; }

# Room 数据库
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keepclassmembers class * {
    @androidx.room.* *;
}

# DataStore
-keep class androidx.datastore.** { *; }

# ---------- Compose 相关 ----------
# 保留 Compose 预览和测试相关类
-keep class androidx.compose.ui.test.** { *; }
-keep class androidx.compose.ui.tooling.** { *; }

# ---------- Dagger Hilt ----------
# Hilt 注解处理器生成的类
-keep class * extends dagger.hilt.android.internal.managers.ApplicationComponentManager { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponentManager { *; }
-keep class dagger.hilt.internal.aggregatedroot.** { *; }
-keep class hilt_aggregated_deps.** { *; }

# Hilt ViewModel
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel { *; }

# ---------- Retrofit / OkHttp ----------
# 保留接口（用于反射）
-keepattributes Signature
-keepattributes *Annotation*

# OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class okio.** { *; }
-dontwarn okio.**

# ---------- TensorFlow Lite ----------
-keep class org.tensorflow.** { *; }
-dontwarn org.tensorflow.**

# ---------- 序列化 ----------
# Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes *Annotation*

# ---------- UI 库 ----------
# Coil
-keep class coil.** { *; }
-dontwarn coil.**

# UCrop
-keep class com.yalantis.ucrop.** { *; }

# Material Design
-keep class com.google.android.material.** { *; }

# ---------- 自定义保留规则 ----------
# 保留应用入口点
-keep class com.atri.seduley.SeduleyApp { *; }
-keep class com.atri.seduley.MainActivity { *; }
-keep class com.atri.seduley.SplashActivity { *; }

# 保留所有 ViewModel
-keep class * extends androidx.lifecycle.ViewModel { *; }

# 保留所有 Service 和 Receiver
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver

# 保留 Parcelable 实现类
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留枚举类
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留资源类中的 R$* 内部类
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 保留带有 @Keep 注解的类和成员
-keep @androidx.annotation.Keep class * { *; }

# ================================================
# 通用优化配置
# ================================================

# 代码优化级别
-optimizationpasses 5
-allowaccessmodification
-mergeinterfacesaggressively
-dontpreverify
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

# 移除日志代码（可选，如果希望移除所有日志调用）
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# 保持泛型信息
-keepattributes Signature,InnerClasses,EnclosingMethod

# 保留行号信息（便于崩溃分析）
-keepattributes SourceFile,LineNumberTable

# ================================================
# 针对依赖库的警告排除
# ================================================

-dontwarn com.google.android.gms.**
-dontwarn com.google.firebase.**
-dontwarn org.jetbrains.annotations.**
-dontwarn sun.misc.**