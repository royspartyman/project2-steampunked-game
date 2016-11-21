package edu.msu.perrym23.project1;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GameActivity extends AppCompatActivity {

    private static final String PLAYERONENAME = "PlayerOneName";
    private static final String PLAYERTWONAME = "PlayerTwoName";
    private static final String PLAYERONECOLOR = "PlayerOneColor";
    private static final String PLAYERTWOCOLOR = "PlayerTwoColor";

    private boolean startGame = false;

    @BindView(R.id.currentPlayer)
    TextView currentPlayerTV;

    @BindView(R.id.install)
    FloatingActionButton installFAB;

    @BindView(R.id.open)
    FloatingActionButton openFAB;

    @BindView(R.id.discard)
    FloatingActionButton discardFAB;

    @BindView(R.id.forfeit)
    FloatingActionButton forfeitFAB;

    @BindView(R.id.bat_player)
    ImageView batPlayerIV;

    @OnClick(R.id.install)
    public void onInstallFABClick(){
        getGameView().installPipe();
        updateUI();
    }

    @OnClick(R.id.open)
    public void onOpenFABClick(){
        Intent intent = new Intent(GameActivity.this, GameCompleteActivity.class);
        Bundle bundle = new Bundle();
        if(getGameView().openValve() && getGameView().isPlayerOneTurn()){
            bundle.putString(GameCompleteActivity.WINNER,getGameView().getPlayerOne().getName());
            bundle.putString(GameCompleteActivity.LOSER,getGameView().getPlayerTwo().getName());
        }
        else if(getGameView().openValve() && getGameView().isPlayerTwoTurn()){
            bundle.putString(GameCompleteActivity.WINNER, getGameView().getPlayerTwo().getName());
            bundle.putString(GameCompleteActivity.LOSER, getGameView().getPlayerOne().getName());
        }
        else if(getGameView().isPlayerOneTurn()){
            bundle.putString(GameCompleteActivity.WINNER, getGameView().getPlayerTwo().getName());
            bundle.putString(GameCompleteActivity.LOSER, getGameView().getPlayerOne().getName());
        }
        else if(getGameView().isPlayerTwoTurn()){
            bundle.putString(GameCompleteActivity.WINNER,getGameView().getPlayerOne().getName());
            bundle.putString(GameCompleteActivity.LOSER, getGameView().getPlayerTwo().getName());
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.discard)
    public void onDiscardFABClick(){
        getGameView().discard();
        updateUI();
    }

    @OnClick(R.id.forfeit)
    public void onForfeitFABClick(){
        Intent intent = new Intent(GameActivity.this, GameCompleteActivity.class);
        Bundle bundle = new Bundle();
        if(getGameView().isPlayerOneTurn()) {
            bundle.putString(GameCompleteActivity.WINNER, getGameView().getPlayerTwo().getName());
            bundle.putString(GameCompleteActivity.LOSER, getGameView().getPlayerOne().getName());
        }
        else {
            bundle.putString(GameCompleteActivity.WINNER,getGameView().getPlayerOne().getName());
            bundle.putString(GameCompleteActivity.LOSER,getGameView().getPlayerTwo().getName());
        }
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
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        if(savedInstanceState!= null){
            getGameView().loadState(savedInstanceState);
        }else {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            String playerOneName = bundle.getString(PLAYERONENAME);
            String playerTwoName = bundle.getString(PLAYERTWONAME);
            Integer player1Color = bundle.getInt(PLAYERONECOLOR);
            Integer player2Color = bundle.getInt(PLAYERTWOCOLOR);
            getGameView().initialize(intent);
            initializeGame(playerOneName, playerTwoName, player1Color, player2Color, GameView.dimension.LARGE);
        }
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initializeGame(String p1, String p2, int colorOne, int colorTwo, GameView.dimension size) {
        if (!getGameView().isInitialized()) {
            getGameView().initialize(size);
        }
        Player playerOne = new Player(p1, colorOne);
        Player playerTwo = new Player(p2, colorTwo);
        getGameView().setPlayers(playerOne, playerTwo, Pipe.PipeGroup.PLAYER_ONE, Pipe.PipeGroup.PLAYER_TWO);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        getGameView().saveState(bundle);
    }

    GameView getGameView() {
        return (GameView) findViewById(R.id.gameView);
    }

    private void updateUI(){
        if(getGameView().isPlayerOneTurn()){
            currentPlayerTV.setText(getGameView().getPlayerOne().getName() + ", it is your turn!");
            batPlayerIV.setColorFilter(getGameView().getPlayerOne().getColor());
        } else {
            currentPlayerTV.setText(getGameView().getPlayerTwo().getName() + ", it is your turn!");
            batPlayerIV.setColorFilter(getGameView().getPlayerTwo().getColor());
        }
    }
}