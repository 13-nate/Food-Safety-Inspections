package ca.sfu.cmpt276projectaluminium.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Renders custom cluster class to render images and add restaurant info to pegs
 */
public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private String restaurantName;
    private String address;
    private String hazardLvl;
    private int iconHazard;

    public ClusterMarker(LatLng position, String title, String snippet, String restaurantName, String address, String hazardLvl, int iconHazard) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.restaurantName = restaurantName;
        this.address = address;
        this.hazardLvl = hazardLvl;
        this.iconHazard = iconHazard;
    }

    public ClusterMarker() {

    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHazardLvl() {
        return hazardLvl;
    }

    public void setHazardLvl(String hazardLvl) {
        this.hazardLvl = hazardLvl;
    }

    public int getIconHazard() {
        return iconHazard;
    }

    public void setIconHazard(int iconHazard) {
        this.iconHazard = iconHazard;
    }
}
