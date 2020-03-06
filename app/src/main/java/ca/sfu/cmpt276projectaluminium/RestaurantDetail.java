package ca.sfu.cmpt276projectaluminium;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.cmpt276projectaluminium.model.Inspection;
import ca.sfu.cmpt276projectaluminium.model.InspectionManager;
import ca.sfu.cmpt276projectaluminium.model.Restaurant;
import ca.sfu.cmpt276projectaluminium.model.RestaurantManager;

/**
 * Implements ListView and gets data for it
 *
 *
 */

//credits
//https://www.flaticon.com/search?search-type=icons&word=Food&license=&color=&stroke=&current_section=&author_id=&pack_id=&family_id=&style_id=2&category_id=

public class RestaurantDetail extends AppCompatActivity {

    private List<Inspection> inspections = new ArrayList<>();
    Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        RestaurantManager.initialize(getResources().openRawResource(R.raw.restaurants_itr1));

        // Fill the InspectionManager with inspections using the csv file stored in raw resources
        InspectionManager.initialize(getResources().openRawResource(R.raw.inspectionreports_itr1));

        initalizeVariables();
        populateListView();
        loadText();
    }



    private void initalizeVariables() {
        //String id = getIntent().getStringExtra(TAG);
        String id = "SDFO-8HKP7E";

        restaurant = RestaurantManager.recreateRestaurant(id);

        InspectionManager inspectionManager = restaurant.createInspectionManager();

        for (Inspection inspection : inspectionManager){
            inspections.add(inspection);
        }

    }

    private void populateListView(){
        ArrayAdapter<Inspection> adapter = new inspectionAdapter();
        ListView list = findViewById(R.id.inspectionList);
        list.setAdapter(adapter);

    }

    private void loadText() {
        TextView name = findViewById(R.id.nameText);
        TextView address = findViewById(R.id.addressText);
        TextView latitude = findViewById(R.id.latitudeText);
        TextView longitude = findViewById(R.id.longitudeText);

        String tempName = restaurant.getName();
        String tempAddress = restaurant.getAddress();
        String tempLatitude = "" + restaurant.getLatitude();
        String tempLongitude = "" + restaurant.getLongitude();

        name.setText(tempName);
        address.setText(tempAddress);
        latitude.setText(tempLatitude);
        longitude.setText(tempLongitude);

    }

    private class inspectionAdapter extends ArrayAdapter<Inspection> {

        inspectionAdapter(){
            super(RestaurantDetail.this, R.layout.listviewlayout, inspections);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listView = convertView;

            if (listView == null){
                listView = getLayoutInflater().inflate(R.layout.listviewlayout, parent, false);
            }

            Inspection inspection = inspections.get(position);

            ImageView imageView = listView.findViewById(R.id.hazardIcon);

            if (inspection.getHazardRating().toLowerCase().equals("low")){
                imageView.setImageResource(R.drawable.foodgreen);
            } else if (inspection.getHazardRating().toLowerCase().equals("moderate")){
                imageView.setImageResource(R.drawable.foodorange);
            } else if (inspection.getHazardRating().toLowerCase().equals("high")){
                imageView.setImageResource(R.drawable.foodred);
            }

            TextView date = listView.findViewById(R.id.Date);
            TextView critical = listView.findViewById(R.id.critical);
            TextView noncritical = listView.findViewById(R.id.noncritical);

            String inspectionDate = getString(R.string.inspection_date);
            String criticalViolations = getString(R.string.critical_issues);
            String nonCriticalViolations = getString(R.string.non_critical_issues);

            inspectionDate = inspectionDate + inspection.getInspectionDate();
            criticalViolations = criticalViolations + inspection.getNumCriticalViolations();
            nonCriticalViolations = nonCriticalViolations + inspection.getNumNonCriticalViolations();

            date.setText(inspectionDate);
            critical.setText(criticalViolations);
            noncritical.setText(nonCriticalViolations);

            return listView;
        }
        private void registerClickCallBack(){
            ListView list = findViewById(R.id.inspectionList);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Intent intent = inspectionActivity.newIntent();
                    //startActivity(intent);
                }
            });
        }
    }
}

