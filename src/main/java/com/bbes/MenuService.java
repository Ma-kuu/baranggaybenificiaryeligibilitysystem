package com.bbes;

import java.util.Arrays;
import java.util.Scanner;

// Handles the CLI menu, user input, and all menu actions.
public class MenuService {

    private static final int MAX_RESIDENTS = 200;

    // core components
    private Resident[] residents;
    private int count;
    private EligibilityService eligibility;
    private ShellSorter sorter;
    private DisplayService display;
    private Scanner scanner;
    private int nextId;

    // constructor
    public MenuService() {
        residents  = new Resident[MAX_RESIDENTS];
        count      = 0;
        eligibility = new EligibilityService();
        sorter     = new ShellSorter();
        scanner    = new Scanner(System.in);
        display    = new DisplayService(scanner);
        nextId     = 1;
    }

    // main menu
    public void start() {
        Resident[] sample = SampleData.loadSampleResidents();
        System.arraycopy(sample, 0, residents, 0, sample.length);
        count  = sample.length;
        nextId = SampleData.getNextIdAfterSample();

        System.out.println("\n=====================================================");
        System.out.println("   BARANGAY BENEFICIARY PRIORITIZATION SYSTEM");
        System.out.println("=====================================================");
        System.out.println(" Sample data loaded: " + count + " residents registered.\n");

        boolean running = true;

        while (running) {
            System.out.println("\n=================== MAIN MENU ===================");
            System.out.println("  [1] Register Resident");
            System.out.println("  [2] View Residents");
            System.out.println("  [3] Check Eligibility");
            System.out.println("  [4] Generate Beneficiary List");
            System.out.println("  [5] Search Resident");
            System.out.println("  [6] Find Most In-Need Residents");
            System.out.println("  [7] Delete Resident");
            System.out.println("  [8] Exit");
            System.out.println("==================================================");
            System.out.print("  Enter choice: ");

            int choice = readInt();

            switch (choice) {
                case 1: registerResident();        break;
                case 2: viewResidents();           break;
                case 3: checkEligibility();        break;
                case 4: generateBeneficiaryList(); break;
                case 5: searchResident();          break;
                case 6: findMostInNeed();          break;
                case 7: deleteResident();          break;
                case 8:
                    running = false;
                    System.out.println("\n  Thank you for using the system. Goodbye!\n");
                    break;
                default:
                    System.out.println("  [!] Invalid choice. Please enter 1-8.");
            }
        }
    }

    // 1. register resident
    private void registerResident() {
        System.out.println("\n============= REGISTER NEW RESIDENT ==============");

        if (count >= MAX_RESIDENTS) {
            System.out.println("  [!] Resident list is full (max " + MAX_RESIDENTS + ").");
            return;
        }

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

        residents[count] = new Resident(nextId, name, age, income, familySize, occupation,
                student, unemployed, soloParent, seniorCitizen, pwd,
                dependentChildren, academicAvg, childOfSoloParent, childOfOfwOwwa);
        count++;

        System.out.println("\n  [+] Resident registered successfully! ID: " + nextId);
        nextId++;
    }

    // 2. view all residents
    private void viewResidents() {
        System.out.println("\n===================== ALL REGISTERED RESIDENTS =====================");

        if (count == 0) {
            System.out.println("  No residents registered yet.");
            return;
        }

        // pass only the filled portion of the array
        display.displayResidentTable(Arrays.copyOf(residents, count));
    }

    // 3. check eligibility for a specific resident
    private void checkEligibility() {
        System.out.println("\n==================== CHECK ELIGIBILITY ====================");

        if (count == 0) {
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

        // get eligible programs and display with priority rank (Shell Sort used here)
        String[] eligible = eligibility.getEligiblePrograms(resident);
        System.out.println("\n  ELIGIBLE PROGRAMS (priority rank via Shell Sort):");

        if (eligible.length == 0) {
            System.out.println("  None");
        } else {
            System.out.println("  +-----+------------------------------------------------------+------+-------+");
            System.out.println("  | No. | Program                                              | Rank | Total |");
            System.out.println("  +-----+------------------------------------------------------+------+-------+");

            for (int i = 0; i < eligible.length; i++) {
                String programName  = eligible[i];
                int programNumber   = eligibility.getProgramNumber(programName);

                // get all qualified residents and sort by income using Shell Sort
                Resident[] qualified = eligibility.getQualifiedResidents(residents, count, programNumber);
                Resident[] sorted    = sorter.shellSort(qualified, (r1, r2) ->
                        Double.compare(r1.getHouseholdIncome(), r2.getHouseholdIncome()));

                // find this resident's rank in the sorted array
                int rank = 1;
                for (int j = 0; j < sorted.length; j++) {
                    if (sorted[j].getResidentId() == id) {
                        rank = j + 1;
                        break;
                    }
                }

                System.out.printf("  | %-3d | %-52s | %-4d | %-5d |%n",
                        (i + 1), display.truncate(programName, 52), rank, sorted.length);
            }

            System.out.println("  +-----+------------------------------------------------------+------+-------+");
        }

        if (eligibility.isEligibleBagongPilipinas(resident) && eligibility.hasBagongPilipinasPriority(resident)) {
            System.out.println("  * Priority for Bagong Pilipinas Scholarship (PWD/Child of Solo Parent)");
        }

        // ineligible programs
        String[] ineligible = eligibility.getIneligiblePrograms(resident);
        System.out.println("\n  INELIGIBLE PROGRAMS:");

        if (ineligible.length == 0) {
            System.out.println("  None");
        } else {
            System.out.println("  +-----+------------------------------------------------------+");
            System.out.println("  | No. | Program                                              |");
            System.out.println("  +-----+------------------------------------------------------+");
            for (int i = 0; i < ineligible.length; i++) {
                System.out.printf("  | %-3d | %-52s |%n", (i + 1), display.truncate(ineligible[i], 52));
            }
            System.out.println("  +-----+------------------------------------------------------+");
        }
    }

    // 4. generate sorted beneficiary list for a program
    private void generateBeneficiaryList() {
        System.out.println("\n============ GENERATE BENEFICIARY LIST =============");

        if (count == 0) {
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

        Resident[] qualified = eligibility.getQualifiedResidents(residents, count, choice);

        System.out.println("\n  Program: " + eligibility.getProgramName(choice));
        System.out.println("  Qualified Residents: " + qualified.length);

        if (qualified.length == 0) {
            System.out.println("  No qualified residents for this program.");
            return;
        }

        System.out.println("\n  Sort By:");
        System.out.println("  [1] Household Income (ascending)");
        System.out.println("  [2] Age (ascending)");
        System.out.println("  [3] Family Size (descending)");
        System.out.println("  [4] Name (alphabetical)");
        System.out.print("  Enter sort choice: ");
        int sortChoice = readInt();

        Resident[] sorted;
        String sortLabel;

        switch (sortChoice) {
            case 1:
                sorted    = sorter.shellSort(qualified, (r1, r2) -> Double.compare(r1.getHouseholdIncome(), r2.getHouseholdIncome()));
                sortLabel = "Household Income (Ascending)";
                break;
            case 2:
                sorted    = sorter.shellSort(qualified, (r1, r2) -> Integer.compare(r1.getAge(), r2.getAge()));
                sortLabel = "Age (Ascending)";
                break;
            case 3:
                sorted    = sorter.shellSort(qualified, (r1, r2) -> Integer.compare(r2.getFamilySize(), r1.getFamilySize()));
                sortLabel = "Family Size (Descending)";
                break;
            case 4:
                sorted    = sorter.shellSort(qualified, (r1, r2) -> r1.getFullName().compareToIgnoreCase(r2.getFullName()));
                sortLabel = "Name (Alphabetical)";
                break;
            default:
                System.out.println("  [!] Invalid sort option.");
                return;
        }

        System.out.println("\n  Sorted by: " + sortLabel + " (using Shell Sort)\n");
        display.displayBeneficiaryTable(sorted);
    }

    // 5. search resident by ID or name
    private void searchResident() {
        System.out.println("\n==================== SEARCH RESIDENT ====================");

        if (count == 0) {
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
            String name    = scanner.nextLine().trim();
            Resident[] results = findByName(name);

            if (results.length == 0) {
                System.out.println("  [!] No resident found matching \"" + name + "\"");
            } else {
                System.out.println("\n  [+] Found " + results.length + " result(s):");
                for (Resident r : results) {
                    display.displayResidentDetail(r);
                }
            }
        } else {
            System.out.println("  [!] Invalid choice.");
        }
    }

    // 6. rank all residents by need score
    private void findMostInNeed() {
        System.out.println("\n============= MOST IN-NEED RESIDENTS ===============");

        if (count == 0) {
            System.out.println("  No residents registered yet.");
            return;
        }

        // sort all residents by need score descending using Shell Sort
        Resident[] sorted = sorter.shellSort(Arrays.copyOf(residents, count), (r1, r2) -> {
            int score1 = eligibility.calculateNeedScore(r1);
            int score2 = eligibility.calculateNeedScore(r2);
            return Integer.compare(score2, score1); // descending
        });

        System.out.println("  Residents ranked by need score (highest = most in need)");
        System.out.println("  Score factors: income, family size, PWD, senior, solo parent,");
        System.out.println("  unemployed, dependent children, eligible program count\n");

        display.displayNeedScoreTable(sorted, eligibility);
    }

    // 7. delete a resident by ID
    private void deleteResident() {
        System.out.println("\n================ DELETE RESIDENT ================");

        if (count == 0) {
            System.out.println("  No residents registered yet.");
            return;
        }

        System.out.print("  Enter Resident ID to delete: ");
        int id = readInt();

        // find the index of the resident to delete
        int indexToDelete = -1;
        for (int i = 0; i < count; i++) {
            if (residents[i].getResidentId() == id) {
                indexToDelete = i;
                break;
            }
        }

        if (indexToDelete == -1) {
            System.out.println("  [!] Resident not found.");
            return;
        }

        Resident found = residents[indexToDelete];
        System.out.println("  Found: " + found.getFullName()
                + " | Age: " + found.getAge()
                + " | Income: Php " + String.format("%,.2f", found.getHouseholdIncome()));
        boolean confirm = readYesNo("  Confirm delete? (yes/no): ");

        if (confirm) {
            // shift all elements left to fill the gap
            for (int i = indexToDelete; i < count - 1; i++) {
                residents[i] = residents[i + 1];
            }
            residents[count - 1] = null; // clear the last slot
            count--;
            System.out.println("  [+] Resident deleted successfully.");
        } else {
            System.out.println("  Cancelled.");
        }
    }

    // helper methods

    private Resident findById(int id) {
        for (int i = 0; i < count; i++) {
            if (residents[i].getResidentId() == id) return residents[i];
        }
        return null;
    }

    private Resident[] findByName(String name) {
        Resident[] results = new Resident[count];
        int r = 0;
        for (int i = 0; i < count; i++) {
            if (residents[i].getFullName().toLowerCase().contains(name.toLowerCase())) {
                results[r++] = residents[i];
            }
        }
        return Arrays.copyOf(results, r);
    }

    // input validation helpers

    private int readInt() {
        try {
            String input = scanner.nextLine().trim();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("  [!] Please enter a valid number.");
            return -1;
        }
    }

    private double readDouble() {
        try {
            String input = scanner.nextLine().trim();
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("  [!] Please enter a valid number.");
            return -1;
        }
    }

    private boolean readYesNo(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("yes") || input.equals("y");
    }
}
