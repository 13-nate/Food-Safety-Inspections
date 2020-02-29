package ca.sfu.cmpt276projectaluminium;

public class Restaurant {
    private String name;
    private String hazardLevel;
    private int numOfIssues;
    private String lastInspectionData;

    public Restaurant(String name, String hazardLevel, int numOfIssues, String lastInspectionData) {
        this.name = name;
        this.hazardLevel = hazardLevel;
        this.numOfIssues = numOfIssues;
        this.lastInspectionData = lastInspectionData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHazardLevel() {
        return hazardLevel;
    }

    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public int getNumOfIssues() {
        return numOfIssues;
    }

    public void setNumOfIssues(int numOfIssues) {
        this.numOfIssues = numOfIssues;
    }

    public String getLastInspectionData() {
        return lastInspectionData;
    }

    public void setLastInspectionData(String lastInspectionData) {
        this.lastInspectionData = lastInspectionData;
    }
}
