package edu.msu.perrym23.project2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;

public class GameView extends View {

    public final static String BOARD_SIZE = "edu.msu.perrym23.project2.BOARD_SIZE";
    public final static String PLAYING_AREA = "playingArea";
    public final static String PIPE_BANK = "pipeBank";
    public final static String PARAMETERS = "parameters";

    public enum dimension {
        SMALL,
        MEDIUM,
        LARGE
    }

    private final static float bankLocation = 0.8f;

    private PlayingArea gameField = null;

    private PipeBank bank = null;

    private Parameters params = null;

    private Touch touch1 = new Touch();

    private Touch touch2 = new Touch();

    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        bank = new PipeBank(getContext());
        params = new Parameters();
    }

    public boolean isInitialized() {
        return gameField != null;
    }

    public void initialize(Intent intent) {
        params.boardSize = (dimension)intent.getSerializableExtra(BOARD_SIZE);
        initializeGameArea(params.boardSize);
    }

    public void initialize(dimension size) {
        params.boardSize = size;
        initializeGameArea(params.boardSize);
    }

    public void loadFieldFromXml(XmlPullParser xml) throws IOException, XmlPullParserException {
        gameField.loadFromSavedState(xml, this);
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    public void loadBankFromXml(XmlPullParser xml) throws IOException, XmlPullParserException {
        bank.loadFromSavedState(xml, this);
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    public void saveToXML(XmlSerializer xml) throws IOException {
        gameField.saveToXML(xml);
        bank.saveToXML(xml);
    }

    public void saveState(Bundle bundle) {
        bundle.putSerializable(PLAYING_AREA, gameField);
        bundle.putSerializable(PIPE_BANK, bank);
        bundle.putSerializable(PARAMETERS, params);
    }

    public void loadState(Bundle bundle) {
        params = (Parameters)bundle.getSerializable(PARAMETERS);

        gameField = (PlayingArea)bundle.getSerializable(PLAYING_AREA);
        if(gameField != null) {
            gameField.syncPipes();
        }

        bank = (PipeBank)bundle.getSerializable(PIPE_BANK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int fieldWidth = canvas.getWidth();
        int fieldHeight = (int)(canvas.getHeight() * bankLocation);
        int bankWidth = canvas.getWidth();
        int bankHeight = (int)(canvas.getHeight() * (1 - bankLocation));

        params.bankXOffset = 0f;
        params.bankYOffset = canvas.getHeight() * bankLocation;

        if(canvas.getWidth() > canvas.getHeight()) {
            fieldWidth = (int)(canvas.getWidth() * bankLocation);
            fieldHeight = canvas.getHeight();
            bankWidth = (int)(canvas.getWidth() * (1 - bankLocation));
            bankHeight = canvas.getHeight();
            params.bankXOffset = canvas.getWidth() * bankLocation;
            params.bankYOffset = 0f;
        }

        if(params.gameFieldScale == -1f) {
            if(fieldWidth <= fieldHeight) {
                params.gameFieldScale = fieldWidth / params.gameFieldWidth;
            } else {
                params.gameFieldScale = fieldHeight / params.gameFieldHeight;
            }
        }

        if(params.marginX == 10000000 || params.marginY == 10000000) {
            params.marginX = (int)((fieldWidth - params.gameFieldWidth * params.gameFieldScale) / 2);
            params.marginY = (int)((fieldHeight - params.gameFieldHeight * params.gameFieldScale) / 2);
        }

        if(canvas.getWidth() < canvas.getHeight()) {
            if(params.gameFieldWidth * params.gameFieldScale < fieldWidth) {
                params.gameFieldScale = fieldWidth / params.gameFieldWidth;
            }
        } else {
            if(params.gameFieldHeight * params.gameFieldScale < fieldHeight) {
                params.gameFieldScale = fieldHeight / params.gameFieldHeight;
            }
        }

        if(params.marginX > 0) {
            params.marginX = 0;
        }

        if(params.marginY > 0) {
            params.marginY = 0;
        }

        if(fieldWidth - params.marginX > params.gameFieldWidth * params.gameFieldScale) {
            params.marginX = fieldWidth - (int)(params.gameFieldWidth * params.gameFieldScale);
        }

        if(fieldHeight - params.marginY > params.gameFieldHeight * params.gameFieldScale) {
            params.marginY = fieldHeight - (int)(params.gameFieldHeight * params.gameFieldScale);
        }

        canvas.save();
        canvas.translate(params.marginX, params.marginY);
        canvas.scale(params.gameFieldScale, params.gameFieldScale);

        if (gameField != null) {
            gameField.draw(canvas, params.blockSize);
        }
        canvas.restore();

        canvas.save();
        canvas.translate(params.bankXOffset, params.bankYOffset);
        bank.draw(canvas, bankWidth, bankHeight, params.blockSize);
        canvas.restore();

        if(params.currentPipe != null){
            canvas.save();
            canvas.translate(params.marginX, params.marginY);
            canvas.scale(params.gameFieldScale, params.gameFieldScale);
            params.currentPipe.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!params.myTurn) {
            return false;
        }

        int id = event.getPointerId(event.getActionIndex());

        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touch1.id = id;
                touch2.id = -1;

                getPositions(event);
                touch1.copyToLast();

                float bankx = (event.getX() - params.bankXOffset);
                float banky = (event.getY() - params.bankYOffset);

                if(bankx >= 0 && banky >= 0) {
                    params.currentPipe = bank.hitPipe(bankx, banky);
                    bank.setActivePipe(params.currentPipe);

                    if(params.currentPipe != null) {
                        params.currentPipe.setLocation(touch1.x,touch1.y);
                        params.currentPipe.setGroup(params.myGroup);
                    }
                }

                params.draggingPipe = false;
                if (params.currentPipe != null) {
                    if(params.currentPipe.hit(touch1.x, touch1.y)) {
                        params.draggingPipe = true;
                    }
                }

                if (!params.draggingPipe) {
                    getPositions(event, false);
                    touch1.copyToLast();
                }

                invalidate();
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (touch1.id >= 0 && touch2.id < 0) {
                    touch2.id = id;
                    if(params.draggingPipe) {
                        getPositions(event);
                    } else {
                        getPositions(event, false);
                    }
                    touch2.copyToLast();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_CANCEL:
                touch1.id = -1;
                touch2.id = -1;
                invalidate();
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                if (id == touch2.id) {
                    touch2.id = -1;
                } else if (id == touch1.id) {
                    Touch t = touch1;
                    touch1 = touch2;
                    touch2 = t;
                    touch2.id = -1;
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                if(params.currentPipe != null && params.draggingPipe) {
                    getPositions(event);
                    moveCurrentPipe();
                } else {
                    getPositions(event, false);
                    movePlayingArea();
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    private void getPositions(MotionEvent event) {
        getPositions(event, true);
    }

    private void getPositions(MotionEvent event, boolean makeRelativeToPlayArea) {
        for(int i=0;  i<event.getPointerCount();  i++) {
            int id = event.getPointerId(i);
            float x = event.getX(i);
            float y = event.getY(i);

            if(makeRelativeToPlayArea) {
                x = (x - params.marginX) / params.gameFieldScale;
                y = (y - params.marginY) / params.gameFieldScale;
            }

            if(id == touch1.id) {
                touch1.copyToLast();
                touch1.x = x;
                touch1.y = y;
            } else if(id == touch2.id) {
                touch2.copyToLast();
                touch2.x = x;
                touch2.y = y;
            }
        }

        invalidate();
    }

    private void movePlayingArea() {
        if(touch1.id < 0) {
            return;
        }

        if(touch1.id >= 0) {
            touch1.computeDeltas();

            params.marginX += touch1.dX;
            params.marginY += touch1.dY;
        }

        if(touch2.id >= 0) {
            float distance1 = distance(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
            float distance2 = distance(touch1.x, touch1.y, touch2.x, touch2.y);
            float ra = distance2 / distance1;
            scalePlayingArea(ra);
        }
    }

    private void scalePlayingArea(float ratio) {
        float width1 = params.gameFieldWidth * params.gameFieldScale;
        float height1 = params.gameFieldHeight * params.gameFieldScale;

        params.gameFieldScale *= ratio;

        float width2 = params.gameFieldWidth * params.gameFieldScale;
        float height2 = params.gameFieldHeight * params.gameFieldScale;

        params.marginX -= (width2 - width1) / 2;
        params.marginY -= (height2 - height1) / 2;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float)Math.hypot(x2 - x1, y2 - y1);
    }

    private void moveCurrentPipe() {
        if(touch1.id < 0) {
            return;
        }

        if(touch1.id >= 0) {
            touch1.computeDeltas();

            params.currentPipe.setLocation(params.currentPipe.getX() + touch1.dX, params.currentPipe.getY() + touch1.dY);
        }
        if(touch2.id >= 0) {
            float angle1 = angle(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
            float angle2 = angle(touch1.x, touch1.y, touch2.x, touch2.y);
            float da = angle2 - angle1;
            rotate(da, touch1.x, touch1.y);
        }
    }

    public void rotate(float dAngle, float x1, float y1) {
        params.currentPipe.setBitmapRotation(params.currentPipe.getBitmapRotation() + dAngle);
    }

    private float angle(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    private void initializeGameArea(dimension board) {
        switch (board) {
            case SMALL:
                gameField = new PlayingArea(5, 5);
                setBoardStartsEnds(0, 1, 0, 3, 4, 2, 4, 4);
                break;
            case MEDIUM:
                gameField = new PlayingArea(10, 10);
                setBoardStartsEnds(0, 2, 0, 6, 9, 3, 9, 7);
                break;
            case LARGE:
                gameField = new PlayingArea(20, 20);
                setBoardStartsEnds(0, 6, 0, 13, 19, 8, 19, 15);
                break;
        }

        params.gameFieldWidth = gameField.getWidth() * params.blockSize;
        params.gameFieldHeight = gameField.getHeight() * params.blockSize;
    }

    private void setBoardStartsEnds(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        params.playerOneStart = new Pipe(getContext(), Pipe.pipeType.START);
        params.playerOneStart.setGroup(Pipe.PipeGroup.PLAYER_ONE);
        params.playerTwoStart = new Pipe(getContext(), Pipe.pipeType.START);
        params.playerTwoStart.setGroup(Pipe.PipeGroup.PLAYER_TWO);
        params.playerOneEnd = new Pipe(getContext(), Pipe.pipeType.END);
        params.playerOneEnd.setGroup(Pipe.PipeGroup.PLAYER_ONE);
        params.playerTwoEnd = new Pipe(getContext(), Pipe.pipeType.END);
        params.playerTwoEnd.setGroup(Pipe.PipeGroup.PLAYER_TWO);

        gameField.add(params.playerOneStart, x1, y1);
        gameField.add(params.playerTwoStart, x2, y2);
        gameField.add(params.playerOneEnd, x3, y3);
        gameField.add(params.playerTwoEnd, x4, y4);

        params.blockSize = params.playerOneStart.getBitmapHeight();
    }
    
    public void installPipe(){
        float originalRotation = params.currentPipe.getBitmapRotation();

        int x = getPlayingAreaXCoord(params.currentPipe.getX());
        int y = getPlayingAreaYCoord(params.currentPipe.getY());
        params.currentPipe.snapRotation();

        params.currentPipe.set(gameField, x, y);
        if(gameField.getPipe(x, y) == null && params.currentPipe.validConnection()) {
            gameField.add(params.currentPipe, x ,y);
            discard();
        } else {
            params.currentPipe.resetConnections();
            params.currentPipe.setBitmapRotation(originalRotation);
            Toast.makeText(getContext(),
                    R.string.invalid_connection,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isMyTurn() {
        return params.myTurn;
    }

    public void startTurn() {
        params.myTurn = true;
    }

    private void endTurn() {
        params.myTurn = false;
    }

    public void discard() {
        bank.setActivePipe(null);
        params.currentPipe = null;
        endTurn();
        invalidate();
    }

    public boolean openValve() {
        params.myStart.setHandleOpen();

        if(!gameField.search(params.myStart)) {
            invalidate();
            return false;
        }

        params.myEnd.setGaugeFull();
        invalidate();
        return true;
    }

    private int getPlayingAreaXCoord(float xLoc) {
        if(xLoc >= 0f && xLoc <= params.gameFieldWidth) {
            return (int)(xLoc / params.blockSize);
        }

        return -1;
    }

    private int getPlayingAreaYCoord(float yLoc) {
        if(yLoc >= 0f && yLoc <= params.gameFieldHeight) {
            return (int)(yLoc / params.blockSize);
        }

        return -1;
    }

    public void setPlayerNames(String me, String them, Pipe.PipeGroup myGroup) {
        params.myGroup = myGroup;
        switch(myGroup) {
            case PLAYER_ONE:
                params.playerOneStart.setPlayerName(me);
                params.playerTwoStart.setPlayerName(them);
                params.myStart = params.playerOneStart;
                params.myEnd = params.playerOneEnd;
                break;
            case PLAYER_TWO:
                params.playerOneStart.setPlayerName(them);
                params.playerTwoStart.setPlayerName(me);
                params.myStart = params.playerTwoStart;
                params.myEnd = params.playerTwoEnd;
                break;
        }
    }

    public dimension getBoardSize() {
        return params.boardSize;
    }
    public void setGameOver(String winner) {
        params.winner = winner;
        params.gameOver = true;
    }
    public boolean gameOver() {
        return params.gameOver;
    }
    public String getWinner() {
        return params.winner;
    }

    private static class Parameters implements Serializable {

        public dimension boardSize;

        public boolean gameOver = false;

        public String winner = "";

        public boolean myTurn = false;

        public Pipe.PipeGroup myGroup;

        public Pipe myStart;

        public Pipe myEnd;

        public Pipe playerOneStart;

        public Pipe playerTwoStart;

        public Pipe playerOneEnd;

        public Pipe playerTwoEnd;

        public Pipe currentPipe = null;

        public boolean draggingPipe = false;

        public float blockSize = 0f;

        public float gameFieldWidth = 0f;

        public float gameFieldHeight = 0f;

        public int marginX = 10000000;

        public int marginY = 10000000;

        public float bankXOffset = 0;

        public float bankYOffset = 0;

        public float gameFieldScale = -1f;
    }

    private class Touch {
        public float dX = 0;

        public float dY = 0;

        public int id = -1;

        public float x = 0;

        public float y = 0;

        public float lastX = 0;

        public float lastY = 0;

        public void copyToLast() {
            lastX = x;
            lastY = y;
        }

        public void computeDeltas() {
            dX = x - lastX;
            dY = y - lastY;
        }
    }

}