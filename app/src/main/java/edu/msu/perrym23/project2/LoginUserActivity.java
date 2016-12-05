package edu.msu.perrym23.project2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginUserActivity extends AppCompatActivity {

    private AlertDialog loginDlg;
    private AlertDialog createUserDlg;
    public boolean newUserMade = false;
    private boolean isLoggedIn = false;
    private String username;
    private String password;
    GameView.dimension boardSize;

    @BindView(R.id.login)
    Button loginButton;

    @BindView(R.id.new_user)
    Button newUserButton;

    @BindViews({R.id.fivebyfive, R.id.tenbyten, R.id.twentybytwenty})
    List<Button> gameSizeButtons;

    /* Start 5x5 game */
    @OnClick(R.id.fivebyfive)
    public void onFiveClick() {
        boardSize = GameView.dimension.SMALL;
        newGame(this.getCurrentFocus());
    }

    /* Start 10x10 game */
    @OnClick(R.id.tenbyten)
    public void onTenClick() {
        boardSize = GameView.dimension.MEDIUM;
        newGame(this.getCurrentFocus());
    }

    /* Start 20x20 game */
    @OnClick(R.id.twentybytwenty)
    public void onTwentyClick() {
        boardSize = GameView.dimension.LARGE;
        newGame(this.getCurrentFocus());
    }

    /* Open new user dialogue */
    @OnClick(R.id.new_user)
    public void OnNewUserClick() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogue_new_user, null);

        final EditText username = (EditText) dialogView.findViewById(R.id.username);
        final EditText password = (EditText) dialogView.findViewById(R.id.password);
        final EditText conPassword = (EditText) dialogView.findViewById(R.id.confirmPassword);

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
                        } else {
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

    /* Open login dialogue */
    @OnClick(R.id.login)
    public void onLoginClick() {
        if (isLoggedIn) {
            isLoggedIn = false;
            updateUI();
        } else {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialogue_login_user, null);

            final EditText usernameET = (EditText) dialogView.findViewById(R.id.username);
            final EditText passwordET = (EditText) dialogView.findViewById(R.id.password);

            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Login");
            dialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String user = usernameET.getText().toString();
                            String pass = passwordET.getText().toString();
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
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/christmas.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_login_user);
        ButterKnife.bind(this);
    }

    /* Start a new game */
    public void newGame(View view) {
        //Only can start a new game if we are logged in!
        if (isLoggedIn) {
            new AsyncTask<String, Void, Boolean>() {
                private ProgressDialog progressDialog;
                private Server server = new Server();
                Intent intent = new Intent(LoginUserActivity.this, GameActivity.class);

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = ProgressDialog.show(LoginUserActivity.this,
                            getString(R.string.please_wait),
                            getString(R.string.logging_in),
                            true, true, new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    server.cancel();
                                }
                            });
                }

                @Override
                protected Boolean doInBackground(String... params) {
                    boolean success = server.joinGame(params[0]);
                    return success;
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    progressDialog.dismiss();
                    if (success) {
                        // Start the game as player 2
                        intent.putExtra(GameView.BOARD_SIZE, boardSize);
                        intent.putExtra(GameActivity.AM_PLAYER_ONE, false);
                    } else {
                        // Start the game as player 1
                        intent.putExtra(GameActivity.AM_PLAYER_ONE, true);
                        intent.putExtra(GameView.BOARD_SIZE, boardSize);
                    }
                    intent.putExtra(GameActivity.MY_NAME, username);
                    startActivity(intent);
                }
            }.execute(username);
        }
    }

    /* Attempt to login the user */
    private void loginUser(String usr, final String pass) {
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
                boolean success = server.login(params[0], params[1]);
                username = params[0];
                password = params[1];
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
                    isLoggedIn = true;
                    updateUI();
                    loginDlg.dismiss();
                } else {
                    Toast.makeText(loginDlg.getContext(),
                            R.string.login_user_fail,
                            Toast.LENGTH_LONG).show();
                    isLoggedIn = false;
                    loginDlg.dismiss();
                }
            }
        }.execute(usr, pass);
    }

    /* Create the user in the table */
    private void createUser(String usr, final String pass) {
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
                    newUserMade = true;
                    createUserDlg.dismiss();
                } else {
                    Toast.makeText(createUserDlg.getContext(),
                            R.string.create_user_fail,
                            Toast.LENGTH_LONG).show();
                    newUserMade = false;
                }
            }
        }.execute(usr, pass);
    }

    /* Change the layout if logged in / logged out */
    private void updateUI() {
        if (isLoggedIn) {
            loginButton.setText(R.string.Logout);
            newUserButton.setVisibility(View.GONE);
            gameSizeButtons.get(0).setVisibility(View.VISIBLE);
            gameSizeButtons.get(1).setVisibility(View.VISIBLE);
            gameSizeButtons.get(2).setVisibility(View.VISIBLE);

        } else {
            loginButton.setText(R.string.login);
            newUserButton.setVisibility(View.VISIBLE);
            gameSizeButtons.get(0).setVisibility(View.GONE);
            gameSizeButtons.get(1).setVisibility(View.GONE);
            gameSizeButtons.get(2).setVisibility(View.GONE);
        }
    }
}
