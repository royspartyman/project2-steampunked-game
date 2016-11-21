package edu.msu.perrym23.project1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import java.io.Serializable;

/**
 * A representation of the playing area
 */
public class PlayingArea implements Serializable {
    /**
     * Width of the playing area (integer number of cells)
     */
    private int width;

    /**
     * Height of the playing area (integer number of cells)
     */
    private int height;

    /**
     * Storage for the pipes
     * First level: X, second level Y
     */
    private Pipe [][] pipes;

    /**
     * TEMPORARY PAINT OBJECT FOR TESTING
     */
    private transient Paint debugPaint;

    /**
     * Construct a playing area
     * @param width Width (integer number of cells)
     * @param height Height (integer number of cells)
     */
    public PlayingArea(int width, int height) {
        this.width = width;
        this.height = height;

        // Allocate the playing area
        // Java automatically initializes all of the locations to null
        pipes = new Pipe[width][height];

        debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //debugPaint.setColor(Color.rgb(255,85,5));  // deep orange
        debugPaint.setColor(Color.rgb(255,255,255));  // white
        //debugPaint.setColor(Color.rgb(255,152,5));  // orange
        //debugPaint.setColor(Color.rgb(255,152,5));  // orange

    }


    /**
     * Get the playing area height
     */
    public void SetHeight(int value) {
        height = value;
    }

    /**
     * Get the playing area width
     */
    public void SetWidth(int value) {

        width = value;
    }

    // height of playingArea
    public int getHeight() {
        return height;
    }

    // width of playingArea
    public int getWidth() {
        return width;
    }


    public Pipe getPipe(int x, int y) {

        if(x < 0 || x >= width || y < 0 || y >= height) {
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

        /*
         * The pipe itself does the actual search
         */
        return start.search();
    }

    public void clearVisitedFlags() {
        /*
         * Set the visited flags to false
         */
        for(Pipe[] row: pipes) {
            for(Pipe pipe : row) {
                if (pipe != null) {
                    pipe.setVisited(false);
                }
            }
        }
    }

    /**
     * Iterate through each pipe in the playing area to set its loaction and
     * ensure it has the reference to this PlayingArea
     */
    public void syncPipes() {
        /*
         * Set the position of each pipe and give it a reference to this PlayingArea
         */
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                if (pipes[x][y] != null) {
                    pipes[x][y].set(this, x, y);
                }
            }
        }
    }

    public void draw(Canvas canvas, float blockSize) {
        canvas.save();

        int playingAreaSize = (int)(width * blockSize);

        /*
         * Draw the outline of the playing field for now
         */
        canvas.drawRect(0, 0, playingAreaSize, playingAreaSize, debugPaint);

        /*
         * Draw each pipe
         */
        for(Pipe[] row : pipes) {
            for(Pipe pipe : row) {
                if(pipe != null) {
                    pipe.drawInPlayingArea(canvas);
                }
            }
        }

        canvas.restore();
    }
}
