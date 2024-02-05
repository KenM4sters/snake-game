import javax.swing.*;

public class Main {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    public static void main(String[] args) {
        final JFrame frame = new JFrame("Snake_Game");
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);


    }
}