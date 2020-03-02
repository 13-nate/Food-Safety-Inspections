package ca.sfu.cmpt276projectaluminium.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages data about a Restaurant's inspections by storing them all in an easily accessible list
 */
public class InspectionManager {
    private static final String TAG = "InspectionManager";
    private static final String INSPECTION_FILE_PATH = "";
    private ArrayList<Inspection> inspectionList = new ArrayList<>();


    /**
     * Private constructor so that InspectionManagers are only instantiated in ways that are allowed
     */
    private InspectionManager() {}

    /**
     * Extracts inspection info line by line and stores it in a list
     * @return A list of strings (each string holds a line of inspection data)
     */
    private ArrayList<String> getFileData() {
        ArrayList<String> inspectionRawData = new ArrayList<>();
        BufferedReader reader;

        // Attempt to open file
        try {
            reader = new BufferedReader(new FileReader(INSPECTION_FILE_PATH));
        } catch (FileNotFoundException ex) {
            Log.i(TAG, "Could not read file at path: " + INSPECTION_FILE_PATH, ex);
            return inspectionRawData;
        }

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
     * Initialize inspectionList with data by parsing inspectionRawData to extract the relevant data
     * @param inspectionRawData A list of strings (each string holds a line of inspection data)
     */
    private void initializeInspectionList(ArrayList<String> inspectionRawData,
                                          String restaurantTrackingNumber) {
        // For each line of csv data, create a inspection with it and put it in the inspection list
        for (String dataLine : inspectionRawData) {
            // Separate the comma-spliced-values
            String[] inspectionValues = dataLine.split("\\s*,\\s*");

            // If an inspection corresponds to the restaurant that created this InspectionManager...
            if (inspectionValues[0].equals(restaurantTrackingNumber)) {
                // Extract the comma-spliced-values into variables
                String trackingNumber = inspectionValues[0];
                String inspectionDate = inspectionValues[1];
                String type = inspectionValues[2];
                String hazardRating = inspectionValues[3];
                int numCriticalViolations = Integer.parseInt(inspectionValues[4]);
                int numNonCriticalViolations = Integer.parseInt(inspectionValues[5]);

                // Create an inspection
                Inspection inspection = new Inspection(trackingNumber, inspectionDate, type,
                        hazardRating, numCriticalViolations, numNonCriticalViolations);

                // Store the inspection inside the list of inspections
                this.inspectionList.add(inspection);
            }
        }
    }

    /**
     * Constructor is package private as it should only be called by Restaurant
     */
    InspectionManager (String restaurantTrackingNumber) {
        // Get data out of the inspections file and store it in a readable way.
        ArrayList<String> inspectionRawData = getFileData();

        // Whilst filling arrayList with inspection objects by properly initializing inspections.
        initializeInspectionList(inspectionRawData, restaurantTrackingNumber);
    }

    public int getSize() {
        return inspectionList.size();
    }

    /**
     * Allows for the iteration of RestaurantManager in a for-each loop as if it were a list
     */
    java.util.Iterator<Inspection> Iterator = inspectionList.iterator();
}