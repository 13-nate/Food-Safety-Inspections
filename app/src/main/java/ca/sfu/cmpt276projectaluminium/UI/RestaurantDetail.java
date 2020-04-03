package ca.sfu.cmpt276projectaluminium.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.model.Inspection;
import ca.sfu.cmpt276projectaluminium.model.InspectionManager;
import ca.sfu.cmpt276projectaluminium.model.Restaurant;
import ca.sfu.cmpt276projectaluminium.model.RestaurantManager;

/**
 * Implements Restaurant details and gets data for it
 * Also grants access to the inspection details activity
 * Sources:
 * https://www.flaticon.com/search?search-type=icons&word=Food&license=&color=&stroke=&current_section=&author_id=&pack_id=&family_id=&style_id=2&category_id=
 * https://stackoverflow.com/questions/14545139/android-back-button-in-the-title-bar
 * https://stackoverflow.com/questions/28144657/android-error-attempt-to-invoke-virtual-method-void-android-app-actionbar-on
 * */

public class RestaurantDetail extends AppCompatActivity {

    private static final String TAG = "RestaurantId";
    private static final String lastActivity = "";
    private ArrayList<Inspection> inspections = new ArrayList<>();
    private Restaurant restaurant;
    private String id;
    private boolean isFromMap = false;
    private String hazardLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        initializeVariables();
        populateListView();
        loadText();
        registerClickCallBack();

        // Create a back button that we can control
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GpsClickCallBack();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.restaurant_details_menu, menu);
        MenuItem item = menu.findItem(R.id.favourite);
        RestaurantManager restaurants = RestaurantManager.getInstance();
        if (restaurants.isFavourite(id)){
            item.setIcon(R.drawable.favourite);
        }

        return super.onCreateOptionsMenu(menu);
    }

    //https://developer.android.com/training/appbar/actions#java
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.favourite) {
            RestaurantManager restaurants = RestaurantManager.getInstance();
            if (restaurants.isFavourite(id)){
                restaurants.deleteFavourite(id);
                item.setIcon(R.drawable.unfavourite);
            }else{
                restaurants.addFavourite(id);
                item.setIcon(R.drawable.favourite);
            }
            String storeFavourites = "";
            for (String favourites: restaurants.getFavourites()){
                storeFavourites = storeFavourites + ", " + favourites;
            }
            SharedPreferences pref = getSharedPreferences(TAG, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(TAG, storeFavourites);
            editor.apply();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeVariables() {
        id = getIntent().getStringExtra(TAG);

        // Create the restaurant object for the restaurant that was clicked on
        RestaurantManager restaurantManager = RestaurantManager.getInstance();
        restaurant = restaurantManager.recreateRestaurant(id);

        // Get a list of inspections for this restaurant
        InspectionManager inspectionManager = InspectionManager.getInstance();
        inspections = inspectionManager.getInspections(restaurant.getTrackingNumber());
        //if null pointer thrown, an invalid id was passed

        isFromMap = getIntent().getBooleanExtra(lastActivity,false);
        //if last activity user visited is map activity
    }

    private void populateListView(){
        ArrayAdapter<Inspection> adapter = new inspectionAdapter();
        ListView list = findViewById(R.id.inspectionList);
        list.setAdapter(adapter);
    }

    private void loadText() {
        TextView address = findViewById(R.id.addressText);
        TextView latitude = findViewById(R.id.latitudeText);
        TextView longitude = findViewById(R.id.longitudeText);


        String tempName = restaurant.getName();
        String tempAddress = restaurant.getAddress();
        String tempLatitude = "" + restaurant.getLatitude();
        String tempLongitude = "" + restaurant.getLongitude();

        getSupportActionBar().setTitle(tempName);
        address.setText(tempAddress);
        latitude.setText(tempLatitude);
        longitude.setText(tempLongitude);
    }

    public static Intent makeIntent(Context context, String restaurantId, boolean isFromMap){
        Intent intent = new Intent(context, RestaurantDetail.class);
        intent.putExtra(TAG, restaurantId);
        intent.putExtra(lastActivity,isFromMap);
        return intent;
    }

    private class inspectionAdapter extends ArrayAdapter<Inspection> {
        inspectionAdapter(){
            super(RestaurantDetail.this, R.layout.inspections_view, inspections);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listView = convertView;

            if (listView == null){
                listView = getLayoutInflater().inflate(R.layout.inspections_view, parent, false);
            }

            Inspection inspection = inspections.get(position);
            ImageView imageView = listView.findViewById(R.id.hazardIcon);
            hazardLevel = inspection.getHazardRating();

            if (hazardLevel.equals("Low")){
                imageView.setImageResource(R.drawable.hazard_low);
                listView.setBackground(getDrawable(R.drawable.border_green));
            } else if (hazardLevel.equals("Moderate")){
                imageView.setImageResource(R.drawable.hazard_medium);
                listView.setBackground(getDrawable(R.drawable.border_yellow));
            } else if (hazardLevel.equals("High")){
                imageView.setImageResource(R.drawable.hazard_high);
                listView.setBackground(getDrawable(R.drawable.border_red));
            } else {
                imageView.setImageResource(R.drawable.not_available);
                listView.setBackground(getDrawable(R.drawable.border_blue));
            }

            TextView date = listView.findViewById(R.id.Date);
            TextView critical = listView.findViewById(R.id.critical);
            TextView noncritical = listView.findViewById(R.id.noncritical);

            String inspectionDate = getString(R.string.inspection_date);
            String criticalViolations = getString(R.string.critical_issues);
            String nonCriticalViolations = getString(R.string.non_critical_issues);

            inspectionDate = inspectionDate + " " + inspection.intelligentDate();
            criticalViolations = criticalViolations + " " + inspection.getNumCriticalViolations();
            nonCriticalViolations = nonCriticalViolations + " " + inspection.getNumNonCriticalViolations();

            date.setText(inspectionDate);
            critical.setText(criticalViolations);
            noncritical.setText(nonCriticalViolations);

            return listView;
        }
    }
    private void registerClickCallBack() {
        ListView list = findViewById(R.id.inspectionList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Inspection clickedInspection = inspections.get(position);
                String trackingNumber = clickedInspection.getTrackingNumber();
                int date = clickedInspection.getInspectionDate();
                String type = clickedInspection.getType();

                Intent intent = InspectionDetails.makeIntent(RestaurantDetail.this,
                        trackingNumber, date, type);
                startActivity(intent);
            }
        });
    }

    private void GpsClickCallBack() {
        ConstraintLayout gpsCords = findViewById(R.id.GpsFrame);
        gpsCords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MapsActivity.makeGPSIntent(RestaurantDetail.this, restaurant.getLatitude(),
                        restaurant.getLongitude(), restaurant.getName(), restaurant.getTrackingNumber(),
                        restaurant.getAddress(), hazardLevel, true);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onBackPressed(){
        Log.i(TAG,"onBackPressed");
        Intent intent = null;
        if(isFromMap) {
            intent =MapsActivity.makeGPSIntent(RestaurantDetail.this, restaurant.getLatitude(),
                    restaurant.getLongitude(), restaurant.getName(), restaurant.getTrackingNumber(),
                    restaurant.getAddress(), hazardLevel, true);
            startActivity(intent);
        } else {
            intent = MainActivity.makeIntent(RestaurantDetail.this);
            startActivity(intent);
        }
    }
}

