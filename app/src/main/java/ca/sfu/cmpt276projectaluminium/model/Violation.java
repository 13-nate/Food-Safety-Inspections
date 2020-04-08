package ca.sfu.cmpt276projectaluminium.model;

/**
 * Manages data about violations by storing it and providing getters in an organized manner
 *
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
