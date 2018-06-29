package augusto.aulas;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

public class AlunoDialog extends DialogFragment {
    // Use this instance of the interface to deliver action events
    DialogListener mListener;
    EditText name;

    // Override the Fragment.onAttach() method to instantiate the DialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Instantiate the DialogListener so we can send events to the host
        mListener = (DialogListener) activity;
        name = activity.findViewById(R.id.student_name);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_student_new)
                .setTitle(R.string.student_title)
                .setMessage(R.string.student_name_add)
                .setIcon(R.drawable.ic_person_add_black_24dp)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(AlunoDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DialogListener {
        void onDialogPositiveClick(AlunoDialog dialog);
    }

}
