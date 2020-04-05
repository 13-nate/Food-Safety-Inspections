package ca.sfu.cmpt276projectaluminium.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The input to the class should be search criteria that we want to use such as:
 * - Search String (Ex. Part of a restaurant name)
 * - Hazard level of the most recent inspection
 * - Number of critical violations within the last year
 * The output from the class should be the restaurants that meet the search criteria (in a list)
 */
public class SearchFilter {
    private List<Restaurant> restaurants;
    private String query;
    private String hazardRating;
    private int numOfCritViolations;

    /**
     * Initialize the search filter with the appropriate search criteria
     * @param query Any restaurant that doesn't contain this string in its name will be filtered out
     * @param hazardRating Any restaurant with that doesn't have this hazard rating will be filtered
     *                     out
     * @param numOfCritViolations Any restaurant with more critical violations than this number will
     *                            be filtered out
     */
    public SearchFilter(String query, String hazardRating, int numOfCritViolations) {
        this.query = query;
        this.hazardRating = hazardRating;
        this.numOfCritViolations = numOfCritViolations;
        this.restaurants = new ArrayList<>();

        // Put all restaurants that match the search criteria in a list
        filterRestaurants();
    }

    /**
     * @return All restaurants that match the search criteria
     */
    public List<Restaurant> getRestaurants() {
        return this.restaurants;
    }

    /**
     * Uses the query, hazardRating, and numOfCritViolations variables to filter out restaurants.
     * - A list of all restaurants can be obtained using getInstance() from restaurant manager
     * - Any restaurants that match the search criteria should be put in this.restaurants
     * - Once this function finishes, this.restaurants should contain all restaurants that match the
     *   search criteria
     */
    private void filterRestaurants() {

    }
}
