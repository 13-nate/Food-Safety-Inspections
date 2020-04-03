package ca.sfu.cmpt276projectaluminium.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.model.QueryPreferences;

public class FilterActivity extends AppCompatActivity {

    public static final String HAZARD_GROUP_INDEX = "hazard group index";
    public static final String HAZARD_FILTER_PICKED = "hazard filter picked";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        onHazardFilterClick();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void onBackPressed(){
        Intent intent = MapsActivity.makeIntent(FilterActivity.this, false);
        startActivity(intent);
    }
    private void onHazardFilterClick() {

        //Sources: https://stackoverflow.com/questions/6780981/android-radiogroup-how-to-configure-the-event-listener
        RadioGroup hazardGroup = findViewById(R.id.hazardGroup);
        hazardGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    int index = hazardGroup.indexOfChild(findViewById(hazardGroup.getCheckedRadioButtonId()));
                    QueryPreferences.setStoredIntQuery(FilterActivity.this,
                            HAZARD_GROUP_INDEX, index);

                    String hazardFilterWanted = checkedRadioButton.getText().toString();
                    QueryPreferences.setStoredStringQuery(MapsActivity.getContextApp(),
                            HAZARD_FILTER_PICKED, hazardFilterWanted);
                }
            }
        });

    }
    public static Intent makeIntent(Context context){
        Intent intent = new Intent(context, FilterActivity.class);
        return intent;
    }
}
