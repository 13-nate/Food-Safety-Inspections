package ca.sfu.cmpt276projectaluminium.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class converts a csv file input stream into lists of objects which you can acquire by
 * calling the following methods:
 * - getRestaurants();
 * - getInspections();
 */
public class CSVFileParser {
    private static final String TAG = "CSVFileParser";
    private InputStream is;  // Stores raw csv data
    private List<List<String>> parsedCSVLines;
    /**
     * Loads an input stream into this objects memory that can be manipulated to produce lists of
     * restaurants/inspections if needed
     * @param is This is presumably an input stream with CSV file data
     */
    CSVFileParser(InputStream is) {
        this.is = is;
        parsedCSVLines = new ArrayList<>();

        parseCSVInput();
        cleanUpParsedInput();
    }

    /**
     * Converts all valid restaurants from the csv into restaurant objects and puts those in a list
     * - All invalid lines in input are ignored
     * @return a list of restaurant objects
     */
    List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();

        // Turn each line of parsed CSV data into a restaurant object
        for (List<String> parsedLine : parsedCSVLines) {
            // Convert the parsed line to a restaurant object
            try {
                // If there are too few elements to make an object, throw an exception
                if (parsedLine.size() < 7) {
                    throw new ArrayIndexOutOfBoundsException("Not enough information to create an" +
                            " object");
                }

                // Gather the relevant restaurant data
                String trackingNumber = parsedLine.get(0);
                String name = parsedLine.get(1);
                String address = parsedLine.get(2);
                String city = parsedLine.get(3);
                String type = parsedLine.get(4);
                double latitude = Double.parseDouble(parsedLine.get(5));
                double longitude = Double.parseDouble(parsedLine.get(6));

                // Create the restaurant
                Restaurant restaurant = new Restaurant(trackingNumber, name, address, city, type,
                        latitude, longitude);

                // Store the restaurant
                restaurants.add(restaurant);
            } catch (Exception e) {
                // Instead of crashing, we simply don't add anything to the list & then print a log
                Log.e(TAG, "getRestaurants: Unable to convert the following csv line to " +
                        "a restaurant: \n" + parsedLine.toString(), e);
            }
        }

        return restaurants;
    }

    /**
     * Converts all valid inspections from the csv into inspection objects and puts those in a list
     * - All invalid lines in input are ignored
     * @return a list of inspection objects
     */
    List<Inspection> getInspections() {
        List<Inspection> inspections = new ArrayList<>();

        // Turn each line of parsed CSV data into an inspection object
        for (List<String> parsedLine : parsedCSVLines) {
            // Convert the parsed line to a restaurant object
            try {
                // If there are too few elements to make an object, throw an exception
                if (parsedLine.size() < 6) {
                    throw new ArrayIndexOutOfBoundsException("Not enough information to create an" +
                            " object");
                }

                // Figure out where the hazard is in this data (There's two types of formatting)
                boolean hazardIsFirst = isHazardBeforeViolationLump(parsedLine);

                /*
                    Gather the relevant inspection data
                 */
                // No matter how hazard rating and violations are ordered, we 100% know the location of
                // these first few variables
                String trackingNumber = parsedLine.get(0);
                int inspectionDate = Integer.parseInt(parsedLine.get(1));
                String type = parsedLine.get(2);
                int numCriticalViolations = Integer.parseInt(parsedLine.get(3));
                int numNonCriticalViolations = Integer.parseInt(parsedLine.get(4));

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
                for (int i = violationStartIndex; i < parsedLine.size(); i += 4) {
                    // If we are looking at the hazard variable, we don't have to do anything.
                    // If we are not looking at the hazard variable, then we can infer that this variable is
                    // the start of a set of four violation variables
                    if (isElementPartOfViolation(parsedLine.get(i))) {
                        // Grab the variables that are a part of the violation
                        int id = Integer.parseInt(parsedLine.get(i));
                        String severity = parsedLine.get(i + 1);
                        String description = parsedLine.get(i + 2);
                        String repeat = parsedLine.get(i + 3);

                        // Put them in our list of violations
                        violations.add(new Violation(id, severity, description, repeat));
                    }
                }

                // Finally, we assign hazard to a variable, its index depends on whether or not it came
                // before the violations
                String hazardRating;
                if (hazardIsFirst) {
                    hazardRating = parsedLine.get(5);
                } else {
                    int lastIndex = parsedLine.size() - 1;
                    hazardRating = parsedLine.get(lastIndex);
                }

                // Create the inspection
                Inspection inspection = new Inspection(trackingNumber, inspectionDate, type,
                        numCriticalViolations, numNonCriticalViolations, hazardRating,
                        violations);

                // Store the inspection
                inspections.add(inspection);
            } catch (Exception e) {
                // Instead of crashing, we simply don't add anything to the list & then print a log
                Log.e(TAG, "getInspections: Unable to convert the following csv line to " +
                        "an inspection: \n" + parsedLine.toString(), e);
            }
        }

        return inspections;
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
    private boolean isHazardBeforeViolationLump(List<String> parsedInspectionLine) {
        if (parsedInspectionLine.get(5).equals("")) {
            return false;
        }

        return !isInteger(parsedInspectionLine.get(5));
    }

    // We know that if the string can be made into an integer, then it is not a hazard rating
    private boolean isElementPartOfViolation(String s) {
        return isInteger(s);
    }

    /**
     * Converts the CSV lines into a list of parsed CSV lines
     * - Example:
     *         "13, 45, 28, 31"       [[13, 45, 28, 31],
     *         "94, 12, 34, 11"  --->  [94, 12, 34, 11],
     *         "17, 82, 91, 23"        [17, 82, 91, 23]]
     * This is so that we can easily look at any element of any line of the CSV
     * - THIS ALSO SPLITS LINES ON VERTICAL BARS
     */
    private void parseCSVInput() {
        // Initialize the reader for the csv file
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                StandardCharsets.UTF_8));

        // Parse each CSV line then store it
        // The try-catch block is to deal with any file-reading/opening errors
        try {
            // Read the file
            String CSVLine;
            while ((CSVLine = reader.readLine()) != null) {
                // Parse the CSV line and store it
                this.parsedCSVLines.add(SplitStringIntoList(CSVLine));
            }
        } catch (IOException ex) {
            Log.e(TAG, "Error reading line", ex);
        } finally {
            try {
                // Close the file
                reader.close();
            } catch (IOException ex) {
                Log.e(TAG, "Error closing file", ex);
            }
        }
    }

    /**
     * Convert a CSV Line data string to a list of the individual elements
     * @param RawCSVLine A CSV line data string, commas and all
     * @return A list of individual CSV elements on a line
     */
    private List<String> SplitStringIntoList(String RawCSVLine) {
        // Separate the comma-spliced-values (and also separate at the pipes to help formatting)
        String[] separatedCSVLine = RawCSVLine.split("\\s*,\\s*|\\|");

        // Properly store the separated values in a list
        List<String> parsedCSVLine = new ArrayList<>();
        Collections.addAll(parsedCSVLine, separatedCSVLine);

        return parsedCSVLine;
    }

    // Remove any quotes that are inside input elements
    private void removeQuotesFromList(List<String> parsedLine) {
        for (int i = 0; i < parsedLine.size(); i++) {
            String quoteFreeString = parsedLine.get(i).replaceAll("^\"|\"$", "");
            parsedLine.set(i, quoteFreeString);
        }
    }

    // Sources:
    // - https://stackoverflow.com/questions/1196586/calling-remove-in-foreach-loop-in-java
    // - https://stackoverflow.com/questions/2608665/how-can-i-trim-beginning-and-ending-double-quotes-from-a-string
    /**
     * Cleans up the input by removing unnecessary clutter:
     * - Any quotes are removed
     * - Any empty lines are removed
     * - The header line is removed
     */
    private void cleanUpParsedInput() {
        // If the first line is empty or a title line without data, we remove the line
        List<String> firstLine = parsedCSVLines.get(0);
        boolean isEmpty = firstLine.size() == 0 || firstLine.get(0).equals("");
        if (isEmpty || firstLine.get(0).toLowerCase().equals("\"trackingnumber\"")) {
            // Remove the line
            parsedCSVLines.remove(0);
        }

        Iterator<List<String>> i = parsedCSVLines.iterator();
        while (i.hasNext()) {
            // Get the next line of input
            List<String> parsedLine = i.next();

            // Remove/modify lines that are incorrectly formatted
            if (parsedLine.size() == 0) {
                i.remove();
            } if (parsedLine.contains("\"\"")) {
                i.remove();
            } else {
                // Remove any quotes from the input
                removeQuotesFromList(parsedLine);
            }
        }

    }
}
