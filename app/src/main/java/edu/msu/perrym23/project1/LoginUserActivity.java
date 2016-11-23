package edu.msu.perrym23.project1;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginUserActivity extends AppCompatActivity{

    private AlertDialog loginDlg;
    private AlertDialog createUserDlg;

    public static boolean loggedIn = false;
    public static boolean newUserMade = false;

    @BindView(R.id.login)
    Button loginButton;

    @BindView(R.id.new_user)
    Button newUserButton;

    @OnClick(R.id.new_user)
    public void onLoginClick(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogue_new_user, null);

        final EditText username = (EditText) dialogView.findViewById(R.id.username);
        final EditText password = (EditText) dialogView.findViewById(R.id.password);
        final EditText conPassword = (EditText) dialogView.findViewById(R.id.password);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Login");
        dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String user = username.getText().toString();
                        String pass = password.getText().toString();
                        String conPass = conPassword.getText().toString();
                        if (!pass.equals(conPass)) {
                            Toast.makeText(dialogView.getContext(),
                                    R.string.verify_error,
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            createUser(user, pass);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog createUserAlertDialog = dialogBuilder.create();
        createUserDlg = createUserAlertDialog;
        createUserAlertDialog.show();
    }

    @OnClick(R.id.login)
    public void onNewUserClick(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogue_login_user, null);

        final EditText username = (EditText) dialogView.findViewById(R.id.username);
        final EditText password = (EditText) dialogView.findViewById(R.id.password);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Login");
        dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String user = username.getText().toString();
                        String pass = password.getText().toString();
                        loginUser(user, pass);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog loginAlertDialog = dialogBuilder.create();
        loginDlg = loginAlertDialog;
        loginAlertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        ButterKnife.bind(this);
    }

    private void loginUser(String usr, String password) {

        new AsyncTask<String, Void, Boolean>() {

            private ProgressDialog progressDialog;
            private Server server = new Server();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(LoginUserActivity.this,
                        getString(R.string.please_wait),
                        getString(R.string.logging_in_user),
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
                boolean success = server.login(params[0], params[1]);
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (success) {
                    Toast.makeText(loginDlg.getContext(),
                            R.string.logged_in_user_success,
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), GameSelectionActivity.class);
                    startActivity(intent);
                    loginDlg.dismiss();
                } else {
                    Toast.makeText(loginDlg.getContext(),
                            R.string.login_user_fail,
                            Toast.LENGTH_LONG).show();
                    LoginUserActivity.loggedIn = false;
                }
            }
        }.execute(usr, password);
    }

    private void createUser(String usr, final String password) {

        new AsyncTask<String, Void, Boolean>() {

            private ProgressDialog progressDialog;
            private Server server = new Server();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(LoginUserActivity.this,
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
                    Toast.makeText(createUserDlg.getContext(),
                            R.string.create_user_success,
                            Toast.LENGTH_SHORT).show();
                    LoginUserActivity.newUserMade = true;
                    createUserDlg.dismiss();
                } else {
                    Toast.makeText(createUserDlg.getContext(),
                            R.string.create_user_fail,
                            Toast.LENGTH_LONG).show();
                    LoginUserActivity.newUserMade = false;
                }
            }
        }.execute(usr, password);
    }
}
