package com.bbes;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * MenuService - handles all CLI menu interactions and user input.
 * This class ties together Resident, EligibilityService, and ShellSorter.
 */
public class MenuService {

    // Core components
    private ArrayList<Resident> residents;       // stores all registered residents
    private EligibilityService eligibility;      // checks program eligibility
    private ShellSorter sorter;                  // sorts beneficiary lists using Shell Sort
    private Scanner scanner;                     // reads user input
    private int nextId;                          // auto-increment resident ID

    // ==================== Constructor ====================
    public MenuService() {
        residents = new ArrayList<>();
        eligibility = new EligibilityService();
        sorter = new ShellSorter();
        scanner = new Scanner(System.in);
        nextId = 1;
    }

    // ==================== Main Menu ====================

    /**
     * Displays the main menu and handles user choices.
     * Loops until the user chooses to exit.
     */
    public void start() {
        loadSampleData(); // pre-load 10 sample residents for testing
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
            System.out.println("  [5] Sort Beneficiary List");
            System.out.println("  [6] Search Resident");
            System.out.println("  [7] Find Most In-Need Residents");
            System.out.println("  [8] Exit");
            System.out.println("==================================================");
            System.out.print("  Enter choice: ");

            int choice = readInt();

            switch (choice) {
                case 1: registerResident();         break;
                case 2: viewResidents();            break;
                case 3: checkEligibility();         break;
                case 4: generateBeneficiaryList();  break;
                case 5: sortBeneficiaryList();      break;
                case 6: searchResident();           break;
                case 7: findMostInNeed();           break;
                case 8:
                    running = false;
                    System.out.println("\n  Thank you for using the system. Goodbye!\n");
                    break;
                default:
                    System.out.println("  [!] Invalid choice. Please enter 1-8.");
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
        System.out.println("\n  [✓] Resident registered successfully! ID: " + nextId);
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

        displayResidentTable(residents);
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
                ArrayList<Resident> sorted = sorter.sortByIncomeAscending(qualified);

                // Find this resident's rank in the sorted list
                int rank = 1;
                for (int j = 0; j < sorted.size(); j++) {
                    if (sorted.get(j).getResidentId() == id) {
                        rank = j + 1; // rank is 1-based
                        break;
                    }
                }

                System.out.printf("  | %-3d | %-52s | %-4d | %-5d |%n",
                        (i + 1), truncate(programName, 52), rank, sorted.size());
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
                System.out.printf("  | %-3d | %-52s |%n", (i + 1), truncate(ineligible.get(i), 52));
            }
            System.out.println("  +-----+------------------------------------------------------+");
        }
    }

    // ==================== 4. Generate Beneficiary List ====================

    /**
     * Lets the user pick a program, then displays all eligible residents.
     * Results are automatically sorted by household income (ascending) using Shell Sort.
     */
    private void generateBeneficiaryList() {
        System.out.println("\n============ GENERATE BENEFICIARY LIST =============");

        if (residents.isEmpty()) {
            System.out.println("  No residents registered yet.");
            return;
        }

        displayProgramMenu();
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

        // Sort by income ascending using Shell Sort (prioritize lowest income)
        qualified = sorter.sortByIncomeAscending(qualified);

        System.out.println("\n  (Sorted by household income, lowest first - via Shell Sort)\n");
        displayBeneficiaryTable(qualified);
    }

    // ==================== 5. Sort Beneficiary List ====================

    /**
     * Lets the user pick a program, then choose a sorting criteria.
     * Uses Shell Sort for all sorting operations.
     */
    private void sortBeneficiaryList() {
        System.out.println("\n============= SORT BENEFICIARY LIST ================");

        if (residents.isEmpty()) {
            System.out.println("  No residents registered yet.");
            return;
        }

        displayProgramMenu();
        System.out.print("  Select program (1-11): ");
        int programChoice = readInt();

        if (programChoice < 1 || programChoice > 11) {
            System.out.println("  [!] Invalid program number.");
            return;
        }

        ArrayList<Resident> qualified = eligibility.getQualifiedResidents(residents, programChoice);

        if (qualified.isEmpty()) {
            System.out.println("  No qualified residents for this program.");
            return;
        }

        System.out.println("\n  Program: " + eligibility.getProgramName(programChoice));
        System.out.println("  Qualified Residents: " + qualified.size());

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
                sorted = sorter.sortByIncomeAscending(qualified);
                sortLabel = "Household Income (Ascending)";
                break;
            case 2:
                sorted = sorter.sortByAgeAscending(qualified);
                sortLabel = "Age (Ascending)";
                break;
            case 3:
                sorted = sorter.sortByFamilySizeDescending(qualified);
                sortLabel = "Family Size (Descending)";
                break;
            case 4:
                sorted = sorter.sortByNameAlphabetical(qualified);
                sortLabel = "Name (Alphabetical)";
                break;
            default:
                System.out.println("  [!] Invalid sort option.");
                return;
        }

        System.out.println("\n  Sorted by: " + sortLabel + " (using Shell Sort)\n");
        displayBeneficiaryTable(sorted);
    }

    // ==================== 6. Search Resident ====================

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
                displayResidentDetail(found);
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
                    displayResidentDetail(r);
                }
            }
        } else {
            System.out.println("  [!] Invalid choice.");
        }
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

    /** Display the list of all available programs. */
    private void displayProgramMenu() {
        System.out.println("\n  Available Programs:");
        String[] programs = eligibility.getAllProgramNames();
        for (int i = 0; i < programs.length; i++) {
            System.out.println("  [" + (i + 1) + "] " + programs[i]);
        }
    }

    /** Display a compact table of all residents with key info and flags. */
    private void displayResidentTable(ArrayList<Resident> list) {
        System.out.println("  +-----+-----------------------+-----+--------------+------+-----------------+-----+-----+-----+-----+-----+");
        System.out.println("  | ID  | Name                  | Age | Income (Php) | Fam. | Occupation      | Stu | Uem | SP  | SC  | PWD |");
        System.out.println("  +-----+-----------------------+-----+--------------+------+-----------------+-----+-----+-----+-----+-----+");

        for (Resident r : list) {
            System.out.printf("  | %-3d | %-21s | %-3d | %12s | %-4d | %-15s | %-3s | %-3s | %-3s | %-3s | %-3s |%n",
                    r.getResidentId(),
                    truncate(r.getFullName(), 21),
                    r.getAge(),
                    String.format("%,.2f", r.getHouseholdIncome()),
                    r.getFamilySize(),
                    truncate(r.getOccupation(), 15),
                    yn(r.isStudent()), yn(r.isUnemployed()), yn(r.isSoloParent()),
                    yn(r.isSeniorCitizen()), yn(r.isPwd()));
        }

        System.out.println("  +-----+-----------------------+-----+--------------+------+-----------------+-----+-----+-----+-----+-----+");
        System.out.println("  Legend: Stu=Student | Uem=Unemployed | SP=Solo Parent | SC=Senior Citizen | PWD=PWD");
        System.out.println("  Total: " + list.size() + " resident(s)");
    }

    /** Display a table of beneficiaries with key details (for program lists). */
    private void displayBeneficiaryTable(ArrayList<Resident> list) {
        System.out.println("  +------+-----+--------------------------+------+--------------+-----------+");
        System.out.println("  | Rank | ID  | Name                     | Age  | Income (Php) | Fam. Size |");
        System.out.println("  +------+-----+--------------------------+------+--------------+-----------+");

        for (int i = 0; i < list.size(); i++) {
            Resident r = list.get(i);
            System.out.printf("  | %-4d | %-3d | %-24s | %-4d | %12s | %-9d |%n",
                    (i + 1),
                    r.getResidentId(),
                    truncate(r.getFullName(), 24),
                    r.getAge(),
                    String.format("%,.2f", r.getHouseholdIncome()),
                    r.getFamilySize());
        }

        System.out.println("  +------+-----+--------------------------+------+--------------+-----------+");
        System.out.println("  Total: " + list.size() + " beneficiar" + (list.size() == 1 ? "y" : "ies"));
    }

    /**
     * Display a compact detail view of a single resident.
     * Used by Search Resident to show full details in a structured format.
     */
    private void displayResidentDetail(Resident r) {
        System.out.println("  +--------------------+----------------------------+");
        System.out.printf("  | %-18s | %-26s |%n", "Resident ID", r.getResidentId());
        System.out.printf("  | %-18s | %-26s |%n", "Full Name", r.getFullName());
        System.out.printf("  | %-18s | %-26s |%n", "Age", r.getAge());
        System.out.printf("  | %-18s | %-26s |%n", "Household Income", "Php " + String.format("%,.2f", r.getHouseholdIncome()));
        System.out.printf("  | %-18s | %-26s |%n", "Family Size", r.getFamilySize());
        System.out.printf("  | %-18s | %-26s |%n", "Occupation", r.getOccupation());
        System.out.printf("  | %-18s | %-26s |%n", "Student", yn(r.isStudent()));
        System.out.printf("  | %-18s | %-26s |%n", "Unemployed", yn(r.isUnemployed()));
        System.out.printf("  | %-18s | %-26s |%n", "Solo Parent", yn(r.isSoloParent()));
        System.out.printf("  | %-18s | %-26s |%n", "Senior Citizen", yn(r.isSeniorCitizen()));
        System.out.printf("  | %-18s | %-26s |%n", "PWD", yn(r.isPwd()));
        System.out.printf("  | %-18s | %-26s |%n", "Dependent Children", yn(r.hasDependentChildren()));
        System.out.printf("  | %-18s | %-26s |%n", "Academic Average", r.getAcademicAverage());
        System.out.printf("  | %-18s | %-26s |%n", "Child of Solo Par.", yn(r.isChildOfSoloParent()));
        System.out.printf("  | %-18s | %-26s |%n", "Child of OFW/OWWA", yn(r.isChildOfOfwOwwa()));
        System.out.println("  +--------------------+----------------------------+");
    }

    /** Truncate a string to a maximum length. */
    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    /** Helper: converts boolean to "Y" or "N" for compact display. */
    private String yn(boolean value) {
        return value ? "Y" : "N";
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

    // ==================== Sample Test Data ====================

    /**
     * Pre-loads 10 sample residents for testing and demonstration.
     * These residents cover a variety of eligibility scenarios.
     */
    private void loadSampleData() {
        // Resident 1: Low-income mother with dependent children (eligible for 4Ps, AKAP)
        residents.add(new Resident(nextId++, "Maria Santos", 34, 8000, 6, "Vendor",
                false, false, false, false, false, true, 0, false, false));

        // Resident 2: Student from low-income family (eligible for SPES, Educational Cash)
        residents.add(new Resident(nextId++, "Juan Dela Cruz", 19, 15000, 5, "Student",
                true, false, false, false, false, false, 88, false, false));

        // Resident 3: Unemployed adult (eligible for TUPAD)
        residents.add(new Resident(nextId++, "Pedro Reyes", 45, 10000, 4, "None",
                false, true, false, false, false, true, 0, false, false));

        // Resident 4: Solo parent (eligible for Solo Parent Benefits)
        residents.add(new Resident(nextId++, "Ana Garcia", 38, 18000, 3, "Teacher",
                false, false, true, false, false, true, 0, false, false));

        // Resident 5: Senior citizen (eligible for Senior Pension)
        residents.add(new Resident(nextId++, "Lolo Ernesto Bautista", 72, 5000, 2, "Retired",
                false, false, false, true, false, false, 0, false, false));

        // Resident 6: PWD student, child of solo parent (eligible for PWD Assistance, Bagong Pilipinas with priority)
        residents.add(new Resident(nextId++, "Carlo Mendoza", 20, 12000, 4, "Student",
                true, false, false, false, true, false, 92, true, false));

        // Resident 7: Farmer (eligible for Farmer Assistance)
        residents.add(new Resident(nextId++, "Mang Tonyo Villanueva", 55, 9500, 5, "Rice Farmer",
                false, false, false, false, false, true, 0, false, false));

        // Resident 8: Student child of OFW (eligible for OWWA Scholarship)
        residents.add(new Resident(nextId++, "Lisa Ramos", 17, 25000, 4, "Student",
                true, false, false, false, false, false, 90, false, true));

        // Resident 9: Low-income student with high grades (eligible for Bagong Pilipinas, SPES, Educational Cash)
        residents.add(new Resident(nextId++, "Mark Rivera", 21, 11000, 7, "Student",
                true, false, false, false, false, false, 95, false, false));

        // Resident 10: Senior PWD solo parent (eligible for multiple programs)
        residents.add(new Resident(nextId++, "Lola Carmen Aquino", 65, 7000, 3, "None",
                false, true, true, true, true, true, 0, false, false));
    }
}
