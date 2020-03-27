package ca.sfu.cmpt276projectaluminium.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;


import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.model.ClusterMarker;
/**
 * This changes the ClusterMarkers to the related hazard image and sets there snippet and title
 * sources: https://www.youtube.com/watch?v=LrjybCD1tT0&list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi&index=11
 * https://www.youtube.com/watch?v=U6Z8FkjGEb4&list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi&index=12
 * these are videos from the video playlists mentioned in the Map activity
 */
public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {
    // generates icons
    private final IconGenerator iconGenerator;
    private ImageView imageView;
    private boolean shouldRenderInfoWindow;

    public MyClusterManagerRenderer(Context context, GoogleMap map,
                                    ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        // readies iconGenerator and sets up the image view
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        int markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        int markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension((R.dimen.custom_marker_padding));
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        imageView.setImageResource((item.getIconHazard()));
        iconGenerator.setBackground(null);
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
    }
    // source: https://github.com/googlemaps/android-maps-utils/blob/master/demo/src/main/java/com/google/maps/android/utils/demo/CustomMarkerClusteringDemoActivity.java
    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        // cluster items if cluster size would be bigger than one
        return cluster.getSize() > 1;
    }

    /**
     * This is used to make the clusterItem window appear when we come from the GPS coordinates to
     * the map, since there is only one cluster item showing
     */
    @Override
    protected void onClusterItemRendered(ClusterMarker clusterItem,Marker marker ){
        super.onClusterItemRendered(clusterItem, marker);
        if(shouldRenderInfoWindow) {
            getMarker(clusterItem).showInfoWindow();
        }
    }

    public void setShouldRenderInfoWindow(boolean shouldRenderInfoWindow) {
        this.shouldRenderInfoWindow = shouldRenderInfoWindow;
    }
}

