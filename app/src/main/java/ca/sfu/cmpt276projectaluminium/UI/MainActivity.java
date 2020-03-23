package ca.sfu.cmpt276projectaluminium.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.model.Inspection;
import ca.sfu.cmpt276projectaluminium.model.InspectionManager;
import ca.sfu.cmpt276projectaluminium.model.Restaurant;
import ca.sfu.cmpt276projectaluminium.model.RestaurantManager;

/**
 * Displays a list of restaurants and some info on the most most recent inspection report for
 * each of the restaurants displayed
 */

public class MainActivity extends AppCompatActivity {
    //for incorrect version
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private RestaurantManager manager = RestaurantManager.getInstance();
    private List<Restaurant> restaurantArray = new ArrayList<>();

    //Give the csv files to the data classes so that the csv files can be read
    void initializeDataClasses(InputStream inputStreamRestaurant, InputStream inputStreamInspection) {
        // Fill the RestaurantManager with restaurants using the csv file stored in raw resources
        RestaurantManager restaurantManager = RestaurantManager.getInstance();

        restaurantManager.initialize(inputStreamRestaurant);

        // Fill the InspectionManager with inspections using the csv file stored in raw resources
        InspectionManager inspectionManager = InspectionManager.getInstance();
        inspectionManager.initialize(inputStreamInspection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(getString(R.string.restaurants));

        getData();

        populateListView();
        registerClickCallBack();
        onBottomToolBarClick();
       // setMenuColor();
    }


    private void getData() {

        Button button = findViewById(R.id.data);
        button.setEnabled(false);

        new CSVRetriever(this).execute(button);
        button.setOnClickListener(v -> {
            recreate();
        });

        InputStream inputStreamRestaurant = null;
        InputStream inputStreamInspection = null;
        try {
            inputStreamRestaurant = openFileInput(CSVRetriever.fileRestaurant);
            inputStreamInspection = openFileInput(CSVRetriever.fileInspection);
            initializeDataClasses(inputStreamRestaurant, inputStreamInspection);
            button.setVisibility(View.GONE);

        } catch (FileNotFoundException e) {
            try {
                if (inputStreamRestaurant != null) {
                    inputStreamRestaurant.close();
                }
                if (inputStreamInspection != null) {
                    inputStreamInspection.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            initializeDataClasses(getResources().openRawResource(R.raw.restaurants_itr1),
                    getResources().openRawResource(R.raw.inspectionreports_itr1));
        }

    }

    private void populateListView() {
        // myListAdapter lets me work with the objects
        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.restaurantListView);

        manager = manager.getInstance();
        for(Restaurant r: manager){
            restaurantArray.add(r);
        }

        list.setAdapter(adapter);
    }

    private void registerClickCallBack() {
        ListView list = findViewById(R.id.restaurantListView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /* Can pass the clicked restaurants tracking number in an intent when clicked
                to make a new activity with that restaurants tracking number

                The Inspections can then be gotten for that restaurant when in the next activity
                with using the tracking number
                */

                Restaurant clickedRestaurant = restaurantArray.get(position);
                Intent intent = RestaurantDetail.makeIntent(MainActivity.this, clickedRestaurant.getTrackingNumber());
                startActivity(intent);
            }
        });
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
                        // in this case we are in the main activity so don't want anything to happen
                        // require bool value so return true when the item is clicked
                        return true;
                        case R.id.navigationMap:
                            //in this case we are in the main activity and want to go to maps

                                Intent intent = MapsActivity.makeIntent(MainActivity.this);
                                startActivity(intent);
                                finish();
                            //if we get here return false don't have proper services
                            return false;
                }
                return false;
            }
        });
    }

    public static Intent makeIntent(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    // Inner class has reference to outer class
    private class MyListAdapter extends ArrayAdapter<Restaurant> {
        // Don't need to pass arguments because has references to outer class
        public MyListAdapter() {
            super(MainActivity.this, R.layout.restaurants_view, restaurantArray);
        }

        @NonNull
        @Override
        //display image bases on position
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // make sure we have a view to work with (may have been given null
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.restaurants_view, parent, false);
            }

            // find restaurant to work with want different hazard images, name, and date and number of issues
            Restaurant currentRestaurant = restaurantArray.get(position);

            // Get relevant inspections
            InspectionManager inspectionManager = InspectionManager.getInstance();
            ArrayList<Inspection> inspections;
            inspections = inspectionManager.getInspections(currentRestaurant.getTrackingNumber());

            // Get the newest inspection
            Inspection newestInspection = inspectionManager.getMostRecentInspection(inspections);

            // fill the view
            // display restaurant name
            TextView nameTxt =  itemView.findViewById(R.id.txtRestaurentName);
            nameTxt.setText(currentRestaurant.getName());

            // display hazard image
            ImageView hazardImage = itemView.findViewById(R.id.iconHazard);
            String hazardRating = newestInspection.getHazardRating();
            if (hazardRating.equals("Low")) {
                hazardImage.setImageResource(R.drawable.hazard_low);
                itemView.setBackground(getDrawable(R.drawable.border_green));

            } else if (hazardRating.equals("Moderate")) {
                hazardImage.setImageResource(R.drawable.hazard_medium);
                itemView.setBackground(getDrawable(R.drawable.border_yellow));

            } else if (hazardRating.equals("High")) {
                hazardImage.setImageResource(R.drawable.hazard_high);
                itemView.setBackground(getDrawable(R.drawable.border_red));

            } else {
                hazardImage.setImageResource(R.drawable.not_available);
                itemView.setBackground(getDrawable(R.drawable.border_blue));
            }

            // display address
            TextView addressTxt = itemView.findViewById(R.id.txtAddress);
            addressTxt.setText(currentRestaurant.getAddress());

            // display number of issues
            TextView issuesNumberTxt = itemView.findViewById(R.id.txtIssuesNumber);
            issuesNumberTxt.setText(getString(R.string.issues) + " "
                    + newestInspection.getNumTotalViolations());

            // display date
            TextView dateTxt = itemView.findViewById(R.id.txtdate);
            dateTxt.setText(getString(R.string.Last_inspection) + " "
                    + newestInspection.intelligentDate());

            return  itemView;
        }
    }
}
