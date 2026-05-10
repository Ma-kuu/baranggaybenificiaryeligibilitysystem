package com.bbes;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * ShellSorter - implements the Shell Sort algorithm for sorting beneficiary lists.
 *
 * Shell Sort is an optimization of Insertion Sort. It works by comparing elements
 * that are far apart first, then progressively reducing the gap between elements.
 * This allows elements to move to their correct position faster than regular
 * Insertion Sort, which only compares adjacent elements.
 *
 * Time Complexity: O(n^2) worst case, but much better in practice.
 * This class uses a single generic method with a Comparator to allow sorting by any field.
 */
public class ShellSorter {

    /**
     * Sorts the resident list using the Shell Sort algorithm based on the provided Comparator.
     * 
     * @param list The list of residents to sort
     * @param comparator The rule used to compare two residents
     * @return A new sorted ArrayList
     */
    public ArrayList<Resident> shellSort(ArrayList<Resident> list, Comparator<Resident> comparator) {
        // Create a copy so we don't modify the original list
        ArrayList<Resident> sorted = new ArrayList<>(list);
        int n = sorted.size();

        // Gap starts at n/2 and halves each iteration
        for (int gap = n / 2; gap > 0; gap /= 2) {

            // Do a gapped insertion sort for this gap size
            for (int i = gap; i < n; i++) {
                Resident temp = sorted.get(i);
                int j = i;

                // Shift elements using the comparator
                // comparator.compare(a, b) > 0 means 'a' should come after 'b'
                while (j >= gap && comparator.compare(sorted.get(j - gap), temp) > 0) {
                    sorted.set(j, sorted.get(j - gap));
                    j -= gap;
                }

                // Place 'temp' at its correct position
                sorted.set(j, temp);
            }
        }

        return sorted;
    }
}
