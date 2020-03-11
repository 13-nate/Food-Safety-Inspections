package ca.sfu.cmpt276projectaluminium.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Manages data about inspection reports by storing it and providing getters in an organized manner
 */
public class Inspection implements Comparable<Inspection> {
    public static final int DAYS30 = 30;
    public static final int DAYS365 = 365;
    private String trackingNumber;
    private int inspectionDate;
    private String type;
    private int numCriticalViolations;
    private int numNonCriticalViolations;
    private String hazardRating;

    /**
     * Private constructor so that inspections are only instantiated in ways that are allowed
     */
    private Inspection() {
    }

    /**
     * Constructor is package private as it should only be called by InspectionManager
     */
    Inspection(String trackingNumber, int inspectionDate, String type, int numCriticalViolations,
               int numNonCriticalViolations, String hazardRating) {
        this.trackingNumber = trackingNumber;
        this.inspectionDate = inspectionDate;
        this.type = type;
        this.numCriticalViolations = numCriticalViolations;
        this.numNonCriticalViolations = numNonCriticalViolations;
        this.hazardRating = hazardRating;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public int getInspectionDate() {
        return inspectionDate;
    }

    public String getType() {
        return type;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public int getNumCriticalViolations() {
        return numCriticalViolations;
    }

    public int getNumNonCriticalViolations() {
        return numNonCriticalViolations;
    }

    public int getNumTotalViolatinos() {
        return numCriticalViolations + numNonCriticalViolations;
    }

    /**
     * Currently returns a hard-coded example set of data as it's unimplemented.
     */
    public ArrayList<Violation> getViolations() {
        ArrayList<Violation> violations = new ArrayList<>();
        violations.add(new Violation(101, "Not Critical", "Plans/construction/alterations not in accordance with the Regulation [s. 3; s. 4]"));
        violations.add(new Violation(404, "Not Critical", "Employee smoking in food preparation/processing/storage areas [s. 21(2)]"));
        violations.add(new Violation(203, "Critical", "Food not cooled in an acceptable manner [s. 12(a)]"));

        return violations;
    }


    /**
     * Returns information about the inspection date based of today's date
     * if the inspection was less than 30 days ago return how many days ago it was
     * if the inspection was less than a 365 days ago return the month and day
     * if the inspection was more than 365 days return the month and year
     */
    /*Sources
    https://stackoverflow.com/questions/13624442/getting-last-day-of-the-month-in-a-given-string-date
    https://stackoverflow.com/questions/7829571/milliseconds-to-days
     */
    public String intelligentDate() {
        // Used to format the inspection day String into a date
        SimpleDateFormat formatDate =new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

        // Used to format the inspection date to get the year, month, or day respectively
        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        SimpleDateFormat formatMonth = new SimpleDateFormat("MMM", Locale.ENGLISH);
        SimpleDateFormat formatDay = new SimpleDateFormat("dd", Locale.ENGLISH);

        // Set to N/A so that when a restaurant has no inspections displays N/A, otherwise set the
        // date base of if else statements
        String smartDate = "N/A";
        try {
            // change inspection from an int to a string then finally to a date data type
            String dateToFormat = String.valueOf(inspectionDate);
            Date inspectionDay = formatDate.parse(dateToFormat);

            // Get today's date find the difference between it and the inspection date
            Date today = Calendar.getInstance().getTime();
            long differenceInMilliSec = today.getTime() - inspectionDay.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(differenceInMilliSec);

            if(diffInDays < DAYS30) {
                smartDate = diffInDays + " days";

            } else if (diffInDays < DAYS365) {

                smartDate = formatMonth.format(inspectionDay) + " " + formatDay.format(inspectionDay);

            } else {
                smartDate = formatMonth.format(inspectionDay) + " " + formatYear.format(inspectionDay);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return smartDate;
    }


    /* Sources:
    https://dzone.com/articles/java-comparable-interface-in-five-minutes
    https://stackoverflow.com/questions/5153496/how-can-i-compare-two-strings-in-java-and-define-which-of-them-is-smaller-than-t
     */
    /**
     * Allows the RestaurantManager to be sorted by its Name
     */
    @Override
    public int compareTo(Inspection other) {
        if (this.inspectionDate < other.inspectionDate) {
            return 1;
        }
        if (this.inspectionDate == other.inspectionDate) {
            return 0;
        }
        return -1;    }
}
