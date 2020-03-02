package ca.sfu.cmpt276projectaluminium.model;

/**
 * Manages data about violations by storing it and providing getters in an organized manner
 */
public class Violation {
    private int ID;
    private String severity;  // Critical vs non-critical
    private String description;  // A full description (including section number)

    Violation(int ID, String severity, String description) {
        this.ID = ID;
        this.severity = severity;
        this.description = description;
    }

    public int getID() {
        return ID;
    }

    public String getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }
}
