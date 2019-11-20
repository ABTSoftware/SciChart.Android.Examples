# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Program Files\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# ignore warnings and save classes required for syntax highlighting
-dontwarn java.awt.**
-dontwarn javax.swing.**
-dontwarn syntaxhighlight.**

-keep public class java.awt.** { *; }
-keep public class javax.swing.** { *; }
-keep public class syntaxhighlight.** { *; }
-keep public class prettify.** { *; }

# keep class to prevent exception
-keep public class android.support.v7.widget.SearchView { *; }

# need to save example fragments and default ctor because we're using reflection when open examples
-keepclassmembers class * extends com.scichart.examples.fragments.base.ExampleBaseFragment {
    public <init>(...);
}

# need to keep these classes and their methods because they are used by resampling code
-keep public class com.scichart.core.model.DoubleValues { *; }
-keep public class com.scichart.core.model.FloatValues { *; }
-keep public class com.scichart.core.model.IntegerValues { *; }
-keep public class com.scichart.data.model.Point2DSeries { *; }

# repack obfuscated classes into single package so it would be hard to find their originall package
-repackageclasses ''
-allowaccessmodification