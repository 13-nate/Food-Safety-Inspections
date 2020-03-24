package ca.sfu.cmpt276projectaluminium.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import ca.sfu.cmpt276projectaluminium.R;

/**
 * The code behind the alert message
 * Handles what the alert message is and how it behaves
 */

public class DownloadMessage extends AppCompatDialogFragment{

    private static final String MESSAGE_DIALOGUE = "MESSAGE_DIALOGUE";
    private static final String DOWNLOAD = "Download Prompt";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.downloadprompt, null);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                ProgressMessage secondDialogue = new ProgressMessage();
                secondDialogue.show(manager, MESSAGE_DIALOGUE);
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle(DOWNLOAD)
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

}
