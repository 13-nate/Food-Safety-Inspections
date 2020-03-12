package ca.sfu.cmpt276projectaluminium.model;

/**
 * Manages data about violations by storing it and providing getters in an organized manner
 */
public class Violation implements Comparable<Violation> {
    private int ID;
    private String severity;  // Critical vs non-critical
    private String fullDescription;  // A full description (including section number)
    private String repeat;  // Repeat vs Not Repeat

    Violation(int ID, String severity, String fullDescription, String repeat) {
        this.ID = ID;
        this.severity = severity;
        this.fullDescription = fullDescription;
        this.repeat = repeat;
    }

    public int getID() {
        return ID;
    }

    public String getSeverity() {
        return severity;
    }

    public String getShortDescription(int ID) {
        if (ID == 101) {
            return "Plans/construction not regulation";
        } else if (ID == 102) {
            return "Unapproved operation of premises";
        } else if (ID == 103) {
            return "Does not possess valid permit";
        } else if (ID == 104) {
            return "Permit not placed conspicuously";
        } else if (ID == 201) {
            return "Food contaminated or unsafe to eat";
        } else if (ID == 202) {
            return "Food not processed safely";
        } else if (ID == 203) {
            return "Food not cooled safely";
        } else if (ID == 204) {
            return "Food not heated safely";
        } else if (ID == 205) {
            return "Food improperly stored above 4°C";
        } else if (ID == 206) {
            return "Food improperly stored below 60°C";
        } else if (ID == 208) {
            return "Food improperly obtained";
        } else if (ID == 209) {
            return "Food improperly protected";
        } else if (ID == 210) {
            return "Food improperly thawed";
        } else if (ID == 211) {
            return "Food improperly stored above -18°C";
        } else if (ID == 212) {
            return "Food handling manual not provided";
        } else if (ID == 301) {
            return "Equipment in unsanitary condition";
        } else if (ID == 302) {
            return "Equipment not properly washed";
        } else if (ID == 303) {
            return "Equipment not adequate";
        } else if (ID == 304) {
            return "Premises not pest-free";
        } else if (ID == 305) {
            return "Insufficient pest prevention";
        } else if (ID == 306) {
            return "Premises in unsanitary condition";
        } else if (ID == 307) {
            return "Equipment not of proper material";
        } else if (ID == 308) {
            return "Equipment not in working order";
        } else if (ID == 309) {
            return "Chemicals improperly stored";
        } else if (ID == 310) {
            return "Disposable utensils are reused";
        } else if (ID == 311) {
            return "Premises not maintained";
        } else if (ID == 312) {
            return "Unrelated items kept on premises";
        } else if (ID == 313) {
            return "Live animals on premises";
        } else if (ID == 314) {
            return "Sanitation manual not provided";
        } else if (ID == 315) {
            return "Thermometers are missing";
        } else if (ID == 401) {
            return "Handwashing stations inadequate";
        } else if (ID == 402) {
            return "Employee didn't wash hands";
        } else if (ID == 403) {
            return "Employee lacks hygiene";
        } else if (ID == 404) {
            return "Employee smoking on premises";
        } else if (ID == 501) {
            return "Operator missing FOODSAFE";
        } else if (ID == 502) {
            return "Staff missing FOODSAFE";
        }

        return "invalid ID";
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public String getRepeat() {
        return repeat;
    }

    @Override
    public int compareTo(Violation o) {
        return this.ID - o.getID();
    }
}
