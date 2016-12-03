package edu.msu.perrym23.project2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.io.Serializable;
import java.util.Random;

class PipeBank implements Serializable {

    public final int bankSize = 5;

    private final PipeProbability[] relativePipeProbs = {
            new PipeProbability(Pipe.pipeType.STRAIGHT, 1),
            new PipeProbability(Pipe.pipeType.RIGHT_ANGLE, 2),
            new PipeProbability(Pipe.pipeType.TEE, 2),
            new PipeProbability(Pipe.pipeType.CAP, 1)
    };

    private Pipe[] pipes = new Pipe[bankSize];

    private Pipe activePipe = null;

    private static Random random = new Random(System.nanoTime());

    private transient Context context;

    private transient Paint bankPaint;

    private float pipeDim;

    private float spacing;

    private boolean horizontal;

    public PipeBank(Context context) {
        this.context = context;

        bankPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bankPaint.setColor(Color.argb(90, 0, 100, 0));

        for (int i = 0; i < bankSize; i++) {
            if (pipes[i] == null) {
                pipes[i] = getRandomPipe();
            }
        }
    }

    private Pipe getRandomPipe() {
        int probTotal = 0;

        for (PipeProbability pair : relativePipeProbs) {
            probTotal += pair.relativeProb;
        }

        int index = random.nextInt(probTotal);
        int sum = 0;
        int i = 0;

        while (sum <= index) {
            sum = sum + relativePipeProbs[i++].relativeProb;
        }

        return new Pipe(context, relativePipeProbs[i - 1].type);
    }

    public void setActivePipe(Pipe active) {
        if (active == null) {
            for (int i = 0; i < pipes.length; i++) {
                if (pipes[i] == activePipe) {
                    pipes[i] = null;
                    pipes[i] = getRandomPipe();
                }
            }
        }
        activePipe = active;
    }

    public Pipe hitPipe(float xpos, float ypos) {

        activePipe = null;

        float pos = xpos;
        if (!horizontal) {
            pos = ypos;
        }

        int section = (int) (pos / (spacing + pipeDim));
        if ((section < bankSize) && (pos % (spacing + pipeDim) > spacing)) {
            Log.i("Hit Pipe", "You hit pipe " + section + " in the pipe bank.");
            return pipes[section];
        }

        return null;
    }

    public void draw(Canvas canvas, float width, float height, float blockSize) {

        canvas.drawRect(0f, 0f, width, height, bankPaint);

        float spacingX, spacingY, scale;

        if (width >= height) {
            horizontal = true;
            pipeDim = width / (bankSize + 2);
            spacing = spacingX = 2 * pipeDim / (bankSize + 1);
            spacingX += (pipeDim / 2);
            scale = pipeDim < height ? pipeDim / blockSize : height / blockSize;
            spacingY = height / 2;

            for (int i = 0; i < bankSize; i++) {
                if ((pipes[i] == null)) {
                    pipes[i] = getRandomPipe();
                }

                canvas.save();
                canvas.translate((pipeDim / 2) * i + spacingX * (i + 1), spacingY);
                canvas.scale(scale, scale);

                if (pipes[i] != activePipe) {
                    pipes[i].resetPipe();
                    pipes[i].draw(canvas);
                }

                canvas.restore();
            }
        } else {
            horizontal = false;
            pipeDim = height / (bankSize + 2);
            spacing = spacingY = 2 * pipeDim / (bankSize + 1);
            spacingY += (pipeDim / 2);
            scale = pipeDim < width ? pipeDim / blockSize : height / blockSize;
            spacingX = width / 2;

            for (int i = 0; i < bankSize; i++) {
                if (pipes[i] == null) {
                    pipes[i] = getRandomPipe();
                }

                canvas.save();
                canvas.translate(spacingX, (pipeDim / 2) * i + spacingY * (i + 1));
                canvas.scale(scale, scale);
                if (pipes[i] != activePipe) {
                    pipes[i].setLocation(0, 0);
                    pipes[i].draw(canvas);
                }

                canvas.restore();
            }
        }
    }

    private static class PipeProbability implements Serializable {
        public Pipe.pipeType type;
        public int relativeProb;

        public PipeProbability(Pipe.pipeType typ, int prob) {
            type = typ;
            relativeProb = prob;
        }
    }
}
