# Expense Tracker — Assignment 01 (Android UI Layout)

Mobile Device Application Development

| | |
|---|---|
| Student ID | `24120507` |
| Name | `Huynh Bao Khang` |
| Design | `3` (StudentID mod 4, 0 → Design 4) |

One screen of an Expense Tracker app, implemented **twice**:

| Part | Approach | File |
|---|---|---|
| 1 | XML layout | `app/src/main/res/layout/activity_main.xml` |
| 2 | Java, no XML (views built in code) | `app/src/main/java/com/example/expensetracker/MainActivity.java` |

## The screen

- **Top bar** — menu icon, "Expense Tracker" title, "+" icon
- **Add New Expense** form — Title, Amount, Category (spinner), Date (date picker), Notes (optional), `ADD EXPENSE` button
- **Recent Expenses** list — colored category icon, title, "date • category", red amount, three-dot icon

## Run

Requires Android Studio and an emulator/device (minSdk 24).

1. Open this folder in Android Studio and let Gradle sync.
2. Press **Run**.

Which version is shown is decided by one line in `MainActivity.onCreate()`:

```java
setContentView(buildRoot());               // Java version (current)
setContentView(R.layout.activity_main);    // XML version
```

To preview the XML version, swap the line and comment out `seedSampleExpenses()` (the XML list rows are already written in the layout).

## Implementation notes

**XML version**
- `ScrollView` → vertical `LinearLayout` (top bar, form, list).
- Each form field is a horizontal `LinearLayout` with the `bg_field` border: icon/label + borderless `EditText` / `Spinner` / `TextView`.
- Recent Expenses rows sit in one white card (`bg_card`); the category circle is `bg_circle` + `backgroundTint`.

**Java version**
- Same hierarchy, built with small helpers (`linear`, `text`, `icon`, `box`, `fieldRow`, `lp`, `dp`).
- `Category` enum carries its own label, color and icon, so no `switch` is needed.
- Beyond the static design: `ADD EXPENSE` validates the form, inserts a row at the top of the list and clears the form; the Date field opens a `DatePickerDialog`.

## Known limits

- Expenses are kept in memory only (lost when the app closes).
- The XML version is static; nothing is wired to the `ADD EXPENSE` button.

## Submission

```
24120507.zip
├── XML/activity_main.xml
├── JAVA/MainActivity.java
└── README.txt (optional)
```
