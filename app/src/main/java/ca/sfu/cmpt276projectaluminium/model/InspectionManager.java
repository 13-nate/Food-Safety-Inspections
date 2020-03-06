package ca.sfu.cmpt276projectaluminium.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages data about a Restaurant's inspections by storing them all in an easily accessible list
 */
public class InspectionManager implements Iterable<Inspection> {
    private static final String TAG = "InspectionManager";
    private static ArrayList<Inspection> completeInspectionList = new ArrayList<>();
    private ArrayList<Inspection> restaurantInspectionList = new ArrayList<>();

    /**
     * Private constructor so that InspectionManagers are only instantiated in ways that are allowed
     */
    private InspectionManager() {}

    /**
     * Fills the ArrayList variable with objects based on provided csv data
     * Should be called once, on program initialization
     */
    public static void initialize(InputStream is) {
        // Get data out of the inspections file and store it in a readable way.
        ArrayList<String> inspectionRawData = getFileData(is);

        // Fill arrayList with inspection objects by properly initializing inspections.
        initializeInspectionList(inspectionRawData);
    }

    /**
     * Extracts inspection info line by line and stores it in a list
     * @return A list of strings (each string holds a line of inspection data)
     */
    private static ArrayList<String> getFileData(InputStream is) {
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
     * Initialize inspectionList with data by parsing inspectionRawData to extract the relevant data
     * @param inspectionRawData A list of strings (each string holds a line of inspection data)
     */
    private static void initializeInspectionList(ArrayList<String> inspectionRawData) {
        // For each line of csv data, create a inspection with it and put it in the inspection list
        for (String dataLine : inspectionRawData) {
            // Separate the comma-spliced-values
            String[] inspectionValues = dataLine.split("\\s*,\\s*");

            // Remove any quotations from entries
            for (int i = 0; i < inspectionValues.length; i++) {
                String str = inspectionValues[i];
                str = str.replaceAll("\"", "");
                inspectionValues[i] = str;
            }

            // If the current csv row is data (and not the title), then add it to the list
            if (!(inspectionValues[0].equals("TrackingNumber"))) {
                // Extract the comma-spliced-values into variables
                String trackingNumber = inspectionValues[0];
                int inspectionDate = Integer.parseInt(inspectionValues[1]);
                String type = inspectionValues[2];
                int numCriticalViolations = Integer.parseInt(inspectionValues[3]);
                int numNonCriticalViolations = Integer.parseInt(inspectionValues[4]);
                String hazardRating = inspectionValues[5];

                // Create an inspection
                Inspection inspection = new Inspection(trackingNumber, inspectionDate, type,
                        numCriticalViolations, numNonCriticalViolations, hazardRating);

                // Store the inspection inside the list of inspections
                completeInspectionList.add(inspection);
            }
        }
    }

    /**
     * Filter inspections so that the only inspections accessible are those that are associated
     * the restaurant that is creating this InspectionManager
     * @param restaurantTrackingNumber The ID of the restaurant that is creating the manager
     */
    private void populateRestaurantInspectionList(String restaurantTrackingNumber) {
        // For every inspection across all restaurants...
        for (Inspection inspection : completeInspectionList) {
            // Add the inspection to the filtered list if it is associated with the restaurant
            String inspectionTrackingNumber = inspection.getTrackingNumber();
            if (inspectionTrackingNumber.equals(restaurantTrackingNumber)) {
                restaurantInspectionList.add(inspection);
            }
        }
    }

    /**
     * Creates and returns the inspection object that correlates to the passed in parameters
     * This can be used to create a inspection after passing the arguments between activities.
     * @param trackingNumber The trackingNumber of the inspection being searched for.
     * @param inspectionDate the date of the inspection being searched for.
     * @return An inspection that exactly matches both parameters
     */
    public static Inspection recreateInspection(String trackingNumber, int inspectionDate) {
        // Find an inspection that matches both the trackingNumber and date, then return it
        for (Inspection inspection : completeInspectionList) {
            boolean trackingNumberMatches = inspection.getTrackingNumber().equals(trackingNumber);
            boolean dateMatches = inspection.getInspectionDate() == inspectionDate;
            if (trackingNumberMatches && dateMatches) {
                return inspection;
            }
        }

        // This should only be returned if an invalid trackingNumber/date combo was passed in
        return null;
    }

    /**
     * CSV setup is done by initialize(), but we still need to filter our inspections down to ones
     * that are relevant to the restaurant that is creating this InspectionManager
     */
    InspectionManager (String restaurantTrackingNumber) {
        // Filter irrelevant inspections out so restaurantInspectionList only contains relevant ones
        populateRestaurantInspectionList(restaurantTrackingNumber);
    }

    public int getSize() {
        return restaurantInspectionList.size();
    }

    /**
     * Allows for the iteration of RestaurantManager in a for-each loop as if it were a list
     */
    @Override
    public Iterator<Inspection> iterator() {
        return restaurantInspectionList.iterator();
    }
}