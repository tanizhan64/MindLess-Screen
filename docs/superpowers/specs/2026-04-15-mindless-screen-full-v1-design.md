# MindLess Screen — Full v1 Production Technical Specification (Revised)

Date: 2026-04-15  
Platform: Android  
Language: Kotlin  
Architecture: MVVM + Clean Architecture + Repository Pattern  
Dependency Injection: Hilt  
UI: Jetpack Compose + Material Design 3 + Navigation Component

## 1) Scope Decision

This specification targets **Full v1** scope in a **single codebase delivered by milestones** to minimize integration failure while preserving complete feature coverage.

### Included Modules
1. Screen Time Tracker
2. Report System (Daily/Weekly/Monthly)
3. Advanced AI Addiction Score
4. Tomorrow Risk Prediction
5. Focus Mode
6. Dopamine Detox Mode
7. Phone Unlock Tracking
8. AI Smart Insights
9. Digital Personality Analysis
10. Gamification
11. Shareable Report Card
12. Monetization (AdMob + Google Play Billing)

## 2) Platform Compatibility and Build Targets

- `minSdk = 26`
- `targetSdk = latest stable Android SDK`
- Kotlin + Coroutines/Flow throughout data and presentation layers.
- Compose-first UI implementation.

## 3) System Architecture

### 3.1 Layering
- **Presentation layer:** Compose screens, ViewModels, UI state reducers, navigation routes.
- **Domain layer:** entities, repository interfaces, use cases, policy engines (score, prediction, personality, gamification, gating).
- **Data layer:** Room, DAOs, local data sources, mappers, repository implementations.
- **Platform services:** UsageStatsManager, Accessibility Service, WorkManager, notifications, billing and ads wrappers.

### 3.2 Module Boundaries
- `core`: common Result wrappers, dispatchers, clock/time helpers, shared constants.
- `domain`: use cases and policies for tracking analytics, addiction score, prediction, insights, personality, streaks, badges, entitlements.
- `data`: Room entities/DAOs, repository implementations, mapping and caching.
- `tracking`: usage ingestion, unlock/session detectors, aggregators.
- `feature/dashboard`
- `feature/focus`
- `feature/reports`
- `feature/insights`
- `feature/profile`
- `monetization`: Billing + AdMob adapters and entitlement state.

### 3.3 Runtime Data Flow
1. Raw usage/unlock/foreground transition signals are collected.
2. Background workers create hourly and daily aggregates.
3. Domain engines compute addiction score, prediction, personality, and gamification outcomes.
4. UI consumes `Flow` read models through ViewModels.
5. Compose screens render immutable UI state.

## 4) Permission Strategy

### 4.1 Required Permissions and Capabilities
- `PACKAGE_USAGE_STATS` (UsageStatsManager access; granted via system Usage Access settings screen)
- `BIND_ACCESSIBILITY_SERVICE` (Accessibility-based hard blocking in Focus/Detox)
- `POST_NOTIFICATIONS` (runtime permission on Android 13+)
- `RECEIVE_BOOT_COMPLETED` (restore workers and active plans after reboot)

### 4.2 Onboarding Permission Flow
1. Explain why usage tracking is required and confirm all behavior data is local-only.
2. Redirect user to Usage Access settings.
3. Verify Usage Access grant when returning to app.
4. Guide user to enable Accessibility service for Focus/Detox blocking.
5. Request notification permission on Android 13+.
6. Enable tracking workers and feature flags only after required permissions are validated.

### 4.3 Denied Permission Fallback Behavior
- **Usage Access denied:** disable tracking, reports, scoring, and predictions; show blocked-state UI with retry CTA.
- **Accessibility not enabled:** keep tracking/reports active; disable Focus/Detox hard-block and show enable-service CTA.
- **Notifications denied:** keep core app behavior active; suppress reminder notifications only.
- **Boot permission unavailable/restricted:** resume workers at app launch and show delayed-tracking warning if needed.

## 5) Data Model and Room Schema

All tracked behavior remains local in Room.

### 5.1 Core Tables
- `app_usage_records`
  - `id`, `date`, `package_name`, `foreground_ms`, `background_ms`, `launches`, `hour_bucket`, `category`
- `unlock_events`
  - `id`, `timestamp`
- `session_records`
  - `id`, `start_ts`, `end_ts`, `duration_ms`, `dominant_app`, `late_night_flag`, `start_reason`, `end_reason`
- `hourly_aggregates`
  - `id`, `date`, `hour_bucket`, `total_screen_ms`, `unlock_count`, `social_ms`, `gaming_ms`, `launches`
- `daily_aggregates`
  - `date` (PK), `total_screen_ms`, `unlock_count`, `avg_session_ms`, `longest_session_ms`,
    `social_ms`, `gaming_ms`, `focus_completed`, `focus_planned`,
    `addiction_score`, `addiction_level`, `distraction_index`, `productivity_score`
- `app_category_map`
  - `package_name` (PK), `category`, `confidence`, `source`
- `focus_sessions`
  - `id`, `start_ts`, `end_ts`, `planned_duration_min`, `completed`, `interruption_count`, `mode`
- `detox_plans`
  - `id`, `start_ts`, `end_ts`, `active`, `blocked_categories_json`, `blocked_packages_json`
- `insight_records`
  - `id`, `date`, `type`, `severity`, `message`, `recommendation`
- `personality_snapshots`
  - `id`, `date`, `personality_type`, `confidence`
- `gamification_states`
  - `date` (PK), `streak_days`, `focus_level`, `badges_json`
- `subscription_states`
  - `id`, `is_premium`, `source`, `expiry_ts`, `last_validated_ts`
- `analytics_events`
  - `id`, `timestamp`, `event_type`, `metadata`

### 5.2 Derived Read Models
- Top apps (today)
- Hourly usage timeline
- Category usage distribution
- Longest continuous session
- Daily/weekly/monthly report DTOs

## 6) Session Tracking Model

### 6.1 Session Start Conditions
- Screen turns on.
- New foreground app launch when no active session exists.

### 6.2 Session End Conditions
- Screen turns off.
- Inactivity timeout greater than 30 seconds.

### 6.3 Session Rules
- Persist each session in `session_records`.
- Merge transition noise shorter than 2 seconds into adjacent activity.
- Compute `dominant_app` as app with highest foreground duration in the session.
- Mark `late_night_flag` if session overlaps night window (default: 00:00–05:00).

## 7) App Category Classification

### 7.1 Supported Categories
- `social`
- `gaming`
- `productivity`
- `communication`
- `entertainment`
- `learning`
- `tools`

### 7.2 Category Resolution Priority
1. Built-in category database.
2. Optional Play Store category lookup (extension path; disabled by default in v1 runtime).
3. Manual user override.

`manual user override` is authoritative when set and is persisted in `app_category_map` with `source = USER_OVERRIDE`.

## 8) Tracking, Background Processing, and Retention

### 8.1 Background Work Cadence
- Light usage scan: every 15 minutes.
- Aggregation jobs: every 6 hours.
- Full daily rebuild: once per day at local midnight.

### 8.2 Scheduling Strategy (WorkManager)
- Use unique periodic work names per pipeline stage.
- Prefer constrained execution and idempotent workers.
- Skip full recompute if watermark indicates no new source data.

### 8.3 Data Retention Policy
- Raw event logs (`unlock_events`, transient raw usage records): retain 30 days.
- Hourly aggregates: retain 90 days.
- Daily aggregates: retain indefinitely.

### 8.4 Cleanup Implementation
- Daily cleanup worker via WorkManager.
- Batched deletes to avoid I/O spikes and ANR risk.

## 9) AI Addiction Score Engine

### 9.1 Inputs (normalized 0..100)
- Total screen time
- Social media usage
- Gaming usage
- Unlock frequency
- Late-night usage
- Session duration

### 9.2 Formula
`score = 0.35*screen + 0.20*social + 0.15*unlock + 0.10*night + 0.10*gaming + 0.10*session`

Clamp output to 0..100.

### 9.3 Risk Bands
- 0–30: Healthy (Green)
- 31–60: Moderate Usage (Yellow)
- 61–80: Risk Zone (Orange)
- 81–100: High Addiction (Red)

## 10) Tomorrow Prediction Model (Clarified)

### 10.1 Feature Inputs
- Last 7 days screen time
- Average unlocks
- Peak usage hour
- Social media usage ratio

### 10.2 Prediction Formula
`predicted_screen_time = 0.6 * average_screen_time_last_7_days + 0.2 * weekend_factor + 0.2 * late_night_penalty`

### 10.3 Factor Definitions
- `weekend_factor`: uplift component derived from historical weekend-vs-weekday delta.
- `late_night_penalty`: penalty component that increases predicted usage by 10–20% when midnight usage exceeds threshold.

Implementation rule for penalty:
- If midnight usage <= threshold, penalty = 0.
- If midnight usage > threshold, penalty multiplier scales linearly from +10% to +20% based on exceedance.

### 10.4 Outputs
- Expected screen time tomorrow
- Predicted addiction risk level
- Single actionable suggestion

Example insight: “Your screen time may increase tomorrow due to late-night usage.”

## 11) Focus Mode and Detox Mode

### 11.1 Focus Mode
- Durations: 25, 45, 60, custom.
- User-selected app block list.
- Hard block via Accessibility overlay activity.
- Motivational quote + countdown + interruption tracking.
- Session outcomes persisted in `focus_sessions`.

### 11.2 Detox Mode
- Durations: 6h, 12h, 24h, 48h.
- Default blocked categories: social, gaming, streaming.
- Package-level customization allowed.
- Uses same hard-block engine as Focus mode.

### 11.3 Safety Guardrails (Must Never Be Blocked)
- Phone dialer
- Emergency call flows
- Alarm clock surfaces
- System UI
- Accessibility settings

### 11.4 Lockout Prevention Logic
- Evaluate allowlist before every block decision.
- Allowlist packages always bypass blocker.
- If blocker state is inconsistent, route user to safe settings recovery path.
- Enforce safe-mode recovery on app startup after crashes/reboot.

## 12) Unlock Tracking

Track and report:
- Unlock count per day
- Unlock pattern per hour
- Average unlock interval

High-unlock alert example: “You unlocked your phone 132 times today.”

## 13) Smart Insights and Personality Analysis

### 13.1 Smart Insights
Generate local insight cards such as:
- Social media share warnings
- Unlock frequency alerts
- Late-night usage warnings
- Focus improvement suggestions

### 13.2 Personality Classification (rule-based v1)
- Night Scroller
- Social Media Addict
- Productivity Seeker
- Weekend Binger
- Balanced User

Each profile includes recommendation templates in Insights/Profile.

## 14) Gamification

- Productivity streaks
- Achievement badges (7-day Silver, 30-day Gold, 90-day Master Focus)
- Focus level progression based on consistency

## 15) Reporting and Visualization

### 15.1 Daily Report
- Total screen time
- Addiction score
- Unlock count
- Most used apps
- Focus time

### 15.2 Weekly Report
- Average screen time
- Weekly trend
- Distraction index
- Improvement percentage

### 15.3 Monthly Report
- Usage heatmap calendar
- Category breakdown
- Longest focus streak
- Productivity score

### 15.4 Required Charts
- Line chart
- Pie chart
- Bar chart
- Usage heatmap calendar

MPAndroidChart-backed rendering is used with Compose wrappers where needed.

## 16) App Startup Data Flow

On each app launch:
1. Check permission and capability states.
2. Load subscription/entitlement state.
3. Load today’s aggregate data.
4. Trigger background aggregation if data is stale.
5. Emit consolidated UI state to Compose screens.

## 17) UI Structure

Bottom navigation:
- Dashboard
- Focus
- Reports
- Insights
- Profile

### Dashboard
- Top statistics card (today screen time, addiction score, unlock count)
- Top used apps
- Quick actions (Start Focus, Enable Detox, View Reports)
- Native ad slot for free tier

### Reports
- Tabs: Daily, Weekly, Monthly
- Trend and category visualizations
- Banner ad slot for free tier

### Insights
- AI-generated insight cards
- Tomorrow prediction card

### Profile
- Preferences, goals, limits
- Premium status and billing actions
- Privacy and local data controls

## 18) Local Analytics Event Logging

`analytics_events` captures lightweight local behavior events for product intelligence and diagnostics.

Example event types:
- `focus_session_started`
- `focus_session_completed`
- `detox_mode_started`
- `report_viewed`
- `prediction_generated`

All event metadata remains local in Room; no external analytics collection.

## 19) Monetization and Premium Gating

### 19.1 AdMob Placements (Free Tier)
- Dashboard: Native Ad
- Reports: Banner Ad
- Focus session end: Rewarded Ad

### 19.2 Free vs Premium Feature Matrix

**Free version**
- Basic screen time tracking
- Daily report
- Focus mode
- Limited detox mode
- Ads enabled

**Premium version**
- AI prediction system
- Advanced analytics
- Unlimited detox mode
- Advanced insights
- Ad-free experience

### 19.3 Billing Strategy
- Google Play Billing with test product IDs in development.
- Entitlement state exposed as reactive flow.
- Gating enforced in both UI and domain use-cases.

## 20) Privacy and Security

- All usage and behavior data is stored locally.
- No external behavior data collection.
- Shareable report cards generated on-device and shared only by explicit user action.
- Billing tokens handled per Play Billing best practices.

## 21) Performance Requirements

- App size target under 25MB (subject to SDK/library floor).
- Battery-efficient scheduling with constrained workers.
- Incremental aggregation preferred over full recompute.
- Smooth animations and bounded chart payloads.

## 22) Testing and Verification

### 22.1 Unit Tests
- Score normalization and weighting
- Prediction math and penalty behavior
- Personality classification rules
- Streak and badge policies

### 22.2 Integration Tests
- Room DAO correctness
- Repository and mapper accuracy
- WorkManager pipeline behavior

### 22.3 Instrumentation Tests
- Permission onboarding and fallback states
- Navigation and primary user flows
- Focus/Detox enforcement and safety bypass allowlist
- Premium gating behavior

### 22.4 Manual Validation Matrix
- Permission denied/revoked scenarios
- Reboot recovery and worker restoration
- Midnight rollover correctness
- Battery saver/idle behavior
- Ads and billing fallback behavior

## 23) Milestone Delivery Plan (Design-Level)

- **M1 Foundation:** project setup, architecture skeleton, Room schema, DI, navigation shell
- **M2 Tracking Core:** ingestion, session/unlock model, category mapping, dashboard essentials
- **M3 Reports:** daily/weekly/monthly report generation + chart screens
- **M4 Focus/Detox:** hard-block engine, safety guardrails, lockout prevention
- **M5 AI Layer:** addiction score, tomorrow prediction, insights, personality
- **M6 Engagement:** gamification and shareable report card
- **M7 Monetization:** AdMob placement, billing, entitlement-based gating
- **M8 Hardening:** optimization, QA matrix, release readiness

## 24) Branding Assets

Provided asset:
- `C:\Users\VICKY BE\Downloads\app logo.png`

Planned usage:
- Source visual for launcher/app icon derivations.
- Source image for splash/onboarding branding.
- Adaptive icon generation should preserve center glyph legibility across light/dark launchers.

## 25) Explicit Non-Goals for v1

- Cloud sync or account system
- Remote model training or telemetry
- Cross-device profile merge
- Social leaderboard backend

## 26) Approval Record

This revised specification extends the previously approved design and incorporates the requested engineering refinements before implementation planning.
