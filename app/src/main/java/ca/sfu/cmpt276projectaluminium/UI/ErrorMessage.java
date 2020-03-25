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
 * The code behind the alert message
 * Opens the progress message on ok
 */

public class ErrorMessage extends AppCompatDialogFragment {

    private static final String ERROR = "An Error has Occurred:";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.errormessage, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle(ERROR)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

}