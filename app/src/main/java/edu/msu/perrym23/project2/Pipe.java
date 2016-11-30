package edu.msu.perrym23.project2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;

/**
 * A representation of a pipe
 */
public class Pipe implements Serializable {

    public enum pipeType {
        START,
        END,
        STRAIGHT,
        RIGHT_ANGLE,
        CAP,
        TEE
    }

    public enum PipeGroup {
        PLAYER_ONE,
        PLAYER_TWO
    }

    private pipeType type;

    private PipeGroup group;

    private transient PlayingArea playingArea = null;

    private boolean[] connect = {false, false, false, false};

    private int xCoord = 0;

    private int yCoord = 0;

    private float x = 0f;

    private float y = 0f;

    private transient Bitmap bitmap;

    private transient Bitmap handleBit = null;

    private transient Bitmap steamBit = null;

    private boolean gaugeFull = false;

    private float bitmapRotation = 0f;

    private float handleRotation = 0f;

    private transient boolean visited = false;

    private transient Paint gaugePaint = null;

    private transient TextPaint namePaint = null;

    private String playerName = null;

    public Pipe(Context context, pipeType type) {
        steamBit = BitmapFactory.decodeResource(context.getResources(), R.drawable.leak);

        this.type = type;
        switch(type) {
            case START:
                setConnections(false, true, false, false);
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.straight);
                bitmapRotation = 90f;
                handleBit = BitmapFactory.decodeResource(context.getResources(), R.drawable.handle);
                break;

            case END:
                setConnections(false, false, false, true);
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gauge);
                bitmapRotation = -90f;
                break;

            case STRAIGHT:
                setConnections(true, false, true, false);
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.straight);
                break;

            case RIGHT_ANGLE:
                setConnections(false, true, true, false);
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.a90);
                break;

            case CAP:
                setConnections(false, false, true, false);
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cap);
                break;

            case TEE:
                setConnections(true, true, true, false);
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tee);
                break;
        }

        if(type == pipeType.START) {
            namePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            namePaint.setColor(Color.BLACK);
            namePaint.setTextSize(50f);
        }

        if(type == pipeType.END) {
            gaugePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            gaugePaint.setColor(Color.RED);
            gaugePaint.setStrokeWidth(6f);
        }
    }

    public static Pipe fieldPipeFromXml(XmlPullParser xml, Context context, PlayingArea field) throws IOException, XmlPullParserException {
        String t = xml.getAttributeValue(null, "type");
        pipeType type = pipeType.STRAIGHT;
        switch (t) {
            case "STRAIGHT":
                break;

            case "START":
                type = pipeType.START;
                break;

            case "END":
                type = pipeType.END;
                break;

            case "CAP":
                type = pipeType.CAP;
                break;

            case "TEE":
                type = pipeType.TEE;
                break;

            case "RIGHT_ANGLE":
                type = pipeType.RIGHT_ANGLE;
                break;
        }
        Pipe newPipe = new Pipe(context, type);

        if (type == pipeType.START) {
            String label = xml.getAttributeValue(null, "label");
            if (!label.equals("")) {
                newPipe.setPlayerName(label);
            } else {
                newPipe.setPlayerName(null);
            }
        }

        String g = xml.getAttributeValue(null, "group");
        PipeGroup group = PipeGroup.PLAYER_ONE;
        switch (g) {
            case "PLAYER_ONE":
                break;

            case "PLAYER_TWO":
                group = PipeGroup.PLAYER_TWO;
                break;
        }
        newPipe.setGroup(group);

        float rot = Float.parseFloat(xml.getAttributeValue(null, "rotation"));

        newPipe.setBitmapRotation(rot);
        newPipe.snapRotation();

        int xCoord = Integer.parseInt(xml.getAttributeValue(null, "x"));
        int yCoord = Integer.parseInt(xml.getAttributeValue(null, "y"));

        newPipe.set(field, xCoord, yCoord);

        return newPipe;
    }

    public void fieldPipeToXml(XmlSerializer xml) throws IOException {
        xml.startTag(null, "pipe");
        xml.attribute(null, "type", type.toString());
        xml.attribute(null, "group", group.toString());
        xml.attribute(null, "rotation", Float.toString(bitmapRotation));
        xml.attribute(null, "x", Integer.toString(xCoord));
        xml.attribute(null, "y", Integer.toString(yCoord));

        if (type == pipeType.START) {
            if (playerName != null) {
                xml.attribute(null, "label", playerName);
            } else {
                xml.attribute(null, "label", "");
            }
        }

        xml.endTag(null, "pipe");
    }

    public static Pipe bankPipeFromXml(XmlPullParser xml, Context context) throws IOException, XmlPullParserException {
        String t = xml.getAttributeValue(null, "type");
        pipeType type = pipeType.STRAIGHT;
        switch (t) {
            case "STRAIGHT":
                break;

            case "START":
                type = pipeType.START;
                break;

            case "END":
                type = pipeType.END;
                break;

            case "CAP":
                type = pipeType.CAP;
                break;

            case "TEE":
                type = pipeType.TEE;
                break;

            case "RIGHT_ANGLE":
                type = pipeType.RIGHT_ANGLE;
                break;
        }
        return new Pipe(context, type);
    }

    public void bankPipeToXml(XmlSerializer xml) throws IOException {
        xml.startTag(null, "pipe");

        xml.attribute(null, "type", type.toString());

        xml.endTag(null, "pipe");
    }

    public void resetPipe() {
        setLocation(0f, 0f);
        resetConnections();
    }

    public void resetConnections() {
        bitmapRotation = 0f;
        switch(type) {
            case START:
                setConnections(false, true, false, false);
                bitmapRotation = 90f;
                break;

            case END:
                setConnections(false, false, false, true);
                bitmapRotation = -90f;
                break;

            case STRAIGHT:
                setConnections(true, false, true, false);
                break;

            case RIGHT_ANGLE:
                setConnections(false, true, true, false);
                break;

            case CAP:
                setConnections(false, false, true, false);
                break;

            case TEE:
                setConnections(true, true, true, false);
                break;
        }
    }

    private void setConnections(boolean north, boolean east, boolean south, boolean west) {
        connect[0] = north;
        connect[1] = east;
        connect[2] = south;
        connect[3] = west;
    }

    public float getBitmapRotation() {
        return bitmapRotation;
    }

    public void setBitmapRotation(float bitmapRotation) {
        this.bitmapRotation = bitmapRotation;
    }

    public void snapRotation() {
        int quarterRotations = Math.round(bitmapRotation / 90f) % 4;

        bitmapRotation = quarterRotations * 90f;

        boolean[] newConnect = new boolean[4];
        for(int i = 0; i < connect.length; i++) {
            int newIndex = (i+quarterRotations) % 4;
            if(newIndex < 0) newIndex += 4;
            newConnect[newIndex] = connect[i];
        }
        connect = newConnect;
    }

    public boolean search() {
        visited = true;

        for(int d=0; d<4; d++) {

            if(!connect[d]) {
                continue;
            }

            Pipe n = neighbor(d);
            if(n == null) {
                return false;
            }

            int dp = (d + 2) % 4;
            if(!n.connect[dp]) {
                return false;
            }

            if(n.visited) {
                continue;
            } else {
                if(!n.search()) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean validConnection() {
        boolean atLeastOneConnection = false;

        for(int d=0; d<4; d++) {
            if(!connect[d]) {
                continue;
            }

            Pipe n = neighbor(d);
            if(n == null) {
                continue;
            }

            int coorespondingDirection = (d + 2) % 4;
            if(n.connect[coorespondingDirection]) {
                atLeastOneConnection = true;
                if(n.getGroup() != this.group) {
                    return false;
                }
            }
        }

        if(!atLeastOneConnection) {
            return false;
        }

        return true;
    }

    private Pipe neighbor(int d) {
        switch(d) {
            case 0:
                return playingArea.getPipe(xCoord, yCoord-1);

            case 1:
                return playingArea.getPipe(xCoord+1, yCoord);

            case 2:
                return playingArea.getPipe(xCoord, yCoord+1);

            case 3:
                return playingArea.getPipe(xCoord-1, yCoord);
        }

        return null;
    }

    public PlayingArea getPlayingArea() {
        return playingArea;
    }

    public float getBitmapHeight() {
        return bitmap.getHeight();
    }

    public float getBitmapWidth() {
        return bitmap.getWidth();
    }

    public void set(PlayingArea playingArea, int x, int y) {
        this.playingArea = playingArea;
        this.xCoord = x;
        this.yCoord = y;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getXLoc() {
        return xCoord;
    }

    public int getYLoc() {
        return yCoord;
    }

    public boolean beenVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void drawInPlayingArea(Canvas canvas) {
        if(playingArea != null) {
            float bitDim = bitmap.getHeight() < bitmap.getWidth() ? bitmap.getHeight() : bitmap.getWidth();
            this.x = coordinateToLocation(xCoord, bitDim);
            this.y = coordinateToLocation(yCoord, bitDim);

            draw(canvas);

            if(type != pipeType.END) {
                for(int d=0; d<4; d++) {
                    if(!connect[d]) {
                        continue;
                    }

                    Pipe n = neighbor(d);

                    if(n == null) {
                        drawSteam(canvas, d, bitDim);
                        continue;
                    }

                    int coorespondingDirection = (d + 2) % 4;

                    if(!n.connect[coorespondingDirection]) {
                        drawSteam(canvas, d, bitDim);
                    }
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        float bitDim = bitmap.getHeight() < bitmap.getWidth() ? bitmap.getHeight() : bitmap.getWidth();

        canvas.save();
        canvas.translate(x, y);
        canvas.rotate(bitmapRotation);
        canvas.translate(-(bitDim / 2), -(bitDim / 2));
        canvas.drawBitmap(bitmap, 0, 0, null);

        drawGaugeLine(canvas);

        canvas.restore();

        if (handleBit != null) {
            canvas.save();
            canvas.translate(x, y);
            canvas.rotate(handleRotation);
            canvas.translate(-bitDim / 2, -bitDim / 2);
            canvas.drawBitmap(handleBit, 0, 0, null);
            canvas.restore();
        }

        if(type == pipeType.START && playerName != null) {
            drawPlayerName(canvas);
        }
    }

    private void drawSteam(Canvas canvas, int dir, float blockDim) {
        int x = this.xCoord;
        int y = this.yCoord;
        float rotation = 0f;
        switch(dir) {
            case 0:
                y--;
                break;
            case 1:
                x++;
                rotation = 90f;
                break;
            case 2:
                y++;
                rotation = 180f;
                break;
            case 3:
                x--;
                rotation = -90f;
                break;
        }

        canvas.save();
        canvas.translate(coordinateToLocation(x, blockDim), coordinateToLocation(y, blockDim));
        canvas.rotate(rotation);
        canvas.translate(-(blockDim / 2), -(blockDim / 2));

        canvas.drawBitmap(steamBit, 0, 0, null);

        canvas.restore();
    }

    private float coordinateToLocation(int coord, float blockDim) {
        return (coord * blockDim) + (blockDim / 2);
    }

    private void drawPlayerName(Canvas canvas) {
        int theMagicNumber = 7;
        canvas.save();
        canvas.translate(x-(bitmap.getWidth()/2)+theMagicNumber, y+(bitmap.getHeight()/2)+theMagicNumber);
        canvas.drawText(playerName, 0, 0, namePaint);
        canvas.restore();
    }

    private void drawGaugeLine(Canvas canvas) {
        if (type == pipeType.END) {
            float x1 = bitmap.getWidth() * 0.71f;
            float y1 = bitmap.getHeight() / 2f - 2;
            float xdiff = 50f;
            float ydiff = 50f;
            if(gaugeFull) {
                canvas.drawLine(x1, y1, x1 - xdiff, y1 + ydiff, gaugePaint);
            } else {
                canvas.drawLine(x1, y1, x1 - xdiff, y1 - ydiff, gaugePaint);
            }
        }
    }

    public void setGaugeFull() {
        gaugeFull = true;
    }

    public void setHandleOpen() {
        handleRotation = 90f;
    }

    public boolean hit(float x, float y) {
        if(x >= this.x - bitmap.getWidth() && x <= this.x + bitmap.getWidth() &&
           y >= this.y - bitmap.getHeight() && y <= this.y + bitmap.getHeight()) {
            return true;
        }

        return false;
    }

    public PipeGroup getGroup() {
        return group;
    }

    public void setGroup(PipeGroup group) {
        this.group = group;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
