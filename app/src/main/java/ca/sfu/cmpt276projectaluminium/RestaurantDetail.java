package ca.sfu.cmpt276projectaluminium;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import ca.sfu.cmpt276projectaluminium.Model.Inspection;
import ca.sfu.cmpt276projectaluminium.Model.InspectionManager;

/**
 * Will Probably need to get passed the name of the restaurant
 * In order to grab the inspection manager
 *
 */
public class RestaurantDetail extends AppCompatActivity {

    private InspectionManager manager = new InspectionManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
    }

    private void populateListView(){
        ArrayAdapter<Inspection> adapter = new inspectionAdapter();
    }

    private class inspectionAdapter extends ArrayAdapter<Inspection> {

        public inspectionAdapter(){
            super(RestaurantDetail.this, R.layout.listviewlayout, manager.getInspections());

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = convertView;

            if (view == null){
                view = getLayoutInflater().inflate(R.layout.listviewlayout, parent, false);
            }

            //Inspection inspection = manager.get(position);

            ImageView imageView = view.findViewById(R.id.hazardIcon);

//            if (inspection.getType()){
//                if (inspection.getHazardRating()){
//                    imageView.setImageResource(R.drawable.ic_launcher_foreground);
//                }
//            }





            return view;
        }
    }
}
