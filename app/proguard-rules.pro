# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# ============ 基本保留规则 ============

# 保留所有类中的序列化相关成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.InputStream);
    void readObjectNoData();
}

# 保留自定义 View 的构造方法
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# 保留注解
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ============ AndroidX / Jetpack 组件 ============

# Room 数据库
-keep class * extends androidx.room.RoomDatabase {
    public static <methods>;
}

-keepclassmembers class * {
    @androidx.room.* *;
}

-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.ColumnInfo *;
    @androidx.room.PrimaryKey *;
    @androidx.room.Ignore *;
}

# Lifecycle
-keep class androidx.lifecycle.** { *; }

# Navigation
-keep class androidx.navigation.** { *; }

# ============ Compose 相关 ============

# Compose 运行时
-keep class androidx.compose.runtime.** { *; }

# Compose UI
-keep class androidx.compose.ui.** { *; }

# Material3
-keep class androidx.compose.material3.** { *; }

# Compose Navigation
-keep class androidx.hilt.navigation.compose.** { *; }
-keep class androidx.navigation.compose.** { *; }

# ============ Dagger Hilt 依赖注入 ============

-keep @javax.inject.Inject class *
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}

-keep class dagger.hilt.internal.aggregatedroot.** { *; }
-keep class hilt_aggregated_deps.** { *; }

# 保持 Hilt 相关的 ViewModel 工厂类
-keep class * implements androidx.lifecycle.ViewModelProvider$Factory { *; }

# ============ 第三方库规则 ============

# Coil 图片加载
-keep class coil.** { *; }
-keep class com.google.accompanist.** { *; }

# OkHttp
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

-keepattributes Signature

# Jsoup
-keep class org.jsoup.** { *; }

# ThreeTenABP (JSR-310 backport)
-keep class org.threeten.bp.** { *; }

# UCrop 图片裁剪
-keep class com.yalantis.ucrop.** { *; }

# TensorFlow Lite
-keep class org.tensorflow.lite.** { *; }
-keep class org.tensorflow.lite.support.** { *; }

# Material Kolor
-keep class com.materialkolor.** { *; }

# Yitter ID Generator
-keep class com.github.yitter.** { *; }

# ============ 数据类 / 模型类保留 ============

# 保留所有数据类（根据您的 Room Entity 等）
-keep class com.atri.seduley.model.** { *; }
-keep class com.atri.seduley.entity.** { *; }
-keep class com.atri.seduley.data.** { *; }

# 保留所有 DTO / 响应类
-keep class com.atri.seduley.dto.** { *; }
-keep class com.atri.seduley.response.** { *; }

# ============ 保留应用特定类 ============

# 保留所有 Activity
-keep class com.atri.seduley.ui.**Activity { *; }

# 保留所有 Fragment
-keep class com.atri.seduley.ui.**Fragment { *; }

# 保留所有 ViewModel
-keep class com.atri.seduley.ui.**ViewModel { *; }

# 保留所有 Composable 函数（如果需要）
-keep class com.atri.seduley.ui.**Composable { *; }

# 保留所有 Repository
-keep class com.atri.seduley.repository.** { *; }

# 保留所有 Service / BroadcastReceiver
-keep class com.atri.seduley.service.** { *; }
-keep class com.atri.seduley.receiver.** { *; }

# ============ 反射相关保留 ============

# 保留通过反射调用的方法
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# 保留序列化/反序列化相关的类
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ============ 资源绑定保留 ============

# 保留 DataBinding / ViewBinding 生成的类
-keep class * extends androidx.viewbinding.ViewBinding { *; }
-keep class **databinding.*Binding { *; }

# ============ 网络相关 ============

# 保留 Retrofit 接口（如果您将来添加）
-keepattributes RuntimeVisibleAnnotations
-keepclassmembers,allowobfuscation class * {
    @retrofit2.http.* <methods>;
}

# ============ 调试相关 ============

# 在调试版本中保留行号信息
-keepattributes LineNumberTable,SourceFile

# Release 版本去除 d, i, w 级别日志
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int i(...);
    public static int w(...);
}

# ============ 性能优化 ============

# 优化指令
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 3
-allowaccessmodification
-mergeinterfacesaggressively

# ============ 排除规则 ============

# 不要警告缺少库
-dontwarn android.**
-dontwarn androidx.**
-dontwarn com.google.**
-dontwarn org.jetbrains.**
-dontwarn kotlin.**
-dontwarn org.intellij.**

# ============ 特定库的排除警告 ============

# Hilt
-dontwarn dagger.hilt.**
-dontwarn hilt_aggregated_deps.**

# TensorFlow Lite
-dontwarn org.tensorflow.**

# UCrop
-dontwarn com.yalantis.ucrop.**

# Material Kolor
-dontwarn com.materialkolor.**