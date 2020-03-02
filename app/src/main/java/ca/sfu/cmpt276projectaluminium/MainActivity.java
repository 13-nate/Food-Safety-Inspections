package ca.sfu.cmpt276projectaluminium;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ca.sfu.cmpt276projectaluminium.model.InspectionManager;
import ca.sfu.cmpt276projectaluminium.model.RestaurantManager;

public class MainActivity extends AppCompatActivity {


    /**
     * Give the csv files to the data classes so that the csv files can be read.
     */
    void initializeDataClasses() {
        // Fill the RestaurantManager with restaurants using the csv file stored in raw resources
        RestaurantManager.initialize(getResources().openRawResource(R.raw.restaurants_itr1));

        // Fill the InspectionManager with inspections using the csv file stored in raw resources
        InspectionManager.initialize(getResources().openRawResource(R.raw.inspectionreports_itr1));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeDataClasses();
    }
}
