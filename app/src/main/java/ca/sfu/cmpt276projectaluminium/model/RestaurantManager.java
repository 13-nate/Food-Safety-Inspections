package ca.sfu.cmpt276projectaluminium.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/* Sources:
 * https://stackabuse.com/reading-and-writing-csvs-in-java/
 * https://stackoverflow.com/questions/6488339/using-filereader-causes-a-compiler-error-unhandled-exception-type-filenotfounde
 * https://stackoverflow.com/questions/2649322/how-do-i-close-a-file-after-catching-an-ioexception-in-java
 * https://javarevisited.blogspot.com/2015/12/how-to-split-comma-separated-string-in-java-example.html
 * https://www.techiedelight.com/differences-between-iterator-and-iterable-in-java/
 */

/**
 * Manages data about restaurants by storing them all in an easily accessible list
 */
public class RestaurantManager {
    private static final String TAG = "RestaurantManager";
    private static final String RESTAURANT_FILE_PATH = "";
    private ArrayList<Restaurant> restaurantList = new ArrayList<>();

    /**
     * Extracts restaurant info line by line and stores it in a list
     * @return A list of strings (each string holds a line of restaurant data)
     */
    private ArrayList<String> getFileData() {
        ArrayList<String> restaurantData = new ArrayList<>();
        BufferedReader reader;

        // Attempt to open file
        try {
            reader = new BufferedReader(new FileReader(RESTAURANT_FILE_PATH));
        } catch (FileNotFoundException ex) {
            Log.i(TAG, "Could not read file at path: " + RESTAURANT_FILE_PATH, ex);
            return restaurantData;
        }

        // Read each line into the ArrayList
        try {
            // Read the file
            String row;
            while ((row = reader.readLine()) != null) {
                restaurantData.add(row);
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
        return restaurantData;
    }

    /**
     * Initialize restaurantList with data by parsing restaurantData to extract the relevant data
     * @param restaurantData A list of strings (each string holds a line of restaurant data)
     */
    private void initializeRestaurantList(ArrayList<String> restaurantData) {
        // For each line of csv data, create a restaurant with it and put it in the restaurant list
        for (String dataLine : restaurantData) {
            // Separate the comma-spliced-values
            String[] restaurantValues = dataLine.split("\\s*,\\s*");

            // Extract the comma-spliced-values into variables
            String trackingNumber = restaurantValues[0];
            String name = restaurantValues[1];
            String address = restaurantValues[2];
            String city = restaurantValues[3];
            String type = restaurantValues[4];
            double latitude = Double.parseDouble(restaurantValues[5]);
            double longitude = Double.parseDouble(restaurantValues[6]);

            // Create a restaurant
            Restaurant restaurant = new Restaurant(trackingNumber, name, address, city, type,
                                                    latitude, longitude);

            // Store the restaurant inside the list of restaurants
            this.restaurantList.add(restaurant);
        }
    }

    public RestaurantManager() {
        // Get data out of the restaurants file and store it in a readable way.
        ArrayList<String> restaurantData = getFileData();

        // Whilst filling arrayList with restaurant objects by properly initializing restaurants.
        initializeRestaurantList(restaurantData);
    }

    public int getSize() {
        return restaurantList.size();
    }

    /**
     * Allows for the iteration of RestaurantManager in a for-each loop as if it were a list
     */
    Iterator<Restaurant> Iterator = restaurantList.iterator();
}
