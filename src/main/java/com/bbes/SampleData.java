package com.bbes;

/**
 * Pre-loaded test residents for demo and testing.
 * 16 residents are loaded on startup to cover different eligibility scenarios
 * and to trigger the pagination feature (page size is 15).
 */
public class SampleData {

    public static Resident[] loadSampleResidents() {
        Resident[] r = new Resident[16];

        // low-income mother with dependent children (4Ps, AKAP)
        r[0]  = new Resident(1,  "Maria Santos",           34, 8000,  6, "Vendor",       false, false, false, false, false, true,  0,  false, false);
        // student from low-income family (SPES, Educational Cash)
        r[1]  = new Resident(2,  "Juan Dela Cruz",         19, 15000, 5, "Student",      true,  false, false, false, false, false, 88, false, false);
        // unemployed adult with family (TUPAD, AKAP)
        r[2]  = new Resident(3,  "Pedro Reyes",            45, 10000, 4, "None",         false, true,  false, false, false, true,  0,  false, false);
        // solo parent (Solo Parent Benefits)
        r[3]  = new Resident(4,  "Ana Garcia",             38, 18000, 3, "Teacher",      false, false, true,  false, false, true,  0,  false, false);
        // senior citizen (Senior Citizen Pension)
        r[4]  = new Resident(5,  "Lolo Ernesto Bautista",  72, 5000,  2, "Retired",      false, false, false, true,  false, false, 0,  false, false);
        // PWD student, child of solo parent (PWD Assistance, Bagong Pilipinas - priority)
        r[5]  = new Resident(6,  "Carlo Mendoza",          20, 12000, 4, "Student",      true,  false, false, false, true,  false, 92, true,  false);
        // rice farmer (Farmer Assistance)
        r[6]  = new Resident(7,  "Mang Tonyo Villanueva",  55, 9500,  5, "Rice Farmer",  false, false, false, false, false, true,  0,  false, false);
        // student child of OFW (OWWA Scholarship)
        r[7]  = new Resident(8,  "Lisa Ramos",             17, 25000, 4, "Student",      true,  false, false, false, false, false, 90, false, true);
        // low-income student, high grades (Bagong Pilipinas, SPES, Educational Cash)
        r[8]  = new Resident(9,  "Mark Rivera",            21, 11000, 7, "Student",      true,  false, false, false, false, false, 95, false, false);
        // senior PWD solo parent - qualifies for many programs
        r[9]  = new Resident(10, "Lola Carmen Aquino",     65, 7000,  3, "None",         false, true,  true,  true,  true,  true,  0,  false, false);
        // young unemployed with large family (TUPAD, AKAP)
        r[10] = new Resident(11, "Ronnie Aguilar",         23, 9000,  6, "None",         false, true,  false, false, false, true,  0,  false, false);
        // elderly PWD woman (PWD Assistance, Senior Citizen Pension)
        r[11] = new Resident(12, "Nora Castillo",          68, 6500,  2, "None",         false, false, false, true,  true,  false, 0,  false, false);
        // college student with OFW parent (OWWA, SPES, Educational Cash)
        r[12] = new Resident(13, "Jasmine Flores",         22, 18000, 4, "Student",      true,  false, false, false, false, false, 87, false, true);
        // rice farmer, large family (Farmer Assistance, AKAP, 4Ps)
        r[13] = new Resident(14, "Arsenio Maceda",         50, 7500,  7, "Rice Farmer",  false, false, false, false, false, true,  0,  false, false);
        // solo parent student, high grades (Solo Parent Benefits, Bagong Pilipinas)
        r[14] = new Resident(15, "Diane Soriano",          28, 13000, 3, "Student",      true,  false, true,  false, false, true,  91, false, false);
        // low-income laborer, large family (AKAP, 4Ps)
        r[15] = new Resident(16, "Efren Navarro",          41, 10500, 5, "Laborer",      false, false, false, false, false, true,  0,  false, false);

        return r;
    }

    // returns the next available ID after sample data is loaded
    public static int getNextIdAfterSample() {
        return 17; // 16 sample residents loaded
    }
}
