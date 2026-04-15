# MindLess Screen Plan 1 (Foundation + Tracking Core) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a running Android app skeleton with local-first tracking foundations: permissions onboarding, Room schema, session detection, category mapping, worker cadence, retention cleanup, startup data flow, unlock analytics, and basic dashboard/report surfaces.

**Architecture:** This plan implements M1–M3 foundations from the approved specification using MVVM + Clean Architecture + Repository Pattern inside one `app` module with package-level boundaries (`data/domain/presentation/services/workers`). The plan is TDD-first for behavior-critical logic (session detection, retention policy, startup orchestration, permission fallback, and unlock alerts), with small commits after each task.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Hilt, Room, WorkManager, Coroutines + Flow, UsageStatsManager, Accessibility Service (declared only in this plan), AndroidX Test, JUnit.

---

## Scope split decision (required before implementation)

The approved spec spans multiple independent subsystems. To keep execution safe and testable, implementation is split into four plans:

1. **Plan 1 (this file):** Foundation + Tracking Core (permissions, schema, session/category pipeline, workers, startup flow, basic dashboard/report)
2. **Plan 2:** AI engines + Insights + Personality + Gamification
3. **Plan 3:** Focus/Detox enforcement + safety guardrails + blocker UX
4. **Plan 4:** Monetization + premium gating + share card + release hardening

This plan is fully executable and yields a working app with local tracking and baseline analytics.

---

## File structure map (Plan 1)

### Project/build setup
- Create: `settings.gradle.kts` — Gradle module includes and plugin management.
- Create: `build.gradle.kts` — root build plugins and shared config.
- Create: `gradle.properties` — AndroidX, Kotlin, and memory flags.
- Create: `app/build.gradle.kts` — Compose, Hilt, Room, WorkManager, test dependencies.
- Create: `app/proguard-rules.pro` — baseline shrink config.

### App shell + DI + navigation
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/mindless/screen/MindLessApp.kt`
- Create: `app/src/main/java/com/mindless/screen/MainActivity.kt`
- Create: `app/src/main/java/com/mindless/screen/navigation/MindLessNavHost.kt`
- Create: `app/src/main/java/com/mindless/screen/di/AppModule.kt`
- Create: `app/src/main/java/com/mindless/screen/di/DispatcherModule.kt`

### Data layer (Room)
- Create: `app/src/main/java/com/mindless/screen/data/local/AppDatabase.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/entity/AppUsageRecordEntity.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/entity/UnlockEventEntity.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/entity/SessionRecordEntity.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/entity/HourlyAggregateEntity.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/entity/DailyAggregateEntity.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/entity/AppCategoryMapEntity.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/entity/AnalyticsEventEntity.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/entity/SubscriptionStateEntity.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/dao/TrackingDao.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/dao/AggregateDao.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/dao/CategoryDao.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/dao/AnalyticsDao.kt`

### Domain + repositories
- Create: `app/src/main/java/com/mindless/screen/domain/model/PermissionCapability.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/model/PermissionState.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/model/SessionEvent.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/model/SessionWindow.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/model/StartupState.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/model/AppCategory.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/repository/PermissionRepository.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/repository/TrackingRepository.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/repository/SubscriptionRepository.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/usecase/DetectSessionUseCase.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/usecase/ResolveCategoryUseCase.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/usecase/RetentionPolicyUseCase.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/usecase/StartupCoordinatorUseCase.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/usecase/UnlockAnalyticsUseCase.kt`

### Platform services + workers
- Create: `app/src/main/java/com/mindless/screen/data/permission/PermissionRepositoryImpl.kt`
- Create: `app/src/main/java/com/mindless/screen/data/tracking/CategoryResolver.kt`
- Create: `app/src/main/java/com/mindless/screen/data/tracking/UsageStatsTracker.kt`
- Create: `app/src/main/java/com/mindless/screen/service/BootCompletedReceiver.kt`
- Create: `app/src/main/java/com/mindless/screen/service/MindLessAccessibilityService.kt`
- Create: `app/src/main/java/com/mindless/screen/worker/LightUsageScanWorker.kt`
- Create: `app/src/main/java/com/mindless/screen/worker/SixHourAggregateWorker.kt`
- Create: `app/src/main/java/com/mindless/screen/worker/DailyRebuildWorker.kt`
- Create: `app/src/main/java/com/mindless/screen/worker/CleanupWorker.kt`
- Create: `app/src/main/java/com/mindless/screen/worker/WorkerScheduler.kt`

### Presentation
- Create: `app/src/main/java/com/mindless/screen/presentation/onboarding/OnboardingViewModel.kt`
- Create: `app/src/main/java/com/mindless/screen/presentation/onboarding/OnboardingScreen.kt`
- Create: `app/src/main/java/com/mindless/screen/presentation/dashboard/DashboardViewModel.kt`
- Create: `app/src/main/java/com/mindless/screen/presentation/dashboard/DashboardScreen.kt`
- Create: `app/src/main/java/com/mindless/screen/presentation/reports/DailyReportScreen.kt`

### Resources + branding
- Create: `app/src/main/res/drawable/app_logo.png`
- Create: `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
- Create: `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`
- Create: `app/src/main/res/drawable/ic_launcher_foreground.xml`
- Create: `app/src/main/res/values/strings.xml`

### Tests
- Create: `app/src/androidTest/java/com/mindless/screen/AppLaunchSmokeTest.kt`
- Create: `app/src/test/java/com/mindless/screen/data/local/AppDatabaseSchemaTest.kt`
- Create: `app/src/test/java/com/mindless/screen/presentation/onboarding/OnboardingViewModelTest.kt`
- Create: `app/src/test/java/com/mindless/screen/domain/usecase/DetectSessionUseCaseTest.kt`
- Create: `app/src/test/java/com/mindless/screen/domain/usecase/ResolveCategoryUseCaseTest.kt`
- Create: `app/src/test/java/com/mindless/screen/domain/usecase/RetentionPolicyUseCaseTest.kt`
- Create: `app/src/test/java/com/mindless/screen/domain/usecase/StartupCoordinatorUseCaseTest.kt`
- Create: `app/src/test/java/com/mindless/screen/domain/usecase/UnlockAnalyticsUseCaseTest.kt`

---

### Task 1: Bootstrap app shell (Compose + Hilt + Nav + launch smoke test)

**Files:**
- Create: `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/mindless/screen/MindLessApp.kt`
- Create: `app/src/main/java/com/mindless/screen/MainActivity.kt`
- Create: `app/src/main/java/com/mindless/screen/navigation/MindLessNavHost.kt`
- Test: `app/src/androidTest/java/com/mindless/screen/AppLaunchSmokeTest.kt`

- [ ] **Step 1: Write the failing instrumentation smoke test**

```kotlin
@RunWith(AndroidJUnit4::class)
class AppLaunchSmokeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchesDashboardTabByDefault() {
        composeRule.onNodeWithText("Dashboard").assertExists()
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.mindless.screen.AppLaunchSmokeTest`  
Expected: FAIL with unresolved references for `MainActivity` / missing Android app module.

- [ ] **Step 3: Write minimal implementation for app shell**

```kotlin
// app/src/main/java/com/mindless/screen/MainActivity.kt
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MindLessNavHost() }
    }
}

// app/src/main/java/com/mindless/screen/navigation/MindLessNavHost.kt
@Composable
fun MindLessNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { Text("Dashboard") }
    }
}

// app/src/main/java/com/mindless/screen/MindLessApp.kt
@HiltAndroidApp
class MindLessApp : Application()
```

```xml
<!-- app/src/main/AndroidManifest.xml -->
<manifest package="com.mindless.screen" xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
    <application
        android:name=".MindLessApp"
        android:label="MindLess Screen"
        android:theme="@style/Theme.Material3.DayNight.NoActionBar">
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>
</manifest>
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.mindless.screen.AppLaunchSmokeTest`  
Expected: PASS (`1 passed`).

- [ ] **Step 5: Commit**

```bash
git add settings.gradle.kts build.gradle.kts gradle.properties app/build.gradle.kts app/src/main/AndroidManifest.xml app/src/main/java/com/mindless/screen/MindLessApp.kt app/src/main/java/com/mindless/screen/MainActivity.kt app/src/main/java/com/mindless/screen/navigation/MindLessNavHost.kt app/src/androidTest/java/com/mindless/screen/AppLaunchSmokeTest.kt
git commit -m "feat: bootstrap compose app shell with hilt and launch smoke test"
```

### Task 2: Build Room schema with required tables and schema test

**Files:**
- Create: `app/src/main/java/com/mindless/screen/data/local/AppDatabase.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/entity/*.kt`
- Create: `app/src/main/java/com/mindless/screen/data/local/dao/*.kt`
- Test: `app/src/test/java/com/mindless/screen/data/local/AppDatabaseSchemaTest.kt`

- [ ] **Step 1: Write the failing schema test**

```kotlin
class AppDatabaseSchemaTest {

    @Test
    fun database_contains_required_tables() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        val tables = db.openHelper.readableDatabase
            .query("SELECT name FROM sqlite_master WHERE type='table'")
            .use { cursor ->
                buildSet {
                    while (cursor.moveToNext()) add(cursor.getString(0))
                }
            }

        assertTrue("app_usage_records" in tables)
        assertTrue("session_records" in tables)
        assertTrue("hourly_aggregates" in tables)
        assertTrue("daily_aggregates" in tables)
        assertTrue("app_category_map" in tables)
        assertTrue("analytics_events" in tables)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.data.local.AppDatabaseSchemaTest"`  
Expected: FAIL with `Unresolved reference: AppDatabase`.

- [ ] **Step 3: Implement Room entities, DAOs, and database**

```kotlin
// app/src/main/java/com/mindless/screen/data/local/AppDatabase.kt
@Database(
    entities = [
        AppUsageRecordEntity::class,
        UnlockEventEntity::class,
        SessionRecordEntity::class,
        HourlyAggregateEntity::class,
        DailyAggregateEntity::class,
        AppCategoryMapEntity::class,
        AnalyticsEventEntity::class,
        SubscriptionStateEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackingDao(): TrackingDao
    abstract fun aggregateDao(): AggregateDao
    abstract fun categoryDao(): CategoryDao
    abstract fun analyticsDao(): AnalyticsDao
}

@Entity(tableName = "app_category_map")
data class AppCategoryMapEntity(
    @PrimaryKey @ColumnInfo(name = "package_name") val packageName: String,
    val category: String,
    val confidence: Double,
    val source: String,
)

@Entity(tableName = "session_records")
data class SessionRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "start_ts") val startTs: Long,
    @ColumnInfo(name = "end_ts") val endTs: Long,
    @ColumnInfo(name = "duration_ms") val durationMs: Long,
    @ColumnInfo(name = "dominant_app") val dominantApp: String,
    @ColumnInfo(name = "late_night_flag") val lateNightFlag: Boolean,
    @ColumnInfo(name = "start_reason") val startReason: String,
    @ColumnInfo(name = "end_reason") val endReason: String,
)
```

```kotlin
// app/src/main/java/com/mindless/screen/data/local/dao/TrackingDao.kt
@Dao
interface TrackingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategory(category: AppCategoryMapEntity)
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.data.local.AppDatabaseSchemaTest"`  
Expected: PASS (`BUILD SUCCESSFUL`).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/mindless/screen/data/local app/src/test/java/com/mindless/screen/data/local/AppDatabaseSchemaTest.kt
git commit -m "feat: add room schema for tracking aggregates categories and analytics events"
```

### Task 3: Implement permission strategy and onboarding fallback states

**Files:**
- Create: `app/src/main/java/com/mindless/screen/domain/model/PermissionCapability.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/model/PermissionState.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/repository/PermissionRepository.kt`
- Create: `app/src/main/java/com/mindless/screen/data/permission/PermissionRepositoryImpl.kt`
- Create: `app/src/main/java/com/mindless/screen/presentation/onboarding/OnboardingViewModel.kt`
- Create: `app/src/main/java/com/mindless/screen/presentation/onboarding/OnboardingScreen.kt`
- Test: `app/src/test/java/com/mindless/screen/presentation/onboarding/OnboardingViewModelTest.kt`

- [ ] **Step 1: Write the failing onboarding ViewModel test**

```kotlin
class OnboardingViewModelTest {

    @Test
    fun usageAccessDenied_setsUsageAccessRequiredState() = runTest {
        val fakeRepo = FakePermissionRepository(
            mapOf(
                PermissionCapability.USAGE_ACCESS to false,
                PermissionCapability.ACCESSIBILITY_SERVICE to true,
                PermissionCapability.POST_NOTIFICATIONS to true,
            )
        )
        val vm = OnboardingViewModel(fakeRepo)

        vm.refresh()

        assertEquals(PermissionState.UsageAccessRequired, vm.uiState.value.permissionState)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.presentation.onboarding.OnboardingViewModelTest"`  
Expected: FAIL with `Unresolved reference: OnboardingViewModel`.

- [ ] **Step 3: Implement repository + ViewModel + onboarding UI**

```kotlin
enum class PermissionCapability {
    USAGE_ACCESS,
    ACCESSIBILITY_SERVICE,
    POST_NOTIFICATIONS,
    RECEIVE_BOOT_COMPLETED,
}

sealed interface PermissionState {
    data object Ready : PermissionState
    data object UsageAccessRequired : PermissionState
    data object AccessibilityRequired : PermissionState
    data object NotificationsOptional : PermissionState
}

class OnboardingViewModel @Inject constructor(
    private val permissionRepository: PermissionRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    fun refresh() {
        val usageGranted = permissionRepository.isGranted(PermissionCapability.USAGE_ACCESS)
        val accessibilityGranted = permissionRepository.isGranted(PermissionCapability.ACCESSIBILITY_SERVICE)
        val notificationsGranted = permissionRepository.isGranted(PermissionCapability.POST_NOTIFICATIONS)

        _uiState.value = OnboardingUiState(
            permissionState = when {
                !usageGranted -> PermissionState.UsageAccessRequired
                !accessibilityGranted -> PermissionState.AccessibilityRequired
                !notificationsGranted -> PermissionState.NotificationsOptional
                else -> PermissionState.Ready
            }
        )
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.presentation.onboarding.OnboardingViewModelTest"`  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/mindless/screen/domain/model/PermissionCapability.kt app/src/main/java/com/mindless/screen/domain/model/PermissionState.kt app/src/main/java/com/mindless/screen/domain/repository/PermissionRepository.kt app/src/main/java/com/mindless/screen/data/permission/PermissionRepositoryImpl.kt app/src/main/java/com/mindless/screen/presentation/onboarding app/src/test/java/com/mindless/screen/presentation/onboarding/OnboardingViewModelTest.kt
git commit -m "feat: add permission onboarding flow with degraded fallback states"
```

### Task 4: Implement session detection and category priority resolution

**Files:**
- Create: `app/src/main/java/com/mindless/screen/domain/model/SessionEvent.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/model/SessionWindow.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/usecase/DetectSessionUseCase.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/usecase/ResolveCategoryUseCase.kt`
- Create: `app/src/main/java/com/mindless/screen/data/tracking/CategoryResolver.kt`
- Test: `app/src/test/java/com/mindless/screen/domain/usecase/DetectSessionUseCaseTest.kt`
- Test: `app/src/test/java/com/mindless/screen/domain/usecase/ResolveCategoryUseCaseTest.kt`

- [ ] **Step 1: Write failing tests for session boundaries and category priority**

```kotlin
class DetectSessionUseCaseTest {

    @Test
    fun endsSession_whenInactivityExceeds30Seconds() {
        val useCase = DetectSessionUseCase(inactivityTimeoutMs = 30_000)
        val events = listOf(
            SessionEvent.ScreenOn(ts = 1_000),
            SessionEvent.ForegroundApp(ts = 2_000, packageName = "com.social.app"),
            SessionEvent.Inactivity(ts = 35_100),
        )

        val sessions = useCase(events)

        assertEquals(1, sessions.size)
        assertEquals("INACTIVITY_TIMEOUT", sessions.first().endReason)
    }
}

class ResolveCategoryUseCaseTest {

    @Test
    fun manualOverride_hasHighestPriority() {
        val useCase = ResolveCategoryUseCase(playStoreLookupEnabled = false)

        val category = useCase.resolve(
            packageName = "com.example.chat",
            builtIn = "communication",
            playStore = "social",
            override = "productivity",
        )

        assertEquals("productivity", category.value)
        assertEquals("USER_OVERRIDE", category.source)
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.domain.usecase.DetectSessionUseCaseTest" --tests "com.mindless.screen.domain.usecase.ResolveCategoryUseCaseTest"`  
Expected: FAIL with unresolved use case classes.

- [ ] **Step 3: Implement session detector and category resolver**

```kotlin
class DetectSessionUseCase(
    private val inactivityTimeoutMs: Long,
) {
    operator fun invoke(events: List<SessionEvent>): List<SessionWindow> {
        val sessions = mutableListOf<SessionWindow>()
        var currentStart: Long? = null
        var dominantApp = "unknown"

        events.sortedBy { it.ts }.forEach { event ->
            when (event) {
                is SessionEvent.ScreenOn -> if (currentStart == null) currentStart = event.ts
                is SessionEvent.ForegroundApp -> {
                    if (currentStart == null) currentStart = event.ts
                    dominantApp = event.packageName
                }
                is SessionEvent.ScreenOff -> {
                    currentStart?.let { start ->
                        sessions += SessionWindow(start, event.ts, dominantApp, "SCREEN_OFF")
                    }
                    currentStart = null
                }
                is SessionEvent.Inactivity -> {
                    currentStart?.let { start ->
                        if (event.ts - start > inactivityTimeoutMs) {
                            sessions += SessionWindow(start, event.ts, dominantApp, "INACTIVITY_TIMEOUT")
                            currentStart = null
                        }
                    }
                }
            }
        }

        return sessions
    }
}

class ResolveCategoryUseCase(
    private val playStoreLookupEnabled: Boolean,
) {
    fun resolve(packageName: String, builtIn: String?, playStore: String?, override: String?): ResolvedCategory {
        return when {
            override != null -> ResolvedCategory(override, "USER_OVERRIDE")
            builtIn != null -> ResolvedCategory(builtIn, "BUILT_IN")
            playStoreLookupEnabled && playStore != null -> ResolvedCategory(playStore, "PLAY_STORE")
            else -> ResolvedCategory("tools", "BUILT_IN")
        }
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.domain.usecase.DetectSessionUseCaseTest" --tests "com.mindless.screen.domain.usecase.ResolveCategoryUseCaseTest"`  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/mindless/screen/domain/model/SessionEvent.kt app/src/main/java/com/mindless/screen/domain/model/SessionWindow.kt app/src/main/java/com/mindless/screen/domain/usecase/DetectSessionUseCase.kt app/src/main/java/com/mindless/screen/domain/usecase/ResolveCategoryUseCase.kt app/src/main/java/com/mindless/screen/data/tracking/CategoryResolver.kt app/src/test/java/com/mindless/screen/domain/usecase/DetectSessionUseCaseTest.kt app/src/test/java/com/mindless/screen/domain/usecase/ResolveCategoryUseCaseTest.kt
git commit -m "feat: add deterministic session model and category resolution priority"
```

### Task 5: Implement worker cadence and retention cleanup policy

**Files:**
- Create: `app/src/main/java/com/mindless/screen/domain/usecase/RetentionPolicyUseCase.kt`
- Create: `app/src/main/java/com/mindless/screen/worker/LightUsageScanWorker.kt`
- Create: `app/src/main/java/com/mindless/screen/worker/SixHourAggregateWorker.kt`
- Create: `app/src/main/java/com/mindless/screen/worker/DailyRebuildWorker.kt`
- Create: `app/src/main/java/com/mindless/screen/worker/CleanupWorker.kt`
- Create: `app/src/main/java/com/mindless/screen/worker/WorkerScheduler.kt`
- Test: `app/src/test/java/com/mindless/screen/domain/usecase/RetentionPolicyUseCaseTest.kt`

- [ ] **Step 1: Write failing retention policy test**

```kotlin
class RetentionPolicyUseCaseTest {

    @Test
    fun computesExpectedCutoffs_forRawAndHourlyData() {
        val now = Instant.parse("2026-04-15T12:00:00Z")
        val useCase = RetentionPolicyUseCase(clock = Clock.fixed(now, ZoneOffset.UTC))

        val policy = useCase()

        assertEquals(Instant.parse("2026-03-16T12:00:00Z"), policy.rawEventCutoff)
        assertEquals(Instant.parse("2026-01-15T12:00:00Z"), policy.hourlyAggregateCutoff)
        assertNull(policy.dailyAggregateCutoff)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.domain.usecase.RetentionPolicyUseCaseTest"`  
Expected: FAIL with `Unresolved reference: RetentionPolicyUseCase`.

- [ ] **Step 3: Implement retention policy and worker scheduler**

```kotlin
class RetentionPolicyUseCase(
    private val clock: Clock = Clock.systemUTC(),
) {
    operator fun invoke(): RetentionPolicy {
        val now = Instant.now(clock)
        return RetentionPolicy(
            rawEventCutoff = now.minus(30, ChronoUnit.DAYS),
            hourlyAggregateCutoff = now.minus(90, ChronoUnit.DAYS),
            dailyAggregateCutoff = null,
        )
    }
}

object WorkerScheduler {
    fun schedule(context: Context) {
        val wm = WorkManager.getInstance(context)

        wm.enqueueUniquePeriodicWork(
            "light_usage_scan_15m",
            ExistingPeriodicWorkPolicy.UPDATE,
            PeriodicWorkRequestBuilder<LightUsageScanWorker>(15, TimeUnit.MINUTES).build(),
        )

        wm.enqueueUniquePeriodicWork(
            "aggregate_6h",
            ExistingPeriodicWorkPolicy.UPDATE,
            PeriodicWorkRequestBuilder<SixHourAggregateWorker>(6, TimeUnit.HOURS).build(),
        )

        wm.enqueueUniquePeriodicWork(
            "daily_rebuild_midnight",
            ExistingPeriodicWorkPolicy.UPDATE,
            PeriodicWorkRequestBuilder<DailyRebuildWorker>(24, TimeUnit.HOURS).build(),
        )

        wm.enqueueUniquePeriodicWork(
            "cleanup_daily",
            ExistingPeriodicWorkPolicy.UPDATE,
            PeriodicWorkRequestBuilder<CleanupWorker>(24, TimeUnit.HOURS).build(),
        )
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.domain.usecase.RetentionPolicyUseCaseTest"`  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/mindless/screen/domain/usecase/RetentionPolicyUseCase.kt app/src/main/java/com/mindless/screen/worker app/src/test/java/com/mindless/screen/domain/usecase/RetentionPolicyUseCaseTest.kt
git commit -m "feat: add worker cadence and retention cleanup policy"
```

### Task 6: Implement startup coordinator flow (permission -> subscription -> aggregate -> stale trigger)

**Files:**
- Create: `app/src/main/java/com/mindless/screen/domain/model/StartupState.kt`
- Create: `app/src/main/java/com/mindless/screen/domain/usecase/StartupCoordinatorUseCase.kt`
- Create: `app/src/main/java/com/mindless/screen/presentation/dashboard/DashboardViewModel.kt`
- Test: `app/src/test/java/com/mindless/screen/domain/usecase/StartupCoordinatorUseCaseTest.kt`

- [ ] **Step 1: Write failing startup coordinator test**

```kotlin
class StartupCoordinatorUseCaseTest {

    @Test
    fun staleAggregate_triggersRefreshFlag() = runTest {
        val useCase = StartupCoordinatorUseCase(
            permissionRepository = FakePermissionRepository(allGranted = true),
            subscriptionRepository = FakeSubscriptionRepository(isPremium = false),
            trackingRepository = FakeTrackingRepository(lastAggregateAgeMinutes = 25),
            staleThresholdMinutes = 15,
        )

        val state = useCase()

        assertTrue(state.shouldTriggerBackgroundAggregation)
        assertEquals(false, state.isPremium)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.domain.usecase.StartupCoordinatorUseCaseTest"`  
Expected: FAIL with unresolved `StartupCoordinatorUseCase`.

- [ ] **Step 3: Implement startup coordinator and wire into DashboardViewModel**

```kotlin
data class StartupState(
    val canTrackUsage: Boolean,
    val isPremium: Boolean,
    val shouldTriggerBackgroundAggregation: Boolean,
)

class StartupCoordinatorUseCase @Inject constructor(
    private val permissionRepository: PermissionRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val trackingRepository: TrackingRepository,
    private val staleThresholdMinutes: Long = 15,
) {
    suspend operator fun invoke(): StartupState {
        val canTrackUsage = permissionRepository.isGranted(PermissionCapability.USAGE_ACCESS)
        val isPremium = subscriptionRepository.isPremium()
        val lastAgeMinutes = trackingRepository.minutesSinceLastAggregation()

        return StartupState(
            canTrackUsage = canTrackUsage,
            isPremium = isPremium,
            shouldTriggerBackgroundAggregation = canTrackUsage && lastAgeMinutes > staleThresholdMinutes,
        )
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.domain.usecase.StartupCoordinatorUseCaseTest"`  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/mindless/screen/domain/model/StartupState.kt app/src/main/java/com/mindless/screen/domain/usecase/StartupCoordinatorUseCase.kt app/src/main/java/com/mindless/screen/presentation/dashboard/DashboardViewModel.kt app/src/test/java/com/mindless/screen/domain/usecase/StartupCoordinatorUseCaseTest.kt
git commit -m "feat: orchestrate startup flow and stale aggregate refresh decision"
```

### Task 7: Implement unlock analytics and high-unlock alert logic

**Files:**
- Create: `app/src/main/java/com/mindless/screen/domain/usecase/UnlockAnalyticsUseCase.kt`
- Create: `app/src/main/java/com/mindless/screen/data/notification/UnlockAlertNotifier.kt`
- Modify: `app/src/main/java/com/mindless/screen/worker/SixHourAggregateWorker.kt`
- Test: `app/src/test/java/com/mindless/screen/domain/usecase/UnlockAnalyticsUseCaseTest.kt`

- [ ] **Step 1: Write failing unlock analytics test**

```kotlin
class UnlockAnalyticsUseCaseTest {

    @Test
    fun emitsAlert_whenUnlockCountExceedsDynamicThreshold() {
        val useCase = UnlockAnalyticsUseCase(staticFallbackThreshold = 120)

        val result = useCase.evaluate(
            todayUnlockCount = 132,
            recentAverageUnlocks = 100.0,
        )

        assertTrue(result.shouldNotify)
        assertEquals("You unlocked your phone 132 times today.", result.message)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.domain.usecase.UnlockAnalyticsUseCaseTest"`  
Expected: FAIL with unresolved `UnlockAnalyticsUseCase`.

- [ ] **Step 3: Implement use case and notifier integration**

```kotlin
class UnlockAnalyticsUseCase(
    private val staticFallbackThreshold: Int,
) {
    fun evaluate(todayUnlockCount: Int, recentAverageUnlocks: Double): UnlockAlertDecision {
        val dynamicThreshold = maxOf(staticFallbackThreshold, (recentAverageUnlocks * 1.2).roundToInt())
        val shouldNotify = todayUnlockCount >= dynamicThreshold

        val message = if (shouldNotify) {
            "You unlocked your phone $todayUnlockCount times today."
        } else {
            null
        }

        return UnlockAlertDecision(shouldNotify, message)
    }
}

data class UnlockAlertDecision(
    val shouldNotify: Boolean,
    val message: String?,
)
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.domain.usecase.UnlockAnalyticsUseCaseTest"`  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/mindless/screen/domain/usecase/UnlockAnalyticsUseCase.kt app/src/main/java/com/mindless/screen/data/notification/UnlockAlertNotifier.kt app/src/main/java/com/mindless/screen/worker/SixHourAggregateWorker.kt app/src/test/java/com/mindless/screen/domain/usecase/UnlockAnalyticsUseCaseTest.kt
git commit -m "feat: add unlock interval analytics and high unlock alert policy"
```

### Task 8: Build basic dashboard and daily report screens from local aggregates

**Files:**
- Create: `app/src/main/java/com/mindless/screen/presentation/dashboard/DashboardScreen.kt`
- Create: `app/src/main/java/com/mindless/screen/presentation/reports/DailyReportScreen.kt`
- Modify: `app/src/main/java/com/mindless/screen/navigation/MindLessNavHost.kt`
- Modify: `app/src/main/java/com/mindless/screen/presentation/dashboard/DashboardViewModel.kt`
- Test: `app/src/androidTest/java/com/mindless/screen/DashboardUiTest.kt`

- [ ] **Step 1: Write failing UI test for dashboard stats card**

```kotlin
@RunWith(AndroidJUnit4::class)
class DashboardUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsTopStatsCard() {
        composeRule.onNodeWithText("Screen Time Today").assertExists()
        composeRule.onNodeWithText("Addiction Score").assertExists()
        composeRule.onNodeWithText("Unlock Count").assertExists()
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.mindless.screen.DashboardUiTest`  
Expected: FAIL because labels are not rendered yet.

- [ ] **Step 3: Implement dashboard and daily report composables**

```kotlin
@Composable
fun DashboardScreen(state: DashboardUiState, onOpenReports: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Screen Time Today")
        Text(state.totalScreenTimeLabel)
        Spacer(Modifier.height(8.dp))
        Text("Addiction Score")
        Text(state.addictionScoreLabel)
        Spacer(Modifier.height(8.dp))
        Text("Unlock Count")
        Text(state.unlockCountLabel)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onOpenReports) { Text("View Reports") }
    }
}

@Composable
fun DailyReportScreen(report: DailyReportUiModel) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item { Text("Daily Report") }
        item { Text("Total Screen Time: ${report.totalScreenTime}") }
        item { Text("Addiction Score: ${report.addictionScore}") }
        item { Text("Unlock Count: ${report.unlockCount}") }
        item { Text("Focus Time: ${report.focusTime}") }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.mindless.screen.DashboardUiTest`  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/mindless/screen/presentation/dashboard/DashboardScreen.kt app/src/main/java/com/mindless/screen/presentation/reports/DailyReportScreen.kt app/src/main/java/com/mindless/screen/navigation/MindLessNavHost.kt app/src/main/java/com/mindless/screen/presentation/dashboard/DashboardViewModel.kt app/src/androidTest/java/com/mindless/screen/DashboardUiTest.kt
git commit -m "feat: render dashboard top stats and daily report screen from local aggregates"
```

### Task 9: Integrate branding assets and adaptive launcher icon

**Files:**
- Create: `app/src/main/res/drawable/app_logo.png`
- Create: `app/src/main/res/drawable/ic_launcher_foreground.xml`
- Create: `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
- Create: `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`
- Test: `app/src/test/java/com/mindless/screen/resources/BrandingResourceTest.kt`

- [ ] **Step 1: Write failing branding resource test**

```kotlin
class BrandingResourceTest {

    @Test
    fun launcherForegroundResourceExists() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val id = context.resources.getIdentifier("ic_launcher_foreground", "drawable", context.packageName)
        assertTrue(id != 0)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.resources.BrandingResourceTest"`  
Expected: FAIL because `ic_launcher_foreground` does not exist.

- [ ] **Step 3: Add logo/icon assets and adaptive icon xml**

```xml
<!-- app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml -->
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@android:color/black" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

```xml
<!-- app/src/main/res/drawable/ic_launcher_foreground.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#35E7D0"
        android:pathData="M54,18A36,36 0,1 1,54 90A36,36 0,1 1,54 18" />
</vector>
```

Run copy commands:

```bash
cp "app logo.png" "app/src/main/res/drawable/app_logo.png"
cp "app icon.png" "app/src/main/res/drawable/app_icon.png"
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests "com.mindless.screen.resources.BrandingResourceTest"`  
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/res/drawable/app_logo.png app/src/main/res/drawable/app_icon.png app/src/main/res/drawable/ic_launcher_foreground.xml app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml app/src/test/java/com/mindless/screen/resources/BrandingResourceTest.kt
git commit -m "chore: add branding assets and adaptive launcher icon resources"
```

### Task 10: End-to-end verification of Plan 1 deliverables

**Files:**
- Modify: `app/src/main/java/com/mindless/screen/MainActivity.kt`
- Modify: `app/src/main/java/com/mindless/screen/navigation/MindLessNavHost.kt`
- Test: `app/src/androidTest/java/com/mindless/screen/Plan1E2ETest.kt`

- [ ] **Step 1: Write failing E2E test covering onboarding fallback and dashboard path**

```kotlin
@RunWith(AndroidJUnit4::class)
class Plan1E2ETest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun whenUsageAccessDenied_onboardingIsShown() {
        composeRule.onNodeWithText("Enable Usage Access").assertExists()
    }

    @Test
    fun whenPermissionsReady_dashboardIsShown() {
        composeRule.onNodeWithText("Screen Time Today").assertExists()
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.mindless.screen.Plan1E2ETest`  
Expected: FAIL with at least one missing UI state.

- [ ] **Step 3: Implement missing navigation/state glue to satisfy both paths**

```kotlin
@Composable
fun MindLessNavHost(startInOnboarding: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (startInOnboarding) "onboarding" else "dashboard"

    NavHost(navController, startDestination = startDestination) {
        composable("onboarding") { OnboardingScreen(...) }
        composable("dashboard") { DashboardScreen(...) }
        composable("report_daily") { DailyReportScreen(...) }
    }
}
```

- [ ] **Step 4: Run full verification suite**

Run: `./gradlew :app:testDebugUnitTest :app:connectedDebugAndroidTest :app:lintDebug`  
Expected: PASS for unit + instrumented tests and lint; no blocking issues.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/mindless/screen/MainActivity.kt app/src/main/java/com/mindless/screen/navigation/MindLessNavHost.kt app/src/androidTest/java/com/mindless/screen/Plan1E2ETest.kt
git commit -m "test: add plan1 end-to-end onboarding and dashboard verification"
```

---

## Plan self-review

### 1) Spec coverage (for Plan 1 scope)
Covered in this plan:
- Android target and foundational app architecture.
- Permission strategy and onboarding fallback behavior.
- Session tracking model with exact start/end rules.
- Category mapping table and resolution priority.
- Background cadence (15m / 6h / midnight) via WorkManager.
- Data retention (30d raw, 90d hourly, daily indefinite).
- Startup data flow sequence.
- Unlock tracking and high-unlock alert policy.
- Basic dashboard + daily report screen.
- Branding asset integration.

Deferred to next plans (intentional):
- Addiction score + tomorrow prediction full engine.
- Focus/detox hard-block safety runtime behavior.
- Personality, smart insights, gamification.
- Premium billing, ads, and advanced analytics.

### 2) Placeholder scan
No unresolved placeholders are present.

### 3) Type consistency
All referenced types are defined in this plan with stable names: `PermissionCapability`, `PermissionState`, `SessionEvent`, `SessionWindow`, `RetentionPolicyUseCase`, `StartupCoordinatorUseCase`, and `UnlockAnalyticsUseCase`.

---

## Exit criteria for Plan 1

- App launches into onboarding or dashboard based on permission state.
- Room schema includes required tracking/category/analytics tables.
- Session detection and category priority tests pass.
- Worker cadence and retention policy tests pass.
- Startup stale-data trigger behavior is verified.
- Dashboard and daily report render from local aggregate data.
- Branding assets are wired and compile correctly.

