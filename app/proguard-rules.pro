# Weldrite CPVC Master — R8/ProGuard rules.
# The whole game is driven from code (no XML-inflated custom views, no
# reflection), so default optimizations are safe. We only keep the entry point
# and our public component constructors to be defensive.

-keep class com.weldrite.cpvcmaster.MainActivity { *; }

# Keep enum values()/valueOf used by save serialization.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Strip Android logging in release for a little extra size/perf.
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
