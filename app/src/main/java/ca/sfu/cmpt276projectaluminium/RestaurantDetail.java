package ca.sfu.cmpt276projectaluminium;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.cmpt276projectaluminium.model.Inspection;
import ca.sfu.cmpt276projectaluminium.model.InspectionManager;
import ca.sfu.cmpt276projectaluminium.model.Restaurant;

/**
 * TODO:
 * Will Probably need to get passed the name of the restaurant
 * In order to grab the inspection manager
 * Overall Need to get Passed restaurant somehow
 *
 *
 *
 */
public class RestaurantDetail extends AppCompatActivity {

    private List<Inspection> inspections = new ArrayList<>();
    Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);


        initalizeVariables();
        populateListView();
    }

    private void initalizeVariables() {
        //need to be properly implemented
        String id = getIntent().getStringExtra(TAG);
        restaurant = new Restaurant(id);

        InspectionManager inspectionManager = restaurant.createInspectionManager();

        for (Inspection inspection : inspectionManager){
            inspections.add(inspection);
        }
    }


    private void populateListView(){
        ArrayAdapter<Inspection> adapter = new inspectionAdapter();
        ListView list = findViewById(R.id.InspectionList);
        list.setAdapter(adapter);

    }

    private class inspectionAdapter extends ArrayAdapter<Inspection> {

        inspectionAdapter(){
            super(RestaurantDetail.this, R.layout.listviewlayout, inspections);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = convertView;

            if (view == null){
                view = getLayoutInflater().inflate(R.layout.listviewlayout, parent, false);
            }

            Inspection inspection = inspections.get(position);

            ImageView imageView = view.findViewById(R.id.hazardIcon);

            if (inspection.getType() == "Pest"){
                if (inspection.getHazardRating() == "Low"){
                    imageView.setImageResource(R.drawable.ic_launcher_foreground);
                }
            }

            TextView date = view.findViewById(R.id.Date);
            TextView critical = view.findViewById(R.id.critical);
            TextView noncritical = view.findViewById(R.id.noncritical);

            date.setText(inspection.getInspectionDate());
            critical.setText(inspection.getNumCriticalViolations());
            noncritical.setText(inspection.getNumNonCriticalViolations());

            return view;
        }
    }
}
