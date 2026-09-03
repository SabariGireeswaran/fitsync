# FitSync 💪

**An intelligent personal wellness and exercise recommendation desktop app.**

FitSync is a JavaFX desktop application that helps a single user track the core
signals of personal fitness — body weight, BMI, workouts and goals — and turns
those numbers into a personalised plan using Google's Gemini API. It is built
as a clean, layered MVC application over a local SQLite database, and ships as a
single Maven project that runs with one command.

---

## Screenshots

> _Placeholder — add PNGs under `docs/screenshots/` and link them here._

| Login | Dashboard | AI Advisor |
|-------|-----------|------------|
| `docs/screenshots/login.png` | `docs/screenshots/dashboard.png` | `docs/screenshots/advisor.png` |

| BMI Calculator | Workout Log | Progress Report |
|----------------|-------------|-----------------|
| `docs/screenshots/bmi.png` | `docs/screenshots/workout.png` | `docs/screenshots/report.png` |

---

## Features by version

FitSync was built in five incremental versions. Every version is included in the
current build.

### Version 1 — Core wellness system
- User registration and login (SQLite-backed)
- Dashboard with live BMI, workout count and current weight
- BMI calculator with category classification and advice
- Workout logging with history table
- MVC architecture, DAO pattern, singleton DB connection

### Version 2 — Weight tracking & goal management
- **Weight Tracker** — log body weight over time with a full history table
- **Goal Management** — set goals with a target/current value and an automatic
  progress percentage
- Dashboard stats now read live from the database

### Version 3 — Reports & progress tracking
- **Progress Report** screen aggregating:
  - total workouts and average calories burned
  - full BMI history
  - weight change over time (first vs. latest entry, direction and range)

### Version 4 — AI recommendation module
- **AI Wellness Advisor** screen powered by the Google Gemini REST API
- `ApiClient` built on `java.net.http.HttpClient` (no SDK dependency)
- `PromptBuilder` assembles a detailed prompt from the user's stats
- Request runs on a background `javafx.concurrent.Task` so the UI never freezes
- Graceful handling of missing key / network / API errors

### Version 5 — Final polish & packaging
- `AlertUtil` — consistent error / success / confirmation dialogs
- `ValidationUtil` — email, password, positive-number and not-empty checks
- All controllers validate input and surface problems through dialogs
- Global stylesheet: hover effects, card shadows, professional sidebar,
  consistent typography and spacing
- Sidebar shows the signed-in user and the app version; dashboard cards use
  Unicode icons (♥ BMI, ⚡ workouts, ⚖ weight)
- Loading spinners and disabled buttons while data is fetched
- Schema self-verification on start-up for all five tables

---

## Technologies used

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 (tested on 21) | Language / runtime |
| JavaFX | 21.0.2 | Desktop UI (controls + FXML) |
| SQLite (via `sqlite-jdbc`) | 3.45.1.0 | Local persistence |
| Maven | 3.9+ | Build & run (`javafx-maven-plugin` 0.0.8) |
| JUnit Jupiter | 5.10.1 | Test scaffold |
| Google Gemini API | `v1beta` / `gemini-1.5-flash` | AI recommendations |

---

## Project architecture

Layered MVC. Dependencies point downward only.

```
com.fitsync
├── FitSyncApp            JavaFX Application; owns the Stage, swaps Scenes,
│                         attaches the shared stylesheet
├── MainLauncher          plain main() entry point
├── config
│   └── AppConfig         all constants: window size, FXML paths, DB path,
│                         API config, BMI thresholds
├── model                 plain data classes (User, WorkoutLog, BmiRecord,
│                         WeightEntry, Goal)
├── dao                   database access, one class per table, PreparedStatement
│                         only; DatabaseManager is the singleton connection owner
├── service               business logic; controllers never touch DAOs directly
├── controller            one controller per FXML screen; validation + navigation
└── util                  AlertUtil, ValidationUtil, ApiClient, PromptBuilder
```

- **MVC** — FXML files are the view, `*Controller` classes are the controllers,
  `model` + `service` + `dao` are the model.
- **DAO pattern** — every table has a dedicated DAO; all SQL uses
  `PreparedStatement` (no string concatenation of user input).
- **Singleton** — `DatabaseManager.getInstance()` holds the one JDBC connection.
- **Screens** are FXML under `src/main/resources/com/fitsync/fxml/`, styled by a
  single `css/style.css` attached to every `Scene`.

Database file: `~/.fitsync/fitsync.db` (created automatically). Tables:
`users`, `workout_logs`, `bmi_records`, `weight_entries`, `goals`.

---

## Setup

### Prerequisites (all platforms)
- **JDK 17 or newer** (`java -version`)
- **Maven 3.9+** (`mvn -version`)

The JavaFX runtime and SQLite driver are pulled automatically by Maven — no
separate JavaFX SDK install is needed.

### Windows

```powershell
git clone https://github.com/SabariGireeswaran/fitsync.git
cd fitsync
mvn javafx:run
```

If `mvn` is not on your `PATH`, install it (e.g. `winget install Apache.Maven`)
or use the copy bundled with IntelliJ IDEA
(`...\plugins\maven-plugin\lib\maven3\bin\mvn.cmd`).

### Ubuntu / Debian

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven

git clone https://github.com/SabariGireeswaran/fitsync.git
cd fitsync
mvn javafx:run
```

On a minimal/headless install you may also need common native libs for JavaFX:

```bash
sudo apt install -y libgtk-3-0 libxtst6
```

### Build a runnable JAR

```bash
mvn clean package
# then run with the JavaFX plugin
mvn javafx:run
```

---

## Configuring the Gemini API key

The AI Wellness Advisor calls the Google Gemini `generateContent` REST API.
FitSync reads the key from an **environment variable** — it is never stored in
source.

`AppConfig.GEMINI_API_KEY`:
```java
public static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY") != null
        ? System.getenv("GEMINI_API_KEY")
        : "your-gemini-key-here";
```

### Windows (persist for your user)
```powershell
[Environment]::SetEnvironmentVariable("GEMINI_API_KEY", "YOUR-GEMINI-KEY", "User")
```
Then open a **new** terminal / restart your IDE.

### Ubuntu (persist in your shell profile)
```bash
echo 'export GEMINI_API_KEY="YOUR-GEMINI-KEY"' >> ~/.bashrc
source ~/.bashrc
```

Without a key the app runs normally; the Advisor screen simply shows a message
explaining that the key is not configured. Get a key at
<https://aistudio.google.com/app/apikey>.

---

## Running the app

```bash
mvn javafx:run
```

Register an account, sign in, and use the sidebar to move between Dashboard,
BMI Calculator, Workout Log, Weight Tracker, Goals, Reports and AI Advisor.

---

## Author

**Sabari Gireeswaran** — MCA student, S T Hindu College, Nagercoil.
