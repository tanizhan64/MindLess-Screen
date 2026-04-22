# MindLess Screen — Plan 2 AI/Insights/Personality/Gamification Design

Date: 2026-04-22  
Scope: Plan 2 only (AI engines, Insights, Personality, Gamification)  
Delivery mode: Incremental checkpoints  
Constraint: Real aggregate-driven outputs from day one (no placeholder constants)

## 1. Scope and goal

This design implements the approved Plan 2 milestone on top of completed Plan 1 foundations. The goal is to produce deterministic, locally computed AI-style outputs and surface them in app UI without expanding scope into Focus/Detox enforcement or monetization.

Included:
- Addiction score engine
- Tomorrow usage prediction
- Smart insights generation
- Personality classification
- Gamification streak/level/badges
- Persistence for all Plan 2 outputs
- Insights/Profile UI surfaces and navigation wiring

Deferred:
- Focus/Detox hard blocking and safety guardrails
- Billing/AdMob/premium gating changes

## 2. Architecture and boundaries

### 2.1 Domain engines (pure logic)
Plan 2 adds six domain use cases:
- `CalculateAddictionScoreUseCase`
- `PredictTomorrowUsageUseCase`
- `ClassifyPersonalityUseCase`
- `GenerateSmartInsightsUseCase`
- `ComputeGamificationUseCase`
- `RunDailyAiAnalysisUseCase` (orchestrator)

The first five are pure deterministic engines with no framework dependencies. `RunDailyAiAnalysisUseCase` composes them and delegates persistence through repository interfaces.

### 2.2 Data boundary
Room schema extends with:
- `insight_records`
- `personality_snapshots`
- `gamification_states`

Repository boundary:
- `AiInsightsRepository` interface in domain
- `AiInsightsRepositoryImpl` in data layer for Room mapping and persistence

### 2.3 Runtime trigger boundary
A `DailyAiAnalysisWorker` triggers orchestration after aggregate readiness checks. Worker responsibilities remain scheduling + invocation; business decisions stay inside domain use cases.

### 2.4 Presentation boundary
- `InsightsViewModel` consumes Plan 2 read models and exposes immutable `InsightsUiState`
- `ProfileViewModel` exposes personality and gamification summaries/history
- `InsightsScreen` and `ProfileScreen` render state only

This split preserves testability: engine logic is unit-tested; persistence/mapping is data-tested; UI remains state-driven.

## 3. Data flow

1. Worker starts on schedule and checks aggregate freshness.
2. Orchestrator loads required real inputs from persisted aggregates (today + trailing window).
3. Engines compute:
   - weighted addiction score and risk band,
   - tomorrow prediction with weekend and late-night components,
   - personality class + confidence,
   - insight cards,
   - gamification state (streak/level/badges).
4. Repository writes generated outputs tagged to run day/time.
5. ViewModels observe latest records and expose UI state.
6. Compose screens render current snapshot and timestamp.

No placeholder constants are used for production computation paths.

## 4. Failure handling and degraded behavior

Primary policy: never crash, never fabricate data.

- If required aggregate inputs are insufficient (for example, limited 7-day history), engines emit low-confidence/neutral outputs with explicit explanatory messaging.
- If write fails, worker returns retry/failure according to transient vs permanent error classification.
- UI renders latest successful snapshot if available.
- If no snapshot exists, UI shows empty state with clear guidance and no invented metrics.
- Each screen shows `generatedAt` metadata so users can understand freshness.

## 5. UI integration

### 5.1 Insights screen
Contains:
- Tomorrow Prediction card
- Personality summary card
- Smart Insights list
- Gamification summary card (streak + badges)
- Snapshot timestamp

### 5.2 Profile screen updates
Contains:
- Current personality type/confidence
- Personality history summary (as available)
- Current streak and level summary

### 5.3 Navigation
Existing nav graph is extended to ensure:
- Dashboard exposes `View Insights` action
- Route to Insights screen
- Route to Profile screen

## 6. Testing and verification strategy

Incremental checkpoints:

### Checkpoint A — domain correctness
Unit tests for:
- weighted score formula and risk bands
- prediction late-night penalty scaling and weekend factor behavior
- personality rule thresholds
- insights generation rules
- gamification streak/badge logic

### Checkpoint B — persistence and mapping
Tests for:
- Room schema table existence and DAO operations
- repository mapping correctness
- idempotent persistence semantics per analysis run

### Checkpoint C — orchestration and worker
Tests for:
- orchestrator producing and persisting all output types
- missing-input degraded behavior
- worker execution path and repeat safety

### Checkpoint D — UI integration
Instrumentation tests for:
- `View Insights` navigation path
- visibility of prediction/personality/insights/gamification content
- empty-state rendering when no snapshots are available

### Completion gate
- `./gradlew :app:testDebugUnitTest`
- targeted `./gradlew :app:connectedDebugAndroidTest` for Plan 2 tests
- `./gradlew :app:lintDebug`

All must pass before Plan 2 is declared complete.

## 7. Implementation sequencing

Recommended sequence:
1. Room schema + DAOs + repository interface/impl
2. Domain engine use cases (pure logic + tests)
3. Orchestrator use case + tests
4. Worker integration + scheduler update
5. ViewModels + screens + nav wiring
6. End-to-end verification pass

This sequence reduces integration churn by stabilizing computation and persistence before UI.

## 8. Non-goals and constraints

Non-goals in this phase:
- No Focus/Detox blocker implementation
- No billing, ads, premium gating changes
- No cloud sync or remote AI service calls

Constraints:
- Local-first processing and storage
- Reuse existing architecture conventions from Plan 1
- Keep each engine deterministic and independently testable

## 9. Acceptance criteria

Plan 2 is complete when:
- All five engines compute against real persisted aggregates
- Orchestrator persists insights/personality/gamification snapshots per run
- Insights/Profile UI surfaces render Plan 2 outputs and handle empty state
- Navigation from Dashboard to Insights/Profile is working
- Required unit, integration, instrumentation, and lint gates pass
- Scope remains limited to approved Plan 2 boundaries
