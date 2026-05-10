package com.bbes;

import java.util.ArrayList;

/**
 * SampleData - provides pre-loaded test data for demonstration and testing.
 * Contains 10 sample residents covering various eligibility scenarios.
 */
public class SampleData {

    /**
     * Creates and returns a list of 10 sample residents.
     * Each resident is designed to test different eligibility scenarios.
     *
     * @return ArrayList of 10 sample Resident objects
     */
    public static ArrayList<Resident> loadSampleResidents() {
        ArrayList<Resident> residents = new ArrayList<>();
        int id = 1;

        // Resident 1: Low-income mother with dependent children (eligible for 4Ps, AKAP)
        residents.add(new Resident(id++, "Maria Santos", 34, 8000, 6, "Vendor",
                false, false, false, false, false, true, 0, false, false));

        // Resident 2: Student from low-income family (eligible for SPES, Educational Cash)
        residents.add(new Resident(id++, "Juan Dela Cruz", 19, 15000, 5, "Student",
                true, false, false, false, false, false, 88, false, false));

        // Resident 3: Unemployed adult (eligible for TUPAD)
        residents.add(new Resident(id++, "Pedro Reyes", 45, 10000, 4, "None",
                false, true, false, false, false, true, 0, false, false));

        // Resident 4: Solo parent (eligible for Solo Parent Benefits)
        residents.add(new Resident(id++, "Ana Garcia", 38, 18000, 3, "Teacher",
                false, false, true, false, false, true, 0, false, false));

        // Resident 5: Senior citizen (eligible for Senior Pension)
        residents.add(new Resident(id++, "Lolo Ernesto Bautista", 72, 5000, 2, "Retired",
                false, false, false, true, false, false, 0, false, false));

        // Resident 6: PWD student, child of solo parent (eligible for PWD, Bagong Pilipinas with priority)
        residents.add(new Resident(id++, "Carlo Mendoza", 20, 12000, 4, "Student",
                true, false, false, false, true, false, 92, true, false));

        // Resident 7: Farmer (eligible for Farmer Assistance)
        residents.add(new Resident(id++, "Mang Tonyo Villanueva", 55, 9500, 5, "Rice Farmer",
                false, false, false, false, false, true, 0, false, false));

        // Resident 8: Student child of OFW (eligible for OWWA Scholarship)
        residents.add(new Resident(id++, "Lisa Ramos", 17, 25000, 4, "Student",
                true, false, false, false, false, false, 90, false, true));

        // Resident 9: Low-income student with high grades (eligible for Bagong Pilipinas, SPES, Educ. Cash)
        residents.add(new Resident(id++, "Mark Rivera", 21, 11000, 7, "Student",
                true, false, false, false, false, false, 95, false, false));

        // Resident 10: Senior PWD solo parent (eligible for multiple programs)
        residents.add(new Resident(id++, "Lola Carmen Aquino", 65, 7000, 3, "None",
                false, true, true, true, true, true, 0, false, false));

        return residents;
    }

    /** Returns the next available ID after loading sample data. */
    public static int getNextIdAfterSample() {
        return 11; // 10 sample residents loaded, next ID is 11
    }
}
