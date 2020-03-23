package ca.sfu.cmpt276projectaluminium.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Collection;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.model.ClusterMarker;
import ca.sfu.cmpt276projectaluminium.model.CustomInfoWindowAdapter;
import ca.sfu.cmpt276projectaluminium.model.Inspection;
import ca.sfu.cmpt276projectaluminium.model.InspectionManager;
import ca.sfu.cmpt276projectaluminium.model.Restaurant;
import ca.sfu.cmpt276projectaluminium.model.RestaurantManager;


/**
 * Displays the google map.
 *
 * It first asks for permissions then checks the location permissions and then will display there
 * location
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<ClusterMarker>,
        ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker> {

    private static final String TAGMAP = "MapsActivity";
    private LocationCallback locationCallback;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;


    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ClusterManager mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private Boolean restaurantCordinatesRequest = false;
    private Boolean mapInitialized = false;
    // want to change camera to User on startUp
    private boolean mapSartUp = true;

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            // checks that all three permissions granted
            if (mLocationPermissionGranted) {
                requestLocationUpdates();
                /*loads the custom map but double draws  when initMap is called again  so need to check
                if it has been initialized before done inside the initMap method*/
                initMap();

            } else {
                getLocationPermission();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        onBottomToolBarClick();
        setMenuColor();

        if (checkMapServices()) {
            // checks that all three permissions granted
            if (mLocationPermissionGranted) {
                initMap();
                requestLocationUpdates();
            } else {
                getLocationPermission();
            }
        }
        requestLocationUpdates();
        goToRestaurantGpsLocation();
    }

    //Give the csv files to the data classes so that the csv files can be read
    void initializeDataClasses() {
        // Fill the RestaurantManager with restaurants using the csv file stored in raw resources
        RestaurantManager restaurantManager = RestaurantManager.getInstance();
        // Fill the InspectionManager with inspections using the csv file stored in raw resources
        InspectionManager inspectionManager = InspectionManager.getInstance();
        if (restaurantManager.getSize() == 0) {
            restaurantManager.initialize(getResources().openRawResource(R.raw.restaurants_itr1));
            inspectionManager.initialize(getResources().openRawResource(R.raw.inspectionreports_itr1));
        }
    }

    // this calls to heck for goolge play and then checks for gps
    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapsActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            // user can make map requests
            Log.d(TAGMAP, "Play services working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occurred but it can be fixed, versioning issue
            Log.d(TAGMAP, "Play services NOT working, but can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().
                    getErrorDialog(MapsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            // nothing we can do
            Toast.makeText(this, getString(R.string.no_map_requests), Toast.LENGTH_SHORT).show();
        }
        // There is a problem so return false
        return false;
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.no_gps_do_u_want))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {

                } else {
                    getLocationPermission();
                }
            }
        }
    }

    private void getLocationPermission() {
        /* Need to explicitly check permissions to use a device location
        we need to only check for ONE course or fine, fine is better because i want to get users
        exact location top place them
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            initMap();
            requestLocationUpdates();
        } else {
            // Ask them to use location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // assume false to begin with
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // check the other permissions
                    for (int i = 0; i < grantResults.length; i++) {
                        mLocationPermissionGranted = true;
                    }
                }
            }
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(!mapInitialized) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mapInitialized = true;
        }
    }

    /*Source: https://codelabs.developers.google.com/codelabs/realtime-asset-tracking/index.html?index=..%2F..index#3
    https://stackoverflow.com/questions/34372990/android-how-to-check-if-mylocation-is-visible-on-the-map-at-the-current-zoom-le
    https://sites.google.com/site/androidhowto/how-to-1/get-notified-when-location-changes
    https://blog.codecentric.de/en/2014/05/android-gps-positioning-location-strategies/
    */
    private void requestLocationUpdates() {
        MyLocationListener myLocListener = new MyLocationListener();
        // The minimum time (in milliseconds) the system will wait until checking if the location changed
        int minTime = 1;
        // The minimum distance (in meters) traveled until notified
        float minDistance = .5f;
        // Get the location manager from the system
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Get the best provider from the criteria specified, and false to say it can turn the provider on if it isn't already
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return;
        }
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(true); // Choose if you use bearing.
        criteria.setCostAllowed(true);

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        String bestProvider = locationManager.getBestProvider(criteria, false);        // Request location updates
        // use gps for accuracy
        locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, myLocListener);
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            float currentZoom;
            //zoom and go to userLocation if we don't want to go a restaurant and app just started
            Intent intent = getIntent();
            boolean goToRestaurant = intent.getBooleanExtra("makeGPSIntent bool", false);
            if(mapSartUp && !goToRestaurant) {
                currentZoom = DEFAULT_ZOOM;
                moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), currentZoom);
            } else {
                currentZoom = mMap.getCameraPosition().zoom;
                // go to a restaurantLocation or user location
                if (goToRestaurant) {
                    goToRestaurantGpsLocation();
                } else {
                    // get current LatLng of user
                    LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    // for smooth transition
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(userPosition)
                            .zoom(currentZoom)
                            .build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    mMap.animateCamera(cameraUpdate);
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            requestLocationUpdates();
        }

        @Override
        public void onProviderDisabled(String provider) {
            buildAlertMessageNoGps();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initializeDataClasses();
        // For dark mode
        // Source https://github.com/googlemaps/android-samples
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAGMAP, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAGMAP, "Can't find style. Error: ", e);
        }

        if (mLocationPermissionGranted) {
            requestLocationUpdates();
            mMap.setMyLocationEnabled(true);
        }
        addMapMarkers();
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        // only executes  if coming from  a restaurant
        goToRestaurantGpsLocation();

    }

    //Sources: https://codinginfinite.com/android-google-map-custom-marker-clustering/

    // Loop through all the restaurants and place markers
    private void addMapMarkers() {
        RestaurantManager restaurantManager;

        // Make sure map not null
        if (mMap != null) {
            // Create a new manger if we don't have one.
            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(this, mMap);
            }
            // Create a new render if we don't have one
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = new MyClusterManagerRenderer(
                        this,
                        mMap,
                        mClusterManager
                );
                mClusterManager.setRenderer(mClusterManagerRenderer);
            }

            // for each restaurant get there details
            restaurantManager = RestaurantManager.getInstance();
            for (Restaurant r : restaurantManager) {
                String snippet;
                try {
                    // Get relevant inspections
                    InspectionManager inspectionManager = InspectionManager.getInstance();
                    ArrayList<Inspection> inspections;
                    inspections = inspectionManager.getInspections(r.getTrackingNumber());

                    // Get the newest inspection
                    Inspection newestInspection = inspectionManager.getMostRecentInspection(inspections);
                    String hazardRating = newestInspection.getHazardRating();
                    int iconHazard;
                    if (hazardRating.equals("Low")) {
                        iconHazard = R.drawable.hazard_low;

                    } else if (hazardRating.equals("Moderate")) {
                        iconHazard = R.drawable.hazard_medium;

                    } else if (hazardRating.equals("High")) {
                        iconHazard = R.drawable.hazard_high;

                    } else {
                        iconHazard = R.drawable.not_available;
                    }
                    snippet = r.getAddress() + "\n "
                            + getString(R.string.hazard_level) + " " + hazardRating;

                    //create individual marker per restaurant
                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(r.getLatitude(), r.getLongitude()),
                            r.getName(), //title
                            snippet,
                            iconHazard,
                            r.getTrackingNumber()
                    );
                    // adds cluster to map
                    mClusterManager.addItem(newClusterMarker);
                    // reference list for markers
                    mClusterMarkers.add(newClusterMarker);
                } catch (NullPointerException e) {
                    Log.e(TAGMAP, "addMapMarkers: NullPointerException: " + e.getMessage());
                }

            }
            //set up info windows
            mClusterManager.getMarkerCollection().setInfoWindowAdapter(
                    new CustomInfoWindowAdapter(MapsActivity.this)
            );
            // adds every thing to the map at end of the loop
            mClusterManager.cluster();
        }
    }

    private void onBottomToolBarClick() {
        /*Sources:
        // https://androidwave.com/bottom-navigation-bar-android-example/
        https://stackoverflow.com/questions/48413808/android-bottomnavigationview-onnavigationitemselectedlistener-code-not-running

         */
        BottomNavigationView bottomNavigation;
        bottomNavigation = findViewById(R.id.bottom_navigationMaps);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // switch based on id of item clicked, id defined in the bottom_navigation_menu

                switch (item.getItemId()) {

                    case R.id.navigationBulletList:
                        //in this case we are in the map activity and want to go to main
                        Intent intent = MainActivity.makeIntent(MapsActivity.this);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.navigationMap:
                        // in this case we are in the main activity so don't want anything to happen
                        // require bool value so return true when the item is clicked
                }
                return false;
            }
        });
    }


    // This way the user can see which activity they are in and can easly tell
    // which icon represents what
    private void setMenuColor() {
        // source: https://stackoverflow.com/questions/30967851/change-navigation-view-item-color-dynamically-android?rq=1

        BottomNavigationView bottomNavigation;
        bottomNavigation = findViewById(R.id.bottom_navigationMaps);

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},  // unchecked
                new int[]{android.R.attr.state_checked},   // checked
                new int[]{}                                // default
        };

        // Fill in color corresponding to state defined in state
        int[] colors = new int[]{
                Color.parseColor("#ff0000"),
                Color.parseColor("#000000"),
                Color.parseColor("#ff0000"),
        };

        // set color list
        ColorStateList navigationViewColorStateList = new ColorStateList(states, colors);
        // apply to icon color
        bottomNavigation.setItemIconTintList(navigationViewColorStateList);
    }


    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, MapsActivity.class);
        return intent;
    }

    @Override
    public void onClusterItemInfoWindowClick(ClusterMarker item) {
        for (ClusterMarker clickedMarker : mClusterMarkers) {
            if (item.getPosition() == clickedMarker.getPosition()) {
                Intent intent = RestaurantDetail.makeIntent(MapsActivity.this,
                        clickedMarker.getTrackingNum());
                startActivity(intent);

            }
        }
    }

    @Override
    public boolean onClusterClick(Cluster<ClusterMarker> cluster) {
        if (cluster == null) {
            return false;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (ClusterMarker cMarker : mClusterMarkers)
            builder.include(cMarker.getPosition());
        LatLngBounds bounds = builder.build();
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Intent makeGPSIntent(Context context, String trackingNum, boolean gpsIntent) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra("makeGPSIntent num", trackingNum);
        intent.putExtra("makeGPSIntent bool", gpsIntent);
        return intent;
    }

    public void goToRestaurantGpsLocation() {
        Intent intent = getIntent();
        LatLng restaurantPosition = new LatLng(0.0, 0.0);
        restaurantCordinatesRequest = intent.getBooleanExtra("makeGPSIntent bool", false);
        if (restaurantCordinatesRequest) {
            String trackingNum = intent.getStringExtra("makeGPSIntent num");
            for (ClusterMarker marker : mClusterMarkers) {
                if (trackingNum.equals(marker.getTrackingNum())) {
                    restaurantPosition = marker.getPosition();
                    break;
                }
            }
            try {
                moveCamera(restaurantPosition, DEFAULT_ZOOM);
            } catch (Exception e) {
                e.printStackTrace(); }
        }
    }
}
