import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

/**
 * Scene 8: Blaze Fight in the Nether.
 * Shows Steve fighting a Blaze to get a Blaze Rod.
 */
public class BlazeFightScene extends Scene {
    private final Random random = new Random(850);

    public BlazeFightScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Nether Background
        for (int i = 0; i < height; i += 10) {
            double t = (double) i / height;
            g2d.setColor(DrawUtils.lerpColor(DrawUtils.NETHER_RED, new Color(50, 10, 10), t));
            g2d.fillRect(0, i, width, 10);
        }

        // Nether Fortress Bridge (Ground)
        int groundY = 400;
        for (int x = 0; x < width; x += DrawUtils.BLOCK_SIZE) {
            for (int y = groundY; y < height; y += DrawUtils.BLOCK_SIZE) {
                DrawUtils.drawBlock(g2d, x, y, DrawUtils.BLOCK_SIZE, new Color(60, 20, 20)); // Nether Brick
            }
        }
        
        // Fortress Pillars
        for (int y = 100; y < groundY; y += DrawUtils.BLOCK_SIZE) {
            DrawUtils.drawBlock(g2d, 100, y, DrawUtils.BLOCK_SIZE, new Color(50, 15, 15));
            DrawUtils.drawBlock(g2d, 500, y, DrawUtils.BLOCK_SIZE, new Color(50, 15, 15));
        }

        // Steve
        int steveX = 150 + (int)(progress < 0.3 ? progress * 200 : 60);
        int steveY = groundY - 80;
        
        // Blaze
        int blazeX = 400;
        int blazeY = groundY - 120 + (int)(Math.sin(progress * Math.PI * 8) * 20);
        
        // Battle Sequence
        if (progress < 0.6) {
            // Blaze alive
            drawBlaze(g2d, blazeX, blazeY, progress);
            
            // Blaze shoots fireball
            if (progress > 0.2 && progress < 0.5) {
                double fbP = (progress - 0.2) / 0.3;
                int fbX = blazeX - (int)(fbP * 300);
                int fbY = blazeY + 20 + (int)(Math.sin(fbP * Math.PI) * 50);
                MidpointDrawing.fillCircleGlow(g2d, fbX, fbY, 15, Color.YELLOW, DrawUtils.LAVA_ORANGE);
            }
            
            // Steve attacks
            double swing = 0;
            if (progress > 0.4 && progress < 0.6) {
                swing = Math.sin((progress - 0.4) * 10 * Math.PI) * 45;
                steveX += (int)(Math.sin((progress - 0.4) * 10 * Math.PI) * 20); // dodge/lunge
            }
            DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 2, true, "sword", swing);
            
        } else if (progress < 0.8) {
            // Blaze dies (particles)
            for (int i = 0; i < 15; i++) {
                int px = blazeX + (int)(random.nextGaussian() * 30 * (progress - 0.6) * 10);
                int py = blazeY + (int)(random.nextGaussian() * 30 * (progress - 0.6) * 10);
                g2d.setColor(random.nextBoolean() ? DrawUtils.LAVA_ORANGE : Color.BLACK);
                g2d.fillRect(px, py, 10, 10);
            }
            DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 2, true, "sword", 0);
        } else {
            // Drop Blaze Rod
            int rodX = blazeX;
            int rodY = groundY - 20;
            g2d.setColor(DrawUtils.GOLD_YELLOW);
            g2d.fillRect(rodX, rodY, 30, 8);
            g2d.setColor(DrawUtils.LAVA_ORANGE);
            g2d.drawRect(rodX, rodY, 30, 8);
            
            // Steve picks it up
            steveX += (int)((progress - 0.8) * 500);
            DrawUtils.drawSteve(g2d, steveX, steveY, 2, true);
        }

        // HUD
        DrawUtils.drawHUD(g2d, width, 8, 10, 8, 45);
    }
    
    private void drawBlaze(Graphics2D g2d, int x, int y, double progress) {
        // Head
        DrawUtils.drawBlock(g2d, x, y, 30, DrawUtils.GOLD_YELLOW);
        // Eyes
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x + 5, y + 10, 6, 6);
        g2d.fillRect(x + 15, y + 10, 6, 6);
        
        // Rods circling
        double angleBase = progress * Math.PI * 4;
        for (int i = 0; i < 6; i++) {
            double angle = angleBase + (i * Math.PI / 3);
            int rodX = x + 15 + (int)(Math.cos(angle) * 30);
            int rodY = y + 20 + (int)(Math.sin(angle) * 10) + (i%2==0? -10 : 10);
            
            g2d.setColor(DrawUtils.GOLD_YELLOW);
            g2d.fillRect(rodX - 4, rodY - 15, 8, 30);
            g2d.setColor(DrawUtils.LAVA_ORANGE);
            g2d.drawRect(rodX - 4, rodY - 15, 8, 30);
        }
    }
}
