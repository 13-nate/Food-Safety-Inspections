package ca.sfu.cmpt276projectaluminium.model;

import java.io.InputStream;
import java.util.List;

public class CSVFileParser {
    private InputStream is;  // Stores raw csv data
    private List<List<String>> parsedCSVLines;

    /**
     * Loads an input stream into this objects memory that can be manipulated to produce lists of
     * restaurants/inspections if needed
     * @param is This is presumably an input stream with CSV file data
     */
    CSVFileParser(InputStream is) {
        this.is = is;
    }

    /**
     * Converts all valid restaurants from the csv into restaurant objects and puts those in a list
     * - All invalid lines in input are ignored
     * @return a list of restaurant objects
     */
    List<Restaurant> getRestaurantList() {

        return null;
    }

    /**
     * Converts all valid inspections from the csv into inspection objects and puts those in a list
     * - All invalid lines in input are ignored
     * @return a list of inspection objects
     */
    List<Restaurant> getInspectionList() {

        return null;
    }

    /**
     * Converts the CSV lines into a list of parsed CSV lines
     * - Example:
     *         "13, 45, 28, 31"       [[13, 45, 28, 31],
     *         "94, 12, 34, 11"  --->  [94, 12, 34, 11],
     *         "17, 82, 91, 23"        [17, 82, 91, 23]]
     * This is so that we can easily look at any element of any line of the CSV
     */
    private void parseCSVInput() {

    }

    /**
     * Cleans up the input by removing unnecessary clutter:
     * - Any quotes and vertical lines are removed
     * - Any empty lines are removed
     * - The header line is removed
     */
    private void cleanUpParsedInput() {

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
