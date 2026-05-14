# Barangay Beneficiary Prioritization System (BBES)

A Java CLI application built for **IT221 - Data Structures and Algorithms**.

This system helps barangay officials check which residents qualify for Philippine government assistance programs and rank them by priority using **Shell Sort**.

---

## How to Run

**Requirements:** Java 17+, Maven

```bash
mvn compile exec:java -Dexec.mainClass="com.bbes.Main"
```

Or just run `Main.java` directly from your IDE (IntelliJ, Eclipse, VS Code).

16 sample residents are loaded automatically on startup — no setup needed.

---

## Features

| # | Feature | Description |
|---|---|---|
| 1 | Register Resident | Add a new resident with their personal and demographic info |
| 2 | View Residents | See all residents in a paginated table (15 per page) |
| 3 | Check Eligibility | See which programs a resident qualifies for + their priority rank |
| 4 | Generate Beneficiary List | Pick a program and sort qualified residents (by income, age, etc.) |
| 5 | Search Resident | Find a resident by ID or name |
| 6 | Find Most In-Need | Rank all residents by a computed need score |
| 7 | Delete Resident | Remove a resident from the system |

---

## Algorithm: Shell Sort

Implemented in `ShellSorter.java`. Used throughout the system to:
- Sort beneficiary lists by income, age, family size, or name
- Determine a resident's **priority rank** within each eligible program
- Rank all residents by **need score** (highest need first)

Shell Sort improves on Insertion Sort by first comparing elements that are far apart (using a gap), then narrowing the gap each pass. This means elements can jump to their correct position faster.

**Time complexity:** O(n²) worst case, but much faster in practice.

---

## Programs Covered (11 total)

1. 4Ps — Pantawid Pamilyang Pilipino Program
2. SPES — Special Program for Employment of Students
3. TUPAD — Tulong Panghanapbuhay sa Ating Disadvantaged Workers
4. Solo Parent Benefits
5. Senior Citizen Pension
6. PWD Assistance
7. AKAP — Ayuda sa Kapos ang Kita Program
8. Farmer Assistance
9. Bagong Pilipinas Merit Scholarship
10. OWWA Scholarship
11. Educational Cash Assistance

> Income thresholds are based on PSA 2023 guidelines (poverty line: Php 12,030/month).

---

## Project Structure

```
src/main/java/com/bbes/
├── Main.java               entry point
├── Resident.java           resident data model (fields + getters)
├── EligibilityService.java eligibility rules for all 11 programs + need score
├── ShellSorter.java        Shell Sort algorithm (generic, uses Comparator)
├── MenuService.java        CLI menu and user interaction
├── DisplayService.java     table printing and pagination
└── SampleData.java         16 preloaded residents for testing
```

---

## Notes

- Data is stored **in memory only** — everything resets when you exit.
- The **need score** is calculated based on: income level, family size, vulnerability flags (PWD, senior, solo parent, unemployed), and number of eligible programs.
- Pagination kicks in automatically when a list exceeds 15 entries. Use `N`, `P`, `Q` to navigate.
