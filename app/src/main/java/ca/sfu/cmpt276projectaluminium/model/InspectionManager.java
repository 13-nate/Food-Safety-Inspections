package ca.sfu.cmpt276projectaluminium.model;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

// Sources:
// https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java

/**
 * Manages data about a Restaurant's inspections by storing them all in an easily accessible list
 */
public class InspectionManager {
    private List<Inspection> allInspections;

    private Inspection noInspection = new Inspection("None", 0, "No type",
            0,0, "No rating",
            new ArrayList<Violation>());

    /*
        Singleton Support (As per https://www.youtube.com/watch?v=evkPjPIV6cw - Brain Fraser)
     */
    private static InspectionManager instance;
    // Private to prevent anyone else from instantiating
    private InspectionManager() {
        this.allInspections = new ArrayList<>();
    }
    // This version is called when you need to simply access the currently stored data
    public static InspectionManager getInstance() {
        if (instance == null) {
            instance = new InspectionManager();
        }

        return instance;
    }
    // This version is called when you also need update/initialize csv data
    public static InspectionManager getInstance(InputStream is) {
        if (instance == null) {
            instance = new InspectionManager();
        }

        CSVFileParser fileParser = new CSVFileParser(is);
        instance.allInspections = fileParser.getInspections();

        return instance;
    }

    /**
     * Creates and returns the inspection object that correlates to the passed in parameters
         * This can be used to create a inspection after passing the arguments between activities.
     * @param trackingNumber The trackingNumber of the inspection being searched for.
     * @param inspectionDate the date of the inspection being searched for.
     * @return An inspection that exactly matches both parameters
     */
    public Inspection recreateInspection(String trackingNumber, int inspectionDate, String type) {
        // Find an inspection that matches both the trackingNumber and date, then return it
        for (Inspection inspection : this.allInspections) {
            boolean trackingNumberMatches = inspection.getTrackingNumber().equals(trackingNumber);
            boolean dateMatches = inspection.getInspectionDate() == inspectionDate;
            boolean typeMatches = inspection.getType().equals(type);
            if (trackingNumberMatches && dateMatches && typeMatches) {
                return inspection;
            }
        }

        // This should only be returned if an invalid trackingNumber/date combo was passed in
        return null;
    }

    /**
     * Puts all inspections associated with the specified restaurant in a list and returns that list
     * @param restaurantTrackingNumber The ID of the restaurant that is looking for its inspections
     */
    public ArrayList<Inspection> getInspections(String restaurantTrackingNumber) {
        ArrayList<Inspection> inspections = new ArrayList<>();

        // For every inspection across all restaurants...
        for (Inspection inspection : this.allInspections) {
            // Add the inspection to the list if it is associated with the restaurant's trackingNum
            String inspectionTrackingNumber = inspection.getTrackingNumber();
            if (inspectionTrackingNumber.equals(restaurantTrackingNumber)) {
                inspections.add(inspection);
            }
        }
        Collections.sort(inspections);

        return inspections;
    }

    /**
     * If the inspection list is sorted, the most recent inspection will be the first inspection.
     */
    public Inspection getMostRecentInspection(ArrayList<Inspection> inspections) {
        // Ensure that the list is sorted
        Collections.sort(inspections);

        if (!inspections.isEmpty()) {
            return inspections.get(0);
        } else {
            return noInspection;
        }
    }
    public int getNumCriticalViolationsWithinYear(String restaurantTrackingNumber) {
        int criticalViolationsWithinAYear = 0;
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        ArrayList<Inspection> inspections = getInspections(restaurantTrackingNumber);
        for(Inspection inspection: inspections) {
            try {
                int dateNumber = inspection.getInspectionDate();
                // change inspection from an int to a string then finally to a date data type
                String dateToFormat = String.valueOf(dateNumber);
                Date inspectionDay = formatDate.parse(dateToFormat);

                // Get today's date find the difference between it and the inspection date
                Date today = Calendar.getInstance().getTime();
                long differenceInMilliSec = today.getTime() - inspectionDay.getTime();
                long diffInDays = TimeUnit.MILLISECONDS.toDays(differenceInMilliSec);
                if(diffInDays <= 365) {
                    criticalViolationsWithinAYear += inspection.getNumCriticalViolations();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return  criticalViolationsWithinAYear;
    }
}