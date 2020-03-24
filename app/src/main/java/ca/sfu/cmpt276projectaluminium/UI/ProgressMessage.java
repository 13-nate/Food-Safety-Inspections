package ca.sfu.cmpt276projectaluminium.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import ca.sfu.cmpt276projectaluminium.R;

public class ProgressMessage extends AppCompatDialogFragment {

    private static final String PROGRESS = "Downloading:";

    private static boolean cancel;

    private ProgressBar progressBar;

    static final String fileRestaurant = "tempRestaurant.csv";
    static final String fileFinalRestaurant = "restaurant.csv";

    static final String fileInspection = "tempInspection.csv";
    static final String fileFinalInspection = "inspection.csv";

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.progressbar, null);

        progressBar = view.findViewById((R.id.progressBar));
        cancel = false;

        new CSVRetriever(getContext()).execute(progressBar);

        //MainActivity.adapter.notifyDataSetChanged();

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancel = true;

            }
        };


        return new AlertDialog.Builder(getActivity())
                .setTitle(PROGRESS)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, listener)
                .create();
    }


    //Sources:
    //https://stackoverflow.com/questions/43328693/java-networkonmainthreadexception-read-csv-file-from-url
    //https://www.youtube.com/watch?v=Vcn4OuV4Ixg
    //https://stackoverflow.com/questions/48381818/this-field-leaks-context-object
    //https://www.youtube.com/watch?v=EcfUkjlL9RI
    //https://www.youtube.com/watch?v=-7CO097C7NM

    /**
     * Gets the csv files for the restaurants and inspections in the background
     * enables the complete button when finished
     */

    public class CSVRetriever extends AsyncTask<ProgressBar, Integer, ProgressBar> {

        private String formatRestaurant;
        private String CSVUrlRestaurant;
        private String lastModifiedRestaurant;


        private String formatInspection;
        private String CSVUrlInspection;
        private String lastModifiedInspection;

        private WeakReference<Context> weakContext;

        CSVRetriever(Context context) {
            weakContext = new WeakReference<>(context);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            progressBar.setProgress(values[0]);
        }

        @Override
        protected ProgressBar doInBackground(ProgressBar... progressBars) {
            try {
                readRestaurant();
                publishProgress(25);
                readInspection();
                publishProgress(50);
                getRestaurantCSV();
                publishProgress(75);
                getInspectionCSV();
                publishProgress(99);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return progressBars[0];
        }

        //Delete Functions Do nothing???
        @Override
        protected void onPostExecute(ProgressBar progressBar) {
            String tempPath = weakContext.get().getFilesDir().getAbsolutePath();
            if (cancel){
                File tempRestaurant = new File (tempPath + "/" + fileRestaurant);
                tempRestaurant.delete();
                File tempInspection = new File (tempPath + "/" + fileInspection);
                tempInspection.delete();
            } else {
                //File.createTempFile();
                weakContext.get().deleteFile(fileFinalRestaurant);
                weakContext.get().deleteFile(fileFinalInspection);

                File tempRestaurant = new File (tempPath + "/" + fileRestaurant);
                tempRestaurant.renameTo(new File (tempPath + "/" + fileFinalRestaurant));

                File tempInspection = new File (tempPath + "/" + fileInspection);
                tempInspection.renameTo(new File (tempPath + "/" + fileFinalInspection));

            }
            dismiss();
            MainActivity.adapter.clear();
        }

        private void readRestaurant() throws IOException, JSONException{
            URL url = new URL("https://data.surrey.ca/api/3/action/package_show?id=restaurants");
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
            URL url = new URL("https://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports");
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
            publishProgress(80);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStreamInspection));
            String totalInput = "";
            String inputLine;

            publishProgress(90);
            while ((inputLine = in.readLine()) != null) {
                totalInput = totalInput + inputLine + "\n";
            }

            in.close();
            publishProgress(95);

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

}