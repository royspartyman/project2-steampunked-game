package edu.msu.perrym23.project1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class GameSelectionActivity extends AppCompatActivity {

    private static final String PLAYERONENAME = "PlayerOneName";
    private static final String PLAYERTWONAME = "PlayerTwoName";
    private static final String PLAYERONECOLOR = "PlayerOneColor";
    private static final String PLAYERTWOCOLOR = "PlayerTwoColor";
    public String playerOne = "Player 1";
    public String playerTwo = "Player 2";

    @BindView(R.id.userOne)
    ImageView userOneIV;

    @BindView(R.id.userTwo)
    ImageView userTwoIV;

    @BindView(R.id.playerOne)
    TextView playerOneTV;

    @BindView(R.id.playerTwo)
    TextView playerTwoTV;

    @BindView(R.id.fivebyfive)
    Button fivebyfive;

    @BindView(R.id.tenbyten)
    Button tenbyten;

    @BindView(R.id.twentybytwenty)
    Button twentybytwenty;

    @OnClick(R.id.fivebyfive)
    public void fiveByFiveClicked() {
        newGame(GameView.dimension.SMALL);
    }

    @OnClick(R.id.tenbyten)
    public void tenBytenClicked() {
        newGame(GameView.dimension.MEDIUM);
    }

    @OnClick(R.id.twentybytwenty)
    public void twentyByTwentClicked() {
        newGame(GameView.dimension.LARGE);
    }

    private int colorOne = 0;
    private int colorTwo = 0;

    @OnClick(R.id.userOne)
    public void userOneIBClicked() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogue_user_name, null);
        final ImageView batPurple = (ImageView) dialogView.findViewById(R.id.bat_purple);
        final ImageView batRed = (ImageView) dialogView.findViewById(R.id.bat_red);
        final ImageView batGreen = (ImageView) dialogView.findViewById(R.id.bat_green);
        final ImageView batYellow = (ImageView) dialogView.findViewById(R.id.bat_yellow);
        final EditText nameEdit = (EditText) dialogView.findViewById(R.id.name);
        nameEdit.setText("Player 1");

        nameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEdit.setCursorVisible(true);
            }
        });

        batPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    changeChecked("batPurple", dialogView);
                    colorOne = ContextCompat.getColor(getApplicationContext(), R.color.purple
                    );
                }
            }
        });

        batGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChecked("batGreen", dialogView);
                colorOne = ContextCompat.getColor(getApplicationContext(), R.color.green);
            }
        });

        batRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChecked("batRed", dialogView);
                colorOne = ContextCompat.getColor(getApplicationContext(), R.color.red);
            }
        });

        batYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChecked("batYellow", dialogView);
                colorOne = ContextCompat.getColor(getApplicationContext(), R.color.yellow);
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Customize your name:");
        dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = nameEdit.getText().toString();
                        playerOneTV.setText(name);
                        if(name != null){
                            playerOne = name;
                        }
                        userOneIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_flying_bat));
                        userOneIV.setColorFilter(colorOne);
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


    @OnClick(R.id.userTwo)
    public void userTwoIBClicked() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogue_user_name, null);

        final ImageView batPurple = (ImageView) dialogView.findViewById(R.id.bat_purple);
        final ImageView batRed = (ImageView) dialogView.findViewById(R.id.bat_red);
        final ImageView batGreen = (ImageView) dialogView.findViewById(R.id.bat_green);
        final ImageView batYellow = (ImageView) dialogView.findViewById(R.id.bat_yellow);
        final EditText nameEdit = (EditText) dialogView.findViewById(R.id.name);
        nameEdit.setText("Player 2");


        nameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEdit.setCursorVisible(true);
            }
        });

        batPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    changeChecked("batPurple", dialogView);
                    colorTwo = ContextCompat.getColor(getApplicationContext(), R.color.purple);
                }
            }
        });

        batGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChecked("batGreen", dialogView);
                colorTwo = ContextCompat.getColor(getApplicationContext(), R.color.green);
            }
        });

        batRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChecked("batRed", dialogView);
                colorTwo = ContextCompat.getColor(getApplicationContext(), R.color.red);
            }
        });

        batYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeChecked("batYellow", dialogView);
                colorTwo = ContextCompat.getColor(getApplicationContext(), R.color.yellow);
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Enter your name:");
        dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String name = nameEdit.getText().toString();
                        playerTwoTV.setText(name);
                        if(name != ""){
                            playerTwo = name;
                        }
                        userTwoIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_flying_bat_two));
                        userTwoIV.setColorFilter(colorTwo);

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


    public void changeChecked(String checkColor, View dialogueView) {

        ImageView batPurple = (ImageView) dialogueView.findViewById(R.id.bat_purple);
        ImageView batRed = (ImageView) dialogueView.findViewById(R.id.bat_red);
        ImageView batGreen = (ImageView) dialogueView.findViewById(R.id.bat_green);
        ImageView batYellow = (ImageView) dialogueView.findViewById(R.id.bat_yellow);

        switch (checkColor) {
            case "batPurple":
                batPurple.setBackgroundColor(getResources().getColor(R.color.deep_purple));
                batGreen.setBackgroundColor(getResources().getColor(R.color.white));
                batRed.setBackgroundColor(getResources().getColor(R.color.white));
                batYellow.setBackgroundColor(getResources().getColor(R.color.white));
                break;

            case "batGreen":
                batPurple.setBackgroundColor(getResources().getColor(R.color.white));
                batGreen.setBackgroundColor(getResources().getColor(R.color.green));
                batRed.setBackgroundColor(getResources().getColor(R.color.white));
                batYellow.setBackgroundColor(getResources().getColor(R.color.white));
                break;

            case "batRed":
                batPurple.setBackgroundColor(getResources().getColor(R.color.white));
                batGreen.setBackgroundColor(getResources().getColor(R.color.white));
                batRed.setBackgroundColor(getResources().getColor(R.color.red));
                batYellow.setBackgroundColor(getResources().getColor(R.color.white));
                break;

            case "batYellow":
                batPurple.setBackgroundColor(getResources().getColor(R.color.white));
                batGreen.setBackgroundColor(getResources().getColor(R.color.white));
                batRed.setBackgroundColor(getResources().getColor(R.color.white));
                batYellow.setBackgroundColor(getResources().getColor(R.color.yellow));
                break;
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Read values from the "savedInstanceState"-object and put them in your textview
        colorOne = savedInstanceState.getInt(PLAYERONECOLOR);
        colorTwo = savedInstanceState.getInt(PLAYERTWOCOLOR);

        if (colorOne != 0) {
            userOneIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_flying_bat));
            userOneIV.setColorFilter(colorOne);
        }

        if (colorTwo != 0) {
            userTwoIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_flying_bat_two));
            userTwoIV.setColorFilter(colorTwo);
        }

        playerOneTV.setText(savedInstanceState.getString(PLAYERONENAME));
        playerTwoTV.setText(savedInstanceState.getString(PLAYERTWONAME));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the values you need from your textview into "outState"-object
        super.onSaveInstanceState(outState);
        outState.putInt(PLAYERONECOLOR, colorOne);
        outState.putInt(PLAYERTWOCOLOR, colorTwo);
        outState.putString(PLAYERONENAME, playerOneTV.getText().toString());
        outState.putString(PLAYERTWONAME, playerTwoTV.getText().toString());
    }

    /**
     * Create a new game
     */
    public void newGame(GameView.dimension dimension ) {
        Intent intent = new Intent(GameSelectionActivity.this, GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(PLAYERONENAME, playerOne);
        bundle.putString(PLAYERTWONAME, playerTwo);
        intent.putExtra(GameView.BOARD_SIZE, dimension);
        bundle.putInt(PLAYERONECOLOR, colorOne);
        bundle.putInt(PLAYERTWOCOLOR, colorTwo);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/freakshow.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_game_selection);
        ButterKnife.bind(this);
        checkForUpdates();
    }

    /*--------------HockeyApp--------------------------------------------*/
    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }
    /*--------------------------------------------------------------------*/
}
