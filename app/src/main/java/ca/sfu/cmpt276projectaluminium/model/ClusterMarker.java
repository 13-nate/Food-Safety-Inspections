package ca.sfu.cmpt276projectaluminium.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import ca.sfu.cmpt276projectaluminium.R;

/**
 * These are the custom cluster items to hold some restaurant information when looking at them as
 * markers
 */
public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private String hazardLevel;
    private int iconHazard;
    private String trackingNum;
    private int criticalViolationsWithInAYear;

    public ClusterMarker(LatLng position, String title, String snippet, String hazardLevel,
                         int iconHazard, String trackingNum, int criticalViolationsWithInAYear) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.hazardLevel = hazardLevel;
        this.iconHazard = iconHazard;
        this.trackingNum = trackingNum;
        this.criticalViolationsWithInAYear =criticalViolationsWithInAYear;

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

    public String getHazardLevel() {
        return hazardLevel;
    }

    public void setHazardLevel(String hazardLevel) {
        this.hazardLevel = hazardLevel;
    }

    public int getCriticalViolationsWithInAYear() {
        return criticalViolationsWithInAYear;
    }

    public void setCriticalViolationsWithInAYear(int criticalViolationsWithInAYear) {
        this.criticalViolationsWithInAYear = criticalViolationsWithInAYear;
    }
}
