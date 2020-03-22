package ca.sfu.cmpt276projectaluminium.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.UI.MapsActivity;
import ca.sfu.cmpt276projectaluminium.model.ClusterMarker;
import ca.sfu.cmpt276projectaluminium.model.CustomInfoWindowAdapter;

public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {
    // generates icons
    private final IconGenerator iconGenerator;

    private ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;

    public MyClusterManagerRenderer(Context context, GoogleMap map,
                                    ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        // readies iconGenerator and sets up the image view
        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int) context.getResources().getDimension((R.dimen.custom_marker_padding));
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);

    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {

        imageView.setImageResource((item.getIconHazard()));
        iconGenerator.setStyle(IconGenerator.STYLE_PURPLE);
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
    }
    // source: https://github.com/googlemaps/android-maps-utils/blob/master/demo/src/main/java/com/google/maps/android/utils/demo/CustomMarkerClusteringDemoActivity.java

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        //bigger than one cluster
        return cluster.getSize() > 2;
    }

}

