package com.bbes;

import java.util.ArrayList;

/**
 * ShellSorter - implements the Shell Sort algorithm for sorting beneficiary lists.
 *
 * Shell Sort is an optimization of Insertion Sort. It works by comparing elements
 * that are far apart first, then progressively reducing the gap between elements.
 * This allows elements to move to their correct position faster than regular
 * Insertion Sort, which only compares adjacent elements.
 *
 * How Shell Sort Works:
 * 1. Start with a large gap (usually half the array size).
 * 2. Compare elements that are 'gap' positions apart and swap if needed.
 * 3. Reduce the gap (usually by dividing by 2).
 * 4. Repeat until gap = 1, which is the same as regular Insertion Sort.
 * 5. By this point, the array is nearly sorted, so the final pass is fast.
 *
 * Time Complexity: O(n^2) worst case, but much better in practice.
 * This class does NOT use any built-in sorting functions (e.g., Collections.sort).
 */
public class ShellSorter {

    /**
     * Sort residents by HOUSEHOLD INCOME in ascending order (lowest first).
     * Residents with lowest income get highest priority for assistance.
     *
     * Shell Sort Steps:
     * 1. Calculate the initial gap as half the list size.
     * 2. For each gap, compare elements 'gap' apart.
     * 3. If the current element has lower income than the element 'gap' positions before,
     *    shift elements forward and insert the current element at the correct spot.
     * 4. Halve the gap and repeat until gap is 0.
     */
    public ArrayList<Resident> sortByIncomeAscending(ArrayList<Resident> list) {
        // Create a copy so we don't modify the original list
        ArrayList<Resident> sorted = new ArrayList<>(list);
        int n = sorted.size();

        // Step 1: Start with a large gap, then reduce it each loop
        // Gap starts at n/2 and halves each iteration: n/2, n/4, n/8, ... , 1
        for (int gap = n / 2; gap > 0; gap /= 2) {

            // Step 2: Do a gapped insertion sort for this gap size
            // We start from index 'gap' and compare with elements 'gap' positions back
            for (int i = gap; i < n; i++) {

                // Step 3: Save the current element to be inserted
                Resident temp = sorted.get(i);
                int j = i;

                // Step 4: Shift elements that are greater than 'temp' to the right
                // Compare by household income (ascending = lower income first)
                while (j >= gap && sorted.get(j - gap).getHouseholdIncome() > temp.getHouseholdIncome()) {
                    sorted.set(j, sorted.get(j - gap)); // shift element forward by 'gap'
                    j -= gap; // move back by 'gap' to check the next element
                }

                // Step 5: Place 'temp' at its correct position
                sorted.set(j, temp);
            }
        }

        return sorted;
    }

    /**
     * Sort residents by AGE in ascending order (youngest first).
     * Uses the same Shell Sort logic as income sorting, but compares age.
     */
    public ArrayList<Resident> sortByAgeAscending(ArrayList<Resident> list) {
        ArrayList<Resident> sorted = new ArrayList<>(list);
        int n = sorted.size();

        // Gap starts at half the list size
        for (int gap = n / 2; gap > 0; gap /= 2) {

            // Gapped insertion sort
            for (int i = gap; i < n; i++) {
                Resident temp = sorted.get(i);
                int j = i;

                // Shift elements with greater age to the right
                while (j >= gap && sorted.get(j - gap).getAge() > temp.getAge()) {
                    sorted.set(j, sorted.get(j - gap));
                    j -= gap;
                }

                sorted.set(j, temp);
            }
        }

        return sorted;
    }

    /**
     * Sort residents by FAMILY SIZE in descending order (largest family first).
     * Larger families may need more assistance, so they get higher priority.
     *
     * Note: The only difference from ascending sort is the comparison operator.
     * We use < instead of > to achieve descending order.
     */
    public ArrayList<Resident> sortByFamilySizeDescending(ArrayList<Resident> list) {
        ArrayList<Resident> sorted = new ArrayList<>(list);
        int n = sorted.size();

        for (int gap = n / 2; gap > 0; gap /= 2) {

            for (int i = gap; i < n; i++) {
                Resident temp = sorted.get(i);
                int j = i;

                // Note: using < instead of > makes it sort in DESCENDING order
                // This means larger family sizes come first
                while (j >= gap && sorted.get(j - gap).getFamilySize() < temp.getFamilySize()) {
                    sorted.set(j, sorted.get(j - gap));
                    j -= gap;
                }

                sorted.set(j, temp);
            }
        }

        return sorted;
    }

    /**
     * Sort residents by FULL NAME in alphabetical order (A to Z).
     * Uses String.compareToIgnoreCase() to compare names.
     * compareToIgnoreCase returns:
     *   - positive number if the first name comes AFTER the second
     *   - negative number if it comes BEFORE
     *   - zero if they are equal
     */
    public ArrayList<Resident> sortByNameAlphabetical(ArrayList<Resident> list) {
        ArrayList<Resident> sorted = new ArrayList<>(list);
        int n = sorted.size();

        for (int gap = n / 2; gap > 0; gap /= 2) {

            for (int i = gap; i < n; i++) {
                Resident temp = sorted.get(i);
                int j = i;

                // compareToIgnoreCase > 0 means the name at (j - gap) comes AFTER temp's name
                // So we shift it forward to place names in alphabetical order
                while (j >= gap && sorted.get(j - gap).getFullName()
                        .compareToIgnoreCase(temp.getFullName()) > 0) {
                    sorted.set(j, sorted.get(j - gap));
                    j -= gap;
                }

                sorted.set(j, temp);
            }
        }

        return sorted;
    }

    /**
     * Sort residents by NEED SCORE in descending order (most in-need first).
     * The need score is calculated by EligibilityService based on income, family size,
     * vulnerability flags, and number of eligible programs.
     *
     * This method uses Shell Sort to rank residents from highest to lowest need score.
     * Residents with the highest score are the ones who need benefits the most.
     */
    public ArrayList<Resident> sortByNeedScoreDescending(ArrayList<Resident> list,
                                                          EligibilityService eligibility) {
        ArrayList<Resident> sorted = new ArrayList<>(list);
        int n = sorted.size();

        // Pre-calculate need scores for all residents (so we don't recalculate each comparison)
        int[] scores = new int[n];
        for (int i = 0; i < n; i++) {
            scores[i] = eligibility.calculateNeedScore(sorted.get(i));
        }

        // Shell Sort by need score in descending order (highest score first)
        for (int gap = n / 2; gap > 0; gap /= 2) {

            for (int i = gap; i < n; i++) {
                Resident tempResident = sorted.get(i);
                int tempScore = scores[i];
                int j = i;

                // Using < to sort in DESCENDING order (highest score first)
                while (j >= gap && scores[j - gap] < tempScore) {
                    sorted.set(j, sorted.get(j - gap));
                    scores[j] = scores[j - gap]; // also shift the score
                    j -= gap;
                }

                sorted.set(j, tempResident);
                scores[j] = tempScore; // place score at correct position
            }
        }

        return sorted;
    }
}
