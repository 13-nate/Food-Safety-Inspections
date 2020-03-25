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
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import ca.sfu.cmpt276projectaluminium.R;

/**
 * The code behind the alert message
 * Runs the download, and contains the necessary components for downloading the csv
 */

public class ProgressMessage extends AppCompatDialogFragment {

    private static final String PROGRESS = "Downloading:";

    private static boolean cancel;

    private ProgressBar progressBar;

    private static final String fileRestaurant = "tempRestaurant.csv";
    static final String fileFinalRestaurant = "restaurant.csv";

    private static final String fileInspection = "tempInspection.csv";
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
                String tempPath = getActivity().getFilesDir().getAbsolutePath();
                File tempRestaurant = new File (tempPath + "/" + fileRestaurant);
                tempRestaurant.delete();
                File tempInspection = new File (tempPath + "/" + fileInspection);
                tempInspection.delete();

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
    //https://stackoverflow.com/questions/15758856/android-how-to-download-file-from-webserver

    /**
     * Gets the csv files for the restaurants and inspections in the background
     */

    public class CSVRetriever extends AsyncTask<ProgressBar, Integer, ProgressBar> {

        private String CSVUrlRestaurant;
        private String CSVUrlInspection;
        boolean exceptionRaised = true;
        private WeakReference<Context> weakContext;
        private static final String MESSAGE_DIALOGUE = "MESSAGE_DIALOGUE";

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
                publishProgress(5);
                readInspection();
                publishProgress(10);
                getRestaurantCSV();
                publishProgress(15);
                getInspectionCSV();
                publishProgress(99);

                exceptionRaised = false;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return progressBars[0];
        }

        //Delete Functions Do nothing???
        @Override
        protected void onPostExecute(ProgressBar progressBar) {
            if (exceptionRaised){
                FragmentManager manager = getActivity().getSupportFragmentManager();
                ErrorMessage dialog = new ErrorMessage();
                dialog.show(manager, MESSAGE_DIALOGUE);
            } else {
                if (!cancel) {
                    String tempPath = weakContext.get().getFilesDir().getAbsolutePath();
                    weakContext.get().deleteFile(fileFinalRestaurant);
                    weakContext.get().deleteFile(fileFinalInspection);

                    File tempRestaurant = new File (tempPath + "/" + fileRestaurant);
                    tempRestaurant.renameTo(new File (tempPath + "/" + fileFinalRestaurant));

                    File tempInspection = new File (tempPath + "/" + fileInspection);
                    tempInspection.renameTo(new File (tempPath + "/" + fileFinalInspection));

                }
                dismiss();
                getActivity().recreate();
            }
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
            CSVUrlRestaurant = jsonObject.getString("url");

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
            CSVUrlInspection = jsonObject.getString("url");

            connection.disconnect();
        }

        private void getRestaurantCSV() throws IOException {
            URL url = new URL(CSVUrlRestaurant);
            int count;

            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            OutputStream output = weakContext.get()
                    .openFileOutput(fileRestaurant, Context.MODE_PRIVATE);

            byte[] data = new byte[1024];

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        }

        private void getInspectionCSV() throws IOException {
            URL url = new URL(CSVUrlInspection);

            int count;
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = weakContext.get()
                    .openFileOutput(fileInspection, Context.MODE_PRIVATE);
            byte[] data = new byte[1024];

            while ((count = input.read(data)) != -1) {

                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

        }


    }

}