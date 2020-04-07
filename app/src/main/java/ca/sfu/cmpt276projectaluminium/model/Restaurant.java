package ca.sfu.cmpt276projectaluminium.model;

import java.util.List;

/**
 * Contains the data about a restaurant and provides getters in an organized manner
 */
public class Restaurant implements Comparable<Restaurant> {
    private String trackingNumber;
    private String name;
    private String address;
    private String city;
    private String type;
    private double latitude;
    private double longitude;

    /**
     * Private constructor so that restaurants are only instantiated in ways that are allowed
     */
    private Restaurant() {}

    /**
     * Constructor is package private as it should only be called by RestaurantManager
     */
    Restaurant(String trackingNumber, String name, String address, String city, String type,
               double latitude, double longitude) {
        this.trackingNumber = trackingNumber;
        this.name = name;
        this.address = address;
        this.city = city;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getType() {
        return type;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns this restaurant's most recent inspection's hazard rating
     * @return The hazard level of the most recent inspection for this restaurant
     */
    String getMostRecentInspectionHazardRating() {
        // Get the list of inspections for this restaurant
        InspectionManager inspectionManager = InspectionManager.getInstance();
        List<Inspection> inspections =
                inspectionManager.getInspections(this.getTrackingNumber());

        // Get the most recent inspection
        Inspection mostRecentInspection = inspectionManager.getMostRecentInspection(inspections);

        // Return the most recent inspection's hazard rating
        return mostRecentInspection.getHazardRating().toLowerCase();
    }

    /**
     * Allows the RestaurantManager to be sorted by its Name
     */
    /* Sources:
    https://dzone.com/articles/java-comparable-interface-in-five-minutes
    https://stackoverflow.com/questions/5153496/how-can-i-compare-two-strings-in-java-and-define-which-of-them-is-smaller-than-t
     */
    @Override
    public int compareTo(Restaurant other) {
        return this.name.compareTo(other.name);
    }
}
