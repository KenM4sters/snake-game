import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private final int width;
    private final int height;
    // Size of individual 'cells' for the snake and food items
    private final int cellSize;
    // Variable to store the high score across games
    private int highScore;
    // Instantiating Random class to generate a random integer for placing food items
    private static final Random random = new Random();
    // Set frame rate (controls size of cells)
    private static final int FRAME_RATE = 20;
    // Variables to dictate the current game state
    private boolean gameStarted = false;
    private boolean gameOver = false;
    // Controls which direction the snake moves in
    private Direction direction = Direction.RIGHT;
    private Direction newDirection = Direction.RIGHT;
    // List of GamePoints (individual squares) to make up the snake's head and body
    private final List<GamePoint> snake = new ArrayList<>();
    // Used to check whether the snake's head occupies the same cell as the food item
    private GamePoint food;
    public SnakeGame(final int width, final int height) {
        super();
        this.width = width;
        this.height = height;
        this.cellSize = width / (FRAME_RATE * 2);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        // Clears the screen
        resetGameData();
    }

    public void startGame() {
        // Used to allow the window to capture key events (keys being pressed/held/released)
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
        // Key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyEvent(e);
            }
        });
        // Timer to listen to keys
        new Timer(1000 / FRAME_RATE, this).start();
    }

    private void handleKeyEvent(final KeyEvent e) {
        // If the game hasn't started yet (returns false)
        // then we want to start it if the space_bar is pressed
        if(!gameStarted) {
            if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                gameStarted = true;
            }
        } else if(gameOver) {
            // If the game is over (snake moved into the walls or itself) we want to restart the game
            // if the player presses the space_bar
            if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                gameStarted = false;
                gameOver = false;
                resetGameData();
            }
        } else {
            // Changes the direction variable to either UP, DOWN, RIGHT or LEFT, but only does so
            // if the current direction is not the opposite of the new direction (can't turn 180deg)
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if(direction != Direction.DOWN)
                        newDirection = Direction.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != Direction.UP)
                        newDirection = Direction.DOWN;
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != Direction.LEFT)
                        newDirection = Direction.RIGHT;
                    break;
                case KeyEvent.VK_LEFT:
                    if(direction != Direction.RIGHT)
                        newDirection = Direction.LEFT;
                    break;
            }
        }
    }

    // Function that prints all the graphics (squares in this case) to the screen
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Initially generate some food, although it isn't rendered to the screen until
        // the game has begun
        if(!gameStarted) {
            generateFood();
            printMessage(g, "press SPACE_BAR to start");
        } else {
            // Set color for food items
            g.setColor(Color.cyan);
            // Draw a square for the food item
            g.fillRect(food.x, food.y, cellSize, cellSize);
            // Set color for the snake
            Color snakeColor = Color.GREEN;
            // Loop to draw each element in the snake list, and handle the color that each square
            // should be
            for(final var point : snake) {
                g.setColor(snakeColor);
                g.fillRect(point.x, point.y, cellSize, cellSize);
                // Gradually decrease the opacity of the next square, with a bottom limit
                // to prevent the tail squares from eventually being invisible
                final int newGreen = (int) Math.max(0.5, Math.round(snakeColor.getGreen() * 0.90));
                snakeColor = new Color(0, newGreen, 0);
            }
            if(gameOver) {
                // Declare and set the current score to the size of the snake list
                // (10 elements or squares = 10 score)
                final int currentScore = snake.size();
                // if the current score is higher than the high score, then set the high score
                // to that current score
                if(currentScore > highScore) {
                    highScore = currentScore;
                }
                // calling method to handle printing text to the screen
                // (see at the bottom of this class)
                printMessage(g, "Current Score:     " + currentScore +
                        "\n High Score:     " + highScore +
                        "\n Press SPACE_BAR to play again"
                );
            }
        }
    }
    // Method to clear the screen and add a single element to the snake list
    private void resetGameData() {
        snake.clear();
        snake.add(new GamePoint(width / 2, height / 2));
    }
    //Method to generate the food items
    private void generateFood() {
        // 1 food item is initially generated in paintComponent(), and when the snake's head occupies
        // the same cell as the food item, we want to generate a new food item
        do {
            food = new GamePoint(random.nextInt(width / cellSize) * cellSize,
                    random.nextInt(height / cellSize) * cellSize);
        } while(snake.contains(food));
    }
    // Method to move the snake over time in the direction set by the variable 'direction'
    private void moveSnake() {
        // Get the first element in the snake list
        final GamePoint currentHead = snake.getFirst();
        // Declare a new element in the list, with its position based on the positions of the
        // current head, only offset depending on the value of 'direction'
        // (either UP, DOWN, RIGHT or LEFT)
        final GamePoint newHead = switch(direction) {
            case UP -> new GamePoint(currentHead.x, currentHead.y - cellSize);
            case DOWN -> new GamePoint(currentHead.x, currentHead.y + cellSize);
            case RIGHT -> new GamePoint(currentHead.x + cellSize, currentHead.y);
            case LEFT -> new GamePoint(currentHead.x - cellSize, currentHead.y);
        };
        // add the newHead to the list
        snake.addFirst(newHead);

        // Call generateFood() if the head of the snake occupies the same cell as a food item
        if(newHead.equals(food)) {
            generateFood();
        } else if(checkCollision()) {
            // if checkCollision() returns true, then the snake has collided with either itself
            // or the boundaries of the window, and we should end the game
            gameOver = true;
            snake.removeFirst();
        } else {
            snake.removeLast();
        }
        // Keep setting the current direction to the new direction since the new direction will
        // change based on user input
        direction = newDirection;
    }
    // Method to check collisions
    private boolean checkCollision() {
        // Get the head of the snake
        final GamePoint head = snake.getFirst();
        // Declare two boolean variables that are true if the position of the head of the snake
        // is equal to goes beyond the width and height of the window
        final boolean invalidWidth = (head.x < 0) || (head.x >= width );
        final boolean invalidHeight = (head.y < 0) || (head.y >= height );
        if(invalidWidth || invalidHeight) {
            return true;
        }
        // Instantiate a new HashSet of the elements in the snake list, and if either of those
        // elements are the same, then the snake has collided with itself and this method should
        // return true
        return snake.size() != new HashSet<>(snake).size();
    }
    // Method to render all the graphics each frame
    @Override
    public void actionPerformed(final ActionEvent e) {
        if(gameStarted && !gameOver) {
            moveSnake();
        }
        repaint();
    }

    // Method to print text to the window
    private void printMessage(final Graphics graphics, final String message) {
        // Set the color of the text
        graphics.setColor(Color.WHITE);
        // Set the font
        graphics.setFont(graphics.getFont().deriveFont(30F));
        int currentHeight = height / 3;
        final var graphics2D = (Graphics2D) graphics;
        final var frc = graphics2D.getFontRenderContext();
        // Loop through each single line of the message, center it in the window and draw it
        for (final var line : message.split("\n")) {
            final var layout = new TextLayout(line, graphics.getFont(), frc);
            final var bounds = layout.getBounds();
            final var targetWidth = (float) (width - bounds.getWidth()) / 2;
            layout.draw(graphics2D, targetWidth, currentHeight);
            currentHeight += graphics.getFontMetrics().getHeight();
        }
    }

    // Record for each square in the window (used for the snake's body and food items)
    private record GamePoint(int x, int y) {}
    // Enum for the possible directions of the snake's movement
    private enum Direction {
        UP, DOWN, RIGHT, LEFT
    }
}
