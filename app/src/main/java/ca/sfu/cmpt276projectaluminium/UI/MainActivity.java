package ca.sfu.cmpt276projectaluminium.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.model.Restaurant;
import ca.sfu.cmpt276projectaluminium.model.RestaurantManager;
import ca.sfu.cmpt276projectaluminium.model.SearchFilter;

// Sources:
// https://stackoverflow.com/questions/5089300/how-can-i-change-the-image-of-an-imageview
// https://stackoverflow.com/questions/36630428/listview-reusing-old-images - Fragments are reused
// https://stackoverflow.com/questions/2275004/in-java-how-do-i-check-if-a-string-contains-a-substring-ignoring-case

/**
 * Displays a list of restaurants and some info on the most most recent inspection report for
 * each of the restaurants displayed
 */
public class MainActivity extends AppCompatActivity {
    //for incorrect version
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String TAG = "MainActivity";

    private ArrayAdapter<Restaurant> adapter;
    private RestaurantManager manager = RestaurantManager.getInstance();
    private List<Restaurant> allRestaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(getString(R.string.restaurants));

        populateListView();
        registerClickCallBack();
        onBottomToolBarClick();
        // setMenuColor();

        // Set up the filter icon's listener
        addFilterIconClickListener();
    }

    private void populateListView() {
        // myListAdapter lets me work with the objects
        adapter = new RestaurantListAdapter(MainActivity.this, allRestaurants);
        ListView list = findViewById(R.id.restaurantListView);

        manager = manager.getInstance();
        for(Restaurant r: manager){
            allRestaurants.add(r);
        }

        list.setAdapter(adapter);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

                Restaurant clickedRestaurant = allRestaurants.get(position);
                Intent intent = RestaurantDetail.makeIntent(MainActivity.this, clickedRestaurant.getTrackingNumber(), false);
                startActivity(intent);
            }
        });
    }

    //Source: https://stackoverflow.com/questions/21253303/exit-android-app-on-back-pressed
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


    /**
     * This way the user can see which activity they are in and can easily tell which icon
     * represents what, not use fragments for both so need to force a color change between activities
     * source: https://stackoverflow.com/questions/30967851/change-navigation-view-item-color-dynamically-android?rq=1
     * https://stackoverflow.com/questions/48413808/android-bottomnavigationview-onnavigationitemselectedlistener-code-not-running
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
                        // in this case we are in the main activity so don't want anything to happen
                        // require bool value so return true when the item is clicked
                        return true;
                        case R.id.navigationMap:
                            //in this case we are in the main activity and want to go to maps
                            Intent intent = MapsActivity.makeIntent(MainActivity.this, false);
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

    // Attaches a listener to the filter icon that makes it switch to the filter activity upon click
    private void addFilterIconClickListener() {
        ImageView searchSettings = findViewById(R.id.searchSettingsMainActivity);
        searchSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = FilterActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Activate the options menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter_menu, menu);

        // Get the view object from the inflated menu item
        MenuItem searchItem = menu.findItem(R.id.action_Search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Set the listener for the menu item
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.e(TAG, "onQueryTextSubmit: " + s, null);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.e(TAG, "onQueryTextChange: " + s, null);
                adapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }


//    private ArrayList<View> applyFilters(ArrayList<View> views) {
//        // Remove all views that don't match from the list of views
//        SearchFilter searchFilter = SearchFilter.getInstance();
//        List<String> filteredRestaurantTrackingNumbers = searchFilter.getRestaurantTrackingNumbers();
//
//        List<View> viewsToRemove = new ArrayList<>();
//
//        // TODO: Convert to an iterator
//        adapter.getItem();
//        adapter.
//
//    }
}
