# Reglas ProGuard para release (no se usa en debug)
# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.music.vinylcollector.data.remote.** { *; }
