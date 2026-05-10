package com.bbes;

/**
 * Resident class - stores all information about a barangay resident.
 * Each resident has personal details and flags used for eligibility checking.
 */
public class Resident {

    // ==================== Fields ====================
    private int residentId;
    private String fullName;
    private int age;
    private double householdIncome;
    private int familySize;
    private String occupation;
    private boolean student;
    private boolean unemployed;
    private boolean soloParent;
    private boolean seniorCitizen;
    private boolean pwd;
    private boolean dependentChildren;
    private double academicAverage;
    private boolean childOfSoloParent;
    private boolean childOfOfwOwwa;

    // ==================== Constructor ====================
    public Resident(
            int residentId, String fullName, int age, double householdIncome,
            int familySize, String occupation, boolean student, boolean unemployed,
            boolean soloParent, boolean seniorCitizen, boolean pwd,
            boolean dependentChildren, double academicAverage,
            boolean childOfSoloParent, boolean childOfOfwOwwa) {

        this.residentId = residentId;
        this.fullName = fullName;
        this.age = age;
        this.householdIncome = householdIncome;
        this.familySize = familySize;
        this.occupation = occupation;
        this.student = student;
        this.unemployed = unemployed;
        this.soloParent = soloParent;
        this.seniorCitizen = seniorCitizen;
        this.pwd = pwd;
        this.dependentChildren = dependentChildren;
        this.academicAverage = academicAverage;
        this.childOfSoloParent = childOfSoloParent;
        this.childOfOfwOwwa = childOfOfwOwwa;
    }

    // ==================== Getters ====================
    public int getResidentId() {
        return residentId;
    }

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    public double getHouseholdIncome() {
        return householdIncome;
    }

    public int getFamilySize() {
        return familySize;
    }

    public String getOccupation() {
        return occupation;
    }

    public boolean isStudent() {
        return student;
    }

    public boolean isUnemployed() {
        return unemployed;
    }

    public boolean isSoloParent() {
        return soloParent;
    }

    public boolean isSeniorCitizen() {
        return seniorCitizen;
    }

    public boolean isPwd() {
        return pwd;
    }

    public boolean hasDependentChildren() {
        return dependentChildren;
    }

    public double getAcademicAverage() {
        return academicAverage;
    }

    public boolean isChildOfSoloParent() {
        return childOfSoloParent;
    }

    public boolean isChildOfOfwOwwa() {
        return childOfOfwOwwa;
    }

    // ==================== Display ====================

    /**
     * Returns a formatted string showing all resident details.
     * Used when viewing resident records in the CLI.
     */
    @Override
    public String toString() {
        return "======================================\n" +
                " Resident ID       : " + residentId + "\n" +
                " Full Name         : " + fullName + "\n" +
                " Age               : " + age + "\n" +
                " Household Income  : Php " + String.format("%,.2f", householdIncome) + "\n" +
                " Family Size       : " + familySize + "\n" +
                " Occupation        : " + occupation + "\n" +
                " Student           : " + yesNo(student) + "\n" +
                " Unemployed        : " + yesNo(unemployed) + "\n" +
                " Solo Parent       : " + yesNo(soloParent) + "\n" +
                " Senior Citizen    : " + yesNo(seniorCitizen) + "\n" +
                " PWD               : " + yesNo(pwd) + "\n" +
                " Dependent Children: " + yesNo(dependentChildren) + "\n" +
                " Academic Average  : " + academicAverage + "\n" +
                " Child of Solo Par.: " + yesNo(childOfSoloParent) + "\n" +
                " Child of OFW/OWWA : " + yesNo(childOfOfwOwwa) + "\n" +
                "======================================";
    }

    /** Helper: converts boolean to "Yes" or "No" for display. */
    private String yesNo(boolean value) {
        return value ? "Yes" : "No";
    }
}
