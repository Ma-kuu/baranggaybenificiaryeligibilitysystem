package com.bbes;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Shell Sort - sorts a Resident array using a gap that halves each pass.
 *
 * Example (sorting [8, 3, 5, 1] ascending):
 *   gap=2: [5, 1, 8, 3]  ->  gap=1: [1, 3, 5, 8]
 *
 * Time complexity: O(n^2) worst case, faster in practice than plain Insertion Sort.
 */
public class ShellSorter {

    // Sorts a copy of the array using the comparator. Does not modify the original.
    public Resident[] shellSort(Resident[] list, Comparator<Resident> comparator) {
        Resident[] sorted = Arrays.copyOf(list, list.length);
        int n = sorted.length;

        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                Resident temp = sorted[i];
                int j = i;

                // shift elements right until we find the right spot for temp
                while (j >= gap && comparator.compare(sorted[j - gap], temp) > 0) {
                    sorted[j] = sorted[j - gap];
                    j -= gap;
                }

                sorted[j] = temp;
            }
        }

        return sorted;
    }
}
