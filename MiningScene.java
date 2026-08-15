import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

public class MiningScene extends Scene {

    public MiningScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        // Cave Background
        g2d.setColor(new Color(30, 25, 25));
        g2d.fillRect(0, 0, width, height);

        // Stone blocks
        Random r = new Random(42);
        for (int x = 0; x < width; x += DrawUtils.BLOCK_SIZE) {
            for (int y = 0; y < height; y += DrawUtils.BLOCK_SIZE) {
                if (r.nextDouble() > 0.3) {
                    DrawUtils.drawBlock(g2d, x, y, DrawUtils.BLOCK_SIZE, DrawUtils.STONE_GRAY);
                    
                    // Random ores
                    double oreRoll = r.nextDouble();
                    if (oreRoll > 0.95) {
                        g2d.setColor(DrawUtils.COAL_BLACK);
                        g2d.fillRect(x+5, y+5, 10, 10);
                    } else if (oreRoll > 0.90) {
                        g2d.setColor(DrawUtils.IRON_GRAY);
                        g2d.fillRect(x+5, y+5, 10, 10);
                    }
                }
            }
        }

        // Carve out a cave area
        g2d.setColor(new Color(30, 25, 25)); // background color
        g2d.fillOval(50, 100, 500, 400); // simplify with Java2D for cave shape

        // Torch on wall
        int torchX = 150;
        int torchY = 250;
        DrawUtils.drawBlock(g2d, torchX, torchY, 10, DrawUtils.WOOD_BROWN);
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(torchX, torchY - 5, 10, 5);
        
        // Torch Glow (MidpointEllipse)
        MidpointDrawing.fillEllipseGlow(g2d, torchX + 5, torchY, 150, 100, new Color(255, 200, 50, 100), new Color(0, 0, 0, 0));

        // Diamond Ore
        int diamondX = 400;
        int diamondY = 350;
        if (progress < 0.7) {
            DrawUtils.drawBlock(g2d, diamondX, diamondY, DrawUtils.BLOCK_SIZE * 2, DrawUtils.STONE_GRAY);
            g2d.setColor(DrawUtils.DIAMOND_BLUE);
            g2d.fillRect(diamondX + 10, diamondY + 10, 10, 10);
            g2d.fillRect(diamondX + 25, diamondY + 20, 8, 8);
            
            // Pulsing diamond glow
            if (progress >= 0.4) {
                int pulse = (int) (10 * Math.sin(progress * Math.PI * 10));
                MidpointDrawing.fillEllipseGlow(g2d, diamondX + 20, diamondY + 20, 30 + pulse, 30 + pulse, 
                        new Color(80, 220, 235, 100), new Color(0, 0, 0, 0));
            }
        }

        // Steve
        int steveX = 250;
        int steveY = 330;
        double swingAngle = 0;
        
        if (progress < 0.3) {
            swingAngle = Math.sin(progress * 40) * Math.PI / 4;
            steveX = 200 + (int)(progress * 100); // walking right
        } else if (progress < 0.6) {
            steveX = 250;
            if (progress > 0.4) {
                // Noticed diamonds
                g2d.setColor(Color.WHITE);
                g2d.drawString("!", steveX + 10, steveY - 10);
            }
        } else if (progress < 0.7) {
            steveX = 350;
            swingAngle = Math.sin(progress * 50) * Math.PI / 4; // mining diamonds
        } else {
            steveX = 350;
            if (progress % 0.1 < 0.05) steveY -= 10; // jumping
            
            // Diamond drops
            g2d.setColor(DrawUtils.DIAMOND_BLUE);
            g2d.fillRect(diamondX + 15, diamondY + 30 + (int)(Math.sin(progress*20)*5), 8, 8);
        }
        
        DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "pickaxe", swingAngle);

        // Achievement
        if (progress >= 0.7) {
            double popupProgress = (progress - 0.7) / 0.3;
            DrawUtils.drawAchievement(g2d, width, "DIAMONDS!", popupProgress);
        }

        // HUD
        DrawUtils.drawHUD(g2d, width, 10, 10, 8, 20 + (int)(progress * 40));
    }
}
