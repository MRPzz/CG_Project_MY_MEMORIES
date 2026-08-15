import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class FirstNightScene extends Scene {

    public FirstNightScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        // Time transitions to night
        double timeOfDay = 0.15 + (progress * 0.35); // goes to 0.5 (night)
        DrawUtils.drawSky(g2d, width, height, timeOfDay);

        // Sun goes down
        int sunY = (int) (100 + (progress / 0.3) * 400);
        if (progress < 0.3) {
            MidpointDrawing.fillCircleGlow(g2d, 100, sunY, 40, Color.WHITE, DrawUtils.SUNSET_ORANGE);
            MidpointDrawing.fillCircle(g2d, 100, sunY, 30, Color.YELLOW);
        }

        // Moon rises
        if (progress > 0.2) {
            int moonY = (int) (400 - ((progress - 0.2) / 0.8) * 300);
            MidpointDrawing.fillCircle(g2d, 500, moonY, 25, Color.LIGHT_GRAY);
        }

        // Stars
        if (progress > 0.3) {
            DrawUtils.drawStars(g2d, width, height, 50, 12345, progress);
        }

        int groundY = 450;
        DrawUtils.drawGround(g2d, width, height, groundY);

        // Dirt Hut
        int hutX = 150;
        int hutY = groundY - 60;
        
        if (progress > 0.1) {
            // Build hut gradually
            int blocksBuilt = (int) ((progress - 0.1) / 0.3 * 10);
            if (blocksBuilt > 10) blocksBuilt = 10;
            
            // Very simple hut logic
            for (int i = 0; i < blocksBuilt; i++) {
                if (progress < 0.75 || i < 4) { // Don't draw some blocks after explosion
                    int bx = hutX + (i % 3) * DrawUtils.BLOCK_SIZE;
                    int by = hutY + (2 - i / 3) * DrawUtils.BLOCK_SIZE;
                    DrawUtils.drawBlock(g2d, bx, by, DrawUtils.BLOCK_SIZE, DrawUtils.DIRT_BROWN);
                }
            }
        }

        // Steve inside hut
        if (progress >= 0.4) {
            DrawUtils.drawSteve(g2d, hutX + 20, groundY - 40, 1, true);
        }

        // Zombie walk past in background
        if (progress >= 0.5 && progress <= 0.7) {
            int zombieX = (int) (500 - ((progress - 0.5) / 0.2) * 600);
            g2d.setColor(DrawUtils.CREEPER_DARK); // using as dark green silhouette
            g2d.fillRect(zombieX, groundY - 40, 20, 40);
        }

        // Creeper approach
        int creeperX = 600;
        if (progress >= 0.6 && progress < 0.75) {
            creeperX = (int) (600 - ((progress - 0.6) / 0.15) * 400);
            // Draw creeper
            g2d.setColor(DrawUtils.CREEPER_GREEN);
            g2d.fillRect(creeperX, groundY - 40, 20, 40);
            g2d.setColor(Color.BLACK);
            // Face
            g2d.fillRect(creeperX + 4, groundY - 35, 4, 4);
            g2d.fillRect(creeperX + 12, groundY - 35, 4, 4);
            g2d.fillRect(creeperX + 8, groundY - 30, 4, 6);
        }

        // Explosion!
        if (progress >= 0.75 && progress < 0.8) {
            // Screen shake
            Random r = new Random();
            g2d.translate(r.nextInt(10) - 5, r.nextInt(10) - 5);
            
            // Flash
            g2d.setColor(new Color(255, 255, 255, 200));
            g2d.fillRect(0, 0, width, height);
            
            // Explosion particles
            for (int i = 0; i < 30; i++) {
                int px = creeperX + r.nextInt(100) - 50;
                int py = groundY - 20 + r.nextInt(100) - 50;
                g2d.setColor(DrawUtils.SUNSET_ORANGE);
                g2d.fillRect(px, py, 5, 5);
            }
        }

        // HUD updating
        int hearts = 10;
        if (progress >= 0.75) hearts = 4;
        DrawUtils.drawHUD(g2d, width, hearts, 10, 8, 20);
    }
}
