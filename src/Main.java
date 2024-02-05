import javax.swing.*;

/*
*  Main Java Class
*/

public class Main {
    // Setting sizes of window
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    public static void main(String[] args) {
        // Java FX setup
        final JFrame frame = new JFrame("Snake_Game");
        frame.setSize(WIDTH, HEIGHT);
        // Instantiating SnakeGame class with all the game logic
        SnakeGame game = new SnakeGame(WIDTH, HEIGHT);
        frame.add(game);
        // Center the window
        frame.setLocationRelativeTo(null);
        // Close the window on exit
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.pack();
        // Init all game methods
        game.startGame();
    }
}