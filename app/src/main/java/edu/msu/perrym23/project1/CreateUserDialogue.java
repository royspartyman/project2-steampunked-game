package edu.msu.perrym23.project1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;


public class CreateUserDialogue extends DialogFragment {

    /**
     * The alert dialog box
     */
    private AlertDialog dlg;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialogue_new_user, null))
                // Add action buttons
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        TextView u = (TextView) dlg.findViewById(R.id.username);
                        TextView p = (TextView) dlg.findViewById(R.id.password);
                        TextView v = (TextView) dlg.findViewById(R.id.confirmPassword);

                        if (!p.getText().toString().equals(v.getText().toString())) {
                            Toast.makeText(dlg.getContext(),
                                    R.string.verify_error,
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            Log.i("create", "user");
                            createUser(u.getText().toString(), p.getText().toString());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel just closes the dialog
                    }
                });
        dlg = builder.create();
        return dlg;
    }

    private void createUser(String usr, String password) {

        new AsyncTask<String, Void, Boolean>() {

            private ProgressDialog progressDialog;
            private Server server = new Server();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(CreateUserDialogue.this.getActivity(),
                        getString(R.string.please_wait),
                        getString(R.string.creating_user),
                        true, true, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                server.cancel();
                            }
                        });
            }

            @Override
            protected Boolean doInBackground(String... params) {
                Server server = new Server();
                Log.i("username: ", params[0]);
                Log.i("password: ", params[1]);
                boolean success = server.createNewUser(params[0], params[1]);
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (success) {
                    Toast.makeText(dlg.getContext(),
                            R.string.create_user_success,
                            Toast.LENGTH_SHORT).show();
                    dlg.dismiss();
                } else {
                    Toast.makeText(dlg.getContext(),
                            R.string.create_user_fail,
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute(usr, password);
    }
}