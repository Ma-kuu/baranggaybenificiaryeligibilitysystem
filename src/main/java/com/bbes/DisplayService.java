package com.bbes;

import java.util.Scanner;

/**
 * DisplayService - handles all CLI display/printing operations.
 * Keeps table formatting and output methods separate from menu logic.
 * Supports pagination for tables with more than 15 entries.
 */
public class DisplayService {

    private static final int PAGE_SIZE = 15;
    private Scanner scanner;

    public DisplayService(Scanner scanner) {
        this.scanner = scanner;
    }

    // paginated table: all residents
    public void displayResidentTable(Resident[] list) {
        int total = list.length;
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        int currentPage = 1;

        while (true) {
            int start = (currentPage - 1) * PAGE_SIZE;
            int end   = Math.min(start + PAGE_SIZE, total);

            System.out.println("  +-----+-----------------------+-----+--------------+------+-----------------+-----+-----+-----+-----+-----+");
            System.out.println("  | ID  | Name                  | Age | Income (Php) | Fam. | Occupation      | Stu | Uem | SP  | SC  | PWD |");
            System.out.println("  +-----+-----------------------+-----+--------------+------+-----------------+-----+-----+-----+-----+-----+");

            for (int i = start; i < end; i++) {
                Resident r = list[i];
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
            System.out.println("  Total: " + total + " resident(s)");

            if (totalPages <= 1) break;
            currentPage = getNextPage(currentPage, totalPages);
            if (currentPage == -1) break;
        }
    }

    // paginated table: beneficiary list
    public void displayBeneficiaryTable(Resident[] list) {
        int total = list.length;
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        int currentPage = 1;

        while (true) {
            int start = (currentPage - 1) * PAGE_SIZE;
            int end   = Math.min(start + PAGE_SIZE, total);

            System.out.println("  +------+-----+--------------------------+------+--------------+-----------+");
            System.out.println("  | Rank | ID  | Name                     | Age  | Income (Php) | Fam. Size |");
            System.out.println("  +------+-----+--------------------------+------+--------------+-----------+");

            for (int i = start; i < end; i++) {
                Resident r = list[i];
                System.out.printf("  | %-4d | %-3d | %-24s | %-4d | %12s | %-9d |%n",
                        (i + 1),
                        r.getResidentId(),
                        truncate(r.getFullName(), 24),
                        r.getAge(),
                        String.format("%,.2f", r.getHouseholdIncome()),
                        r.getFamilySize());
            }

            System.out.println("  +------+-----+--------------------------+------+--------------+-----------+");
            System.out.println("  Total: " + total + " beneficiar" + (total == 1 ? "y" : "ies"));

            if (totalPages <= 1) break;
            currentPage = getNextPage(currentPage, totalPages);
            if (currentPage == -1) break;
        }
    }

    // paginated table: need score ranking
    public void displayNeedScoreTable(Resident[] list, EligibilityService eligibility) {
        int total = list.length;
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        int currentPage = 1;

        while (true) {
            int start = (currentPage - 1) * PAGE_SIZE;
            int end   = Math.min(start + PAGE_SIZE, total);

            System.out.println("  +------+-----+--------------------------+------+--------------+-----------+-------+");
            System.out.println("  | Rank | ID  | Name                     | Age  | Income (Php) | Fam. Size | Score |");
            System.out.println("  +------+-----+--------------------------+------+--------------+-----------+-------+");

            for (int i = start; i < end; i++) {
                Resident r = list[i];
                int score = eligibility.calculateNeedScore(r);
                System.out.printf("  | %-4d | %-3d | %-24s | %-4d | %12s | %-9d | %-5d |%n",
                        (i + 1),
                        r.getResidentId(),
                        truncate(r.getFullName(), 24),
                        r.getAge(),
                        String.format("%,.2f", r.getHouseholdIncome()),
                        r.getFamilySize(),
                        score);
            }

            System.out.println("  +------+-----+--------------------------+------+--------------+-----------+-------+");
            System.out.println("  Total: " + total + " resident(s)");

            if (totalPages <= 1) break;
            currentPage = getNextPage(currentPage, totalPages);
            if (currentPage == -1) break;
        }
    }

    // non-paginated: single resident detail
    public void displayResidentDetail(Resident r) {
        System.out.println("  +--------------------+----------------------------+");
        System.out.printf("  | %-18s | %-26s |%n", "Resident ID",        r.getResidentId());
        System.out.printf("  | %-18s | %-26s |%n", "Full Name",          r.getFullName());
        System.out.printf("  | %-18s | %-26s |%n", "Age",                r.getAge());
        System.out.printf("  | %-18s | %-26s |%n", "Household Income",   "Php " + String.format("%,.2f", r.getHouseholdIncome()));
        System.out.printf("  | %-18s | %-26s |%n", "Family Size",        r.getFamilySize());
        System.out.printf("  | %-18s | %-26s |%n", "Occupation",         r.getOccupation());
        System.out.printf("  | %-18s | %-26s |%n", "Student",            yn(r.isStudent()));
        System.out.printf("  | %-18s | %-26s |%n", "Unemployed",         yn(r.isUnemployed()));
        System.out.printf("  | %-18s | %-26s |%n", "Solo Parent",        yn(r.isSoloParent()));
        System.out.printf("  | %-18s | %-26s |%n", "Senior Citizen",     yn(r.isSeniorCitizen()));
        System.out.printf("  | %-18s | %-26s |%n", "PWD",                yn(r.isPwd()));
        System.out.printf("  | %-18s | %-26s |%n", "Dependent Children", yn(r.hasDependentChildren()));
        System.out.printf("  | %-18s | %-26s |%n", "Academic Average",   r.getAcademicAverage());
        System.out.printf("  | %-18s | %-26s |%n", "Child of Solo Par.", yn(r.isChildOfSoloParent()));
        System.out.printf("  | %-18s | %-26s |%n", "Child of OFW/OWWA",  yn(r.isChildOfOfwOwwa()));
        System.out.println("  +--------------------+----------------------------+");
    }

    /** Display the list of all available programs. */
    public void displayProgramMenu(EligibilityService eligibility) {
        System.out.println("\n  Available Programs:");
        String[] programs = eligibility.getAllProgramNames();
        for (int i = 0; i < programs.length; i++) {
            System.out.println("  [" + (i + 1) + "] " + programs[i]);
        }
    }

    // pagination helpers

    private int getNextPage(int currentPage, int totalPages) {
        System.out.printf("\n  Page %d of %d  |  [N] Next  [P] Previous  [Q] Back\n  Enter choice: ", currentPage, totalPages);
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("n") && currentPage < totalPages) return currentPage + 1;
        if (input.equals("p") && currentPage > 1)         return currentPage - 1;
        if (input.equals("q"))                             return -1;

        System.out.println("  [!] Invalid input or page boundary.");
        return currentPage;
    }

    // formatting helpers

    public String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    public String yn(boolean value) {
        return value ? "Y" : "N";
    }
}
