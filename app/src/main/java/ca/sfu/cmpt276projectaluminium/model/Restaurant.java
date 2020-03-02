package ca.sfu.cmpt276projectaluminium.model;

/**
 * Contains the data about a restaurant and provides getters in an organized manner
 */
public class Restaurant {
    private String trackingNumber;
    private String name;
    private String address;
    private String city;
    private String type;
    private double latitude;
    private double longitude;

    /**
     * Constructor is package private as it should only be called by the Restaurant manager.
     * The only thing creating a restaurant object is the restaurant manager
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
}
