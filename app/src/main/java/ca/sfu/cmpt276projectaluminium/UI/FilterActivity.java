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

    public static final String FILTER_ACTIVITY_IS_MAPS = "filter Activity isMaps";
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
        Intent fromIntent = getIntent();
        boolean isMap = fromIntent.getBooleanExtra(FILTER_ACTIVITY_IS_MAPS, false);
        Intent intent;
        if(isMap) {
            intent = MapsActivity.makeIntent(FilterActivity.this, false);
            startActivity(intent);
        } else {
            intent = MainActivity.makeIntent(FilterActivity.this);
            startActivity(intent);
        }
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
                    if( index == 0) {
                        searchFilter.setHazardRating("any");
                    } else if (index == 1) {
                        searchFilter.setHazardRating("low");
                    } else if (index == 2) {
                        searchFilter.setHazardRating("moderate");
                    } else if (index == 3) {
                        searchFilter.setHazardRating("high");
                    }

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
                    searchFilter.setViolationIndex(index);

                    if(index == 0) {
                        EditText violationNumber = findViewById(R.id.numberOfViolations);
                        violationNumber.setText("");
                        violationNumber.setHint(getString(R.string.pick_an_equality_first));
                        violationNumber.setEnabled(false);
                        violationNumber.setClickable(false);
                        searchFilter.resetViolationsFilters();
                    } else {
                        EditText violationNumber = findViewById(R.id.numberOfViolations);
                        violationNumber.setHint(getString(R.string.enter_number_of_violations));
                        violationNumber.setClickable(true);
                        violationNumber.setEnabled(true);
                        if(index == 1 )  {
                            searchFilter.setViolationFilterType("below");
                        } else if( index == 2) {
                            searchFilter.setViolationFilterType("above");
                        }
                    }
                }
            }
        });
    }
    public static Intent makeIntent(Context context, boolean isMaps){
        Intent intent = new Intent(context, FilterActivity.class);
        intent.putExtra(FILTER_ACTIVITY_IS_MAPS, isMaps);
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
                    searchFilter.resetViolationsFilters();
                } else {
                    int violationsNum = Integer.parseInt(violationsText);
                    searchFilter.setViolationsThreshold(violationsNum);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
