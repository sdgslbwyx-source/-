package com.example.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder holder;
    private Thread gameThread;
    private boolean running = false;

    // Game elements
    private int screenWidth, screenHeight;
    private int gridSize = 20;
    private int gridWidth, gridHeight;
    private int cellSize;

    private List<Point> snake;
    private Point food;
    private int direction = 1; // 0=up, 1=right, 2=down, 3=left
    private int nextDirection = 1;
    private int score = 0;
    private boolean gameOver = false;

    private Paint snakePaint, foodPaint, backgroundPaint, textPaint;
    private Random random = new Random();

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);

        initPaints();
    }

    private void initPaints() {
        snakePaint = new Paint();
        snakePaint.setColor(Color.GREEN);
        snakePaint.setStyle(Paint.Style.FILL);

        foodPaint = new Paint();
        foodPaint.setColor(Color.RED);
        foodPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);
        backgroundPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenWidth = getWidth();
        screenHeight = getHeight();

        // Calculate grid dimensions
        cellSize = screenWidth / gridSize;
        gridWidth = screenWidth / cellSize;
        gridHeight = screenHeight / cellSize;

        initGame();

        // Start game thread
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Not used
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initGame() {
        snake = new ArrayList<>();
        // Start with 3 segments in the middle
        int startX = gridWidth / 2;
        int startY = gridHeight / 2;
        snake.add(new Point(startX, startY));
        snake.add(new Point(startX - 1, startY));
        snake.add(new Point(startX - 2, startY));

        direction = 1;
        nextDirection = 1;
        score = 0;
        gameOver = false;

        generateFood();
    }

    private void generateFood() {
        int x, y;
        boolean onSnake;
        do {
            x = random.nextInt(gridWidth);
            y = random.nextInt(gridHeight);
            onSnake = false;
            for (Point segment : snake) {
                if (segment.x == x && segment.y == y) {
                    onSnake = true;
                    break;
                }
            }
        } while (onSnake);

        food = new Point(x, y);
    }

    private void update() {
        if (gameOver) return;

        // Update direction
        direction = nextDirection;

        // Get head position
        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);

        // Move head based on direction
        switch (direction) {
            case 0: // up
                newHead.y--;
                break;
            case 1: // right
                newHead.x++;
                break;
            case 2: // down
                newHead.y++;
                break;
            case 3: // left
                newHead.x--;
                break;
        }

        // Check collision with walls
        if (newHead.x < 0 || newHead.x >= gridWidth ||
            newHead.y < 0 || newHead.y >= gridHeight) {
            gameOver = true;
            return;
        }

        // Check collision with self
        for (Point segment : snake) {
            if (segment.x == newHead.x && segment.y == newHead.y) {
                gameOver = true;
                return;
            }
        }

        // Add new head
        snake.add(0, newHead);

        // Check if food eaten
        if (newHead.x == food.x && newHead.y == food.y) {
            score += 10;
            generateFood();
        } else {
            // Remove tail if no food eaten
            snake.remove(snake.size() - 1);
        }
    }

    private void draw(Canvas canvas) {
        canvas.drawRect(0, 0, screenWidth, screenHeight, backgroundPaint);

        if (gameOver) {
            canvas.drawText("Game Over! Score: " + score,
                screenWidth / 2, screenHeight / 2, textPaint);
            canvas.drawText("Touch to restart",
                screenWidth / 2, screenHeight / 2 + 50, textPaint);
            return;
        }

        // Draw snake
        for (Point segment : snake) {
            int left = segment.x * cellSize;
            int top = segment.y * cellSize;
            int right = left + cellSize;
            int bottom = top + cellSize;
            canvas.drawRect(left, top, right, bottom, snakePaint);
        }

        // Draw food
        int left = food.x * cellSize;
        int top = food.y * cellSize;
        int right = left + cellSize;
        int bottom = top + cellSize;
        canvas.drawRect(left, top, right, bottom, foodPaint);

        // Draw score
        canvas.drawText("Score: " + score, screenWidth / 2, 50, textPaint);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerUpdate = 1000000000.0 / 10.0; // 10 updates per second

        while (running) {
            long now = System.nanoTime();
            long elapsed = now - lastTime;

            if (elapsed > nsPerUpdate) {
                update();
                lastTime = now;

                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    if (canvas != null) {
                        draw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }

            // Sleep a bit to avoid consuming too much CPU
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (gameOver) {
                initGame();
                return true;
            }

            // Calculate touch direction
            float x = event.getX();
            float y = event.getY();

            // Determine swipe direction based on touch position relative to center
            int centerX = screenWidth / 2;
            int centerY = screenHeight / 2;

            float dx = x - centerX;
            float dy = y - centerY;

            // Change direction based on swipe
            if (Math.abs(dx) > Math.abs(dy)) {
                // Horizontal swipe
                if (dx > 0 && direction != 3) {
                    nextDirection = 1; // right
                } else if (dx < 0 && direction != 1) {
                    nextDirection = 3; // left
                }
            } else {
                // Vertical swipe
                if (dy > 0 && direction != 0) {
                    nextDirection = 2; // down
                } else if (dy < 0 && direction != 2) {
                    nextDirection = 0; // up
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        if (!running) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }
}