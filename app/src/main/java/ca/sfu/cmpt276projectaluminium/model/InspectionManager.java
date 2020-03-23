package ca.sfu.cmpt276projectaluminium.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Manages data about a Restaurant's inspections by storing them all in an easily accessible list
 */
public class InspectionManager {
    private static final int NUM_OF_VIOLATION_ATTRIBUTES = 4;
    private static final int ID = 2;
    private static final int SEVERITY = 3;
    private static final int DESCRIPTION = 0;
    private static final int REPEAT = 1;
    private static final String TAG = "InspectionManager";

    private ArrayList<Inspection> completeInspectionList = new ArrayList<>();

    private Inspection noInspection = new Inspection("None", 0, "No type",
            0,0, "No rating",
            new ArrayList<Violation>());

    /*
        Singleton Support (As per https://www.youtube.com/watch?v=evkPjPIV6cw - Brain Fraser)
     */
    private static InspectionManager instance;
    private InspectionManager() {
        // Private to prevent anyone else from instantiating
    }
    public static InspectionManager getInstance() {
        if (instance == null) {
            instance = new InspectionManager();
        }

        return instance;
    }

    /**
     * Fills the ArrayList variable with objects based on provided csv data
     * Should be called once, on program initialization
     */
    public void initialize(InputStream is) {
        //empties the list in case of additional runs
        completeInspectionList = new ArrayList<>();

        // Get data out of the inspections file and store it in a readable way.
        ArrayList<String> inspectionRawData = getFileData(is);

        // Fill arrayList with inspection objects by properly initializing inspections.
        initializeInspectionList(inspectionRawData);
    }

    /**
     * Extracts inspection info line by line and stores it in a list
     * @return A list of strings (each string holds a line of inspection data)
     */
    private ArrayList<String> getFileData(InputStream is) {
        ArrayList<String> inspectionRawData = new ArrayList<>();

        // Initialize the reader for the csv file
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                Charset.forName("UTF-8")));

        // Read each line into the ArrayList
        try {
            // Read the file
            String row;
            while ((row = reader.readLine()) != null) {
                inspectionRawData.add(row);
            }
        } catch (IOException ex) {
            Log.i(TAG, "Error reading line", ex);
        } finally {
            try {
                // Close the file
                reader.close();
            } catch (IOException ex) {
                Log.i(TAG, "Error closing file", ex);
            }
        }

        // Return data
        return inspectionRawData;
    }

    /**
     * To create all violations that an inspection has, we need to extract the information from the
     * list of data.  All information that is important for violations begins at index 6, which is
     * why the loop in this function starts searching from index 6.
     *
     * This is what the inspection data looks like starting at index 6:
     *      "ID", "Severity", "Description", "Repeat", "ID, "Severity", etc...
     *
     * This method loops through the above data, creates a violation with it, then puts it in a list
     *
     * @param inspectionData A list of inspection information - stored as a list of strings
     * @return A list of violations that are inside the inspection
     */
    private ArrayList<Violation> populateViolationList(String[] inspectionData) {
        ArrayList<Violation> violationList = new ArrayList<>();
        int id = -1;
        String repeat, description = "", severity = "";

        int violationDataStartIndex = 6;

        // Create violations and store them by looping through the data inside the list
        for (int listIndex = violationDataStartIndex;
             listIndex < inspectionData.length; listIndex++) {
            // Calculate which violation attribute is in the string
            int violationData = listIndex % NUM_OF_VIOLATION_ATTRIBUTES;

            // Depending on which attribute is in the string, assign the attribute to a variable
            if (violationData == ID) {
                id = Integer.parseInt(inspectionData[listIndex]);
            } else if (violationData == SEVERITY) {
                severity = inspectionData[listIndex];
            } else if (violationData == DESCRIPTION) {
                description = inspectionData[listIndex];
            } else {
                repeat = inspectionData[listIndex];

                // Reaching this point means that the violation object is now ready to be created
                // Thus, we create the violation
                Violation violation = new Violation(id, severity, description, repeat);

                // We then store the violation in a list for future use
                violationList.add(violation);
            }
        }

        return violationList;
    }

    /**
     * Initialize inspectionList with data by parsing inspectionRawData to extract the relevant data
     * @param inspectionRawData A list of strings (each string holds a line of inspection data)
     */
    private void initializeInspectionList(ArrayList<String> inspectionRawData) {
        // For each line of csv data, create a inspection with it and put it in the inspection list
        try {
            for (String dataLine : inspectionRawData) {
                // Separate the comma-spliced-values (and also separate at the pipes to help formatting)
                String[] inspectionValues = dataLine.split("\\s*,\\s*|\\|");

                // Remove any quotations from entries
                for (int i = 0; i < inspectionValues.length; i++) {
                    String str = inspectionValues[i];
                    str = str.replaceAll("\"", "");
                    inspectionValues[i] = str;
                }

                // If the current csv row is data (and not the title), then add it to the list
                if (!(inspectionValues[0].toUpperCase().equals("TRACKINGNUMBER"))) {
                    // Extract the comma-spliced-values into variables
                    String trackingNumber = inspectionValues[0];
                    int inspectionDate = Integer.parseInt(inspectionValues[1]);
                    String type = inspectionValues[2];
                    int numCriticalViolations = Integer.parseInt(inspectionValues[3]);
                    int numNonCriticalViolations = Integer.parseInt(inspectionValues[4]);
                    String hazardRating = inspectionValues[5];

                    // Extract violations out of the inspection csv and store them in an organized way
                    ArrayList<Violation> violationList = populateViolationList(inspectionValues);

                    // Create an inspection
                    Inspection inspection = new Inspection(trackingNumber, inspectionDate, type,
                            numCriticalViolations, numNonCriticalViolations, hazardRating,
                            violationList);

                    // Store the inspection inside the list of inspections
                    this.completeInspectionList.add(inspection);
                }
            }
        } catch (Exception e){

        }


    }

    /**
     * Creates and returns the inspection object that correlates to the passed in parameters
         * This can be used to create a inspection after passing the arguments between activities.
     * @param trackingNumber The trackingNumber of the inspection being searched for.
     * @param inspectionDate the date of the inspection being searched for.
     * @return An inspection that exactly matches both parameters
     */
    public Inspection recreateInspection(String trackingNumber, int inspectionDate, String type) {
        // Find an inspection that matches both the trackingNumber and date, then return it
        for (Inspection inspection : this.completeInspectionList) {
            boolean trackingNumberMatches = inspection.getTrackingNumber().equals(trackingNumber);
            boolean dateMatches = inspection.getInspectionDate() == inspectionDate;
            boolean typeMatches = inspection.getType().equals(type);
            if (trackingNumberMatches && dateMatches && typeMatches) {
                return inspection;
            }
        }

        // This should only be returned if an invalid trackingNumber/date combo was passed in
        return null;
    }

    /**
     * Puts all inspections associated with the specified restaurant in a list and returns that list
     * @param restaurantTrackingNumber The ID of the restaurant that is looking for its inspections
     */
    public ArrayList<Inspection> getInspections(String restaurantTrackingNumber) {
        ArrayList<Inspection> inspections = new ArrayList<>();

        // For every inspection across all restaurants...
        for (Inspection inspection : this.completeInspectionList) {
            // Add the inspection to the list if it is associated with the restaurant's trackingNum
            String inspectionTrackingNumber = inspection.getTrackingNumber();
            if (inspectionTrackingNumber.equals(restaurantTrackingNumber)) {
                inspections.add(inspection);
            }
        }
        Collections.sort(inspections);

        return inspections;
    }

    /**
     * If the inspection list is sorted, the most recent inspection will be the first inspection.
     */
    public Inspection getMostRecentInspection(ArrayList<Inspection> inspections) {
        // Ensure that the list is sorted
        Collections.sort(inspections);

        if (!inspections.isEmpty()) {
            return inspections.get(0);
        } else {
            return noInspection;
        }
    }
}