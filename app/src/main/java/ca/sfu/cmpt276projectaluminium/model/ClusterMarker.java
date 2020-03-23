package ca.sfu.cmpt276projectaluminium.model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Renders custom cluster class to render images and add restaurant info to pegs
 */
public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private int iconHazard;
    private String trackingNum;

    public ClusterMarker(LatLng position, String title, String snippet, int iconHazard, String trackingNum) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconHazard = iconHazard;
        this.trackingNum = trackingNum;

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

    public int getIconHazard() {
        return iconHazard;
    }

    public void setIconHazard(int iconHazard) {
        this.iconHazard = iconHazard;
    }

    public String getTrackingNum() {
        return trackingNum;
    }

    public void setTrackingNum(String trackingNum) {
        this.trackingNum = trackingNum;
    }

}
