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

    /**
     * Private constructor so that inspections are only instantiated in ways that are allowed
     */
    private Inspection() {}

    /**
     * Constructor is package private as it should only be called by InspectionManager
     */
    public Inspection(String trackingNumber, String inspectionDate, String type, String hazardRating,
                      int numCriticalViolations, int numNonCriticalViolations) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.type = type;
        this.hazardRating = hazardRating;
        this.numCriticalViolations = numCriticalViolations;
        this.numNonCriticalViolations = numNonCriticalViolations;
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
