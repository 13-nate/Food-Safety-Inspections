package ca.sfu.cmpt276projectaluminium.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

    private List<Restaurant> restaurantList;

    /*
        Singleton Support (As per https://www.youtube.com/watch?v=evkPjPIV6cw - Brain Fraser)
     */
    private static RestaurantManager instance;
    // Private to prevent anyone else from instantiating
    private RestaurantManager() {
        this.restaurantList = new ArrayList<>();
    }
    // This version is called when you need to simply access the currently stored data
    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }

        return instance;
    }
    // This version is called when you also need update/initialize csv data
    public static RestaurantManager getInstance(InputStream is) {
        if (instance == null) {
            instance = new RestaurantManager();
        }

        CSVFileParser fileParser = new CSVFileParser(is);
        instance.restaurantList = fileParser.getRestaurants();
        Collections.sort(instance.restaurantList);

        return instance;
    }

    /**
     * Creates and returns the restaurant object that correlates to the passed in parameter
     * This can be used to create a restaurant after passing the argument between activities.
     * @param trackingNumber The number that uniquely identifies a restaurant.
     * @return A restaurant that matches the parameter
     */
    public Restaurant recreateRestaurant(String trackingNumber) {
        // Find a restaurant that matches the trackingNumber and return it.
        for (Restaurant restaurant : this.restaurantList) {
            boolean trackingNumberMatches = restaurant.getTrackingNumber().equals(trackingNumber);
            if (trackingNumberMatches) {
                return restaurant;
            }
        }

        // This should only be returned if an invalid trackingNumber was passed in
        return null;
    }

    public int getSize() {
        return this.restaurantList.size();
    }

    /**
     * Allows for the iteration of RestaurantManager in a for-each loop as if it were a list
     */
    @Override
    public Iterator<Restaurant> iterator () {
        return this.restaurantList.iterator();
    }
}
