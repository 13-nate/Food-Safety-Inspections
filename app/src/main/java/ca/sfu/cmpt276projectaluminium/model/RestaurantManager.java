package ca.sfu.cmpt276projectaluminium.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import ca.sfu.cmpt276projectaluminium.model.Restaurant;

/* Sources:
 * https://stackabuse.com/reading-and-writing-csvs-in-java/
 * https://stackoverflow.com/questions/6488339/using-filereader-causes-a-compiler-error-unhandled-exception-type-filenotfounde
 * https://stackoverflow.com/questions/2649322/how-do-i-close-a-file-after-catching-an-ioexception-in-java
 * https://javarevisited.blogspot.com/2015/12/how-to-split-comma-separated-string-in-java-example.html
 * https://www.techiedelight.com/differences-between-iterator-and-iterable-in-java/
 * https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app
 * https://stackoverflow.com/questions/18895915/how-to-sort-an-array-of-objects-in-java
 * https://stackoverflow.com/questions/19316394/removing-double-quotes-from-a-string-in-java/19316426
 */

/**
 * Manages data about restaurants by storing them all in an easily accessible list
 */
public class RestaurantManager implements Iterable<Restaurant>{
    private static final String TAG = "RestaurantManager";
    private static ArrayList<Restaurant> restaurantList = new ArrayList<>();

    /**
     * Fills the ArrayList variable with objects based on provided csv data
     * Should be called once, on program initialization
     */
    public static void initialize(InputStream is) {
        // Get data out of the restaurants file and store it in a readable way.
        ArrayList<String> restaurantData = getFileData(is);

        // Fill arrayList with restaurant objects by properly initializing restaurants.
        initializeRestaurantList(restaurantData);
    }

    /**
     * Extracts restaurant info line by line and stores it in a list
     * @return A list of strings (each string holds a line of restaurant data)
     */
    private static ArrayList<String> getFileData(InputStream is) {
        ArrayList<String> restaurantData = new ArrayList<>();

        // Initialize the reader for the csv file
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                Charset.forName("UTF-8")));

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
    private static void initializeRestaurantList(ArrayList<String> restaurantData) {
        // For each line of csv data, create a restaurant with it and put it in the restaurant list
        for (String dataLine : restaurantData) {
            // Separate the comma-spliced-values
            String[] restaurantValues = dataLine.split("\\s*,\\s*");

            // Remove any quotations from entries
            for (int i = 0; i < restaurantValues.length; i++) {
                String str = restaurantValues[i];
                str = str.replaceAll("\"", "");
                restaurantValues[i] = str;
            }

            // If the current csv row is data (and not the title), then add it to the list
            if (!(restaurantValues[0].equals("TRACKINGNUMBER"))) {
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
                restaurantList.add(restaurant);
            }
        }
        Collections.sort(restaurantList);
    }

    /**
     * Creates and returns the restaurant object that correlates to the passed in parameter
     * This can be used to create a restaurant after passing the argument between activities.
     * @param trackingNumber The number that uniquely identifies a restaurant.
     * @return A restaurant that matches the parameter
     */
    public static Restaurant recreateRestaurant(String trackingNumber) {
        // Find a restaurant that matches the trackingNumber and return it.
        for (Restaurant restaurant : restaurantList) {
            boolean trackingNumberMatches = restaurant.getTrackingNumber().equals(trackingNumber);
            if (trackingNumberMatches) {
                return restaurant;
            }
        }

        // This should only be returned if an invalid trackingNumber was passed in
        return null;
    }

    /**
     * Nothing special needs to be done for constructor because setup is done by initialize()
     */
    public RestaurantManager() {

    }

    public int getSize() {
        return restaurantList.size();
    }

    /**
     * Allows for the iteration of RestaurantManager in a for-each loop as if it were a list
     */
    @Override
    public Iterator<Restaurant> iterator () {
        return restaurantList.iterator();
    }
}
