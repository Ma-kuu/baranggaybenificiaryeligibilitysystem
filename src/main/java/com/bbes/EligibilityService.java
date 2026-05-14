package com.bbes;

import java.util.Arrays;

/**
 * Checks resident eligibility for 11 Philippine government programs.
 * Poverty threshold: Php 12,030/month | Low income ceiling: Php 21,000/month (PSA 2023)
 */
public class EligibilityService {

    private static final double POVERTY_THRESHOLD  = 12030.0;
    private static final double LOW_INCOME_THRESHOLD = 21000.0;

    private static final String[] PROGRAM_NAMES = {
        "4Ps (Pantawid Pamilyang Pilipino Program)",
        "SPES (Special Program for Employment of Students)",
        "TUPAD (Tulong Panghanapbuhay sa Ating Disadvantaged Workers)",
        "Solo Parent Benefits",
        "Senior Citizen Pension",
        "PWD Assistance",
        "AKAP (Ayuda sa Kapos ang Kita Program)",
        "Farmer Assistance",
        "Bagong Pilipinas Merit Scholarship",
        "OWWA Scholarship",
        "Educational Cash Assistance"
    };

    // individual eligibility checks

    /** 4Ps: income below poverty threshold AND has dependent children */
    public boolean isEligible4Ps(Resident r) {
        return r.getHouseholdIncome() < POVERTY_THRESHOLD && r.hasDependentChildren();
    }

    /** SPES: age 15-30, student, low income */
    public boolean isEligibleSPES(Resident r) {
        return r.getAge() >= 15 && r.getAge() <= 30
                && r.isStudent()
                && r.getHouseholdIncome() < LOW_INCOME_THRESHOLD;
    }

    /** TUPAD: unemployed, age 18+, low income */
    public boolean isEligibleTUPAD(Resident r) {
        return r.isUnemployed()
                && r.getAge() >= 18
                && r.getHouseholdIncome() < LOW_INCOME_THRESHOLD;
    }

    /** Solo Parent Benefits: must be a solo parent */
    public boolean isEligibleSoloParent(Resident r) {
        return r.isSoloParent();
    }

    /** Senior Pension: senior citizen AND age >= 60 */
    public boolean isEligibleSeniorPension(Resident r) {
        return r.isSeniorCitizen() && r.getAge() >= 60;
    }

    /** PWD Assistance: must be a PWD */
    public boolean isEligiblePWD(Resident r) {
        return r.isPwd();
    }

    /** AKAP: household income <= 20000 AND family size >= 4 */
    public boolean isEligibleAKAP(Resident r) {
        return r.getHouseholdIncome() <= 20000 && r.getFamilySize() >= 4;
    }

    /** Farmer Assistance: occupation must be farming-related */
    public boolean isEligibleFarmerAssistance(Resident r) {
        String occ = r.getOccupation().toLowerCase();
        return occ.contains("farmer") || occ.contains("agricultural worker");
    }

    /** Bagong Pilipinas: student, average >= 90, low income */
    public boolean isEligibleBagongPilipinas(Resident r) {
        return r.isStudent()
                && r.getAcademicAverage() >= 90
                && r.getHouseholdIncome() < LOW_INCOME_THRESHOLD;
    }

    /** Priority consideration for Bagong Pilipinas (PWD or child of solo parent) */
    public boolean hasBagongPilipinasPriority(Resident r) {
        return r.isPwd() || r.isChildOfSoloParent();
    }

    /** OWWA Scholarship: student, child of OFW/OWWA, average >= 85 */
    public boolean isEligibleOWWA(Resident r) {
        return r.isStudent()
                && r.isChildOfOfwOwwa()
                && r.getAcademicAverage() >= 85;
    }

    /** Educational Cash Assistance: student AND low income */
    public boolean isEligibleEducationalCash(Resident r) {
        return r.isStudent() && r.getHouseholdIncome() < LOW_INCOME_THRESHOLD;
    }

    // aggregate methods

    // returns all programs the resident qualifies for as a String array
    public String[] getEligiblePrograms(Resident r) {
        String[] eligible = new String[PROGRAM_NAMES.length];
        int count = 0;

        if (isEligible4Ps(r))              eligible[count++] = PROGRAM_NAMES[0];
        if (isEligibleSPES(r))             eligible[count++] = PROGRAM_NAMES[1];
        if (isEligibleTUPAD(r))            eligible[count++] = PROGRAM_NAMES[2];
        if (isEligibleSoloParent(r))       eligible[count++] = PROGRAM_NAMES[3];
        if (isEligibleSeniorPension(r))    eligible[count++] = PROGRAM_NAMES[4];
        if (isEligiblePWD(r))              eligible[count++] = PROGRAM_NAMES[5];
        if (isEligibleAKAP(r))             eligible[count++] = PROGRAM_NAMES[6];
        if (isEligibleFarmerAssistance(r)) eligible[count++] = PROGRAM_NAMES[7];
        if (isEligibleBagongPilipinas(r))  eligible[count++] = PROGRAM_NAMES[8];
        if (isEligibleOWWA(r))             eligible[count++] = PROGRAM_NAMES[9];
        if (isEligibleEducationalCash(r))  eligible[count++] = PROGRAM_NAMES[10];

        return Arrays.copyOf(eligible, count);
    }

    // returns all programs the resident does NOT qualify for
    public String[] getIneligiblePrograms(Resident r) {
        String[] eligible   = getEligiblePrograms(r);
        String[] ineligible = new String[PROGRAM_NAMES.length];
        int count = 0;

        // for each program, check if it appears in the eligible array
        outer:
        for (String program : PROGRAM_NAMES) {
            for (String e : eligible) {
                if (program.equals(e)) continue outer;
            }
            ineligible[count++] = program;
        }

        return Arrays.copyOf(ineligible, count);
    }

    // returns all residents who qualify for the given program number (1-11)
    public Resident[] getQualifiedResidents(Resident[] residents, int count, int programNumber) {
        Resident[] qualified = new Resident[count];
        int q = 0;

        for (int i = 0; i < count; i++) {
            Resident r = residents[i];
            boolean eligible = false;

            switch (programNumber) {
                case 1:  eligible = isEligible4Ps(r);              break;
                case 2:  eligible = isEligibleSPES(r);             break;
                case 3:  eligible = isEligibleTUPAD(r);            break;
                case 4:  eligible = isEligibleSoloParent(r);       break;
                case 5:  eligible = isEligibleSeniorPension(r);    break;
                case 6:  eligible = isEligiblePWD(r);              break;
                case 7:  eligible = isEligibleAKAP(r);             break;
                case 8:  eligible = isEligibleFarmerAssistance(r); break;
                case 9:  eligible = isEligibleBagongPilipinas(r);  break;
                case 10: eligible = isEligibleOWWA(r);             break;
                case 11: eligible = isEligibleEducationalCash(r);  break;
            }

            if (eligible) qualified[q++] = r;
        }

        return Arrays.copyOf(qualified, q);
    }

    /** Returns the program name given its number (1-11). */
    public String getProgramName(int programNumber) {
        if (programNumber >= 1 && programNumber <= PROGRAM_NAMES.length) {
            return PROGRAM_NAMES[programNumber - 1];
        }
        return "Unknown Program";
    }

    /** Returns the total number of programs. */
    public int getProgramCount() {
        return PROGRAM_NAMES.length;
    }

    /** Returns the program number (1-11) given a program name. Returns -1 if not found. */
    public int getProgramNumber(String programName) {
        for (int i = 0; i < PROGRAM_NAMES.length; i++) {
            if (PROGRAM_NAMES[i].equals(programName)) {
                return i + 1;
            }
        }
        return -1;
    }

    /** Returns all program names as a String array. */
    public String[] getAllProgramNames() {
        return PROGRAM_NAMES;
    }

    // need score calculation

    /**
     * Calculates a "need score" for a resident.
     * Higher score = the resident needs more assistance.
     *
     * Scoring:
     *   Income < poverty (12,030)  -> +5 pts
     *   Income < low income (21,000) -> +3 pts
     *   Income < 30,000            -> +1 pt
     *   Family size >= 6           -> +3 pts
     *   Family size >= 4           -> +2 pts
     *   Family size >= 2           -> +1 pt
     *   PWD, Senior, Solo Parent, Unemployed -> +2 pts each
     *   Dependent children         -> +1 pt
     *   +1 pt per eligible program
     */
    public int calculateNeedScore(Resident r) {
        int score = 0;

        if (r.getHouseholdIncome() < POVERTY_THRESHOLD)    score += 5;
        else if (r.getHouseholdIncome() < LOW_INCOME_THRESHOLD) score += 3;
        else if (r.getHouseholdIncome() < 30000)           score += 1;

        if (r.getFamilySize() >= 6)      score += 3;
        else if (r.getFamilySize() >= 4) score += 2;
        else if (r.getFamilySize() >= 2) score += 1;

        if (r.isPwd())           score += 2;
        if (r.isSeniorCitizen()) score += 2;
        if (r.isSoloParent())    score += 2;
        if (r.isUnemployed())    score += 2;

        if (r.hasDependentChildren()) score += 1;

        score += getEligiblePrograms(r).length; // +1 per eligible program

        return score;
    }
}
