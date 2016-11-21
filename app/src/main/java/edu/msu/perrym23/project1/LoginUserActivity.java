package edu.msu.perrym23.project1;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginUserActivity extends AppCompatActivity{

    @BindView(R.id.login)
    Button loginButton;

    @BindView(R.id.new_user)
    Button newUserButton;

    @OnClick(R.id.login)
    public void onLoginClick(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogue_login_user, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Login");
        dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @OnClick(R.id.new_user)
    public void onNewUserClick(){
       CreateUserDialogue createUserDialogue = new CreateUserDialogue();
        createUserDialogue.show(getFragmentManager(), "create");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);
        ButterKnife.bind(this);
    }


}
