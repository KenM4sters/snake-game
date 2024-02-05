import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.List;

public class SnakeGame extends JPanel implements ActionListener {
    private final int width;
    private final int height;
    private final int cellSize;
    private static final int FRAME_RATE = 20;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private Direction direction = Direction.RIGHT;
    private Direction newDirection = Direction.RIGHT;
    private final List<GamePoint> snake = new ArrayList<>();
    public SnakeGame(final int width, final int height) {
        super();
        this.width = width;
        this.height = height;
        this.cellSize = width / (FRAME_RATE * 2);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);

        snake.addFirst(new GamePoint(500, 300));
        snake.addFirst(new GamePoint(600, 300));
        resetGameData();
    }

    public void startGame() {
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyEvent(e);
            }
        });
        new Timer(1000 / FRAME_RATE, this).start();
    }

    private void handleKeyEvent(final KeyEvent e) {
        if(!gameStarted) {
            if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                gameStarted = true;
            }
        } else {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(!gameStarted) {
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(30F));
            int currentHeight = height / 4;
            final var g2D = (Graphics2D) g;
            final var frc = g2D.getFontRenderContext();
            final String message = "press SPACE_BAR to begin game";
            for(final var line : message.split("\n")) {
                final var layout = new TextLayout(line, g.getFont(), frc);
                final var bounds = layout.getBounds();
                final var targetWidth = (float) (width - bounds.getWidth()) / 2;
                layout.draw(g2D, targetWidth, currentHeight);
                currentHeight += g.getFontMetrics().getHeight();
            }
        } else {
            g.setColor(Color.GREEN);
            for(final var point : snake) {
                g.fillRect(point.x, point.y, cellSize, cellSize);
            }
        }
    }

    private void resetGameData() {
        snake.clear();
        snake.add(new GamePoint(width / 2, height / 2));
    }

    private void generateFood() {}

    private void moveSnake() {
        final GamePoint currentHead = snake.getFirst();
        final GamePoint newHead = switch(direction) {
            case UP -> new GamePoint(currentHead.x, currentHead.y - cellSize);
            case DOWN -> new GamePoint(currentHead.x, currentHead.y + cellSize);
            case RIGHT -> new GamePoint(currentHead.x + cellSize, currentHead.y);
            case LEFT -> new GamePoint(currentHead.x - cellSize, currentHead.y);
        };

        snake.addFirst(newHead);

        if(checkCollision()) {
            gameOver = true;
        }

        direction = newDirection;
        snake.removeLast();
    }

    private boolean checkCollision() {
        final GamePoint head = snake.getFirst();
        final var invalidWidth = (head.x < 0) || (head.x >= width - cellSize);
        final var invalidHeight = (head.y < 0) || (head.y >= height - cellSize);
        return (invalidWidth || invalidHeight);

    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if(gameStarted && !gameOver) {
            System.out.println("playing");
            moveSnake();
        }
        repaint();
    }

    private record GamePoint(int x, int y) {}
    private enum Direction {
        UP, DOWN, RIGHT, LEFT
    }
}
