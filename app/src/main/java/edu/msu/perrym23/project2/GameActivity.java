package edu.msu.perrym23.project2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    public final static String MY_NAME = "edu.msu.rudnickw.steampunked.MY_NAME";
    public final static String AM_PLAYER_ONE = "edu.msu.rudnickw.steampunked.AM_PLAYER_ONE";

    private final static String PLAYER_NAME = "my_name";
    private final static String TOKEN = "token";
    private final static String OPPONENT_NAME = "opponent_name";
    private final static String IS_PLAYER_ONE = "is_player_one";

    private String myName = "";
    private String opponentName = "";
    private boolean isPlayerOne;

    private ProgressDialog progressDialog;
    private boolean startGame = false;
    Server server = new Server();

    TimerTask waitForP2Timer;
    TimerTask waitForMyTurn;

    private GameView.dimension gameSize = null;

    private boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (savedInstanceState != null) {
            myName = savedInstanceState.getString(PLAYER_NAME);
            opponentName = savedInstanceState.getString(OPPONENT_NAME);
            isPlayerOne = savedInstanceState.getBoolean(IS_PLAYER_ONE);

            getGameView().loadState(savedInstanceState);
        } else { // There is no saved state, use the intent for initialization
            Intent intent = getIntent();
            myName = intent.getStringExtra(MY_NAME);
            isPlayerOne = intent.getBooleanExtra(AM_PLAYER_ONE, false);

            if (isPlayerOne) {
                getGameView().initialize(intent);
                uploadGameState(Server.GamePostMode.CREATE);
                setWaitForPlayerTwo();
            } else {
                getInitialGame();
                isFirstTime = false;
            }
        }
        updateUI();
    }

    public void loadFromXML(XmlPullParser xml, String obj) throws IOException, XmlPullParserException {
        switch (obj) {
            case "field":
                getGameView().loadFieldFromXml(xml);
                break;
        }
    }

    public void saveToXML(XmlSerializer xml) throws IOException {
        xml.startTag(null, "game");

        String p1 = isPlayerOne ? myName : opponentName;
        String p2 = isPlayerOne ? opponentName : myName;
        xml.attribute(null, "player1", p1);
        xml.attribute(null, "player2", p2);

        String size = "small";
        switch (getGameView().getBoardSize()) {
            case SMALL:
                break;
            case MEDIUM:
                size = "medium";
                break;
            case LARGE:
                size = "large";
                break;
        }
        xml.attribute(null, "size", size);

        String over = getGameView().gameOver() ? "true" : "false";
        xml.attribute(null, "gameover", over);
        if (getGameView().gameOver()) {
            xml.attribute(null, "winner", getGameView().getWinner());
        }

        getGameView().saveToXML(xml);

        xml.endTag(null, "game");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setWaitForPlayerTwo() {
        if (!startGame) {
            progressDialog = ProgressDialog.show(GameActivity.this,
                    getString(R.string.please_wait),
                    getString(R.string.waiting_for_p2), true, false);
            final Handler handler = new Handler();
            Timer timer = new Timer();
            waitForP2Timer = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                checkGameReady(myName);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                            }
                        }
                    });
                }
            };
            timer.schedule(waitForP2Timer, 0, 5000); //execute in every 50000 ms
        }
    }

    public void switchTurns(){
        changeTurn(opponentName);
    }

    public void setWaitForMyTurn() {
        progressDialog = ProgressDialog.show(GameActivity.this,
                getString(R.string.please_wait),
                getString(R.string.waiting_for_other_player), true, false);
        final Handler handler = new Handler();
        Timer timer = new Timer();
        waitForMyTurn = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            checkTurn(myName);
                            Log.i("check turn", "checking");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(waitForMyTurn, 0, 3000); //execute in every 50000 ms
    }

    private void changeTurn(final String usr) {

        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                boolean success = server.changeTurn(params[0]);
                Log.i("Turn change", "changing the turn");
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                setWaitForMyTurn();
            }
        }.execute(usr);
    }

    private void checkTurn(final String usr) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String player = server.getCurrentPlayer(params[0]);
                Log.i("player turn check", "checking");
                return player;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected void onPostExecute(String player) {
                if(Objects.equals(player, myName)){
                    if(!getGameView().isInitialized() && gameSize != null) {
                        getGameView().initialize(gameSize);
                    }

                    loadGameState();
                    startGame();
                    waitForMyTurn.cancel();
                }
            }
        }.execute(usr);
    }

    private void checkGameReady(final String usr) {

        new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                boolean success = server.gameReady(params[0]);
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Log.i("success", "joined");
                    getPlayerTwo(myName);
                    waitForP2Timer.cancel();
                }
            }
        }.execute(usr);
    }

    private void getPlayerTwo(final String usr) {
        new AsyncTask<String, String, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                String name = server.getPlayerTwo(params[0]);
                Log.i("player two", name);
                return name;
            }

            @Override
            protected void onPostExecute(String name) {
                opponentName = name;
                getGameView().setPlayerNames(myName, opponentName, Pipe.PipeGroup.PLAYER_ONE);
                startGame();
            }
        }.execute(usr);
    }

    private void startGame() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        startGame = true;
        startTurn();
    }

    private void startTurn() {
        getGameView().startTurn();
        getGameView().invalidate();
        updateUI();
    }

    private void getInitialGame() {
        new AsyncTask<String, Void, Boolean>() {

            private ProgressDialog progressDialog;
            private Server server = new Server();
            private volatile boolean cancel = false;
            private volatile String p1;
            private volatile String p2;
            private volatile String dim;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(GameActivity.this,
                        getString(R.string.initializing),
                        null, true, true, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                cancel = true;
                            }
                        });
            }

            @Override
            protected Boolean doInBackground(String... params) {
                InputStream stream = server.getGameState(params[0]);
                boolean success = stream != null;
                if (success) {
                    try {
                        if (cancel) {
                            return false;
                        }

                        XmlPullParser xml = Xml.newPullParser();
                        xml.setInput(stream, "UTF-8");

                        xml.nextTag();      // Advance to first tag
                        xml.require(XmlPullParser.START_TAG, null, "game");
                        p1 = xml.getAttributeValue(null, "player1");
                        p2 = xml.getAttributeValue(null, "player2");
                        dim = xml.getAttributeValue(null, "size");

                    } catch (IOException ex) {
                        success = false;
                    } catch (XmlPullParserException ex) {
                        success = false;
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException ex) {
                        }
                    }
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                progressDialog.dismiss();
                if (success) {
                    GameView.dimension size = GameView.dimension.SMALL;
                    switch (dim) {
                        case "small":
                            break;
                        case "medium":
                            size = GameView.dimension.MEDIUM;
                            break;
                        case "large":
                            size = GameView.dimension.LARGE;
                            break;
                        default:
                            size = null;
                    }

                    gameSize = size;

                    if (size != null && p1 != null && p2 != null) {
                        initializeGame(p1, p2, size);
                    } else {
                        Toast.makeText(GameActivity.this,
                                R.string.init_fail,
                                Toast.LENGTH_LONG).show();
                        quitGame();
                    }

                    setWaitForMyTurn();
                }
            }
        }.execute(myName);
    }

    private void initializeGame(String p1, String p2, GameView.dimension size) {
        if (!getGameView().isInitialized()) {
            getGameView().initialize(size);
        }
        if (!isPlayerOne) {
            opponentName = p1;
            getGameView().setPlayerNames(myName, opponentName, Pipe.PipeGroup.PLAYER_TWO);
        } else {
            opponentName = p2;
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(PLAYER_NAME, myName);
        bundle.putString(OPPONENT_NAME, opponentName);
        bundle.putSerializable(IS_PLAYER_ONE, isPlayerOne);
        getGameView().saveState(bundle);
    }

    private void loadGameState() {
        LoadTask load = new LoadTask();
        load.setGame(this);
        load.execute(myName);
    }

    private void uploadGameState(Server.GamePostMode mode) {
        UploadTask update = new UploadTask();
        update.setGame(this);
        update.setUploadMode(mode);
        switch (mode) {
            case UPDATE:
                update.execute(myName);
                break;
            case CREATE:
                update.execute(myName);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        /*
         * Ask if the user is sure they want to quit the current game.
         *
         * If they do then call super.onBackPressed().
         * If they don't then do nothing.
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(getGameView().getContext());
        builder.setTitle(R.string.quit_game);
        builder.setMessage(R.string.quit_game_confirmation);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                quitGame();
            }
        });
        builder.setNegativeButton(R.string.no, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void quitGame() {

        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                Server server = new Server();
                server.quitGame(params[0]);
                return null;
            }
        }.execute(myName);

        Intent intent = new Intent(this, LoginUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onSurrender(View view) {
        onGameOver(opponentName);
    }

    public void onInstall(View view) {
        getGameView().installPipe();
        updateUI();

        if (!getGameView().isTurn()) {
            uploadGameState(Server.GamePostMode.UPDATE);
        }
    }

    public void onDiscard(View view) {
        getGameView().discard();
        updateUI();

        if (!getGameView().isTurn()) {
            uploadGameState(Server.GamePostMode.UPDATE);
        }
    }

    public void onOpenValve(View view) {
        if (getGameView().tryOpenValve()) {
            onGameOver(myName);
        } else {
            onGameOver(opponentName);
        }
    }

    /**
     * Once someone wins or there is a forfeit
     */
    public void onGameOver(String winner) {
        getGameView().isGameOver(winner);
        uploadGameState(Server.GamePostMode.UPDATE);
        Intent intent = new Intent(this, EndGameActivity.class);
        intent.putExtra(EndGameActivity.WINNER, winner);
        startActivity(intent);
    }

    GameView getGameView() {
        return (GameView) findViewById(R.id.gameView);
    }

    //set the current active player
    private void updateUI() {
        TextView currentPlayer = (TextView) findViewById(R.id.currentPlayer);
        String yourTurn = getString(R.string.your_turn) + '\n' + myName;
        String waitingFor = getString(R.string.waiting_for) + '\n' + opponentName;
        Button discard = (Button) findViewById(R.id.discardButton);
        Button install = (Button) findViewById(R.id.installButton);
        Button surrender = (Button) findViewById(R.id.surrender);
        Button openValve = (Button) findViewById(R.id.openValveButton);

        if (getGameView().isTurn()) {
            currentPlayer.setText(yourTurn);
            discard.setEnabled(true);
            install.setEnabled(true);
            surrender.setEnabled(true);
            openValve.setEnabled(true);
        } else {
            currentPlayer.setText(waitingFor);
            discard.setEnabled(false);
            install.setEnabled(false);
            surrender.setEnabled(false);
            openValve.setEnabled(false);
        }
    }


    private class UploadTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog progressDialog;
        private Server server = new Server();
        private GameActivity game;
        private Server.GamePostMode uploadMode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(GameActivity.this,
                    getString(R.string.uploading),
                    null, true, true, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            server.cancel();
                        }
                    });
        }

        public void setGame(GameActivity act) {
            this.game = act;
        }

        public void setUploadMode(Server.GamePostMode mode) {
            uploadMode = mode;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean success = server.sendGameState(params[0], game, uploadMode);
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressDialog.dismiss();
            if (!success) {
                Toast.makeText(GameActivity.this,
                        R.string.upload_fail,
                        Toast.LENGTH_LONG).show();
                uploadGameState(uploadMode);
            } else {
                if(isFirstTime){
                    Log.i("UPLOAD GAME", "COMPLETE FIRST TIME");
                    isFirstTime = false;
                }else{
                    Log.i("UPLOAD GAME", "COMPLETE: SWITCHING TURNS");
                    switchTurns();
                }
            }
        }
    }

    private class LoadTask extends AsyncTask<String, Void, Integer> {

        private ProgressDialog progressDialog;
        private Server server = new Server();
        private volatile boolean cancel = false;
        private String winner = "";
        private GameActivity game;

        public void setGame(GameActivity game) {
            this.game = game;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(GameActivity.this,
                    getString(R.string.loading_game),
                    null, true, true, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            cancel = true;
                        }
                    });
        }

        @Override
        protected Integer doInBackground(String... params) {
            InputStream stream = server.getGameState(params[0]);
            boolean success = stream != null;
            boolean gOver = false;
            if (success) {
                try {
                    if (cancel) {
                        return 1;
                    }

                    XmlPullParser xml = Xml.newPullParser();
                    xml.setInput(stream, "UTF-8");

                    xml.nextTag();      // Advance to first tag
                    xml.require(XmlPullParser.START_TAG, null, "game");
                    String gameOver = xml.getAttributeValue(null, "gameover");
                    if (gameOver.equals("false")) {
                        while (xml.nextTag() == XmlPullParser.START_TAG) {
                            if (cancel) {
                                return 1;
                            }
                            if (xml.getName().equals("field")) {
                                game.loadFromXML(xml, "field");
                            }
                        }
                    } else {
                        gOver = true;
                        winner = xml.getAttributeValue(null, "winner");
                    }

                } catch (IOException ex) {
                    success = false;
                } catch (XmlPullParserException ex) {
                    success = false;
                } finally {
                    try {
                        stream.close();
                    } catch (IOException ex) {
                    }
                }
            }
            if (success && gOver) {
                return 2;   // Game over
            } else if (success) {
                return 0;   // Game not over, load succeeded
            } else {
                return 1;   // Load failed
            }
        }

        @Override
        protected void onPostExecute(Integer retCode) {
            progressDialog.dismiss();
            if (retCode == 2) {
                onGameOver(winner);
            } else if (retCode == 0) {
                startTurn();
            } else {
                Toast.makeText(GameActivity.this,
                        R.string.loading_fail,
                        Toast.LENGTH_LONG).show();
                loadGameState();
            }
        }

    }
}