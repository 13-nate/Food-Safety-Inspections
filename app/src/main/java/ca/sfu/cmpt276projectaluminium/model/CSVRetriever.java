package ca.sfu.cmpt276projectaluminium.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class CSVRetriever {

    private String format;
    private String CSVUrl;
    private String lastModified;


    public void getCSV()
    {

    }

    //http://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html
    private void readJson()
    {
        try {
            URL url = new URL("http://data.surrey.ca/api/3/action/package_show?id=restaurants");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String totalInput = "";
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                totalInput += inputLine;
            }
            in.close();
            JSONObject json = new JSONObject(totalInput);
            json = json.getJSONObject("result");
            JSONArray jsonArray = json.getJSONArray("result");
            json = jsonArray.getJSONObject(0);

            format = json.getString("format");
            CSVUrl = json.getString("url");
            lastModified = json.getString("last_modified");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
