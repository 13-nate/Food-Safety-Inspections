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

public class CSVFileParser {
    private static final String TAG = "CSVFileParser";
    private InputStream is;  // Stores raw csv data
    private List<List<String>> parsedCSVLines;
    // TODO: Deal with an empty csv file
    /**
     * Loads an input stream into this objects memory that can be manipulated to produce lists of
     * restaurants/inspections if needed
     * @param is This is presumably an input stream with CSV file data
     */
    public CSVFileParser(InputStream is) {  // TODO: Make package-private
        this.is = is;
        parsedCSVLines = new ArrayList<>();

        parseCSVInput();
        cleanUpParsedInput();
        getInspectionList();
    }

    /**
     * Converts all valid restaurants from the csv into restaurant objects and puts those in a list
     * - All invalid lines in input are ignored
     * @return a list of restaurant objects
     */
    List<Restaurant> getRestaurantList() {
        List<Restaurant> restaurants = new ArrayList<>();

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
                Log.e(TAG, "getRestaurantList: Unable to convert the following csv line to " +
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
    List<Restaurant> getInspectionList() {
        List<Inspection> inspections = new ArrayList<>();

        for (List<String> parsedLine : parsedCSVLines) {
            // Check that size is a minimum

            // Check what kind it is
        }

        return null;
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

    /**
     * Cleans up the input by removing unnecessary clutter:
     * - Any quotes are removed
     * - Any empty lines are removed
     * - The header line is removed
     * Sources:
     * - https://stackoverflow.com/questions/1196586/calling-remove-in-foreach-loop-in-java
     * - https://stackoverflow.com/questions/2608665/how-can-i-trim-beginning-and-ending-double-quotes-from-a-string
     */
    private void cleanUpParsedInput() {
        // If the first line is empty or a title line without data, we remove the line
        List<String> firstLine = parsedCSVLines.get(0);
        boolean isEmpty = firstLine.size() == 0;
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

    /**
     * Checks if the parsed CSV input is valid.
     * @param parsedCSVLine The CSV input that corresponds to a restaurant object
     * @return True if input can be made into an object with no problems, false otherwise
     */
    private boolean isValidRestaurant(List<String> parsedCSVLine) {

        return false;
    }

    /**
     * Checks if the parsed CSV input is valid.
     * @param parsedCSVLine The CSV input that corresponds to an inspection object
     * @return True if input can be made into an object with no problems, false otherwise
     */
    private boolean isValidInspection(List<String> parsedCSVLine) {

        return false;
    }
}
