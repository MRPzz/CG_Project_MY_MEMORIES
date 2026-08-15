import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AnimationPanel extends JPanel implements ActionListener {

    private final List<Scene> scenes;
    private final Timer timer;
    private long startTime = -1;
    private final int TRANSITION_DURATION = 200;

    public AnimationPanel() {
        scenes = new ArrayList<>();
        scenes.add(new TitleScene("Title", 4000));
        scenes.add(new CreateWorldScene("CreateWorld", 4000));
        scenes.add(new FirstDayScene("FirstDay", 5000));
        scenes.add(new FirstNightScene("FirstNight", 5000));
        scenes.add(new MiningScene("Mining", 5000));
        scenes.add(new BuildHomeScene("BuildHome", 5000));
        scenes.add(new NetherPortalScene("NetherPortal", 5000));
        scenes.add(new BlazeFightScene("BlazeFight", 5000));
        scenes.add(new EyeOfEnderScene("EyeOfEnder", 7000));
        scenes.add(new DragonFightScene("DragonFight", 7000));
        scenes.add(new CreditsScene("Credits", 8000));
        timer = new Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        
        int currentSceneIndex = 0;
        long timeAccumulator = 0;
        
        while (currentSceneIndex < scenes.size()) {
            int duration = scenes.get(currentSceneIndex).getDurationMs();
            if (elapsed < timeAccumulator + duration) {
                break;
            }
            timeAccumulator += duration;
            currentSceneIndex++;
        }

        if (currentSceneIndex >= scenes.size()) {
            // Animation finished, just render the last frame of the last scene
            if (!scenes.isEmpty()) {
                scenes.get(scenes.size() - 1).render(g2d, getWidth(), getHeight(), 1.0);
            }
            return;
        }

        Scene currentScene = scenes.get(currentSceneIndex);
        long sceneElapsed = elapsed - timeAccumulator;
        double progress = (double) sceneElapsed / currentScene.getDurationMs();
        
        // Check for transition
        if (sceneElapsed > currentScene.getDurationMs() - TRANSITION_DURATION && currentSceneIndex < scenes.size() - 1) {
            // Draw current scene
            currentScene.render(g2d, getWidth(), getHeight(), progress);
            
            // Draw fade overlay
            double transitionProgress = (double) (sceneElapsed - (currentScene.getDurationMs() - TRANSITION_DURATION)) / TRANSITION_DURATION;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) transitionProgress));
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        } else {
            currentScene.render(g2d, getWidth(), getHeight(), progress);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
