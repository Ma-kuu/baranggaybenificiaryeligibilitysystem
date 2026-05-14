package com.bbes;

/**
 * Entry point for the Barangay Beneficiary Prioritization System (BBES).
 *
 * Checks resident eligibility for 11 Philippine government programs
 * and uses Shell Sort to rank and prioritize beneficiaries.
 */
public class Main {
    public static void main(String[] args) {
        MenuService menu = new MenuService();
        menu.start();
    }
}