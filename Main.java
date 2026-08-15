import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MINECRAFT");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 600);
            frame.setResizable(false);
            
            AnimationPanel panel = new AnimationPanel();
            frame.add(panel);
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
