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

    private class CustomMapClusterRenderer<T extends ClusterItem> extends DefaultClusterRenderer<T> {
        CustomMapClusterRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onClusterItemRendered(T clusterItem,Marker marker ){

            super.onClusterItemRendered(clusterItem, marker);
            getMarker(clusterItem).showInfoWindow();

        }

        @Override
        protected void onClusterRendered(Cluster<T> cluster, Marker marker) {
            super.onClusterRendered(cluster, marker);
            //add infowindow to cluster icon
            marker.setTitle("count");
            marker.setSnippet("Total Count - " + cluster.getItems().size());
        }
    }

}
