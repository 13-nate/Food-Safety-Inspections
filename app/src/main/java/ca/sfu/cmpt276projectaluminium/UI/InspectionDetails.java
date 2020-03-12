package ca.sfu.cmpt276projectaluminium.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.model.Inspection;
import ca.sfu.cmpt276projectaluminium.model.InspectionManager;
import ca.sfu.cmpt276projectaluminium.model.Violation;

//credits
//https://stackoverflow.com/questions/7524892/onitemclick-listener-on-getview-method

/**
 * Implements inspection details and gets data for it
 * Also responsible for grabbing the short descriptions from strings.xml
 *
 */

public class InspectionDetails extends AppCompatActivity {

    private static final String INSPECTION_ID = "violationId";
    public static final String INSPECTION_DATE = "inspection Date for violation";
    public static final String INSPECTION_TYPE = "inspectionType";
    private List<Violation> violations = new ArrayList<>();
    Inspection inspection;
    InspectionManager inspectionManager;

    // Control what happens upon pressing back button
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_details);

        initializeVariables();
        populateListView();
        loadData();

        // Create a back button that we can control
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private String getShortDescription(int ID) {
        switch (ID) {
            case 101:
                return getString(R.string.violation101);
            case 102:
                return getString(R.string.violation102);
            case 103:
                return getString(R.string.violation103);
            case 104:
                return getString(R.string.violation104);
            case 201:
                return getString(R.string.violation201);
            case 202:
                return getString(R.string.violation202);
            case 203:
                return getString(R.string.violation203);
            case 204:
                return getString(R.string.violation204);
            case 205:
                return getString(R.string.violation205);
            case 206:
                return getString(R.string.violation206);
            case 208:
                return getString(R.string.violation208);
            case 209:
                return getString(R.string.violation209);
            case 210:
                return getString(R.string.violation210);
            case 211:
                return getString(R.string.violation211);
            case 212:
                return getString(R.string.violation212);
            case 301:
                return getString(R.string.violation301);
            case 302:
                return getString(R.string.violation302);
            case 303:
                return getString(R.string.violation303);
            case 304:
                return getString(R.string.violation304);
            case 305:
                return getString(R.string.violation305);
            case 306:
                return getString(R.string.violation306);
            case 307:
                return getString(R.string.violation307);
            case 308:
                return getString(R.string.violation308);
            case 309:
                return getString(R.string.violation309);
            case 310:
                return getString(R.string.violation310);
            case 311:
                return getString(R.string.violation311);
            case 312:
                return getString(R.string.violation312);
            case 313:
                return getString(R.string.violation313);
            case 314:
                return getString(R.string.violation314);
            case 315:
                return getString(R.string.violation315);
            case 401:
                return getString(R.string.violation401);
            case 402:
                return getString(R.string.violation402);
            case 403:
                return getString(R.string.violation403);
            case 404:
                return getString(R.string.violation404);
            case 501:
                return getString(R.string.violation501);
            case 502:
                return getString(R.string.violation502);
            default:
                return getString(R.string.invalid_id);
        }
    }

    private void populateListView() {
        ArrayAdapter<Violation> adapter = new InspectionDetails.violationAdapter();
        ListView list = findViewById(R.id.violationList);
        list.setAdapter(adapter);
    }

    private void initializeVariables() {
        Intent intent = getIntent();
        String trackingNum = intent.getStringExtra(INSPECTION_ID);
        int date = intent.getIntExtra(INSPECTION_DATE, 0);
        String type = intent.getStringExtra(INSPECTION_TYPE);

        inspectionManager = InspectionManager.getInstance();
        inspection = inspectionManager.recreateInspection(trackingNum, date, type);
        violations = inspection.getViolations();
    }

    private void loadData() {
        // get text views
        TextView type = findViewById(R.id.txtType);
        TextView critical = findViewById(R.id.txtCritical);
        TextView nonCritical = findViewById(R.id.txtNonCritical);
        TextView hazard = findViewById(R.id.txtHazardLvl);

        // get string values
        String tempDate = inspection.fullDate();
        String tempType = getString(R.string.inspectiontype) + " "
                + inspection.getType();
        String tempCritical = getString(R.string.critical) +  " "
                + inspection.getNumCriticalViolations();

        String tempNonCritical = getString(R.string.non_critical) + " "
                + inspection.getNumNonCriticalViolations();

        // load values into UI elements
        getSupportActionBar().setTitle(tempDate);
        type.setText(tempType);
        critical.setText(tempCritical);
        nonCritical.setText(tempNonCritical);
        hazard.setText(inspection.getHazardRating());

        //find places for images
        ImageView hazardImage = findViewById(R.id.imgHazardLvl);
        LinearLayout hazardLayout = findViewById(R.id.hazardLayout);

        //set images
        String hazardRating = inspection.getHazardRating();
        if (hazardRating.equals("Low")) {
            hazardImage.setImageResource(R.drawable.hazard_low);
            hazardLayout.setBackground(getDrawable(R.drawable.border_green));

        } else if (hazardRating.equals("Moderate")) {
            hazardImage.setImageResource(R.drawable.hazard_medium);
            hazardLayout.setBackground(getDrawable(R.drawable.border_yellow));


        } else if (hazardRating.equals("High")) {
            hazardImage.setImageResource(R.drawable.hazard_high);
            hazardLayout.setBackground(getDrawable(R.drawable.border_red));


        } else {
            hazardImage.setImageResource(R.drawable.not_available);
            hazardLayout.setBackground(getDrawable(R.drawable.border_blue));

        }

    }

    public static Intent makeIntent(Context context, String inspectionId, int date, String type) {
        Intent intent = new Intent(context, InspectionDetails.class);
        intent.putExtra(INSPECTION_ID, inspectionId);
        intent.putExtra(INSPECTION_DATE, date);
        intent.putExtra(INSPECTION_TYPE, type);
        return intent;
    }

    private class violationAdapter extends ArrayAdapter<Violation> {

        violationAdapter() {
            super(InspectionDetails.this, R.layout.violations_view, violations);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listView = convertView;
            if (listView == null) {
                listView = getLayoutInflater().inflate(R.layout.violations_view, parent, false);
            }
            Violation violation = violations.get(position);

            // set violation number
            TextView idTextview = listView.findViewById(R.id.txtViolaionId);
            String idString = getString(R.string.id) + " " + violation.getID();
            idTextview.setText(idString);

            //set description text
            TextView description = listView.findViewById(R.id.txtShortDescription);
            String detailOfViolation = getShortDescription(violation.getID());
            description.setText(detailOfViolation);

            //set violationImage
            int drawableId = getViolationImage(violation.getID());
            ImageView violationImageView = listView.findViewById(R.id.imgViolation);
            violationImageView.setImageResource(drawableId);

            //set critical image
            String severity = violation.getSeverity();
            ImageView criticalImage = listView.findViewById(R.id.imgCritical);
            if (severity.equals("Not Critical")) {
                criticalImage.setImageResource(R.drawable.non_critical);
                listView.setBackground(getDrawable(R.drawable.border_green));
            } else if (severity.equals("Critical")) {
                criticalImage.setImageResource(R.drawable.critical_violation);
                listView.setBackground(getDrawable(R.drawable.border_red));
            }

            listView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(InspectionDetails.this, violation.getFullDescription(),
                            Toast.LENGTH_LONG).show();
                }
            });

            return listView;
        }
    }
    private int getViolationImage(int ID) {
        switch (ID) {
            case 101:
                return (R.drawable.bad_building);
            case 102:
                return (R.drawable.unapproved_food);
            case 103:
                return (R.drawable.document_error);
            case 104:
                return (R.drawable.document_error);
            case 201:
                return (R.drawable.contaminated_food);
            case 202:
                return (R.drawable.contaminated_food);
            case 203:
                return (R.drawable.defrost);
            case 204:
                return (R.drawable.to_hot);
            case 205:
                return (R.drawable.to_hot);
            case 206:
                return (R.drawable.unapproved_food);
            case 208:
                return (R.drawable.bad_information);
            case 209:
                return (R.drawable.not_protected);
            case 210:
                return (R.drawable.to_hot);
            case 211:
                return (R.drawable.to_hot);
            case 212:
                return (R.drawable.bad_information);
            case 301:
                return (R.drawable.bad_equipment2);
            case 302:
                return (R.drawable.bad_equipment2);
            case 303:
                return (R.drawable.no_water);
            case 304:
                return (R.drawable.pests);
            case 305:
                return (R.drawable.pests);
            case 306:
                return (R.drawable.dirty_place);
            case 307:
                return (R.mipmap.dirty_dishes);
            case 308:
                return (R.drawable.bad_equipment2);
            case 309:
                return (R.drawable.bad_chemicals);
            case 310:
                return (R.drawable.bad_single_use_items);
            case 311:
                return (R.drawable.bad_building);
            case 312:
                return (R.drawable.bad_equipment2);
            case 313:
                return (R.drawable.no_pets);
            case 314:
                return (R.drawable.dirty_place);
            case 315:
                return (R.drawable.no_thermometer);
            case 401:
                return (R.drawable.wash_hands);
            case 402:
                return (R.drawable.wash_hands);
            case 403:
                return (R.drawable.wash_hands);
            case 404:
                return (R.drawable.no_smoking);
            case 501:
                return (R.drawable.food_safe);
            case 502:
                return (R.drawable.food_safe);
            default:
                return (R.drawable.food_safe);
        }
    }
}


