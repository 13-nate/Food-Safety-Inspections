package ca.sfu.cmpt276projectaluminium.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

// Sources:
// https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java

/**
 * Manages data about a Restaurant's inspections by storing them all in an easily accessible list
 */
public class InspectionManager {
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

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }

        // We're here if is possible to convert the string to an int
        return true;
    }

    /**
     * If the hazard rating is ordered before the violation lump, then the parsed inspection line
     * will have a string at index 5, if the hazard rating is ordered after, then the parsed
     * inspection line will have an integer at index 5.
     * If the hazard rating is before the violation dump, then index 5 will have:
     *  - a string
     * If the hazard rating is after the violation dump, then index 5 will have either:
     *  - an integer OR
     *  - an empty string
     * @param parsedInspectionLine The array that holds the data all split up
     * @return True if the hazard rating comes before violation lump order-wise in the string.
     *         False otherwise
     */
    private boolean isHazardBeforeViolationLump(String[] parsedInspectionLine) {
        if (parsedInspectionLine.length < 6){
            return false;
        }
        if (parsedInspectionLine[5].equals("")) {
            return false;
        }

        return !isInteger(parsedInspectionLine[5]);
    }

    // We know that if the string can be made into an integer, then it is not a hazard rating
    private boolean variableIsNotAHazardRating(String s) {
        return isInteger(s);
    }

    /**
     * Creates an inspection object using a csv line of input formatted as an array of strings.
     * Based on what kind of data is being received, it parses the string differently, however,
     * the end result is still an Inspection object no matter what kind of known inspection input is
     * received.
     * @param parsedInspectionLine A csv line of input formatted as an array of strings.
     * @param hazardIsFirst How the hazard var. is ordered in the array (before/after violations)
     * @return An inspection that can be created from
     */
    private Inspection createInspectionFromCSVLine(String[] parsedInspectionLine,
                                                   boolean hazardIsFirst) {
        // No matter how hazard rating and violations are ordered, we 100% know the location of
        // these first few variables
        String trackingNumber = parsedInspectionLine[0];
        int inspectionDate = Integer.parseInt(parsedInspectionLine[1]);
        String type = parsedInspectionLine[2];
        int numCriticalViolations = Integer.parseInt(parsedInspectionLine[3]);
        int numNonCriticalViolations = Integer.parseInt(parsedInspectionLine[4]);

        // We need to parse through violations (which are 4 long), so we first find the index that
        // the violation lump starts at.  That depends on whether or not the hazard comes first
        int violationStartIndex;
        if (hazardIsFirst) {
            violationStartIndex = 6;
        } else {
            violationStartIndex = 5;
        }

        // Here we parse through violations, 4 at a time, and stop once we find an item that is not
        // part of a violation
        ArrayList<Violation> violations = new ArrayList<>();
        for (int i = violationStartIndex; i < parsedInspectionLine.length; i += 4) {
            // If we are looking at the hazard variable, we don't have to do anything.
            // If we are not looking at the hazard variable, then we can infer that this variable is
            // the start of a set of four violation variables
            if (variableIsNotAHazardRating(parsedInspectionLine[i])) {
                // Grab the variables that are a part of the violation
                int id = Integer.parseInt(parsedInspectionLine[i]);
                String severity = parsedInspectionLine[i + 1];
                String description = parsedInspectionLine[i + 2];
                String repeat = parsedInspectionLine[i + 3];

                // Put them in our list of violations
                violations.add(new Violation(id, severity, description, repeat));
            }
        }

        // Finally, we assign hazard to a variable, its index depends on whether or not it came
        // before the violations
        String hazardRating;
        if (hazardIsFirst) {
            hazardRating = parsedInspectionLine[5];
        } else {
            int lastIndex = parsedInspectionLine.length - 1;
            hazardRating = parsedInspectionLine[lastIndex];
        }

        // Now that all the variables are assigned, we can return our completed inspection object
        return new Inspection(trackingNumber, inspectionDate, type,
                numCriticalViolations, numNonCriticalViolations, hazardRating,
                violations);
    }

    /**
     * Initialize inspectionList with data by parsing inspectionRawData to extract the relevant data
     * @param inspectionRawData A list of strings (each string holds a line of inspection data)
     */
    private void initializeInspectionList(ArrayList<String> inspectionRawData) {
        // For each line of csv data, create a inspection with it and put it in the inspection list
        for (String dataLine : inspectionRawData) {
            // Separate the comma-spliced-values (and also separate at the pipes to help formatting)
            String[] parsedInspectionLine = dataLine.split("\\s*,\\s*|\\|");

            // Remove any quotations from entries
            for (int i = 0; i < parsedInspectionLine.length; i++) {
                String str = parsedInspectionLine[i];
                str = str.replaceAll("\"", "");
                parsedInspectionLine[i] = str;
            }

            // We want use csv lines that have data on them, so we don't do anything in the event
            // that a csv line filled with column titles is read in.
            if (parsedInspectionLine.length != 0 && !parsedInspectionLine[0].toUpperCase().equals("TRACKINGNUMBER")) {
                // Figure out which style of input we are reading, then read it based off that
                // This is necessary because iteration 1 and the city of surrey data have different
                // orders for their data
                boolean hazardIsFirst = isHazardBeforeViolationLump(parsedInspectionLine);
                Inspection inspection = createInspectionFromCSVLine(parsedInspectionLine,
                        hazardIsFirst);
                this.completeInspectionList.add(inspection);
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