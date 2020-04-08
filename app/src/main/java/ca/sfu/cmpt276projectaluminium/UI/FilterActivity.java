package ca.sfu.cmpt276projectaluminium.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.model.QueryPreferences;
import ca.sfu.cmpt276projectaluminium.model.SearchFilter;

public class FilterActivity extends AppCompatActivity {

    public static final String HAZARD_GROUP_INDEX = "hazard group index";
    public static final String HAZARD_FILTER_PICKED = "hazard filter picked";
    public static final String VIOLATION_GROUP_INDEX = "violation group index";
    public static final String VIOLATION_FILTER_PICKED = "violation filter picked";
    public static final String VIOLATIONS_NUMBER_PICKED = "violations number picked";
    public static final String IS_VIOLATIONS_PICKED = " a violation was picked";
    public static final int VIOLATION_PICKED = 1;
    public static final int VIOLATION_NOT_PICKED = 0;
    private SearchFilter searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        onHazardFilterClick();
        violationsGroupClick();
        violationsNumber();
        searchFilter = SearchFilter.getInstance();

        RadioGroup hazardGroup = findViewById(R.id.hazardGroup);
        int hazardIndexSelected  = searchFilter.getHazardIndex();
        if(hazardIndexSelected != 0) {
            ((RadioButton)hazardGroup.getChildAt(hazardIndexSelected)).setChecked(true);
        } else {
            ((RadioButton)hazardGroup.getChildAt(0)).setChecked(true);
        }

        RadioGroup violationGroup = findViewById(R.id.violationGroup);
        int violationIndexSelected  = searchFilter.getViolationIndex();
        if(violationIndexSelected != 0) {
            ((RadioButton)violationGroup.getChildAt(violationIndexSelected)).setChecked(true);
            EditText violationNumber =  findViewById(R.id.numberOfViolations);
            int oldViolationThreshold = searchFilter.getViolationsThreshold();
            violationNumber.setText("" + oldViolationThreshold);
        } else {
            ((RadioButton)violationGroup.getChildAt(0)).setChecked(true);
        }
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
        searchFilter = SearchFilter.getInstance();
        //Sources: https://stackoverflow.com/questions/6780981/android-radiogroup-how-to-configure-the-event-listener
        RadioGroup hazardGroup = findViewById(R.id.hazardGroup);
        hazardGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    int index = hazardGroup.indexOfChild(findViewById(hazardGroup.getCheckedRadioButtonId()));
                    searchFilter.setHazardIndex(index);
                   /* QueryPreferences.setStoredIntQuery(FilterActivity.this,
                            HAZARD_GROUP_INDEX, index);*/

                    String hazardFilterWanted = checkedRadioButton.getText().toString();
                    searchFilter.setHazardRating(hazardFilterWanted);
                    /*QueryPreferences.setStoredStringQuery(MapsActivity.getContextApp(),
                            HAZARD_FILTER_PICKED, hazardFilterWanted);*/
                }
            }
        });
    }
    private void violationsGroupClick() {
        RadioGroup violationGroup = findViewById(R.id.violationGroup);
        violationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                searchFilter = SearchFilter.getInstance();
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    int index = violationGroup.indexOfChild(findViewById(violationGroup.getCheckedRadioButtonId()));
                    /*QueryPreferences.setStoredIntQuery(FilterActivity.this,
                            VIOLATION_GROUP_INDEX, index);*/
                    searchFilter.setViolationIndex(index);

                    String violationFilterWanted = checkedRadioButton.getText().toString();
                    /*QueryPreferences.setStoredStringQuery(MapsActivity.getContextApp(),
                            VIOLATION_FILTER_PICKED, violationFilterWanted);*/
                    searchFilter.setViolationFilterType(violationFilterWanted);
                    if(index == 0) {
                        /*QueryPreferences.setStoredIntQuery(MapsActivity.getContextApp(),
                                IS_VIOLATIONS_PICKED, VIOLATION_NOT_PICKED);*/
                        EditText violationNumber = findViewById(R.id.numberOfViolations);
                        violationNumber.setText("");
                        violationNumber.setHint(getString(R.string.pick_an_equality_first));
                        violationNumber.setEnabled(false);
                        violationNumber.setClickable(false);

                    } else {
                        EditText violationNumber = findViewById(R.id.numberOfViolations);
                        violationNumber.setHint(getString(R.string.enter_number_of_violations));
                        violationNumber.setClickable(true);
                        violationNumber.setEnabled(true);
                    }
                }
            }
        });
    }
    public static Intent makeIntent(Context context){
        Intent intent = new Intent(context, FilterActivity.class);
        return intent;
    }

    private void violationsNumber() {
        EditText violationNumber = findViewById(R.id.numberOfViolations);
        violationNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String violationsText = violationNumber.getText().toString();
                RadioGroup violationGroup = findViewById(R.id.violationGroup);
                boolean equalityPicked = ((RadioButton)violationGroup.getChildAt(0)).isChecked();

                if(equalityPicked) {
                    violationNumber.setHint(getString(R.string.pick_an_equality_first));
                }
                if(violationsText.equals("")){
                    ((RadioButton)violationGroup.getChildAt(0)).setChecked(true);
                    /*SharedPreferences clearData = PreferenceManager.getDefaultSharedPreferences(MapsActivity.getContextApp());
                    SharedPreferences.Editor editor = clearData.edit();
                    editor.remove(VIOLATIONS_NUMBER_PICKED);
                    editor.apply();
                    QueryPreferences.setStoredIntQuery(MapsActivity.getContextApp(),
                            IS_VIOLATIONS_PICKED, VIOLATION_NOT_PICKED);*/
                    searchFilter.resetViolationsFilters();
                } else {
                    int violationsNum = Integer.parseInt(violationsText);
                    /*QueryPreferences.setStoredIntQuery(MapsActivity.getContextApp(),
                            VIOLATIONS_NUMBER_PICKED, violationsNum);
                    QueryPreferences.setStoredIntQuery(MapsActivity.getContextApp(),
                            IS_VIOLATIONS_PICKED, VIOLATION_PICKED);*/
                    searchFilter.setViolationsThreshold(violationsNum);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
