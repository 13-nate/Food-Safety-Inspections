package ca.sfu.cmpt276projectaluminium.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.model.ClusterMarker;
import ca.sfu.cmpt276projectaluminium.model.CustomInfoWindowAdapter;
import ca.sfu.cmpt276projectaluminium.model.Inspection;
import ca.sfu.cmpt276projectaluminium.model.InspectionManager;
import ca.sfu.cmpt276projectaluminium.model.MyClusterManagerRenderer;
import ca.sfu.cmpt276projectaluminium.model.Restaurant;
import ca.sfu.cmpt276projectaluminium.model.RestaurantManager;
import ca.sfu.cmpt276projectaluminium.model.SearchFilter;


/**
 * Displays the google map
 *
 * It first asks for permissions then checks the location permissions and then will display there
 * location
 *
 * Sources:
 * https://www.youtube.com/playlist?list=PLgCYzUzKIBE-vInwQhGSdnbyJ62nixHCt
 * https://www.youtube.com/playlist?list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi
 * The above sources are video playLists that where used to do alot of this class
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<ClusterMarker>,
        ClusterManager.OnClusterItemInfoWindowClickListener<ClusterMarker>,
        ClusterManager.OnClusterItemClickListener<ClusterMarker> {


    private static final String TAGMAP = "MapsActivity";
    private static final String MAKE_GPS_INTENT_LATITUDE = "make gps intent latitude";
    private static final String MAKE_GPS_INTENT_LONGITUDE = "make gps intent longitude";
    private static final String MAKE_GPS_INTENT_TITLE = "make gps intent title";
    private static final String MAKE_GPS_INTENT_ADDRESS = "make gps intent address";
    private static final String MAKE_GPS_INTENT_HAZARD_RATING = "make gps intent hazardRating";
    private static final String MAKE_GPS_INTENT_BOOL = "makeGPSIntent bool";
    private static final String MAKE_GPS_INTENT_NUM = "makeGPSIntent num";
    private static final String MESSAGE_DIALOGUE = "MESSAGE_DIALOGUE";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    public static final String MAKE_GPS_INTENT_TOTAL_CRITICAL_VIOLATIONS = "make gps intent totalCriticalViolations";
    SearchFilter searchFilter;

    public static Context contextApp;


    private static final String favouritesTAG = "RestaurantId";
    private GoogleMap mMap;
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ClusterManager mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private Boolean restaurantCoordinatesRequest = false;
    private Boolean mapInitialized = false;
    private LatLng restaurantPosition;
    // lets us go to user location on start up
    private boolean mapSartUp = true;
    // This is used to remove the marker from the manager so there are no duplicates when coming
    // back from a gpsClick
    private ClusterMarker restaurantClusterMarker;
    private LocationManager locationManager;
    private MyLocationListener myLocListener;
    private boolean goToRestaurant;
    private ArrayList<ClusterMarker> mClusterMarkersCopy = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        if (checkMapServices()) {
            // checks that all three permissions granted
            if (mLocationPermissionGranted) {
                requestLocationUpdates();
                /*loads the custom map but double draws  when initMap is called again  so need to check
                if it has been initialized before done inside the initMap method*/
            } else {
                getLocationPermission();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        contextApp = getApplicationContext();
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
    }


    // Sources: https://www.youtube.com/watch?v=oh4YOj9VkVE
    // https://www.youtube.com/watch?v=sJ-Z9G0SDhc
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_Search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mClusterMarkersCopy.clear();
                String searchText = newText.toLowerCase().trim();
                mClusterManager.clearItems();
                if (searchText == null || searchText.length() == 0) {
                    // in this case show all restaurants, mClusterMarkers has all of them
                    searchFilter.resetSearchTerm();
                    mClusterMarkersCopy = applyFilters();
                    mClusterManager.addItems(mClusterMarkersCopy);
                    mClusterManager.cluster();
                } else {
                    searchFilter.setSearchTerm(searchText);
                    mClusterMarkersCopy = applyFilters();
                    mClusterManager.addItems(mClusterMarkersCopy);
                    mClusterManager.cluster();
                }
                return false;
            }
        });
        return true;
    }

    // Fill our model with the csv data
    void initializeManagers(InputStream inputStreamRestaurant, InputStream inputStreamInspection) {
        // Fill the RestaurantManager with restaurants using the csv file stored in raw resources
        RestaurantManager restaurantManager = RestaurantManager.getInstance(inputStreamRestaurant);
        // Fill the InspectionManager with inspections using the csv file stored in raw resources
        InspectionManager inspectionManager = InspectionManager.getInstance(inputStreamInspection);
    }

    //gets data from the csv
    //if there is no csv files on internal memory, take the default one
    //also runs checkFileDate, which checks for download
    private void getData() {
        RestaurantManager restaurants = RestaurantManager.getInstance();

        if (restaurants.isUpdateData()){
            getFavourites(restaurants);

            restaurants.setUpdateData(false);
            checkFileDate();
            InputStream inputStreamRestaurant = null;
            InputStream inputStreamInspection = null;

            List<Restaurant> favouriteList = new ArrayList<>();
            //keep old inspections around, to use later when checking for new inspections
            List<List<Inspection>> inspections = new ArrayList<>();

            getOldInspections(restaurants, favouriteList, inspections);
            try {
                inputStreamRestaurant = openFileInput(ProgressMessage.fileFinalRestaurant);
                inputStreamInspection = openFileInput(ProgressMessage.fileFinalInspection);
                initializeManagers(inputStreamRestaurant, inputStreamInspection);
            } catch (FileNotFoundException e) {
                try {
                    if (inputStreamRestaurant != null) {
                        inputStreamRestaurant.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                initializeManagers(getResources().openRawResource(R.raw.restaurants_itr1),
                        getResources().openRawResource(R.raw.inspectionreports_itr1));
            }

            //compared the old data with the new ones.
            if (restaurants.isCheckFavourites()) {
                compareFavourites(restaurants, favouriteList, inspections);
            }

            deleteOldFavourites(restaurants);
        }
    }

    private void getFavourites(RestaurantManager restaurants) {
        SharedPreferences pref = getSharedPreferences(favouritesTAG, Context.MODE_PRIVATE);
        String favourites = pref.getString(favouritesTAG, "");
        String[] splitFavourites = favourites.split(", ");

        //reset current favourite list
        restaurants.resetFavourites();
        for (String favourite: splitFavourites){
            restaurants.addFavourite(favourite);
        }
    }

    private void getOldInspections(RestaurantManager restaurants, List<Restaurant> favouriteList, List<List<Inspection>> inspections) {
        //saves the current favourited restaurants to check later
        //only runs when checkfavourite is true, which is set when download is complete
        InspectionManager oldInspections = InspectionManager.getInstance();
        if (restaurants.isCheckFavourites()){
            for (Restaurant restaurant: restaurants){
                if (restaurants.isFavourite(restaurant.getTrackingNumber())){
                    favouriteList.add(restaurant);
                    inspections.add(
                            oldInspections.getInspections(restaurant.getTrackingNumber()));
                }
            }
        }
    }

    private void compareFavourites(RestaurantManager restaurants,
                                   List<Restaurant> favouriteList,
                                   List<List<Inspection>> inspections) {

        restaurants = RestaurantManager.getInstance();
        restaurants.setCheckFavourites(false);

        List<Restaurant> updatedFavourites = new ArrayList<>();

        InspectionManager inspectionManager = InspectionManager.getInstance();
        for (Restaurant restaurant : favouriteList) {
            for (Restaurant newRestaurant : restaurants) {
                //if the current restaurant is a favourite
                if (restaurant.getTrackingNumber().equals(newRestaurant.getTrackingNumber())) {
                    int oldInspectionSize = inspections.size();
                    int newInspectionSize = inspectionManager
                            .getInspections(newRestaurant.getTrackingNumber()).size();

                    //if the inspections were updated
                    if (oldInspectionSize != newInspectionSize) {
                        updatedFavourites.add(newRestaurant);
                    }
                }
            }
        }
        //figure out ui for showing the updated restaurants
        showDialogPopup(updatedFavourites, getString(R.string.updated_restaurants));
    }

    private void deleteOldFavourites(RestaurantManager restaurants) {
        for (String favourite: restaurants.getFavourites()){
            boolean restaurantExists = false;
            for (Restaurant restaurant: restaurants){
                restaurantExists = true;
            }
            if (!restaurantExists){
                restaurants.deleteFavourite(favourite);
            }
        }
    }

    //checks the file date to see if we should check for a new update
    private void checkFileDate(){
        Date currentDate = Calendar.getInstance().getTime();

        String tempPath = getFilesDir().getAbsolutePath();

        File fileRestaurant = new File
                (tempPath + "/" + ProgressMessage.fileFinalRestaurant);

        File fileInspection = new File
                (tempPath + "/" + ProgressMessage.fileFinalInspection);
        if (fileRestaurant.exists() && fileInspection.exists()){

            Date fileDateRestaurant = new Date(fileRestaurant.lastModified());
            Date fileDateInspection = new Date(fileInspection.lastModified());

            long timeDifferentRestaurant = currentDate.getTime() - fileDateRestaurant.getTime();
            long diffRestaurant = TimeUnit.MILLISECONDS.toHours(timeDifferentRestaurant);

            long timeDifferentInspection = currentDate.getTime() - fileDateInspection.getTime();
            long diffInspection = TimeUnit.MILLISECONDS.toHours(timeDifferentInspection);

            if (diffInspection > 20 || diffRestaurant > 20){
                //runs updatechecker, which is a new thread of execution that checks if
                //whether there is new data on the website
                new updateChecker().execute();
            }
        } else {
            //runs updatechecker, which is a new thread of execution that checks if
            //whether there is new data on the website
            new updateChecker().execute();
        }
    }

    // this calls to heck for google play and then checks for gps
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
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
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
        if (!mapInitialized) {
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
        myLocListener = new MyLocationListener();
        // The minimum time (in milliseconds) the system will wait until checking if the location changed
        int minTime = 5000;
        // The minimum distance (in meters) traveled until notified
        float minDistance = 1;
        // Get the location manager from the system
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            // on map
            if (mapSartUp && !goToRestaurant) {
                moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM);
                mapSartUp = false;
            } else {
                currentZoom = mMap.getCameraPosition().zoom;
                // go to a restaurantLocation or user location
                if (goToRestaurant) {
                    // in this case the we want to look at a restaurant and not the user so don't move the camera
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
        getData();
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

        initManagerAndRenderer();
        /* Don't want a random infoWindow to appear, because the last icon rendered will have
            one showing if there is more than one marker on the map, in the case where there is
            only one marker on the map we want one to shoe because we called goToRestaurantLocation
            and want to show the user that restaurants infoWindow
         */
        Intent intent =getIntent();
        restaurantCoordinatesRequest = intent.getBooleanExtra(MAKE_GPS_INTENT_BOOL, false);
        if(restaurantCoordinatesRequest) {
            mClusterManagerRenderer.setShouldRenderInfoWindow(true);
        } else {
            mClusterManagerRenderer.setShouldRenderInfoWindow(false);
        }
        // Don't add the map markers just want to see one marker the one we make in goToRestaurantGpsLocation();
        if(!restaurantCoordinatesRequest) {
            addMapMarkers();
        }

        // Only executes  if coming from  a restaurant
        goToRestaurantGpsLocation();
        onMapClickCallBack();
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.setOnClusterItemInfoWindowClickListener(MapsActivity.this);
        getDeviceLocation();
        filterClickCallBack();
    }

    private void filterClickCallBack() {
        ImageView searchSettings = findViewById(R.id.searchSettings);
        searchSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = FilterActivity.makeIntent(MapsActivity.this, true);
                startActivity(intent);
            }
        });
    }

    //sources: https://www.baeldung.com/java-concurrentmodificationexception
    private ArrayList<ClusterMarker> applyFilters() {
        searchFilter = SearchFilter.getInstance();
        mClusterMarkersCopy.clear();
        List<String> filterList = searchFilter.getRestaurantTrackingNumbers();
         for(ClusterMarker clusterMarker: mClusterMarkers) {
             String trackingNum = clusterMarker.getTrackingNum();
             if (filterList.contains(trackingNum)) {
                 mClusterMarkersCopy.add(clusterMarker);
             }
         }
        return mClusterMarkersCopy;
    }


    public void onMapClickCallBack() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng clickedPosition) {
                Log.d(TAGMAP, "in map clickListener");
                if(restaurantCoordinatesRequest){
                    Log.d(TAGMAP, "ifStateMent");
                    mClusterManager.removeItem(restaurantClusterMarker);
                    mClusterMarkersCopy.remove(restaurantClusterMarker);
                    mClusterMarkers.remove(restaurantClusterMarker);
                    initManagerAndRenderer();
                    mClusterManagerRenderer.setShouldRenderInfoWindow(false);
                    addMapMarkers();
                    restaurantCoordinatesRequest =false;
                    goToRestaurant = false;
                }
            }
        });
    }

    private void getDeviceLocation() {
        if(!goToRestaurant) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            try {
                if (mLocationPermissionGranted) {
                    Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Log.d(TAGMAP, "found Location");
                                Location currentLocation = (Location) task.getResult();
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                            } else {
                                Log.d(TAGMAP, "Location is null");
                                Toast.makeText(MapsActivity.this,
                                        "Unable to get current location", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                }
            } catch (SecurityException e) {
                Log.e(TAGMAP, "getDeviceLocation: securityException: " + e.getMessage());
            }
        }
    }
    public void initManagerAndRenderer() {
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
            }
            mClusterManager.setRenderer(mClusterManagerRenderer);
            //set up info windows
            mClusterManager.getMarkerCollection().setInfoWindowAdapter(
                    new CustomInfoWindowAdapter(MapsActivity.this)
            );
        }
    }


    // Create a dialog with a list filled with restaurants that are in the cluster
    // Then show it
    private void showDialogPopup(List<Restaurant> restaurants, String title) {
        // Create builder for dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this,
                R.style.darkDialogTheme);
        // Set attributes for dialog
        builder.setTitle(title + "\n");

        // Create cancel button for dialog
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        // Sort the restaurants by alphabetical order
        Collections.sort(restaurants);

        // Use an arrayAdapter to converts objects to a list view
        final ArrayAdapter<Restaurant> arrayAdapter
                = new RestaurantListAdapter(MapsActivity.this, restaurants);
        ListView restaurantListView = new ListView(this);
        restaurantListView.setDividerHeight(15);
        restaurantListView.setAdapter(arrayAdapter);

        // Make the list view elements clickable
        // - Clicking a restaurant will take you to the restaurant details activity
        restaurantListView.setOnItemClickListener((parent, view, position, id) -> {
            Restaurant clickedRestaurant = restaurants.get(position);
            Intent intent = RestaurantDetail.makeIntent(MapsActivity.this,
                    clickedRestaurant.getTrackingNumber(), true);
            startActivity(intent);
        });

        // Put the listView inside our dialog
        builder.setView(restaurantListView);

        // Show our dialog
        builder.show();
    }


    /**
     * Loop through all the restaurants and place markers
     * Sources: https://codinginfinite.com/android-google-map-custom-marker-clustering/
     * */
    private void addMapMarkers() {
        Log.d(TAGMAP, "adding all markers");
        RestaurantManager restaurantManager;
        // Make sure map not null
        if (mMap != null) {
            initManagerAndRenderer();
            mClusterManager.clearItems();
            mMap.clear();
            mClusterMarkers.clear();
            /*
            Create a listener for clusters.  The listener will open a list of restaurants
            inside the cluster.  This will be used to display clustered restaurants.
            Source:
            - https://stackoverflow.com/questions/15762905/how-can-i-display-a-list-view-in-an-android-alert-dialog/15763023
            - https://stackoverflow.com/questions/3718523/create-listview-programmatically/6157182
            - https://stackoverflow.com/questions/18346920/change-the-background-color-of-a-pop-up-dialog
            */
            mClusterManager.setOnClusterClickListener(cluster -> {
                // Used to recreate restaurant objects
                RestaurantManager rManager = RestaurantManager.getInstance();
                 // Will hold restaurants that are inside the cluster
                 List<Restaurant> restaurants = new ArrayList<>();
                // For each marker inside the cluster, store the corresponding restaurant
                for (Object o : cluster.getItems()) {
                    ClusterMarker cm = (ClusterMarker) o;
                    restaurants.add(rManager.recreateRestaurant(cm.getTrackingNum()));
                }
             // Create a dialog popup that has a list with all restaurants inside the cluster
             showDialogPopup(restaurants, getString(R.string.restaurants_at_location));
             return false;
            });
            // for each restaurant get there details
            restaurantManager = RestaurantManager.getInstance();
            for (Restaurant r : restaurantManager) {
                String snippet;
                int criticalViolationsWithinAYear = 0;
                try {
                    // Get relevant inspections
                    InspectionManager inspectionManager = InspectionManager.getInstance();
                    ArrayList<Inspection> inspections;
                    inspections = inspectionManager.getInspections(r.getTrackingNumber());
                    criticalViolationsWithinAYear = inspectionManager.getNumCriticalViolationsWithinYear(r.getTrackingNumber());
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
                            hazardRating,
                            iconHazard,
                            r.getTrackingNumber(),
                            criticalViolationsWithinAYear
                    );
                    // reference list for markers
                    mClusterMarkers.add(newClusterMarker);
                } catch (NullPointerException e) {
                    Log.e(TAGMAP, "addMapMarkers: NullPointerException: " + e.getMessage());
                }
            }
            mClusterMarkersCopy.addAll(mClusterMarkers);
            mClusterMarkersCopy = applyFilters();
            // adds every thing to the map at end of the loop
            mClusterManager.addItems(mClusterMarkersCopy);
            mClusterManager.cluster();
        }
    }
    /*Sources:
        https://androidwave.com/bottom-navigation-bar-android-example/
        https://stackoverflow.com/questions/48413808/android-bottomnavigationview-onnavigationitemselectedlistener-code-not-running
     */
    private void onBottomToolBarClick() {
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
                }
                return false;
            }
        });
    }

    /**
     * This way the user can see which activity they are in and can easily tell which icon
     * represents what, not use fragments for both so need to force a color change between activities
     * source: https://stackoverflow.com/questions/30967851/change-navigation-view-item-color-dynamically-android?rq=1
     * https://stackoverflow.com/questions/48413808/android-bottomnavigationview-onnavigationitemselectedlistener-code-not-running
     */
    private void setMenuColor() {
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

    @Override
    public void onClusterItemInfoWindowClick(ClusterMarker clickedMarker) {
        for (ClusterMarker item : mClusterMarkers) {
            if (item.getPosition() == clickedMarker.getPosition()) {
                Intent intent = RestaurantDetail.makeIntent(MapsActivity.this,
                        clickedMarker.getTrackingNum(), true);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public boolean onClusterItemClick(ClusterMarker item) {
        if (item == null) {
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

    public static Intent makeGPSIntent(Context context, double latitude, double longitude,
                                       String title, String trackingNum, String address,
                                       String hazardRating, int totalCriticalViolations, boolean gpsIntent) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(MAKE_GPS_INTENT_LATITUDE, latitude);
        intent.putExtra(MAKE_GPS_INTENT_LONGITUDE, longitude);
        intent.putExtra(MAKE_GPS_INTENT_TITLE, title);
        intent.putExtra(MAKE_GPS_INTENT_ADDRESS, address);
        intent.putExtra(MAKE_GPS_INTENT_NUM, trackingNum);
        intent.putExtra(MAKE_GPS_INTENT_HAZARD_RATING, hazardRating);
        intent.putExtra(MAKE_GPS_INTENT_BOOL, gpsIntent);
        intent.putExtra(MAKE_GPS_INTENT_TOTAL_CRITICAL_VIOLATIONS, totalCriticalViolations);
        return intent;
    }

    public static Intent makeIntent(Context context, boolean gpsIntent) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(MAKE_GPS_INTENT_BOOL, gpsIntent);
        return intent;
    }

    //Source: https://stackoverflow.com/questions/21253303/exit-android-app-on-back-pressed
    public void onBackPressed() {
        Log.i(TAGMAP, "onBackPressed");
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    // Sources: https://stackoverflow.com/questions/36902890/how-i-can-call-showinfowindow-in-a-marker-within-cluster-manager
    // https://stackoverflow.com/questions/52288720/how-to-change-visibility-of-markers-in-clustermanager-while-also-having-access-t
    public void goToRestaurantGpsLocation() {
        Intent intent = getIntent();
        restaurantCoordinatesRequest = intent.getBooleanExtra(MAKE_GPS_INTENT_BOOL, false);
        if (restaurantCoordinatesRequest) {
            goToRestaurant = true;
            double latitude = intent.getDoubleExtra(MAKE_GPS_INTENT_LATITUDE, 0);
            double longitude = intent.getDoubleExtra(MAKE_GPS_INTENT_LONGITUDE, 0);
            String title = intent.getStringExtra(MAKE_GPS_INTENT_TITLE);
            String address = intent.getStringExtra(MAKE_GPS_INTENT_ADDRESS);
            String hazardRating = intent.getStringExtra(MAKE_GPS_INTENT_HAZARD_RATING);
            String trackingNum = intent.getStringExtra(MAKE_GPS_INTENT_NUM);
            int criticalViolationsWithinAYear = intent.getIntExtra(MAKE_GPS_INTENT_TOTAL_CRITICAL_VIOLATIONS,0);

            String snippet = address + "\n "
                    + getString(R.string.hazard_level) + " " + hazardRating;
            restaurantPosition = new LatLng(latitude, longitude);

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
            ClusterMarker restaurantMaker = new ClusterMarker(
                    restaurantPosition,
                    title,
                    snippet,
                    hazardRating,
                    iconHazard,
                    trackingNum,
                    criticalViolationsWithinAYear
            );
            mClusterManager.addItem(restaurantMaker);
            mClusterMarkers.add(restaurantMaker);
            mClusterMarkersCopy.add(restaurantMaker);
            restaurantClusterMarker = restaurantMaker;
        }
        try {
            moveCamera(restaurantPosition, DEFAULT_ZOOM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //a new thread of execution that runs to check if there is new data avaliable
    private class updateChecker extends AsyncTask<Void, Void, Void> {

        private Date lastModifiedRestaurant;
        private Date lastModifiedInspection;
        boolean exceptionRaised = true;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                readRestaurant();
                readInspection();
                exceptionRaised = false;
            } catch (IOException | JSONException | ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //if exception is raised, an error message will run instead of prompting download
            if (exceptionRaised){
                FragmentManager manager = getSupportFragmentManager();
                ErrorMessage dialog = new ErrorMessage();
                dialog.show(manager, MESSAGE_DIALOGUE);
            } else {
                promptDownload();
            }
        }

        //takes in a json and checks whether the csv file has updated
        private void readRestaurant() throws IOException, JSONException, ParseException {
            URL url = new URL("https://data.surrey.ca/api/3/action/package_show?id=restaurants");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String totalInput = "";
            String inputLine;

            //reads in the entire json
            while ((inputLine = in.readLine()) != null) {
                totalInput += inputLine;
            }
            in.close();
            JSONObject json = new JSONObject(totalInput);

            JSONObject jsonObject = json.getJSONObject("result");
            JSONArray jsonArray = jsonObject.getJSONArray("resources");
            jsonObject = jsonArray.getJSONObject(0);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            String lastModified = jsonObject.getString("last_modified");
            lastModified = lastModified.replace("T", " ");
            lastModified = lastModified.substring(0, lastModified.length() - 3);
            lastModifiedRestaurant = simpleDateFormat.parse(lastModified);

            connection.disconnect();
        }

        //takes in a json and checks whether the csv file has updated
        private void readInspection() throws IOException, JSONException, ParseException {
            URL url = new URL("https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String totalInput = "";
            String inputLine;

            //reads in the entire json
            while ((inputLine = in.readLine()) != null) {
                totalInput += inputLine;

            }
            in.close();
            JSONObject json = new JSONObject(totalInput);

            JSONObject jsonObject = json.getJSONObject("result");
            JSONArray jsonArray = jsonObject.getJSONArray("resources");
            jsonObject = jsonArray.getJSONObject(0);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            String lastModified = jsonObject.getString("last_modified");
            lastModified = lastModified.replace("T", " ");
            lastModified = lastModified.substring(0, lastModified.length() - 3);
            lastModifiedInspection = simpleDateFormat.parse(lastModified);

            connection.disconnect();
        }

        //checks whether we should prompt a download
        //this is based on whether there is new data, and whether we have asked the user before
        private void promptDownload() {
            String tempPath = MapsActivity.this.getFilesDir().getAbsolutePath();

            File fileRestaurant = new File
                    (tempPath + "/" + ProgressMessage.fileFinalRestaurant);

            File fileInspection = new File
                    (tempPath + "/" + ProgressMessage.fileFinalInspection);

            if (fileRestaurant.exists() && fileInspection.exists()) {
                Date fileDateRestaurant = new Date(fileRestaurant.lastModified());
                Date fileDateInspection = new Date(fileInspection.lastModified());

                if (lastModifiedInspection != null && lastModifiedRestaurant != null){
                    long timeDifferentRestaurant =
                            lastModifiedRestaurant.getTime() - fileDateRestaurant.getTime();

                    long timeDifferentInspection =
                            lastModifiedInspection.getTime() - fileDateInspection.getTime();

                    if (checkFirstTime()
                            && ((timeDifferentRestaurant) > 0 || (timeDifferentInspection) > 0)) {
                        //opens up the download prompt
                        FragmentManager manager = getSupportFragmentManager();
                        DownloadMessage dialog = new DownloadMessage();
                        dialog.show(manager, MESSAGE_DIALOGUE);
                    }
                }
            } else if (checkFirstTime()) {
                //opens up the download prompt
                FragmentManager manager = getSupportFragmentManager();
                DownloadMessage dialog = new DownloadMessage();
                dialog.show(manager, MESSAGE_DIALOGUE);
            }

        }

        //checks whether this is the first time the prompt has been raised this application run
        private boolean checkFirstTime(){
            RestaurantManager restaurants = RestaurantManager.getInstance();

            if (restaurants.isFirstRun()){
                restaurants.setFirstRun(false);
                return true;
            }
            return false;
        }
    }

    public static Context getContextApp() {
        return contextApp;
    }

}
