package ca.sfu.cmpt276projectaluminium.UI;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.UI.MainActivity;

//Sources:
//https://stackoverflow.com/questions/43328693/java-networkonmainthreadexception-read-csv-file-from-url
//https://www.youtube.com/watch?v=Vcn4OuV4Ixg
//https://stackoverflow.com/questions/48381818/this-field-leaks-context-object
//https://www.youtube.com/watch?v=EcfUkjlL9RI

public class CSVRetriever extends AsyncTask <Context, Void, Void> {

    private String formatRestaurant;
    private String CSVUrlRestaurant;
    private String lastModifiedRestaurant;
    static final String fileRestaurant = "restaurant.csv";

    private String formatInspection;
    private String CSVUrlInspection;
    private String lastModifiedInspection;
    static final String fileInspection = "inspection.csv";

    private WeakReference<Context> weakContext;


    CSVRetriever(Context context) {
        weakContext = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        try {
            readRestaurant();
            readInspection();
            getRestaurantCSV();
            getInspectionCSV();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    private void readRestaurant() throws IOException, JSONException{
        URL url = new URL("http://data.surrey.ca/api/3/action/package_show?id=restaurants");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = connection.getInputStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String totalInput = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            totalInput += inputLine;
        }
        in.close();
        JSONObject json = new JSONObject(totalInput);

        JSONObject jsonObject = json.getJSONObject("result");
        JSONArray jsonArray = jsonObject.getJSONArray("resources");
        jsonObject = jsonArray.getJSONObject(0);

        formatRestaurant = jsonObject.getString("format");
        CSVUrlRestaurant = jsonObject.getString("url");
        lastModifiedRestaurant = jsonObject.getString("last_modified");

        connection.disconnect();
    }

    //takes in the json that gives you the choice between varieties of files
    //gets the csv containing useful data from it
    private void readInspection() throws IOException, JSONException{
        URL url = new URL("http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = connection.getInputStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        String totalInput = "";
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            totalInput += inputLine;
        }
        in.close();
        JSONObject json = new JSONObject(totalInput);

        JSONObject jsonObject = json.getJSONObject("result");
        JSONArray jsonArray = jsonObject.getJSONArray("resources");
        jsonObject = jsonArray.getJSONObject(0);

        formatInspection = jsonObject.getString("format");
        CSVUrlInspection = jsonObject.getString("url");
        lastModifiedInspection = jsonObject.getString("last_modified");

        connection.disconnect();
    }

    private void getRestaurantCSV() throws IOException {
        URL url = new URL(CSVUrlRestaurant);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream inputStreamRestaurant = connection.getInputStream();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(inputStreamRestaurant));
        String totalInput = "";
        String inputLine;
        //first line is a header for the format, so is discarded
        //in.readLine();
        while ((inputLine = in.readLine()) != null) {
            totalInput = totalInput + inputLine + "\n";

        }
        in.close();

        FileOutputStream fos = weakContext.get().openFileOutput(fileRestaurant, Context.MODE_PRIVATE);
        fos.write(totalInput.getBytes());
        fos.close();

        connection.disconnect();
    }

    private void getInspectionCSV() throws IOException {
        URL url = new URL(CSVUrlInspection);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream inputStreamInspection = connection.getInputStream();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(inputStreamInspection));
        String totalInput = "";
        String inputLine;
        //first line is a header for the format, so is discarded
        //in.readLine();
        while ((inputLine = in.readLine()) != null) {
            totalInput = totalInput + inputLine + "\n";
        }
        in.close();

        FileOutputStream fos = weakContext.get().openFileOutput(fileInspection, Context.MODE_PRIVATE);
        fos.write(totalInput.getBytes());
        fos.close();

        connection.disconnect();
    }

    public String getFormatRestaurant() {
        return formatRestaurant;
    }

    public String getCSVUrlRestaurant() {
        return CSVUrlRestaurant;
    }

    public String getLastModifiedRestaurant() {
        return lastModifiedRestaurant;
    }

    public String getFormatInspection() {
        return formatInspection;
    }

    public String getCSVUrlInspection() {
        return CSVUrlInspection;
    }

    public String getLastModifiedInspection() {
        return lastModifiedInspection;
    }

}
