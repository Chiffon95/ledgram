# WorkManager's Room database is instantiated reflectively by its generated name.
# Keep only the no-argument constructor that Room needs at startup.
-keep class androidx.work.impl.WorkDatabase_Impl {
    <init>();
}

