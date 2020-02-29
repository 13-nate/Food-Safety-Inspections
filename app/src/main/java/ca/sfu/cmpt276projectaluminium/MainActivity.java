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

public class MainActivity extends AppCompatActivity {

    private RestaurantManager manager = new RestaurantManager();
    // dumby list for  temp data
    // need a collection of data
    private List<Restaurant> restaurantArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateRestaurantsList();
        populateListView();
    }

    private void populateRestaurantsList() {
        manager.add(new Restaurant("Macas", "low", 5, "May 24th"));
        manager.add(new Restaurant("BP", "moderate", 20, "24 days" ));
        manager.add(new Restaurant("Browns", "high",80, "May 2018"));

        for(Restaurant r: manager){
            restaurantArray.add(r);
        }
    }

    private void populateListView() {
        // change string to what holds resteraunt data type
        // myListAdapter lets me work with the objects

        ArrayAdapter<Restaurant> adapter = new MyListAdapter();
        ListView list = findViewById(R.id.restaurantListView);
        list.setAdapter(adapter);
    }

    // inner class has reference to outer class
    private class MyListAdapter extends ArrayAdapter<Restaurant> {
        // don't need to pass arguments because has references to outer class
        public MyListAdapter() {
            super(MainActivity.this, R.layout.item_view, restaurantArray);
        }

        @NonNull
        @Override
        //display image bases on position
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // make sure we have a view to work with (may have been given null
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
            }

            // find resteraunt to work with want different hazard images, name, and date and number of issues
            Restaurant currerntResteraunt = restaurantArray.get(position);
            // fill the view

            // display restaurant name
            TextView nameTxt =  itemView.findViewById(R.id.txtRestaurentName);
            nameTxt.setText(currerntResteraunt.getName());

            // display hazard image
            ImageView hazardImage = itemView.findViewById(R.id.iconHazard);
            if (currerntResteraunt.getHazardLevel() == "low") {
                hazardImage.setImageResource(R.drawable.cutlery_crossbones_green);

            } else if (currerntResteraunt.getHazardLevel() == "moderate") {
                hazardImage.setImageResource(R.drawable.cutlery_crossbones_yellow);

            } else {
                hazardImage.setImageResource(R.drawable.cutlery_crossbones_red);
            }

            // display number of issues
            TextView issuesNumberTxt = itemView.findViewById(R.id.txtIssuesNumber);
            issuesNumberTxt.setText("Issues" + currerntResteraunt.getNumOfIssues());

            // display date
            TextView dateTxt = itemView.findViewById(R.id.txtdate);
            dateTxt.setText(currerntResteraunt.getLastInspectionData());

            return  itemView;
        }
    }


}
