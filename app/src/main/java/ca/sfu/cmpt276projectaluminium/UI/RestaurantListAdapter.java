package ca.sfu.cmpt276projectaluminium.UI;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.model.Inspection;
import ca.sfu.cmpt276projectaluminium.model.InspectionManager;
import ca.sfu.cmpt276projectaluminium.model.Restaurant;
import ca.sfu.cmpt276projectaluminium.model.RestaurantManager;
import ca.sfu.cmpt276projectaluminium.model.SearchFilter;

/*
Sources:
 - https://stackoverflow.com/questions/6958279/how-to-use-a-custom-arrayadapter-in-a-separate-class
    - Help make this external
 */

/**
 * This class is used to convert a list of restaurant objects into a list of display objects for
 * a list view
 */
public class RestaurantListAdapter extends ArrayAdapter<Restaurant> implements Filterable {
    private static final String TAG = "RestaurantListAdapter";
    private List<Restaurant> filteredRestaurants;
    private Filter restaurantFilter;
    private Context context;

    // Don't need to pass arguments because has references to outer class
    public RestaurantListAdapter(Context context, List<Restaurant> restaurants) {
        super(context, R.layout.restaurants_view, restaurants);
        this.filteredRestaurants = restaurants;
        this.context = context;
    }

    /**
     * This function takes a restaurant name and finds it's corresponding icon.
     * It then returns the android resource id of the icon
     * @param s The restaurant name
     * @return The restaurant icon resource id number
     */
    private int getDrawableID(String s) {
        String restaurantName = s.toLowerCase();

        if (restaurantName.contains("7-eleven")) {
            return R.drawable.restauraunt_icon_7_eleven;
        } else if (restaurantName.contains("a&w") || restaurantName.contains("a & w")) {
            return R.drawable.restauraunt_icon_a_and_w;
        } else if (restaurantName.contains("blenz")) {
            return R.drawable.restauraunt_icon_blenz;
        } else if (restaurantName.contains("booster juice")) {
            return R.drawable.restauraunt_icon_booster_juice;
        } else if (restaurantName.contains("boston pizza")) {
            return R.drawable.restauraunt_icon_boston_pizza;
        } else if (restaurantName.contains("burger king")) {
            return R.drawable.restauraunt_icon_burger_king;
        } else if (restaurantName.contains("church's chicken")) {
            return R.drawable.restauraunt_icon_churchs_chicken;
        } else if (restaurantName.contains("cobs bread")) {
            return R.drawable.restauraunt_icon_cobs_bread;
        } else if (restaurantName.contains("d-plus pizza")) {
            return R.drawable.restauraunt_icon_d_plus_pizza;
        } else if (restaurantName.contains("dairy queen")) {
            return R.drawable.restauraunt_icon_dairy_queen;
        } else if (restaurantName.contains("domino's pizza")) {
            return R.drawable.restauraunt_icon_dominos_pizza;
        } else if (restaurantName.contains("freshii")) {
            return R.drawable.restauraunt_icon_freshii;
        } else if (restaurantName.contains("freshslice")) {
            return R.drawable.restauraunt_icon_freshslice;
        } else if (restaurantName.contains("orange julius")) {
            return R.drawable.restauraunt_icon_orange_julius;
        } else if (restaurantName.contains("panago")) {
            return R.drawable.restauraunt_icon_panago;
        } else if (restaurantName.contains("papa john's")) {
            return R.drawable.restauraunt_icon_papa_johns;
        } else if (restaurantName.contains("pizza hut")) {
            return R.drawable.restauraunt_icon_pizza_hut;
        } else if (restaurantName.contains("quesada")) {
            return R.drawable.restauraunt_icon_quesada;
        } else if (restaurantName.contains("quizno's") || restaurantName.contains("quiznos")) {
            return R.drawable.restauraunt_icon_quiznos;
        } else if (restaurantName.contains("real canadian superstore")) {
            return R.drawable.restauraunt_icon_real_canadian_superstore;
        } else if (restaurantName.contains("safeway")) {
            return R.drawable.restauraunt_icon_safeway;
        } else if (restaurantName.contains("save on foods")) {
            return R.drawable.restauraunt_icon_save_on_foods;
        } else if (restaurantName.contains("starbucks coffee")) {
            return R.drawable.restauraunt_icon_starbucks;
        } else if (restaurantName.contains("subway")) {
            return R.drawable.restauraunt_icon_subway;
        } else if (restaurantName.contains("t&t")) {
            return R.drawable.restauraunt_icon_t_and_t;
        } else if (restaurantName.contains("tim horton's") || restaurantName.contains("tim hortons")) {
            return R.drawable.restauraunt_icon_tim_hortons;
        } else if (restaurantName.contains("wendy's")) {
            return R.drawable.restauraunt_icon_wendys;
        } else if (restaurantName.contains("white spot")) {
            return R.drawable.restauraunt_icon_white_spot;
        } else {
            return R.drawable.cooking;
        }
    }

    @NonNull
    @Override
    //display image bases on position
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // make sure we have a view to work with (may have been given null
        View itemView = convertView;
        if (itemView == null) {
            itemView = ((Activity)context).getLayoutInflater()
                    .inflate(R.layout.restaurants_view, parent, false);
        }

        // find restaurant to work with want different hazard images, name, and date and number of issues
        Restaurant currentRestaurant = this.getItem(position);

        // Get relevant inspections
        InspectionManager inspectionManager = InspectionManager.getInstance();
        ArrayList<Inspection> inspections;
        inspections = inspectionManager.getInspections(currentRestaurant.getTrackingNumber());

        // Get the newest inspection
        Inspection newestInspection = inspectionManager.getMostRecentInspection(inspections);

        // fill the view
        // display restaurant name
        TextView nameTxt =  itemView.findViewById(R.id.txtRestaurentName);
        nameTxt.setText(currentRestaurant.getName());

        // display hazard image
        ImageView hazardImage = itemView.findViewById(R.id.iconHazard);
        String hazardRating = newestInspection.getHazardRating();
        if (hazardRating.equals("Low")) {
            hazardImage.setImageResource(R.drawable.hazard_low);
            itemView.setBackground(context.getDrawable(R.drawable.border_green));

        } else if (hazardRating.equals("Moderate")) {
            hazardImage.setImageResource(R.drawable.hazard_medium);
            itemView.setBackground(context.getDrawable(R.drawable.border_yellow));

        } else if (hazardRating.equals("High")) {
            hazardImage.setImageResource(R.drawable.hazard_high);
            itemView.setBackground(context.getDrawable(R.drawable.border_red));

        } else {
            hazardImage.setImageResource(R.drawable.not_available);
            itemView.setBackground(context.getDrawable(R.drawable.border_blue));
        }

        RestaurantManager restaurantManager = RestaurantManager.getInstance();

        if (restaurantManager.isFavourite(currentRestaurant.getTrackingNumber())){
            itemView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }

        // Fetch the imageView so we can later set it to a restaurant icon
        ImageView restaurantIcon = itemView.findViewById(R.id.iconRestaurant);

        // Get the id of the image we want to use, depending on the restaurant name
        int drawableID = getDrawableID(currentRestaurant.getName());

        // Set imageView to restaurant icon. If restaurant doesn't have icon, use default one
        restaurantIcon.setImageResource(drawableID);

        // display address
        TextView addressTxt = itemView.findViewById(R.id.txtAddress);
        addressTxt.setText(currentRestaurant.getAddress());

        // display number of issues
        TextView issuesNumberTxt = itemView.findViewById(R.id.txtIssuesNumber);
        issuesNumberTxt.setText(context.getString(R.string.issues) + " "
                + newestInspection.getNumTotalViolations());

        // display date
        TextView dateTxt = itemView.findViewById(R.id.txtdate);
        dateTxt.setText(context.getString(R.string.Last_inspection) + " "
                + newestInspection.intelligentDate());
        return itemView;
    }

    // Sauce:
    // https://www.survivingwithandroid.com/android-listview-custom-filter/
    @Override
    public Filter getFilter() {
        if (restaurantFilter == null) {
            restaurantFilter = new RestaurantFilter();
        }

        return restaurantFilter;
    }

    // Sauce:
    // https://www.survivingwithandroid.com/android-listview-custom-filter/
    private class RestaurantFilter extends Filter {
        // Invoked in a worker thread, tasked to filter results according to the constraint
        // Constraint is the text that is being searched for
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();  // Holds the results of this function
            List<Restaurant> filteredRestaurants = new ArrayList<>();
            SearchFilter searchFilter = SearchFilter.getInstance();  // Used to perform filtering

            // Get the list of all restaurants
            RestaurantManager restaurantManager = RestaurantManager.getInstance();
            List<Restaurant> allRestaurants = new ArrayList<>();
            for (Restaurant restaurant : restaurantManager) {
                allRestaurants.add(restaurant);
            }

            // Set the search term depending on what's in the search box
            if (constraint == null || constraint.length() == 0) {  // If search box is empty...
                // Reset the search filter search term so it does not contain a search term
                searchFilter.resetSearchTerm();
            } else {  // If search box has text...
                // Setup the filter to contain the search term
                searchFilter.setSearchTerm(constraint.toString());
            }

            // Get the filtered tracking numbers
            List<String> filteredTrackingNumbers = searchFilter.getRestaurantTrackingNumbers();

            // Apply the filter
            for (Restaurant restaurant : allRestaurants) {
                // If the current restaurant matches the filter, save it for display later
                String restaurantTrackingNumber = restaurant.getTrackingNumber();
                if (filteredTrackingNumbers.contains(restaurantTrackingNumber)) {
                    filteredRestaurants.add(restaurant);
                }
            }

            // Set the results to contain only the filtered restaurants
            results.values = filteredRestaurants;

            // Return the now-populated search results
            return results;
        }

        // Tasked to show the result set created by the performingFiltering method
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list of filtered objects
            filteredRestaurants.clear();
            filteredRestaurants.addAll((List<Restaurant>) results.values);
            notifyDataSetChanged();
        }
    }
}
