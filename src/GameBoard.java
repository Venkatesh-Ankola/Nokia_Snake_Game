import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GameBoard extends JPanel implements ActionListener {
    static final int BOARD_WIDTH = 600;
    private int highestScore = 0;
    private boolean pausedState = false;
    static final int BOARD_HEIGHT = 600;
    static final int TILE_SIZE = 25;
    static final int TOTAL_TILES = (BOARD_WIDTH * BOARD_HEIGHT) / TILE_SIZE;
    static final int DELAY_MILLIS = 90;
    final int[] xCoords = new int[TOTAL_TILES];
    final int[] yCoords = new int[TOTAL_TILES];
    int snakeLength = 6;
    int fruitsEaten;
    int fruitX;
    int fruitY;
    char moveDirection = 'R';
    boolean gameRunning = false;
    Timer gameTimer;
    Random randomGen;

    private void togglePauseGame() {
        pausedState = !pausedState;
        if (pausedState) {
            gameTimer.stop();
        } else {
            gameTimer.start();
        }
    }

    GameBoard() {
        randomGen = new Random();
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyHandler());
        startGame();
    }

    public void startGame() {
        generateFruitPosition();
        gameRunning = true;
        gameTimer = new Timer(DELAY_MILLIS, this);
        gameTimer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGame(g);
    }

    public void drawGame(Graphics g) {
        if (gameRunning) {
            if (pausedState) {
                g.setColor(new Color(72, 45, 189));
                g.setFont(new Font("Ink Free", Font.BOLD, 75));
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString("Paused", (BOARD_WIDTH - metrics.stringWidth("Paused")) / 2, BOARD_HEIGHT / 2);
            }

            g.setColor(Color.red);
            g.fillOval(fruitX, fruitY, TILE_SIZE, TILE_SIZE);

            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(xCoords[i], yCoords[i], TILE_SIZE, TILE_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(xCoords[i], yCoords[i], TILE_SIZE, TILE_SIZE);
                }
            }
            g.setColor(new Color(72, 45, 189));
            g.setFont(new Font("Ink Free", Font.BOLD, 75));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + fruitsEaten, (BOARD_WIDTH - metrics.stringWidth("Score: " + fruitsEaten)) / 2, g.getFont().getSize());
        } else {
            gameOverScreen(g);
        }
    }

    public void generateFruitPosition() {
        fruitX = randomGen.nextInt((int) (BOARD_WIDTH / TILE_SIZE)) * TILE_SIZE;
        fruitY = randomGen.nextInt((int) (BOARD_HEIGHT / TILE_SIZE)) * TILE_SIZE;
    }

    public void moveSnake() {
        for (int i = snakeLength; i > 0; i--) {
            xCoords[i] = xCoords[i - 1];
            yCoords[i] = yCoords[i - 1];
        }
        switch (moveDirection) {
            case 'U':
                yCoords[0] = yCoords[0] - TILE_SIZE;
                break;
            case 'D':
                yCoords[0] = yCoords[0] + TILE_SIZE;
                break;
            case 'L':
                xCoords[0] = xCoords[0] - TILE_SIZE;
                break;
            case 'R':
                xCoords[0] = xCoords[0] + TILE_SIZE;
                break;
        }
    }

    public void checkFruitEaten() {
        if ((xCoords[0] == fruitX) && (yCoords[0] == fruitY)) {
            snakeLength++;
            fruitsEaten++;
            if (fruitsEaten > highestScore) {
                highestScore = fruitsEaten;
            }
            generateFruitPosition();
        }
    }

    public void checkCollisions() {
        //head collision detection
        for (int i = snakeLength; i > 0; i--) {
            if ((xCoords[0] == xCoords[i]) && (yCoords[0] == yCoords[i])) {
                gameRunning = false;
            }
        }
        //check if head touches left border
        if (xCoords[0] < 0) {
            gameRunning = false;
        }
        //check if head touches right border
        if (xCoords[0] > BOARD_WIDTH) {
            gameRunning = false;
        }
        //check if head touches top border
        if (yCoords[0] < 0) {
            gameRunning = false;
        }
        //check if head touches bottom border
        if (yCoords[0] > BOARD_HEIGHT) {
            gameRunning = false;
        }
        if (!gameRunning) {
            gameTimer.stop();
        }
    }

    public void gameOverScreen(Graphics g) {
        g.setColor(new Color(72, 45, 189));
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + fruitsEaten, (BOARD_WIDTH - metrics2.stringWidth("Score: " + fruitsEaten)) / 2, g.getFont().getSize());

        // Game Over Text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (BOARD_WIDTH - metrics.stringWidth("Game Over")) / 2, BOARD_HEIGHT / 2);

        // Display best score
        g.setColor(new Color(72, 45, 189));
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Best Score: " + highestScore, (BOARD_WIDTH - metrics3.stringWidth("Best Score: " + highestScore)) / 2, BOARD_HEIGHT / 2 + 100);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning && !pausedState) {
            moveSnake();
            checkFruitEaten();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (moveDirection != 'R') {
                        moveDirection = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (moveDirection != 'L') {
                        moveDirection = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (moveDirection != 'D') {
                        moveDirection = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (moveDirection != 'U') {
                        moveDirection = 'D';
                    }
                    break;
                case KeyEvent.VK_P:
                    togglePauseGame();
                    break;
                case KeyEvent.VK_ENTER:
                    restartGame();
                    break;
            }
        }
    }

    private void restartGame() {
        gameRunning = true;
        snakeLength = 6;
        fruitsEaten = 0;
        moveDirection = 'R';

        for (int i = 0; i < TOTAL_TILES; i++) {
            xCoords[i] = 0;
            yCoords[i] = 0;
        }

        generateFruitPosition();

        gameTimer.restart();
    }
}