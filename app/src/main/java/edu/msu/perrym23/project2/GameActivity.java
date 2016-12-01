package edu.msu.perrym23.project2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

public class GameActivity extends AppCompatActivity {

    public final static String PLAYER_ONE_NAME = "edu.msu.perrym23.project2.PLAYER_ONE_NAME";
    public final static String PLAYER_TWO_NAME = "edu.msu.perrym23.project2.PLAYER_TWO_NAME";
    public final static String PLAYER_ONE_STARTING = "edu.msu.perrym23.project2.PLAYER_ONE_STARTING";

    private final static String P1_NAME = "p1_name";
    private final static String P2_NAME = "p2_name";
    private final static String IS_PLAYER_ONE = "is_player_one";
    private final static String IS_PLAYER_TWO = "is_player_two";

    private boolean joined = false;

    Server server = new Server();

    private Handler waitForPlayerTwoHandler;
    private Handler waitForPlayerOneHandler;

    private String playerOneName = "";
    private String playerTwoName = "";
    private boolean isPlayerOne;
    private boolean isPlayerTwo;

    private ProgressDialog progressDialog;
    private boolean startGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if(savedInstanceState != null) {
            playerOneName = savedInstanceState.getString(P1_NAME);
            playerTwoName = savedInstanceState.getString(P2_NAME);
            isPlayerOne = savedInstanceState.getBoolean(IS_PLAYER_ONE);
            isPlayerTwo = savedInstanceState.getBoolean(IS_PLAYER_TWO);
            getGameView().loadState(savedInstanceState);
        } else { // There is no saved state, use the intent for initialization
            Intent intent = getIntent();
            playerOneName = intent.getStringExtra(PLAYER_ONE_NAME);
            isPlayerOne = intent.getBooleanExtra(PLAYER_ONE_STARTING, false);
            if (isPlayerOne) {
                isPlayerTwo = false;
                getGameView().initialize(intent);
                uploadGameState(Server.GamePostMode.CREATE);
                waitForPlayerTwo();
            } else {
                playerTwoName = intent.getStringExtra(PLAYER_TWO_NAME);
                getInitialGame();
                waitForPlayerOne();
            }
        }
        updateUI();
    }

    private void waitForPlayerOne() {
        if (!startGame) {
            progressDialog = ProgressDialog.show(GameActivity.this,
                    getString(R.string.hold_horses),
                    getString(R.string.waiting_for_p1), true, false);
            startWaitForPlayerOneHandler();
        }
    }

    private void waitForPlayerTwo() {
        if (!startGame) {
            progressDialog = ProgressDialog.show(GameActivity.this,
                    getString(R.string.hold_horses),
                    getString(R.string.waiting_for_p2), true, false);
            startWaitForPlayerTwoHandler();
        }
    }

    public void startWaitForPlayerOneHandler()
    {
        waitForPlayerOneHandler = new Handler();
        waitForPlayerOneHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkForPlayerTwoJoined();
                waitForPlayerOneHandler.postDelayed(this, 1000);
            }
        }, 5000);  //the time is in miliseconds
    }

    public void startWaitForPlayerTwoHandler()
    {
        waitForPlayerTwoHandler = new Handler();
        waitForPlayerTwoHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkForPlayerTwoJoined();
                waitForPlayerTwoHandler.postDelayed(this, 1000);
            }
        }, 5000);  //the time is in miliseconds
    }

    private void checkForPlayerTwoJoined()
    {
        checkGameReady(playerOneName);
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
                Log.i("success", "waiting");
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Log.i("success", "joined");
                    joined = true;
                    joinedSuccess();
                    waitForPlayerTwoHandler.removeCallbacksAndMessages(null);
                }
            }
        }.execute(usr);
    }

    private void joinedSuccess(){
        getPlayerTwo(playerOneName);
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
                    playerTwoName = name;
            }
        }.execute(usr);
    }

    public void loadFromXML(XmlPullParser xml, String obj) throws IOException, XmlPullParserException {
        switch (obj) {
            case "field":
                getGameView().loadFieldFromXml(xml);
                break;
            case "bank":
                getGameView().loadBankFromXml(xml);
                break;
        }
    }

    public void saveToXML(XmlSerializer xml) throws IOException {
        xml.startTag(null, "game");

        String p1 = isPlayerOne ? playerOneName : playerTwoName;
        String p2 = isPlayerOne ? playerTwoName : playerOneName;
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

    private void handleGameChanging(Intent intent) {

    }

    private void getInitialGame() {
        new AsyncTask<String, Void, Boolean>() {

            private Server server = new Server();
            private volatile boolean cancel = false;
            private volatile String p1;
            private volatile String p2;
            private volatile String dim;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                InputStream stream = server.getGameState(params[0]);
                boolean success = stream != null;
                if(success) {
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

                    } catch(IOException ex) {
                        success = false;
                    } catch(XmlPullParserException ex) {
                        success = false;
                    } finally {
                        try {
                            stream.close();
                        } catch(IOException ex) {
                        }
                    }
                }
                return success;
            }

            @Override
            protected void onPostExecute(Boolean success) {
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
                    if (size != null && p1 != null && p2 != null) {
                        initializeGame(p1, p2, size);
                    } else {
                        Toast.makeText(GameActivity.this,
                                R.string.init_fail,
                                Toast.LENGTH_LONG).show();
                        quitGame();
                    }
                }
            }
        }.execute(playerOneName);
    }

    /* Call this function once we know player one and player two name to start and set this players pipes*/
    private void initializeGame(String p1, String p2, GameView.dimension size) {
        if (!getGameView().isInitialized()) {
            getGameView().initialize(size);
        }
        if (!isPlayerOne) {
            playerTwoName = p1;
            getGameView().setPlayerNames(playerOneName, playerTwoName, Pipe.PipeGroup.PLAYER_TWO);
        } else {
            playerTwoName = p2;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(P1_NAME, playerOneName);
        bundle.putString(P2_NAME, playerTwoName);
        bundle.putSerializable(IS_PLAYER_ONE, isPlayerOne);
        bundle.putSerializable(IS_PLAYER_TWO, isPlayerTwo);
        getGameView().saveState(bundle);
    }

    private void loadGameState() {
        LoadTask load = new LoadTask();
        load.setGame(this);
        load.execute(playerOneName);
    }

    private void uploadGameState(Server.GamePostMode mode) {
        UploadTask update = new UploadTask();
        update.setGame(this);
        update.setUploadMode(mode);
        switch (mode) {
            case UPDATE:
                update.execute(playerOneName, null);
                break;
            case CREATE:
                update.execute(playerOneName, "");
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
        }.execute(playerOneName);

        Intent intent = new Intent(this, LoginUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onSurrender(View view) {
        onGameOver(playerTwoName);
    }
    public void onInstall(View view) {
        getGameView().installPipe();
        updateUI();

        if(!getGameView().isMyTurn()) {
            uploadGameState(Server.GamePostMode.UPDATE);
        }
    }
    public void onDiscard(View view) {
        getGameView().discard();
        updateUI();

        if(!getGameView().isMyTurn()) {
            uploadGameState(Server.GamePostMode.UPDATE);
        }
    }
    public void onOpenValve(View view) {
        if(getGameView().openValve()) {
            onGameOver(playerOneName);
        } else {
            onGameOver(playerTwoName);
        }
    }

    /**
     * Once someone wins or there is a forfeit
     */
    public void onGameOver(String winner) {
        getGameView().setGameOver(winner);
        uploadGameState(Server.GamePostMode.UPDATE);

        Intent intent = new Intent(this, EndGameActivity.class);

        intent.putExtra(EndGameActivity.WINNER, winner);

        startActivity(intent);
    }

    GameView getGameView() {
        return (GameView)findViewById(R.id.gameView);
    }

    //set the current active player
    private void updateUI(){
        TextView currentPlayer = (TextView)findViewById(R.id.currentPlayer);
        String yourTurn = getString(R.string.your_turn) + '\n' + playerOneName;
        String waitingFor = getString(R.string.waiting_for) + '\n' + playerTwoName;
        Button discard = (Button) findViewById(R.id.discardButton);
        Button install = (Button) findViewById(R.id.installButton);
        Button surrender = (Button) findViewById(R.id.surrender);
        Button openValve = (Button) findViewById(R.id.openValveButton);

        if (getGameView().isMyTurn()) {
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

        private Server server = new Server();
        private GameActivity game;
        private Server.GamePostMode uploadMode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            if (!success) {
                Toast.makeText(GameActivity.this,
                        R.string.upload_fail,
                        Toast.LENGTH_LONG).show();
                uploadGameState(uploadMode);
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
            if(success) {
                try {
                    if (cancel) {
                        return 1;
                    }

                    XmlPullParser xml = Xml.newPullParser();
                    xml.setInput(stream, "UTF-8");

                    xml.nextTag();      // Advance to first tag
                    xml.require(XmlPullParser.START_TAG, null, "game");
                    String gameOver = xml.getAttributeValue(null, "gameover");
                    if(gameOver.equals("false")) {
                        while(xml.nextTag() == XmlPullParser.START_TAG) {
                            if (cancel) {
                                return 1;
                            }
                            if(xml.getName().equals("field")) {
                                game.loadFromXML(xml, "field");
                            } else if (xml.getName().equals("bank")) {
                                game.loadFromXML(xml, "bank");
                            }
                        }
                    } else {
                        gOver = true;
                        winner = xml.getAttributeValue(null, "winner");
                    }

                } catch(IOException ex) {
                    success = false;
                } catch(XmlPullParserException ex) {
                    success = false;
                } finally {
                    try {
                        stream.close();
                    } catch(IOException ex) {
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