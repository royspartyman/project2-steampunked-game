package edu.msu.perrym23.project2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;

public class PlayingArea implements Serializable {

    private int width;

    private int height;

    private Pipe[][] pipes;

    private transient Paint playingAreaPaint;

    public PlayingArea(int width, int height) {
        this.width = width;
        this.height = height;
        pipes = new Pipe[width][height];
        playingAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        playingAreaPaint.setColor(Color.rgb(255,255,255));
    }

    public void loadFromSavedState(XmlPullParser xml, GameView view) throws IOException, XmlPullParserException {

        final Pipe[][] newPipes = new Pipe[width][height];

        while (xml.nextTag() == XmlPullParser.START_TAG) {
            if (xml.getName().equals("pipe")) {
                Pipe newPipe = Pipe.fieldPipeFromXml(xml, view.getContext(), this);
                newPipes[newPipe.getXLoc()][newPipe.getYLoc()] = newPipe;
            }
            Server.skipToEndTag(xml);
        }

        view.post(new Runnable() {

            @Override
            public void run() {
                pipes = newPipes;
            }
        });
    }

    public void saveToXML(XmlSerializer xml) throws IOException {

        xml.startTag(null, "field");

        for (Pipe[] row : pipes) {
            for (Pipe p : row) {
                if (p != null) {
                    p.fieldPipeToXml(xml);
                }
            }
        }

        xml.endTag(null, "field");
    }

    public void SetHeight(int value) {
        height = value;
    }

    public void SetWidth(int value) {
        width = value;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Pipe getPipe(int x, int y) {

        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        return pipes[x][y];
    }

    public void add(Pipe pipe, int x, int y) {
        pipes[x][y] = pipe;
        pipe.set(this, x, y);
    }

    public boolean search(Pipe start) {
        clearVisitedFlags();
        return start.search();
    }

    public void clearVisitedFlags() {
        for (Pipe[] row : pipes) {
            for (Pipe pipe : row) {
                if (pipe != null) {
                    pipe.setVisited(false);
                }
            }
        }
    }

    public void syncPipes() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pipes[x][y] != null) {
                    pipes[x][y].set(this, x, y);
                }
            }
        }
    }

    public void draw(Canvas canvas, float blockSize) {
        canvas.save();

        int playingAreaSize = (int) (width * blockSize);

        canvas.drawRect(0, 0, playingAreaSize, playingAreaSize, playingAreaPaint);

        for (Pipe[] row : pipes) {
            for (Pipe pipe : row) {
                if (pipe != null) {
                    pipe.drawInPlayingArea(canvas);
                }
            }
        }

        canvas.restore();
    }
}
