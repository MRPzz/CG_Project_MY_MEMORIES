import java.awt.*;
import java.awt.geom.*;

/**
 * Scene 9: Eye of Ender.
 * Steve is in the Overworld, throws the Eye of Ender, and jumps into the End Portal.
 */
public class EyeOfEnderScene extends Scene {

    public EyeOfEnderScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (progress < 0.6) {
            // Part 1: Overworld - Throwing Eye of Ender
            DrawUtils.drawSky(g2d, width, height, 0.4); // Afternoon
            DrawUtils.drawGround(g2d, width, height, 420);
            
            // Steve
            int steveX = 100 + (int)(progress * 150);
            DrawUtils.drawSteve(g2d, steveX, 340, 2, true);
            
            // Eye of Ender Path
            if (progress > 0.1 && progress < 0.5) {
                double eyeP = (progress - 0.1) / 0.4;
                int eyeX = steveX + 20 + (int)(eyeP * 300);
                // Parabolic arc for throwing
                int eyeY = 320 - (int)(Math.sin(eyeP * Math.PI) * 150) - (int)(eyeP * 50);
                
                // Draw Eye of Ender
                MidpointDrawing.fillEllipseGlow(g2d, eyeX, eyeY, 8, 5, DrawUtils.CREEPER_GREEN, DrawUtils.ENDER_PURPLE);
                g2d.setColor(Color.BLACK);
                g2d.fillRect(eyeX - 1, eyeY - 2, 2, 4); // Pupil
                
                // Trail particles
                g2d.setColor(DrawUtils.ENDER_PURPLE);
                g2d.fillRect(eyeX - 10, eyeY + 5, 4, 4);
                g2d.fillRect(eyeX - 20, eyeY, 3, 3);
            }

            // Transition Flash
            if (progress > 0.5) {
                double flash = (progress - 0.5) / 0.1;
                g2d.setColor(new Color(0, 0, 0, (int)(flash * 255)));
                g2d.fillRect(0, 0, width, height);
            }
            
            // HUD
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, 45);
            
        } else {
            // Part 2: Stronghold - Jumping into the End Portal
            // Background
            g2d.setColor(DrawUtils.NIGHT_SKY);
            g2d.fillRect(0, 0, width, height);
            
            // Stone Bricks
            for (int x = 0; x < width; x += DrawUtils.BLOCK_SIZE) {
                for (int y = 0; y < height; y += DrawUtils.BLOCK_SIZE) {
                    if (y > 450 || x < 100 || x > 500) {
                        DrawUtils.drawBlock(g2d, x, y, DrawUtils.BLOCK_SIZE, DrawUtils.STONE_GRAY);
                    }
                }
            }
            
            // Portal Frame (Midpoint Ellipse used to define the portal shape)
            int portalCX = 300;
            int portalCY = 450;
            
            // End Portal blocks
            g2d.setColor(new Color(80, 100, 80)); // End portal frame color
            g2d.fillRect(portalCX - 100, portalCY - 10, 200, 20);
            
            // The actual portal (black/starry circle on the floor)
            // Simulated by drawing an ellipse for perspective
            MidpointDrawing.fillEllipse(g2d, portalCX, portalCY, 80, 20, Color.BLACK);
            // Portal stars
            g2d.setColor(Color.WHITE);
            for(int i=0; i<15; i++) {
                int px = portalCX - 70 + (int)(Math.random() * 140);
                int py = portalCY - 15 + (int)(Math.random() * 30);
                // Simple distance check to keep inside ellipse
                if (Math.pow((px-portalCX)/80.0, 2) + Math.pow((py-portalCY)/20.0, 2) < 1.0) {
                    g2d.fillRect(px, py, 2, 2);
                }
            }
            
            // Steve jumping in
            double jumpP = (progress - 0.6) / 0.4;
            int steveX = 300 - 15; // center
            int steveY = 100 + (int)(jumpP * jumpP * 400); // accelerating fall
            
            // Draw Steve only if not fully submerged
            if (steveY < portalCY + 20) {
                // Steve falls from top
                DrawUtils.drawSteve(g2d, steveX, steveY, 2, true);
            }
            
            // HUD
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, 45);
        }
    }
}
