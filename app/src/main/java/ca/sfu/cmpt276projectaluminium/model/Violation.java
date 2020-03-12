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

    public String getShortDescription() {
        if (this.ID == 101) {
            return "Plans/construction not regulation";
        } else if (this.ID == 102) {
            return "Unapproved operation of premises";
        } else if (this.ID == 103) {
            return "Does not possess valid permit";
        } else if (this.ID == 104) {
            return "Permit not placed conspicuously";
        } else if (this.ID == 201) {
            return "Food contaminated or unsafe to eat";
        } else if (this.ID == 202) {
            return "Food not processed safely";
        } else if (this.ID == 203) {
            return "Food not cooled safely";
        } else if (this.ID == 204) {
            return "Food not heated safely";
        } else if (this.ID == 205) {
            return "Food improperly stored above 4°C";
        } else if (this.ID == 206) {
            return "Food improperly stored below 60°C";
        } else if (this.ID == 208) {
            return "Food improperly obtained";
        } else if (this.ID == 209) {
            return "Food improperly protected";
        } else if (this.ID == 210) {
            return "Food improperly thawed";
        } else if (this.ID == 211) {
            return "Food improperly stored above -18°C";
        } else if (this.ID == 212) {
            return "Food handling manual not provided";
        } else if (this.ID == 301) {
            return "Equipment in unsanitary condition";
        } else if (this.ID == 302) {
            return "Equipment not properly washed";
        } else if (this.ID == 303) {
            return "Equipment not adequate";
        } else if (this.ID == 304) {
            return "Premises not pest-free";
        } else if (this.ID == 305) {
            return "Insufficient pest prevention";
        } else if (this.ID == 306) {
            return "Premises in unsanitary condition";
        } else if (this.ID == 307) {
            return "Equipment not of proper material";
        } else if (this.ID == 308) {
            return "Equipment not in working order";
        } else if (this.ID == 309) {
            return "Chemicals improperly stored";
        } else if (this.ID == 310) {
            return "Disposable utensils are reused";
        } else if (this.ID == 311) {
            return "Premises not maintained";
        } else if (this.ID == 312) {
            return "Unrelated items kept on premises";
        } else if (this.ID == 313) {
            return "Live animals on premises";
        } else if (this.ID == 314) {
            return "Sanitation manual not provided";
        } else if (this.ID == 315) {
            return "Thermometers are missing";
        } else if (this.ID == 401) {
            return "Handwashing stations inadequate";
        } else if (this.ID == 402) {
            return "Employee didn't wash hands";
        } else if (this.ID == 403) {
            return "Employee lacks hygiene";
        } else if (this.ID == 404) {
            return "Employee smoking on premises";
        } else if (this.ID == 501) {
            return "Operator missing FOODSAFE";
        } else if (this.ID == 502) {
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
