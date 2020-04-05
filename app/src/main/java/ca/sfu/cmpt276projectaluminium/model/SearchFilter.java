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
    private List<Restaurant> filteredRestaurants;
    private String query;  // If the restaurant is not being searched for by name, this should be ""
    private String hazardRating;  // Can be Any, Low, Moderate, or High
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
        this.query = query.toLowerCase();
        this.hazardRating = hazardRating.toLowerCase();
        this.numOfCritViolations = numOfCritViolations;
        this.filteredRestaurants = new ArrayList<>();

        // Put all restaurants that match the search criteria in a list
        filterRestaurants();
    }

    /**
     * @return All restaurants that match the search criteria
     */
    public List<Restaurant> getRestaurants() {
        return this.filteredRestaurants;
    }

    /**
     * Uses the query, hazardRating, and numOfCritViolations variables to filter out restaurants.
     * - A list of all restaurants can be obtained using getInstance() from restaurant manager
     * - Any restaurants that match the search criteria should be put in this.restaurants
     * - Once this function finishes, this.restaurants should contain all restaurants that match the
     *   search criteria
     */
    private void filterRestaurants() {
        // Get all the restaurants that currently exist
        RestaurantManager allRestaurants = RestaurantManager.getInstance();

        // If a restaurant meets all the criteria, then add it to the list of filtered restaurants
        for (Restaurant restaurant : filteredRestaurants) {
            if (nameMatches(restaurant) &&
                hazardRatingMatches(restaurant) &&
                violationsMatch(restaurant)) {
                filteredRestaurants.add(restaurant);
            }
        }
    }

    // Ensures that the name of the restaurant contains the query provided to the filter
    // Returns true if the restaurant name contains the query
    // Returns false if the restaurant name does not contain the query
    private Boolean nameMatches(Restaurant restaurant) {
        String restaurantName = restaurant.getName().toLowerCase();
        if (restaurantName.contains(this.query)) {
            return true;
        } else {
            return false;
        }
    }

    // Ensures that the most recent hazard rating of the restaurant matches the one provided to the
    // filter
    // Returns true if the restaurant rating matches
    // Returns false if the restaurant rating doesn't match
    private Boolean hazardRatingMatches(Restaurant restaurant) {
        // Get the most recent inspection's hazard rating
        InspectionManager inspectionManager = InspectionManager.getInstance();
        List<Inspection> inspections =
                inspectionManager.getInspections(restaurant.getTrackingNumber());
        Inspection mostRecentInspection = inspectionManager.getMostRecentInspection(inspections);
        String restaurantMostRecentHazardRating =
                mostRecentInspection.getHazardRating().toLowerCase();

        // If no specific hazard rating is used for this filter (Any hazard rating fine)...
        if (this.hazardRating.equals("any")) {
            return true;
        }

        // If the restaurant's most recent hazard rating matches the hazard rating provided...
        if (restaurantMostRecentHazardRating.equals(this.hazardRating)) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean violationsMatch(Restaurant restaurant) {
        // If the number of violations is not provided to the filter
        // return true;

        // If the number of violations of the restaurant are in the required threshold
        // return true;
        // else
        // return false;
        return true;
    }
}
