package ca.sfu.cmpt276projectaluminium.model;

import com.google.maps.android.clustering.ClusterItem;
import com.google.android.gms.maps.model.LatLng;

public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private String restaurantName;
    private String address;
    private String hazardLvl;
    private int iconHazard;

}
