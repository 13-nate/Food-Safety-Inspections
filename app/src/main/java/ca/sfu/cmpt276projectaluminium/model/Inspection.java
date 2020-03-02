package ca.sfu.cmpt276projectaluminium.model;

/**
 * Manages data about inspection reports by storing it and providing getters in an organized manner
 */
public class Inspection {
    private String trackingNumber;
    private String inspectionDate;
    private String type;
    private String hazardRating;
    private int numCriticalViolations;
    private int numNonCriticalViolations;

    Inspection() {

    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public String getType() {
        return type;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public int getNumCriticalViolations() {
        return numCriticalViolations;
    }

    public int getNumNonCriticalViolations() {
        return numNonCriticalViolations;
    }
}
