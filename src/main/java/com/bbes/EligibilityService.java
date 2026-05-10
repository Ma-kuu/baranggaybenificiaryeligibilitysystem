package com.bbes;

import java.util.ArrayList;

/**
 * EligibilityService - checks which government programs a resident qualifies for.
 * Contains all eligibility rules for the 11 assistance and scholarship programs.
 *
 * Poverty threshold is set at Php 12,030/month based on PSA guidelines.
 * "Low income" is defined as household income below Php 21,000/month.
 */
public class EligibilityService {

    // Income thresholds used for eligibility checking
    private static final double POVERTY_THRESHOLD = 12030.0;  // monthly poverty threshold
    private static final double LOW_INCOME_THRESHOLD = 21000.0; // low income ceiling

    // List of all program names (indices match the check methods below)
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

    // ==================== Individual Eligibility Checks ====================

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
        String occupation = r.getOccupation().toLowerCase();
        return occupation.contains("farmer")
                || occupation.contains("coco farmer")
                || occupation.contains("rice farmer")
                || occupation.contains("corn farmer")
                || occupation.contains("agricultural worker");
    }

    /**
     * Bagong Pilipinas Merit Scholarship:
     * - student, academic average >= 90, low income
     * - priority if PWD or child of solo parent
     */
    public boolean isEligibleBagongPilipinas(Resident r) {
        return r.isStudent()
                && r.getAcademicAverage() >= 90
                && r.getHouseholdIncome() < LOW_INCOME_THRESHOLD;
    }

    /** Check if resident has priority consideration for Bagong Pilipinas */
    public boolean hasBagongPilipinasPriority(Resident r) {
        return r.isPwd() || r.isChildOfSoloParent();
    }

    /** OWWA Scholarship: student, child of OFW/OWWA, academic average >= 85 */
    public boolean isEligibleOWWA(Resident r) {
        return r.isStudent()
                && r.isChildOfOfwOwwa()
                && r.getAcademicAverage() >= 85;
    }

    /** Educational Cash Assistance: student AND low income */
    public boolean isEligibleEducationalCash(Resident r) {
        return r.isStudent()
                && r.getHouseholdIncome() < LOW_INCOME_THRESHOLD;
    }

    // ==================== Aggregate Methods ====================

    /**
     * Returns a list of all program names the resident is eligible for.
     */
    public ArrayList<String> getEligiblePrograms(Resident r) {
        ArrayList<String> eligible = new ArrayList<>();

        if (isEligible4Ps(r))              eligible.add(PROGRAM_NAMES[0]);
        if (isEligibleSPES(r))             eligible.add(PROGRAM_NAMES[1]);
        if (isEligibleTUPAD(r))            eligible.add(PROGRAM_NAMES[2]);
        if (isEligibleSoloParent(r))       eligible.add(PROGRAM_NAMES[3]);
        if (isEligibleSeniorPension(r))    eligible.add(PROGRAM_NAMES[4]);
        if (isEligiblePWD(r))              eligible.add(PROGRAM_NAMES[5]);
        if (isEligibleAKAP(r))             eligible.add(PROGRAM_NAMES[6]);
        if (isEligibleFarmerAssistance(r)) eligible.add(PROGRAM_NAMES[7]);
        if (isEligibleBagongPilipinas(r))  eligible.add(PROGRAM_NAMES[8]);
        if (isEligibleOWWA(r))             eligible.add(PROGRAM_NAMES[9]);
        if (isEligibleEducationalCash(r))  eligible.add(PROGRAM_NAMES[10]);

        return eligible;
    }

    /**
     * Returns a list of all program names the resident is NOT eligible for.
     */
    public ArrayList<String> getIneligiblePrograms(Resident r) {
        ArrayList<String> ineligible = new ArrayList<>();

        if (!isEligible4Ps(r))              ineligible.add(PROGRAM_NAMES[0]);
        if (!isEligibleSPES(r))             ineligible.add(PROGRAM_NAMES[1]);
        if (!isEligibleTUPAD(r))            ineligible.add(PROGRAM_NAMES[2]);
        if (!isEligibleSoloParent(r))       ineligible.add(PROGRAM_NAMES[3]);
        if (!isEligibleSeniorPension(r))    ineligible.add(PROGRAM_NAMES[4]);
        if (!isEligiblePWD(r))              ineligible.add(PROGRAM_NAMES[5]);
        if (!isEligibleAKAP(r))             ineligible.add(PROGRAM_NAMES[6]);
        if (!isEligibleFarmerAssistance(r)) ineligible.add(PROGRAM_NAMES[7]);
        if (!isEligibleBagongPilipinas(r))  ineligible.add(PROGRAM_NAMES[8]);
        if (!isEligibleOWWA(r))             ineligible.add(PROGRAM_NAMES[9]);
        if (!isEligibleEducationalCash(r))  ineligible.add(PROGRAM_NAMES[10]);

        return ineligible;
    }

    /**
     * Given a program number (1-11), returns all eligible residents from the list.
     */
    public ArrayList<Resident> getQualifiedResidents(ArrayList<Resident> residents, int programNumber) {
        ArrayList<Resident> qualified = new ArrayList<>();

        for (Resident r : residents) {
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

            if (eligible) {
                qualified.add(r);
            }
        }

        return qualified;
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
                return i + 1; // program numbers are 1-based
            }
        }
        return -1;
    }

    // ==================== Need Score ====================

    /**
     * Calculates a "need score" for a resident.
     * Higher score = the resident needs more assistance.
     *
     * Scoring breakdown:
     *   Income:
     *     - Below poverty threshold (12,030): +5 points
     *     - Below low income (21,000):        +3 points
     *     - Below 30,000:                     +1 point
     *   Family size:
     *     - 6 or more members: +3 points
     *     - 4-5 members:       +2 points
     *     - 2-3 members:       +1 point
     *   Vulnerability flags (2 points each):
     *     - PWD, Senior Citizen, Solo Parent, Unemployed
     *   Other flags (1 point each):
     *     - Dependent Children
     *   Program eligibility:
     *     - +1 point per eligible program (more programs = more need indicators)
     */
    public int calculateNeedScore(Resident r) {
        int score = 0;

        // Income-based scoring (lower income = higher score)
        if (r.getHouseholdIncome() < POVERTY_THRESHOLD) {
            score += 5;
        } else if (r.getHouseholdIncome() < LOW_INCOME_THRESHOLD) {
            score += 3;
        } else if (r.getHouseholdIncome() < 30000) {
            score += 1;
        }

        // Family size scoring (bigger family = more mouths to feed)
        if (r.getFamilySize() >= 6) {
            score += 3;
        } else if (r.getFamilySize() >= 4) {
            score += 2;
        } else if (r.getFamilySize() >= 2) {
            score += 1;
        }

        // Vulnerability flags (2 points each)
        if (r.isPwd())           score += 2;
        if (r.isSeniorCitizen()) score += 2;
        if (r.isSoloParent())    score += 2;
        if (r.isUnemployed())    score += 2;

        // Other flags (1 point each)
        if (r.hasDependentChildren()) score += 1;

        // Add 1 point per eligible program
        int eligibleCount = getEligiblePrograms(r).size();
        score += eligibleCount;

        return score;
    }

    /** Returns all program names as an array. */
    public String[] getAllProgramNames() {
        return PROGRAM_NAMES;
    }
}
