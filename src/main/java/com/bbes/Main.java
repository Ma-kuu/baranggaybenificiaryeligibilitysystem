package com.bbes;

/**
 * Main class - entry point for the Barangay Beneficiary Prioritization System.
 *
 * This system:
 * 1. Stores barangay resident records using ArrayList (in-memory only).
 * 2. Checks eligibility for 11 Philippine government assistance programs.
 * 3. Generates and prioritizes beneficiary lists using Shell Sort algorithm.
 *
 * Classes:
 * - Resident          : stores resident data (model)
 * - EligibilityService: contains eligibility rules for all programs
 * - ShellSorter       : implements Shell Sort for sorting beneficiary lists
 * - MenuService       : handles CLI menu and user interaction
 * - Main              : entry point
 */
public class Main {
    public static void main(String[] args) {
        MenuService menu = new MenuService();
        menu.start();
    }
}