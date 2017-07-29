-dontwarn java.nio.**
-dontwarn org.codehaus.**
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

-keepattributes *Annotation*,EnclosingMethod,LineNumberTable

# Couchbase Lite
-keep public class com.couchbase.lite.store.** { *; }

# Jackson
-keep @com.fasterxml.jackson.annotation.JsonIgnoreProperties class * { *; }
-keep class com.fasterxml.** { *; }
-keep class org.codehaus.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepclassmembers public final enum com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility {
    public static final com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility *;
}

# Saripaar
-keep class com.mobsandgeeks.saripaar.** {*;}

# Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
}