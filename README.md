# Weldrite CPVC Master

> **Strong Bond. Zero Leaks.**

A complete, offline Android game where you play a professional plumber joining
CPVC pipes and fittings with **Weldrite** solvent cement. Prepare the pipe, lay
down an even coat of cement, align the fitting, hold the joint until it sets,
then run a pressure test — and chase a perfect, leak‑free score.

The gameplay art (pipes, fittings, water, UI) and all audio are **generated in
code**. The official **Weldrite logo and CPVC‑456 product image** are bundled in
`assets/weldrite/` and used with the brand owner's authorization for this
branded title. The game makes no network calls and has no ads or in‑app
purchases.

---

## 📑 Table of Contents
- [Highlights](#-highlights)
- [Gameplay](#-gameplay)
- [Game Modes](#-game-modes)
- [Progression & Scoring](#-progression--scoring)
- [Tech Stack & Architecture](#-tech-stack--architecture)
- [Project Structure](#-project-structure)
- [Build From Source](#-build-from-source)
- [Install the APK](#-install-the-apk)
- [How to Play](#-how-to-play)
- [Testing](#-testing)
- [Performance & Size](#-performance--size)
- [Implementation Notes](#-implementation-notes)
- [License](#-license)

---

## ✨ Highlights

- **Six‑step joining loop**: Select → Prepare (cut / clean / deburr) → Cement →
  Align fitting → Join (press‑and‑hold) → Pressure test.
- **3 game modes**: Career (50 progressive levels), Time Attack (60s), Endless.
- **5 pipe sizes** (½″, ¾″, 1″, 1.5″, 2″) and **4 fittings** (elbow, tee,
  coupler, reducer).
- **Progression**: stars, six plumber ranks, 9 achievements, and a **daily streak**.
- **Juice & game feel**: screen shake, hit-stop (freeze-frame), particle bursts,
  floating "PERFECT / GREAT / GOOD" judgments, a **combo system** with escalating
  feedback and rising pitch, "GET READY / GO!" intros, and a **FLAWLESS** bonus.
- **Interactive, skippable tutorial** covering all five core skills.
- **Settings**: Music, Sound FX, Haptics, **Language (English / Español /
  हिन्दी)**, and Graphics Quality (Low / Medium / High).
- **Original audio engine**: a real‑time software synth mixes sound effects and
  a looping workshop music track — zero audio files shipped.
- **Polished, family‑friendly visuals** in the Weldrite palette (blue / white /
  red), all vector‑drawn.
- **Local save**, **haptic feedback**, immersive full‑screen, 60 FPS target.
- **Tiny footprint**: the signed release APK is **~1.2 MB** including the bundled
  brand images (limit was 150 MB).

---

## 🎮 Gameplay

Each joint is built by working through six steps. Every step contributes to the
final **joint quality**, which decides the pressure‑test outcome:

| Step | Action | Mini‑game |
|------|--------|-----------|
| 1. Select Pipe | Pick the correct CPVC size | Tap the right diameter |
| 2. Prepare | Cut, clean and deburr | Tap to cut straight · swipe to clean · tap the burrs |
| 3. Cement | Apply Weldrite solvent cement | Drag around the pipe end for even, correct coverage |
| 4. Align | Choose & align the fitting | Pick the right fitting · drag to rotate to target |
| 5. Join | Insert & hold | Press and hold steady until the bond sets |
| 6. Pressure Test | Run water through | Result: **Perfect**, **Minor Leak**, or **Major Leak** |

---

## 🕹️ Game Modes

- **Career** — 50 hand‑tuned levels across six environments (Kitchen, Bathroom,
  House, Apartment, Commercial, Factory). Difficulty ramps up: larger pipes,
  more fittings, tighter tolerances and shorter time limits. Each level is rated
  **1–3 stars** and unlocks the next.
- **Time Attack** — Complete as many successful joints as you can in **60
  seconds**.
- **Endless** — Infinite, escalating randomly generated joints. You have **3
  lives**; a major leak costs one.

---

## 🏆 Progression & Scoring

Points reward **accuracy, speed, even cement coverage, leak prevention** and
**correct fitting selection**. Stars are awarded from your average joint quality
and whether you avoided leaks.

**Ranks** (earned by accumulating stars):
`Apprentice Plumber → Junior Technician → Technician → Senior Technician →
Plumbing Expert → Master Plumber`

**Achievements** include *First Joint*, *Leak‑Free Expert*, *Speed Plumber*,
*100 Perfect Connections*, *Triple Threat*, *Marathon*, *Journeyman*,
*All Fixed Up*, and *Master Installer*.

Progress, settings and achievements are saved locally (SharedPreferences) and
persist between sessions.

---

## 🎚️ Game Feel & Retention (research-informed)

Design decisions are grounded in established game-feel and mobile-retention
research:

- **Juice / impact feedback** — screen shake, **hit-stop**, particle bursts on
  contact, easing/overshoot pop-ins, color flashes and a vignette for focus.
  Effects "echo the core gameplay" and scale with the moment (a perfect joint
  shakes harder than a cut). Screen-shake strength scales with the **Graphics
  Quality** setting, so *Low* doubles as a reduce-motion option. (Vlambeer's
  *Art of Screenshake*; GameAnalytics, *Squeezing more juice out of your game*.)
- **Success gradation + outcome binding** — every step shows an instant
  judgment ("PERFECT / GREAT / GOOD") and floating `+points`, and a **combo**
  multiplier rewards skillful streaks with escalating visuals and rising-pitch
  audio. (CHI 2024, *How does Juicy Game Feedback Motivate?*)
- **Near-miss effect** — a just-missed perfect shows "SO CLOSE!" to encourage
  another try (players are ~2.4× more likely to continue after a near-win).
- **Gentle onboarding & flow** — an auto-launching interactive tutorial, a
  "GET READY / GO!" intro, and an **assist** that widens tolerances and slows
  timing on the first three career levels, keeping the early difficulty slope
  shallow while visual complexity ramps up across the six environments.
- **Retention loop** — a **daily streak** counter on the menu, plus stars,
  ranks and achievements.
- **Mobile UX** — large touch targets, primary actions in the bottom thumb-zone,
  high-contrast text with backing panels, and gesture-first interactions.
- **Audio** — synthesized SFX with a subtle ambient workshop pad beneath the
  music ("felt, not heard").

*Sources:* [GameAnalytics – Squeezing more juice](https://www.gameanalytics.com/blog/squeezing-more-juice-out-of-your-game-design),
[CHI 2024 – Juicy Game Feedback](https://dl.acm.org/doi/10.1145/3613904.3642656),
[Mobile UX – tap targets & thumb zones](https://parachutedesign.ca/blog/thumb-zone-ux/),
[Hyper-casual retention & difficulty](https://riseuplabs.com/game-retention-metrics/).

## 🔬 Real-World Accuracy

The gameplay mirrors the **actual CPVC solvent-cement joining procedure** defined
by ASTM D2846 / F493 / F656 and major manufacturers (Weld-On/IPS, Oatey, Lubrizol
FlowGuard Gold / Corzan, Charlotte Pipe). The step order and on-screen pro-tips
follow the real sequence:

1. **Cut square** — maximizes the bonding surface.
2. **Deburr & chamfer** the edge (~10–15°) — so it can't scrape cement out of the socket.
3. **Clean & dry** both surfaces — done *after* deburring to clear filings.
4. **Apply CPVC cement** — heavy on the pipe, light in the socket (CPVC-specific, F493).
5. **Align, insert with a 1/4 turn** to full depth while wet, then **hold** so it can't push out.
6. **Witness bead & pressure test** — an even bead all around signals a good joint;
   let it **cure**, then test **with water, never compressed air**.

Each step shows a real-world **pro tip**, and the join step now includes the
quarter-turn cue and a witness-bead visual at the joint. Note: **Weldrite 456 is a
one-step cement** — no separate primer is needed where local codes permit (some
potable-water codes still require priming), which is why the flow has no primer step.

*Sources:* [Weld-On Solvent Welding Guide](https://weldon.com/wp-content/uploads/2015/03/WeldOn_SolventWelding_Guide_2013.pdf) ·
[Oatey — perfect solvent-cement joint](https://www.oatey.com/faqs-blog-videos-case-studies/blog/how-complete-perfect-solvent-cement-joint) ·
[FlowGuard Gold — step-by-step](https://www.flowguardgold.com/step-by-step-instructions) ·
[Corzan — solvent-welding do's & don'ts](https://www.corzan.com/en-us/blog/the-dos-and-donts-of-cpvc-piping-solvent-cement-welding) ·
ASTM D2846 / F493 / F656

## 🧱 Tech Stack & Architecture

- **Language:** Kotlin
- **Build:** Gradle (Kotlin DSL) + Android Gradle Plugin 8.6.1
- **Min / Target SDK:** 29 (Android 10) / 34 (Android 14)
- **No game engine and no external libraries** beyond `androidx.core` — the game
  runs on a small custom 2D engine built directly on `SurfaceView` + `Canvas`.

**Engine design**
- A dedicated render thread runs a frame‑limited game loop (~60 FPS) drawing to a
  **hardware‑accelerated canvas** (software fallback).
- Touch input is captured on the UI thread and queued, then drained on the game
  thread, so **all game state is single‑threaded** (no locks in hot paths).
- A `Screen` stack with cross‑fade transitions drives navigation
  (Splash → Menu → …).
- `Painter` provides a resolution‑independent drawing vocabulary (design units
  map screen width to 1080), so the UI is crisp on any device/aspect ratio.
- `AudioEngine` is a tiny mixing synth: it streams 16‑bit PCM to an `AudioTrack`,
  summing short‑lived oscillator "voices" for SFX and a step‑sequenced music
  loop.

---

## 📁 Project Structure

```
game/
├── app/
│   ├── build.gradle.kts            # module config, signing, R8
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/weldrite/cpvcmaster/
│       │   │   ├── MainActivity.kt          # single-activity host + lifecycle
│       │   │   ├── engine/                  # Game, GameView, Screen, Painter,
│       │   │   │                            #   Particles, Geom, Haptics
│       │   │   ├── audio/AudioEngine.kt     # software synth + mixer
│       │   │   ├── data/                    # Models, Levels, Achievements,
│       │   │   │                            #   SaveManager, Loc (i18n)
│       │   │   ├── gfx/                      # PipeRenderer, Decor (branding)
│       │   │   ├── ui/Widgets.kt            # buttons, toggles, palette
│       │   │   └── screens/                 # Splash, Menu, CareerMap, Settings,
│       │   │                                #   Achievements, Tutorial, Play
│       │   └── res/                          # icon (adaptive), theme, colors
│       └── test/java/com/weldrite/cpvcmaster/
│           ├── LogicTest.kt                  # pure JVM logic tests
│           └── SmokeTest.kt                  # Robolectric crash-smoke (all screens/phases)
├── build.gradle.kts                          # plugin versions
├── settings.gradle.kts
├── gradle/ + gradlew + gradlew.bat           # Gradle wrapper (8.14.3)
├── weldrite-release.keystore                 # DEMO signing key (replace for release)
├── keystore.properties                       # points the build at the demo key
└── Weldrite_CPVC_Master.apk                  # prebuilt signed release APK
```

---

## 🔧 Build From Source

### Requirements
- **JDK 17+** (JDK 21 tested)
- **Android SDK** with `platforms;android-34` and `build-tools;34.0.0`
- The repo includes the **Gradle wrapper**, so you don't need Gradle installed.

### 1. Point the build at your SDK
Create `local.properties` in the project root (Android Studio does this
automatically):
```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

### 2. Build
```bash
# Signed release APK (uses the bundled demo keystore)
./gradlew assembleRelease

# or a debug build
./gradlew assembleDebug

# run the test suite
./gradlew testDebugUnitTest
```

### 3. Find the APK
```
app/build/outputs/apk/release/app-release.apk   # signed, minified
app/build/outputs/apk/debug/app-debug.apk
```

> **Or just open the project in Android Studio** (Giraffe/Koala or newer),
> let it sync, and press **Run ▶**.

### Signing
The project ships with a **throwaway demo keystore** (`weldrite-release.keystore`,
configured via `keystore.properties`) so `assembleRelease` produces a signed,
installable APK out of the box. **Replace it before any real distribution:**
```bash
keytool -genkeypair -v -keystore my-release.keystore -alias my \
        -keyalg RSA -keysize 2048 -validity 10000
# then update keystore.properties to point at your key
```

---

## 📲 Install the APK

A prebuilt, signed release APK is included at the repo root:
**`Weldrite_CPVC_Master.apk`**.

**Via adb (USB debugging on):**
```bash
adb install -r Weldrite_CPVC_Master.apk
```

**Manually (sideload):**
1. Copy `Weldrite_CPVC_Master.apk` to your Android 10+ device.
2. Open it with a file manager.
3. If prompted, allow **“Install unknown apps”** for that app.
4. Tap **Install**, then open **Weldrite CPVC Master**.

No additional setup, data files, or network connection is required.

---

## 👆 How to Play

- **Tap** options/buttons to select pipes and fittings and to confirm.
- **Tap** to cut at the centred guide for a straight cut.
- **Swipe** across the pipe to clean it; **tap** the red burrs to deburr.
- **Drag** over the pipe end to apply an even coat of cement.
- **Drag** left/right to rotate a fitting to its target, then **Confirm**.
- **Press and hold** on the joint until the progress ring fills and the bond
  sets — keep steady!
- Watch the **pressure test** for your result, then move to the next joint.

Pause anytime with the **II** button (top‑right) for Resume / Restart / Exit.

---

## 🧪 Testing

Two test suites run on the JVM (no device needed):

- **`LogicTest`** — verifies the 50‑level career, progressive difficulty, that
  every joint's options always contain the correct answer, rank thresholds, and
  achievement unlock logic.
- **`SmokeTest`** (Robolectric) — boots the game and **fuzzes touch input
  through every screen and all gameplay phases** (career easy + hard, time
  attack, endless) against a real `Bitmap`‑backed `Canvas` for thousands of
  frames, asserting nothing throws.

```bash
./gradlew testDebugUnitTest
```
All 17 tests pass.

---

## ⚡ Performance & Size

- Frame‑limited ~60 FPS loop on a hardware‑accelerated canvas.
- Lightweight vector rendering and a pooled particle system; the **Graphics
  Quality** setting scales particle density and background detail for lower‑end
  devices.
- Single‑threaded game state with a lock‑free hot path.
- **Signed release APK: ~1.2 MB** (the only bundled media is the Weldrite logo
  and product image), against the 150 MB limit.

---

## 📝 Implementation Notes

The brief listed Unity as the *preferred* engine but explicitly allowed
**“Kotlin (Android Studio) or Unity C#.”** This project takes the Kotlin /
Android Studio route with a purpose‑built 2D engine. The benefits:

- **Code‑generated gameplay art and audio**, plus the official **Weldrite brand
  assets** (logo + product image) bundled with the brand owner's authorization.
- A **tiny, fast, fully offline** APK that installs and runs on any Android 10+
  device with no extra setup.
- Clean, dependency‑light, readable architecture.

Visuals are a polished, modern **2D** presentation (shaded "cylinder" pipes and
fittings) rather than photoreal 3D.

---

## 📄 License

All source code, gameplay art and audio in this repository are original and
created for this project. The **Weldrite** name, logo and product imagery in
`assets/weldrite/` are the property of their owner and are included in this
branded title **with the brand owner's authorization**; they are not covered by
the project's own license and should not be reused without permission.
