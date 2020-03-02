package ca.sfu.cmpt276projectaluminium.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/* Sources:
 * https://stackabuse.com/reading-and-writing-csvs-in-java/
 * https://stackoverflow.com/questions/6488339/using-filereader-causes-a-compiler-error-unhandled-exception-type-filenotfounde
 * https://stackoverflow.com/questions/2649322/how-do-i-close-a-file-after-catching-an-ioexception-in-java
 */

/**
 * Manages data about restaurants by storing them all in an easily accessible list
 */
public class RestaurantManager {
    private static final String TAG = "RestaurantManager";
    private static final String FILE_PATH = "";
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
            reader = new BufferedReader(new FileReader(FILE_PATH));
        } catch (FileNotFoundException ex) {
            Log.i(TAG, "Could not read file at path: " + FILE_PATH, ex);
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
    }

    RestaurantManager() {
        // Get data out of the restaurants file and store it in a readable way.
        ArrayList<String> restaurantData = getFileData();

        // Whilst filling arrayList with restaurant objects, initialize a restaurant.
        initializeRestaurantList(restaurantData);
    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurantList;
    }

    public int getSize() {
        return restaurantList.size();
    }

    // Implement an iterator that lets them call for each on it - that way they don't modify the list directly
}
