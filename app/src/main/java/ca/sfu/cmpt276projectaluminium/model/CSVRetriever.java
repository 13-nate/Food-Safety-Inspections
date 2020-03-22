package ca.sfu.cmpt276projectaluminium.model;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ca.sfu.cmpt276projectaluminium.R;
import ca.sfu.cmpt276projectaluminium.UI.MainActivity;

//Sources:
//https://stackoverflow.com/questions/43328693/java-networkonmainthreadexception-read-csv-file-from-url
//https://www.youtube.com/watch?v=Vcn4OuV4Ixg

public class CSVRetriever extends AsyncTask <Void, Void, Void> {

    private String formatRestaurant;
    private String CSVUrlRestaurant;
    private String lastModifiedRestaurant;
    private InputStream inputStreamRestaurant;

    private String formatInspection;
    private String CSVUrlInspection;
    private String lastModifiedInspection;
    private InputStream inputStreamInspection;



    public CSVRetriever() {
    }

    @Override
    protected Void doInBackground(Void... voids) {
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

        MainActivity.initializeDataClasses(inputStreamRestaurant, inputStreamInspection);
    }

    private void readRestaurant() throws IOException, JSONException{
        URL url = new URL("http://data.surrey.ca/api/3/action/package_show?id=restaurants");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        inputStreamRestaurant = connection.getInputStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStreamRestaurant));

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

    private void readInspection() throws IOException, JSONException{
        URL url = new URL("http://data.surrey.ca/api/3/action/package_show?id=restaurants");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        inputStreamInspection = connection.getInputStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(inputStreamInspection));

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
        inputStreamRestaurant = connection.getInputStream();
        connection.disconnect();
    }

    private void getInspectionCSV() throws IOException {
        URL url = new URL(CSVUrlInspection);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        inputStreamInspection = connection.getInputStream();
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

    public InputStream getInputStreamRestaurant() {
        return inputStreamRestaurant;
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

    public InputStream getInputStreamInspection() {
        return inputStreamInspection;
    }
}
