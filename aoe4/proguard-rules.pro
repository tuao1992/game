# AoE4 Advisor — ProGuard/R8 rules.
# The civ dataset is plain Kotlin objects (no reflection/serialization), so no
# keep rules are required for it. Compose ships its own consumer rules.
-dontwarn org.jetbrains.annotations.**
