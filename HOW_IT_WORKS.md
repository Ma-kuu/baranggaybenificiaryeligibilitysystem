# How Each Java File Works

This document explains what each file does, what data it holds, and how it connects to the others.

---

## 1. `Resident.java` — The Data Model

This is just a plain data class. It holds all the information about one barangay resident.

**Fields it stores:**
| Field | Type | What it means |
|---|---|---|
| `residentId` | int | Unique ID assigned to each resident |
| `fullName` | String | Full name |
| `age` | int | Age in years |
| `householdIncome` | double | Monthly household income in Php |
| `familySize` | int | Number of people in the household |
| `occupation` | String | Job or occupation |
| `student` | boolean | Whether the resident is a student |
| `unemployed` | boolean | Whether the resident is unemployed |
| `soloParent` | boolean | Whether the resident is a solo parent |
| `seniorCitizen` | boolean | Whether the resident is a senior citizen |
| `pwd` | boolean | Whether the resident is a PWD |
| `dependentChildren` | boolean | Whether the resident has dependent children |
| `academicAverage` | double | Academic average (0 if not a student) |
| `childOfSoloParent` | boolean | For scholarship priority checks |
| `childOfOfwOwwa` | boolean | For OWWA scholarship eligibility |

**It does not contain any logic.** It only stores data and provides getters so other classes can read it.

---

## 2. `SampleData.java` — Test Data

Provides 16 pre-built `Resident` objects for testing and demo purposes.

- Returns a `Resident[]` array with 16 residents already filled in
- Each resident is designed to trigger a different eligibility scenario (e.g. one is a farmer, one is a PWD student, one is a senior citizen)
- The 16 residents also push past the pagination limit of 15, so the paging feature is visible during demo
- `getNextIdAfterSample()` returns `17` so newly registered residents don't get duplicate IDs

**Used by:** `MenuService` at startup via `SampleData.loadSampleResidents()`

---

## 3. `ShellSorter.java` — The Algorithm

This is the core DSA component of the project.

**What it does:**  
Sorts a `Resident[]` array using the **Shell Sort** algorithm and returns a new sorted copy (the original is not modified).

**How Shell Sort works here:**
1. Start with a gap = `n / 2`
2. Compare elements that are `gap` positions apart and swap if out of order
3. Halve the gap and repeat
4. When gap = 1, it finishes like a regular Insertion Sort — but by then the array is nearly sorted, so it's fast

**The `Comparator` parameter** is what makes it flexible. Instead of hardcoding "sort by income", you pass in the sorting rule when you call it:

```java
// sort by income ascending
sorter.shellSort(qualified, (r1, r2) -> Double.compare(r1.getHouseholdIncome(), r2.getHouseholdIncome()));

// sort by name alphabetically
sorter.shellSort(qualified, (r1, r2) -> r1.getFullName().compareToIgnoreCase(r2.getFullName()));
```

**Used by:** `MenuService` in 3 places — generating beneficiary lists, finding priority rank, and ranking by need score.

---

## 4. `EligibilityService.java` — Eligibility Rules + Need Score

Contains all the rules for determining which programs a resident qualifies for.

**Two income thresholds are defined at the top:**
- `POVERTY_THRESHOLD` = Php 12,030/month
- `LOW_INCOME_THRESHOLD` = Php 21,000/month

**Key methods:**

| Method | What it does |
|---|---|
| `isEligible4Ps(r)` ... `isEligibleEducationalCash(r)` | Returns true/false for one specific program |
| `getEligiblePrograms(r)` | Returns a `String[]` of all programs the resident qualifies for |
| `getIneligiblePrograms(r)` | Returns a `String[]` of all programs they do NOT qualify for |
| `getQualifiedResidents(residents, count, programNumber)` | Scans the whole array and returns a `Resident[]` of everyone who qualifies for a given program |
| `calculateNeedScore(r)` | Returns a score (higher = more in need). Used for ranking. |
| `getProgramName(number)` | Gets a program name by its number (1–11) |
| `getProgramNumber(name)` | Gets a program number by its name |

**The need score breakdown:**
- Income below poverty line → +5 pts
- Income below low income → +3 pts
- Large family (6+) → +3 pts, (4–5) → +2 pts
- PWD, Senior, Solo Parent, Unemployed → +2 pts each
- Dependent children → +1 pt
- +1 pt for every program they're eligible for

**Used by:** `MenuService` for eligibility checking, beneficiary list generation, and need score ranking.

---

## 5. `DisplayService.java` — Table Printing + Pagination

Handles all the output formatting. `MenuService` never prints tables directly — it always calls `DisplayService`.

**Key methods:**

| Method | What it does |
|---|---|
| `displayResidentTable(list)` | Prints a compact table of all residents with flags (Stu/Uem/SP/SC/PWD) |
| `displayBeneficiaryTable(list)` | Prints a ranked table of beneficiaries for a program |
| `displayNeedScoreTable(list, eligibility)` | Prints residents ranked by need score |
| `displayResidentDetail(r)` | Prints all fields of one resident in a two-column table |
| `displayProgramMenu(eligibility)` | Prints the numbered list of 11 programs |

**Pagination:**  
If a list has more than 15 entries, it automatically splits into pages. The user can type `N` (next), `P` (previous), or `Q` (go back). This is handled internally by `getNextPage()`.

**Helper methods:**
- `truncate(text, maxLength)` — cuts long names so tables don't break
- `yn(boolean)` — returns `"Y"` or `"N"` for flag columns

---

## 6. `MenuService.java` — CLI Menu + All User Actions

The biggest file. It connects everything together and runs the main loop.

**Data storage:**
```java
Resident[] residents = new Resident[200];  // fixed-size array
int count = 0;                             // how many slots are actually used
```
This is a plain array, not a list. `count` always tracks how many residents are stored.

**Menu options and what they do:**

| Option | Method | What happens |
|---|---|---|
| 1 | `registerResident()` | Reads input from user, creates a `Resident`, adds it to `residents[count]`, increments `count` |
| 2 | `viewResidents()` | Passes `Arrays.copyOf(residents, count)` to `DisplayService` |
| 3 | `checkEligibility()` | Finds a resident by ID, calls `EligibilityService`, uses Shell Sort to find their rank in each eligible program |
| 4 | `generateBeneficiaryList()` | Gets qualified residents for a program, sorts them with Shell Sort by the user's chosen field |
| 5 | `searchResident()` | Searches by ID (linear scan) or name (partial match, linear scan) |
| 6 | `findMostInNeed()` | Shell Sort all residents by their need score descending |
| 7 | `deleteResident()` | Finds the resident by ID, then shifts all elements left in the array to fill the gap |
| 8 | Exit | Sets `running = false` |

**Delete uses a manual array shift:**
```java
for (int i = indexToDelete; i < count - 1; i++) {
    residents[i] = residents[i + 1];
}
residents[count - 1] = null;
count--;
```

---

## 7. `Main.java` — Entry Point

The only thing this file does is create a `MenuService` and call `start()`.

```java
public static void main(String[] args) {
    MenuService menu = new MenuService();
    menu.start();
}
```

---

## How the Files Connect

```
Main
 └── MenuService          (runs the loop, holds the Resident[] array)
      ├── SampleData      (loads 16 test residents at startup)
      ├── EligibilityService  (checks program rules + calculates need score)
      ├── ShellSorter     (sorts Resident[] by any field using Comparator)
      └── DisplayService  (prints all tables and handles pagination)
           └── Resident   (the data object passed around everywhere)
```

Every feature in the menu ultimately flows through these 4 services. `Resident` itself has no logic — it's just the data that gets passed around.
