package ca.sfu.cmpt276projectaluminium.UI;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private static final String MESSAGE_DIALOGUE = "MESSAGE_DIALOGUE";
    public static final String FIRST_TIME = "first_time";

    private Context context;

    private RestaurantManager manager = RestaurantManager.getInstance();
    private List<Restaurant> restaurantArray = new ArrayList<>();
    static ArrayAdapter<Restaurant> adapter;

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

        context = this;

        checkFileDate();
        InputStream inputStreamRestaurant = null;
        InputStream inputStreamInspection = null;
        try {
            inputStreamRestaurant = openFileInput(ProgressMessage.fileFinalRestaurant);
            inputStreamInspection = openFileInput(ProgressMessage.fileFinalInspection);
            initializeDataClasses(inputStreamRestaurant, inputStreamInspection);

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

    private void checkFileDate(){
        Date currentDate = Calendar.getInstance().getTime();

        File fileRestaurant = new File(ProgressMessage.fileFinalRestaurant);
        File fileInspection = new File(ProgressMessage.fileFinalRestaurant);
        if (fileRestaurant.exists() && fileInspection.exists()){

            Date fileDateRestaurant = new Date(fileRestaurant.lastModified());
            Date fileDateInspection = new Date(fileInspection.lastModified());

            long timeDifferentRestaurant = currentDate.getTime() - fileDateRestaurant.getTime();
            long diffRestaurant = TimeUnit.MILLISECONDS.toHours(timeDifferentRestaurant);

            long timeDifferentInspection = currentDate.getTime() - fileDateInspection.getTime();
            long diffInspection = TimeUnit.MILLISECONDS.toHours(timeDifferentInspection);

            if (diffInspection > 20 || diffRestaurant > 20){
                new updateChecker().execute();
            }
        } else {
            new updateChecker().execute();
        }
    }

    private void populateListView() {
        // myListAdapter lets me work with the objects
        adapter = new MyListAdapter();
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

        @Override
        public void clear() {
            super.clear();
            recreate();
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

    private class updateChecker extends AsyncTask<Void, Void, Void> {

        private Date lastModifiedRestaurant;
        private Date lastModifiedInspection;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                readRestaurant();
                readInspection();
            } catch (IOException | JSONException | ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            promptDownload();
        }

        private void readRestaurant() throws IOException, JSONException, ParseException {
            URL url = new URL("https://data.surrey.ca/api/3/action/package_show?id=restaurants");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String totalInput = "";
            String inputLine;

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

        //takes in the json that gives you the choice between varieties of files
        //gets the csv containing useful data from it
        private void readInspection() throws IOException, JSONException, ParseException {
            URL url = new URL("https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String totalInput = "";
            String inputLine;

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

        private void promptDownload() {
            String tempPath = context.getFilesDir().getAbsolutePath();

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
                        FragmentManager manager = getSupportFragmentManager();
                        DownloadMessage dialog = new DownloadMessage();
                        dialog.show(manager, MESSAGE_DIALOGUE);
                    }
                }
            } else if (checkFirstTime()) {
                FragmentManager manager = getSupportFragmentManager();
                DownloadMessage dialog = new DownloadMessage();
                dialog.show(manager, MESSAGE_DIALOGUE);
            }

        }

        private boolean checkFirstTime(){
            RestaurantManager restaurants = RestaurantManager.getInstance();

            if (restaurants.isFirstRun()){
                restaurants.setFirstRun(false);
                return true;
            }
            return false;
        }
    }
}
