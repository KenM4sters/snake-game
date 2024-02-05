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
    private final List<GamePoint> snake = new ArrayList<>();
    public SnakeGame(final int width, final int height) {
        super();
        this.width = width;
        this.height = height;
        this.cellSize = width / (FRAME_RATE * 2);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
    }

    public void startGame() {
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameStarted = true;
                }
            }
        });
        new Timer(1000 / FRAME_RATE, this).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        resetGameData();
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

    private void moveSnake() {
        final GamePoint currentHead = snake.getFirst();
        final GamePoint newHead = new GamePoint(currentHead.x + cellSize, currentHead.y);
        snake.addFirst(newHead);
        snake.removeLast();
        for(final var point : snake) {
            System.out.println(point.x);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if(gameStarted) {
            moveSnake();
        }
        repaint();
    }

    private record GamePoint(int x, int y) {}
}
