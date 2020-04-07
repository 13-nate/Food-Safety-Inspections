package ca.sfu.cmpt276projectaluminium.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to filter out restaurants that don't meet the filter requirements
 * To use it:
 * - Get an instance of the SearchFilter object
 * - Set any filters you want to use (Or reset filters you don't want to use)
 * - Call getRestaurantTrackingNumbers() to get your list of filtered restaurants
 */
public class SearchFilter {
    private static final String TAG = "model.SearchFilter";

    private List<String> filteredRestaurantTrackingNumbers;
    private String searchTerm;
    private String hazardRating;
    private int violationsThreshold;
    private String violationFilterType;

    /*
        Singleton Support (As per https://www.youtube.com/watch?v=evkPjPIV6cw - Brain Fraser)
     */
    private static SearchFilter instance;
    // Private to prevent anyone else from instantiating
    private SearchFilter() {
        this.filteredRestaurantTrackingNumbers = new ArrayList<>();
        this.searchTerm = "";
        this.hazardRating = "any";
        this.violationsThreshold = 0;
        this.violationFilterType = "none";
    }
    public static SearchFilter getInstance() {
        if (instance == null) {
            instance = new SearchFilter();
        }

        return instance;
    }

    /**
     * @return All restaurants that match the search criteria
     */
    public List<String> getRestaurantTrackingNumbers() {
        // Filter restaurants based on the preferences
        filterRestaurants();

        // Returned the filtered restaurants
        return this.filteredRestaurantTrackingNumbers;
    }

    /**
     * Any restaurants that do not contain the provided search term will be filtered out when the
     * method getRestaurantTrackingNumbers() is called
     * @param searchTerm The string used to filter out restaurants
     */
    public void setSearchTerm(String searchTerm) {
        // The searchTerm cannot be null (If you want to clear the search term, just pass in "")
        if (searchTerm != null) {
            this.searchTerm = searchTerm.toLowerCase();
        } else {
            Log.e(TAG, "setSearchTerm: searchTerm cannot be null.", null);
        }

    }

    /**
     * Any restaurants whose most recent inspection does not have the provided hazard rating will be
     * filtered out when the method getRestaurantTrackingNumbers() is called
     * Hazard rating can be:
     * - "any"
     * - "low"
     * - "moderate"
     * - "high"
     * @param hazardRating The hazard rating used to filter out restaurants
     */
    public void setHazardRating(String hazardRating) {
        // Passed in strings cannot be null.
        if (hazardRating == null) {
            Log.e(TAG, "setViolationFilterType: hazardRating cannot be null.", null);
            return;
        }

        // Convert the string to lowercase for consistency
        hazardRating = hazardRating.toLowerCase();

        // The hazard rating must be one of four specified strings.
        if (hazardRating.equals("any") || hazardRating.equals("low") ||
                hazardRating.equals("moderate") || hazardRating.equals("high")) {
            this.hazardRating = hazardRating;
        } else {
            Log.e(TAG, "setSearchTerm: hazardRating can only be \"any\", \"low\", " +
                    "\"moderate\", or \"high\".", null);
        }
    }

    /**
     * Any restaurants that does not meet the provided number of violations within the past year
     * will be filtered out when the method getRestaurantTrackingNumbers() is called
     * @param violationsThreshold The number of violations used to filter out restaurants
     */
    public void setViolationsThreshold(int violationsThreshold) {
        // The number of violations for the threshold can be any non-negative number
        if (violationsThreshold >= 0) {
            this.violationsThreshold = violationsThreshold;
        } else {
            Log.e(TAG, "setSearchTerm: violationsThreshold must be a non-negative " +
                    "number.", null);
        }
    }

    /**
     * This sets whether the number of violations being searched for must be above or below the
     * threshold.
     * Depending on the filter type, any restaurants will be checked if their number of violations
     * are above/below the threshold when the method getRestaurantTrackingNumbers() is called
     * Filter type can be:
     * - "none"
     * - "below"
     * - "above"
     * @param violationFilterType The filter type is used to filter out restaurants
     */
    public void setViolationFilterType(String violationFilterType) {
        // Passed in strings cannot be null.
        if (violationFilterType == null) {
            Log.e(TAG, "setViolationFilterType: violationFilterType cannot be null.", null);
            return;
        }

        // Convert the string to lowercase for consistency
        violationFilterType = violationFilterType.toLowerCase();

        // The filter type must be one of three specified strings.
        if (violationFilterType.equals("none") || violationFilterType.equals("below") ||
                violationFilterType.equals("above")) {
            this.violationFilterType = violationFilterType;
        } else {
            Log.e(TAG, "setSearchTerm: violationFilterType can only be \"none\", " +
                    "\"below\", or \"above\".", null);
        }
    }

    /**
     * If you do not want to filter by search term, call this method and it will reset the search
     * term filter
     */
    public void resetSearchTerm() {
        setSearchTerm("");
    }

    /**
     * If you do not want to filter by hazard rating, call this method and it will reset the hazard
     * rating filter
     */
    public void resetHazardRating() {
        setHazardRating("any");
    }

    /**
     * If you do not want to filter by violations, call this method and it will reset the violations
     * filter
     */
    public void resetViolationsFilters() {
        setViolationsThreshold(0);
        setViolationFilterType("none");
    }

    /**
     * If you do not want to filter by anything, call this method and it will reset all filters
     */
    public void resetAllFilters() {
        setSearchTerm("");
        setHazardRating("any");
        setViolationsThreshold(0);
        setViolationFilterType("none");
    }

    /**
     * Uses the searchTerm, hazardRating, violationsThreshold, and violationFilterType variables to
     * filter out restaurants.
     * - A list of all restaurants can be obtained using getInstance() from restaurant manager
     * - Any restaurants that match the search criteria will have their tracking numbers put in the
     *   list of filtered tracking numbers
     * - Once this function finishes, this.restaurants should contain all restaurant tracking
     *   numbers that match the search criteria
     */
    private void filterRestaurants() {
        // Reset the filtered restaurants
        this.filteredRestaurantTrackingNumbers.clear();

        // Get all the restaurants that currently exist
        RestaurantManager allRestaurants = RestaurantManager.getInstance();

        // If a restaurant meets all the criteria, then add its tracking number to the filtered list
        for (Restaurant restaurant : allRestaurants) {
            if (nameMatches(restaurant) &&
                hazardRatingMatches(restaurant) &&
                violationsMatch(restaurant)) {
                this.filteredRestaurantTrackingNumbers.add(restaurant.getTrackingNumber());
            }
        }
    }

    // Ensures that the name of the restaurant contains the query provided to the filter
    // Returns true if the restaurant name contains the query
    // Returns false if the restaurant name does not contain the query
    private Boolean nameMatches(Restaurant restaurant) {
        String restaurantName = restaurant.getName().toLowerCase();
        return restaurantName.contains(this.searchTerm);
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

        // todo: restaurantMostRecentHazardRating = restaurant.getmostrecentinspectionhazardrating();

        // If no specific hazard rating is used for this filter (Any hazard rating fine)...
        if (this.hazardRating.equals("any")) {
            return true;
        }

        // If the restaurant's most recent hazard rating matches the hazard rating provided...
        return restaurantMostRecentHazardRating.equals(this.hazardRating);
    }

    // Ensures that the violations in the past year are at the level required by the filter
    // Returns true if the restaurant's violations from the past year are within bounds
    // Returns false if the restaurant's violations from the past year are not within bounds
    private Boolean violationsMatch(Restaurant restaurant) {
        // Get the number of critical violations within the last year for the restaurant
        int critViolationsWithinYear = this.violationsThreshold;
//TODO:        int critViolationsWithinYear = restaurant.getNumCriticalViolationsWithinYear();

        // Return true if the violation is within bounds, false if it is not
        if (this.violationFilterType.equals("below")) {
            return critViolationsWithinYear <= this.violationsThreshold;
        } else if (this.violationFilterType.equals("above")) {
            return critViolationsWithinYear >= this.violationsThreshold;
        } else {
            // If the number of violations is not provided to the filter, return true
            return true;
        }
    }
}
