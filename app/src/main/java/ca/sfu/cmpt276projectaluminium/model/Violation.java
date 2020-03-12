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
        switch (this.ID) {
            case 101:
                return "Plans/construction not regulation";
            case 102:
                return "Unapproved operation of premises";
            case 103:
                return "Does not possess valid permit";
            case 104:
                return "Permit not placed conspicuously";
            case 201:
                return "Food contaminated or unsafe to eat";
            case 202:
                return "Food not processed safely";
            case 203:
                return "Food not cooled safely";
            case 204:
                return "Food not heated safely";
            case 205:
                return "Food improperly stored above 4°C";
            case 206:
                return "Food improperly stored below 60°C";
            case 208:
                return "Food improperly obtained";
            case 209:
                return "Food improperly protected";
            case 210:
                return "Food improperly thawed";
            case 211:
                return "Food improperly stored above -18°C";
            case 212:
                return "Food handling manual not provided";
            case 301:
                return "Equipment in unsanitary condition";
            case 302:
                return "Equipment not properly washed";
            case 303:
                return "Equipment not adequate";
            case 304:
                return "Premises not pest-free";
            case 305:
                return "Insufficient pest prevention";
            case 306:
                return "Premises in unsanitary condition";
            case 307:
                return "Equipment not of proper material";
            case 308:
                return "Equipment not in working order";
            case 309:
                return "Chemicals improperly stored";
            case 310:
                return "Disposable utensils are reused";
            case 311:
                return "Premises not maintained";
            case 312:
                return "Unrelated items kept on premises";
            case 313:
                return "Live animals on premises";
            case 314:
                return "Sanitation manual not provided";
            case 315:
                return "Thermometers are missing";
            case 401:
                return "Handwashing stations inadequate";
            case 402:
                return "Employee didn't wash hands";
            case 403:
                return "Employee lacks hygiene";
            case 404:
                return "Employee smoking on premises";
            case 501:
                return "Operator missing FOODSAFE";
            case 502:
                return "Staff missing FOODSAFE";
            default:
                return "invalid ID";
        }
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
