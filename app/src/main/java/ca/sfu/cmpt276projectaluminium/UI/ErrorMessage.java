package ca.sfu.cmpt276projectaluminium.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import ca.sfu.cmpt276projectaluminium.R;

/**
 * The code behind the error message
 * Only runs if the checking for updates or downloading updates encountered an error
 */

public class ErrorMessage extends AppCompatDialogFragment {

    private static String ERROR = "An Error has Occurred:";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ERROR = getActivity().getString(R.string.ERROR);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.errormessage, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle(ERROR)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

}