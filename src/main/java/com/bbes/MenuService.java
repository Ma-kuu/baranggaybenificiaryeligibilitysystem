package com.bbes;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * MenuService - handles CLI menu logic and user input.
 * Delegates display to DisplayService and uses SampleData for test data.
 */
public class MenuService {

    // Core components
    private ArrayList<Resident> residents;       // stores all registered residents
    private EligibilityService eligibility;      // checks program eligibility
    private ShellSorter sorter;                  // sorts beneficiary lists using Shell Sort
    private DisplayService display;              // handles all table printing
    private Scanner scanner;                     // reads user input
    private int nextId;                          // auto-increment resident ID

    // ==================== Constructor ====================
    public MenuService() {
        residents = new ArrayList<>();
        eligibility = new EligibilityService();
        sorter = new ShellSorter();
        scanner = new Scanner(System.in);
        display = new DisplayService(scanner); // share scanner for pagination input
        nextId = 1;
    }

    // ==================== Main Menu ====================

    /**
     * Displays the main menu and handles user choices.
     * Loops until the user chooses to exit.
     */
    public void start() {
        // Load sample data from SampleData class
        residents = SampleData.loadSampleResidents();
        nextId = SampleData.getNextIdAfterSample();

        System.out.println("\n=====================================================");
        System.out.println("   BARANGAY BENEFICIARY PRIORITIZATION SYSTEM");
        System.out.println("=====================================================");
        System.out.println(" Sample data loaded: " + residents.size() + " residents registered.\n");

        boolean running = true;

        while (running) {
            System.out.println("\n=================== MAIN MENU ===================");
            System.out.println("  [1] Register Resident");
            System.out.println("  [2] View Residents");
            System.out.println("  [3] Check Eligibility");
            System.out.println("  [4] Generate Beneficiary List");
            System.out.println("  [5] Search Resident");
            System.out.println("  [6] Find Most In-Need Residents");
            System.out.println("  [7] Exit");
            System.out.println("==================================================");
            System.out.print("  Enter choice: ");

            int choice = readInt();

            switch (choice) {
                case 1: registerResident();         break;
                case 2: viewResidents();            break;
                case 3: checkEligibility();         break;
                case 4: generateBeneficiaryList();  break;
                case 5: searchResident();           break;
                case 6: findMostInNeed();           break;
                case 7:
                    running = false;
                    System.out.println("\n  Thank you for using the system. Goodbye!\n");
                    break;
                default:
                    System.out.println("  [!] Invalid choice. Please enter 1-7.");
            }
        }
    }

    // ==================== 1. Register Resident ====================

    /**
     * Collects resident information from the user and adds a new Resident.
     */
    private void registerResident() {
        System.out.println("\n============= REGISTER NEW RESIDENT ==============");

        System.out.print("  Full Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("  [!] Name cannot be empty.");
            return;
        }

        System.out.print("  Age: ");
        int age = readInt();
        if (age <= 0 || age > 120) {
            System.out.println("  [!] Invalid age.");
            return;
        }

        System.out.print("  Household Income (monthly): ");
        double income = readDouble();
        if (income < 0) {
            System.out.println("  [!] Income cannot be negative.");
            return;
        }

        System.out.print("  Family Size: ");
        int familySize = readInt();
        if (familySize <= 0) {
            System.out.println("  [!] Family size must be at least 1.");
            return;
        }

        System.out.print("  Occupation: ");
        String occupation = scanner.nextLine().trim();

        boolean student           = readYesNo("  Student (yes/no): ");
        boolean unemployed        = readYesNo("  Unemployed (yes/no): ");
        boolean soloParent        = readYesNo("  Solo Parent (yes/no): ");
        boolean seniorCitizen     = readYesNo("  Senior Citizen (yes/no): ");
        boolean pwd               = readYesNo("  PWD (yes/no): ");
        boolean dependentChildren = readYesNo("  Has Dependent Children (yes/no): ");

        System.out.print("  Academic Average (0 if N/A): ");
        double academicAvg = readDouble();

        boolean childOfSoloParent = readYesNo("  Child of Solo Parent (yes/no): ");
        boolean childOfOfwOwwa    = readYesNo("  Child of OFW/OWWA Member (yes/no): ");

        Resident resident = new Resident(nextId, name, age, income, familySize, occupation,
                student, unemployed, soloParent, seniorCitizen, pwd,
                dependentChildren, academicAvg, childOfSoloParent, childOfOfwOwwa);

        residents.add(resident);
        System.out.println("\n  [+] Resident registered successfully! ID: " + nextId);
        nextId++;
    }

    // ==================== 2. View Residents ====================

    /**
     * Displays all registered residents in a compact table.
     */
    private void viewResidents() {
        System.out.println("\n===================== ALL REGISTERED RESIDENTS =====================");

        if (residents.isEmpty()) {
            System.out.println("  No residents registered yet.");
            return;
        }

        display.displayResidentTable(residents);
    }

    // ==================== 3. Check Eligibility ====================

    /**
     * Checks which programs a specific resident is eligible/ineligible for.
     * Uses Shell Sort to determine the resident's priority rank in each eligible program.
     *
     * How Shell Sort is used here:
     * - For each program the resident qualifies for, we gather ALL qualified residents.
     * - We sort them by household income (ascending) using Shell Sort.
     * - We then find where this resident falls in the sorted list = their priority rank.
     * - Lower income = higher priority (rank 1 = most in need).
     */
    private void checkEligibility() {
        System.out.println("\n==================== CHECK ELIGIBILITY ====================");

        if (residents.isEmpty()) {
            System.out.println("  No residents registered yet.");
            return;
        }

        System.out.print("  Enter Resident ID: ");
        int id = readInt();

        Resident resident = findById(id);
        if (resident == null) {
            System.out.println("  [!] Resident not found.");
            return;
        }

        System.out.println("\n  Resident: " + resident.getFullName() + " (ID: " + id + ")");

        // Get eligible programs and display as table with priority rank
        ArrayList<String> eligible = eligibility.getEligiblePrograms(resident);
        System.out.println("\n  ELIGIBLE PROGRAMS (priority rank via Shell Sort):");

        if (eligible.isEmpty()) {
            System.out.println("  None");
        } else {
            System.out.println("  +-----+------------------------------------------------------+------+-------+");
            System.out.println("  | No. | Program                                              | Rank | Total |");
            System.out.println("  +-----+------------------------------------------------------+------+-------+");

            for (int i = 0; i < eligible.size(); i++) {
                String programName = eligible.get(i);

                // Get the program number so we can find all qualified residents
                int programNumber = eligibility.getProgramNumber(programName);

                // Get all qualified residents for this program
                ArrayList<Resident> qualified = eligibility.getQualifiedResidents(residents, programNumber);

                // Sort by income ascending using Shell Sort (lowest income = highest priority)
                ArrayList<Resident> sorted = sorter.shellSort(qualified, (r1, r2) -> Double.compare(r1.getHouseholdIncome(), r2.getHouseholdIncome()));

                // Find this resident's rank in the sorted list
                int rank = 1;
                for (int j = 0; j < sorted.size(); j++) {
                    if (sorted.get(j).getResidentId() == id) {
                        rank = j + 1; // rank is 1-based
                        break;
                    }
                }

                System.out.printf("  | %-3d | %-52s | %-4d | %-5d |%n",
                        (i + 1), display.truncate(programName, 52), rank, sorted.size());
            }

            System.out.println("  +-----+------------------------------------------------------+------+-------+");
        }

        // Check Bagong Pilipinas priority
        if (eligibility.isEligibleBagongPilipinas(resident) && eligibility.hasBagongPilipinasPriority(resident)) {
            System.out.println("  * Priority for Bagong Pilipinas Scholarship (PWD/Child of Solo Parent)");
        }

        // Get ineligible programs as a simple numbered table
        ArrayList<String> ineligible = eligibility.getIneligiblePrograms(resident);
        System.out.println("\n  INELIGIBLE PROGRAMS:");

        if (ineligible.isEmpty()) {
            System.out.println("  None");
        } else {
            System.out.println("  +-----+------------------------------------------------------+");
            System.out.println("  | No. | Program                                              |");
            System.out.println("  +-----+------------------------------------------------------+");
            for (int i = 0; i < ineligible.size(); i++) {
                System.out.printf("  | %-3d | %-52s |%n", (i + 1), display.truncate(ineligible.get(i), 52));
            }
            System.out.println("  +-----+------------------------------------------------------+");
        }
    }

    // ==================== 4. Generate Beneficiary List ====================

    /**
     * Lets the user pick a program, choose a sorting criteria, then displays
     * all eligible residents sorted using Shell Sort.
     */
    private void generateBeneficiaryList() {
        System.out.println("\n============ GENERATE BENEFICIARY LIST =============");

        if (residents.isEmpty()) {
            System.out.println("  No residents registered yet.");
            return;
        }

        display.displayProgramMenu(eligibility);
        System.out.print("  Select program (1-11): ");
        int choice = readInt();

        if (choice < 1 || choice > 11) {
            System.out.println("  [!] Invalid program number.");
            return;
        }

        // Get all qualified residents for the selected program
        ArrayList<Resident> qualified = eligibility.getQualifiedResidents(residents, choice);

        System.out.println("\n  Program: " + eligibility.getProgramName(choice));
        System.out.println("  Qualified Residents: " + qualified.size());

        if (qualified.isEmpty()) {
            System.out.println("  No qualified residents for this program.");
            return;
        }

        // Ask user how to sort the beneficiary list
        System.out.println("\n  Sort By:");
        System.out.println("  [1] Household Income (ascending)");
        System.out.println("  [2] Age (ascending)");
        System.out.println("  [3] Family Size (descending)");
        System.out.println("  [4] Name (alphabetical)");
        System.out.print("  Enter sort choice: ");
        int sortChoice = readInt();

        ArrayList<Resident> sorted;
        String sortLabel;

        switch (sortChoice) {
            case 1:
                sorted = sorter.shellSort(qualified, (r1, r2) -> Double.compare(r1.getHouseholdIncome(), r2.getHouseholdIncome()));
                sortLabel = "Household Income (Ascending)";
                break;
            case 2:
                sorted = sorter.shellSort(qualified, (r1, r2) -> Integer.compare(r1.getAge(), r2.getAge()));
                sortLabel = "Age (Ascending)";
                break;
            case 3:
                sorted = sorter.shellSort(qualified, (r1, r2) -> Integer.compare(r2.getFamilySize(), r1.getFamilySize())); // Descending
                sortLabel = "Family Size (Descending)";
                break;
            case 4:
                sorted = sorter.shellSort(qualified, (r1, r2) -> r1.getFullName().compareToIgnoreCase(r2.getFullName()));
                sortLabel = "Name (Alphabetical)";
                break;
            default:
                System.out.println("  [!] Invalid sort option.");
                return;
        }

        System.out.println("\n  Sorted by: " + sortLabel + " (using Shell Sort)\n");
        display.displayBeneficiaryTable(sorted);
    }

    // ==================== 5. Search Resident ====================

    /**
     * Searches for a resident by ID or full name.
     */
    private void searchResident() {
        System.out.println("\n==================== SEARCH RESIDENT ====================");

        if (residents.isEmpty()) {
            System.out.println("  No residents registered yet.");
            return;
        }

        System.out.println("  Search by:");
        System.out.println("  [1] Resident ID");
        System.out.println("  [2] Full Name");
        System.out.print("  Enter choice: ");
        int choice = readInt();

        if (choice == 1) {
            System.out.print("  Enter Resident ID: ");
            int id = readInt();
            Resident found = findById(id);

            if (found != null) {
                System.out.println("\n  [+] Resident found!");
                display.displayResidentDetail(found);
            } else {
                System.out.println("  [!] No resident found with ID " + id);
            }

        } else if (choice == 2) {
            System.out.print("  Enter Full Name: ");
            String name = scanner.nextLine().trim();
            ArrayList<Resident> results = findByName(name);

            if (results.isEmpty()) {
                System.out.println("  [!] No resident found matching \"" + name + "\"");
            } else {
                System.out.println("\n  [+] Found " + results.size() + " result(s):");
                for (Resident r : results) {
                    display.displayResidentDetail(r);
                }
            }
        } else {
            System.out.println("  [!] Invalid choice.");
        }
    }

    // ==================== 6. Find Most In-Need Residents ====================

    /**
     * Ranks ALL residents by their "need score" using Shell Sort.
     * The need score is computed based on income, family size, vulnerability flags,
     * and number of eligible programs. Higher score = more in need.
     *
     * Shell Sort is used here to sort all residents by need score in descending order,
     * so the most in-need residents appear at the top.
     */
    private void findMostInNeed() {
        System.out.println("\n============= MOST IN-NEED RESIDENTS ===============");

        if (residents.isEmpty()) {
            System.out.println("  No residents registered yet.");
            return;
        }

        // Sort all residents by need score (descending) using Shell Sort
        ArrayList<Resident> sorted = sorter.shellSort(residents, (r1, r2) -> {
            int score1 = eligibility.calculateNeedScore(r1);
            int score2 = eligibility.calculateNeedScore(r2);
            return Integer.compare(score2, score1); // Descending
        });

        System.out.println("  Residents ranked by need score (highest = most in need)");
        System.out.println("  Score factors: income, family size, PWD, senior, solo parent,");
        System.out.println("  unemployed, dependent children, eligible program count\n");

        display.displayNeedScoreTable(sorted, eligibility);
    }

    // ==================== Helper Methods ====================

    /** Find a resident by their ID. */
    private Resident findById(int id) {
        for (Resident r : residents) {
            if (r.getResidentId() == id) {
                return r;
            }
        }
        return null;
    }

    /** Find residents whose name contains the search string (case-insensitive). */
    private ArrayList<Resident> findByName(String name) {
        ArrayList<Resident> results = new ArrayList<>();
        for (Resident r : residents) {
            if (r.getFullName().toLowerCase().contains(name.toLowerCase())) {
                results.add(r);
            }
        }
        return results;
    }

    // ==================== Input Validation ====================

    /** Read an integer safely. Returns -1 if input is invalid. */
    private int readInt() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("  [!] Please enter a valid number.");
            return -1;
        }
    }

    /** Read a double safely. Returns -1 if input is invalid. */
    private double readDouble() {
        try {
            String input = scanner.nextLine().trim();
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("  [!] Please enter a valid number.");
            return -1;
        }
    }

    /** Read a yes/no input. Returns true for yes, false for anything else. */
    private boolean readYesNo(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("yes") || input.equals("y");
    }
}
