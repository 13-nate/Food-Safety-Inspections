package ca.sfu.cmpt276projectaluminium.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.model.Inspection;
import ca.sfu.cmpt276projectaluminium.model.InspectionManager;
import ca.sfu.cmpt276projectaluminium.model.Violation;

public class inspection_details extends AppCompatActivity {

    private static final String TAG = "violationId";
    private List<Violation>violations = new ArrayList<>();
    Inspection inspection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_detail);

        initializeVariables();
        populateListView();
        loadText();
    }

    private void populateListView(){
        ArrayAdapter<Violation> adapter = new inspection_details.violationAdapter();
        ListView list = findViewById(R.id.inspectionList);
        list.setAdapter(adapter);



    }

    private void initializeVariables() {
        String date = getIntent().getStringExtra(TAG);
        String trackingnum = null ;

        int i=0;

        for (String tags:date.split("@", 2))
        {
            if (i==0) {
                trackingnum = tags;
                i++;
            }
            else
                date=tags;



        }


        int Date= Integer.parseInt(date);

        InspectionManager inspectionManager = InspectionManager.getInstance();
        Inspection inspection = inspectionManager.recreateInspection(trackingnum, Date);

        violations=inspection.getViolations();


    }

    private void loadText() {
        TextView Date = findViewById(R.id.textView6);
        TextView type = findViewById(R.id.textView7);
        TextView crit = findViewById(R.id.crit);
        TextView noncrit = findViewById(R.id.noncrit);

        String tempDate = inspection.intelligentDate();
        String temptype = inspection.getType();
        String tempcrit = inspection.getNumCriticalViolations()+" of critical issues";
        String tempNcrit = inspection.getNumNonCriticalViolations()+" of non-critical issues";



        Date.setText(tempDate);
        type.setText(temptype);
        crit.setText(tempcrit);
        noncrit.setText(tempNcrit);

    }

    public static Intent makeIntent(Context context, String inspectionId){
        Intent intent = new Intent(context, inspection_details.class);
        intent.putExtra(TAG,inspectionId);
        return intent;
    }

    private class violationAdapter extends ArrayAdapter<Violation> {

        violationAdapter(){
            super(inspection_details.this, R.layout.violationlist, violations);

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listView = convertView;

            if (listView == null){
                listView = getLayoutInflater().inflate(R.layout.violationlist, parent, false);
            }

            Violation violation = violations.get(position);


           /* ImageView imageView = listView.findViewById(R.id.hazardIcon);

            if (inspection.getHazardRating().toLowerCase().equals("low")){
                imageView.setImageResource(R.drawable.cancel_cutlery_green);
            } else if (inspection.getHazardRating().toLowerCase().equals("moderate")){
                imageView.setImageResource(R.drawable.cancel_cutlery_orange);
            } else if (inspection.getHazardRating().toLowerCase().equals("high")){
                imageView.setImageResource(R.drawable.cancel_cutlery_red);
            } else {
                imageView.setImageResource(R.drawable.cancel_cutlery_black);
            }*/

            TextView vid = listView.findViewById(R.id.trackingId);
            TextView iscritical = listView.findViewById(R.id.isCrit);
            TextView description = listView.findViewById(R.id.detail);

            String vioID = "ID: "+ violation.getID();
            String isCritical = violation.getSeverity();
            String detailOfViolation = violation.getFullDescription();



            vid.setText(vioID);
            iscritical.setText(isCritical);
            description.setText(detailOfViolation);

            return listView;
        }
    }
}
