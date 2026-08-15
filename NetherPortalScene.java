import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

/**
 * Scene 7: Nether Portal
 * Shows building and activating a portal, then transitions to the Nether.
 */
public class NetherPortalScene extends Scene {
    private final Random random = new Random(700);

    public NetherPortalScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int groundY = 420;
        int bs = DrawUtils.BLOCK_SIZE;

        if (progress < 0.5) {
            // First Half: Overworld
            double overworldProgress = progress * 2.0;

            // Night Sky
            g2d.setColor(DrawUtils.NIGHT_SKY);
            g2d.fillRect(0, 0, width, groundY);
            DrawUtils.drawStars(g2d, width, groundY, 50, 12345, overworldProgress);
            MidpointDrawing.fillCircle(g2d, 500, 100, 30, Color.WHITE); // Moon

            // Ground
            DrawUtils.drawGround(g2d, width, height, groundY);

            // Portal Frame base coords
            int px = width / 2 - 2 * bs;
            int py = groundY;

            // Obsidian portal frame (4 wide, 5 tall, hollow)
            int[][] framePositions = {
                {0, 0}, {1, 0}, {2, 0}, {3, 0},
                {0, -1}, {3, -1},
                {0, -2}, {3, -2},
                {0, -3}, {3, -3},
                {0, -4}, {1, -4}, {2, -4}, {3, -4}
            };

            for (int i = 0; i < framePositions.length; i++) {
                double appearTime = i * (0.2 / framePositions.length);
                if (overworldProgress > appearTime) {
                    DrawUtils.drawBlock(g2d, px + framePositions[i][0] * bs, py + framePositions[i][1] * bs, bs, DrawUtils.OBSIDIAN);
                }
            }

            // Steve
            DrawUtils.drawSteve(g2d, px - 3 * bs, groundY, 1, true);

            // Spark (0.2 - 0.3)
            if (overworldProgress > 0.4 && overworldProgress < 0.6) {
                g2d.setColor(Color.YELLOW);
                for (int i = 0; i < 5; i++) {
                    g2d.fillRect(px + bs + random.nextInt(bs * 2), py - bs - random.nextInt(bs * 2), 3, 3);
                }
            }

            // Portal Activation (0.3 - 0.5 in overworld, which is 0.6 - 1.0 of local)
            if (overworldProgress > 0.6) {
                int cx = px + 2 * bs;
                int cy = py - 2 * bs;
                int rx = bs;
                int ry = (int) (1.5 * bs);

                // Wavy animation
                double wave = Math.sin(overworldProgress * Math.PI * 10) * 2;
                
                MidpointDrawing.fillEllipseGlow(g2d, cx, cy, rx + (int)wave, ry + (int)wave, DrawUtils.PORTAL_PURPLE, DrawUtils.ENDER_PURPLE);
                
                // Particles
                g2d.setColor(DrawUtils.PORTAL_PURPLE);
                for(int i = 0; i < 10; i++) {
                    double angle = random.nextDouble() * 2 * Math.PI + overworldProgress * 5;
                    int r = bs + random.nextInt(bs);
                    int x = cx + (int)(r * Math.cos(angle));
                    int y = cy + (int)(r * Math.sin(angle));
                    g2d.fillRect(x, y, 4, 4);
                }
            }
            
            // HUD
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, 40);

        } else if (progress < 0.55) {
            // Transition flash
            double flash = (progress - 0.5) / 0.05;
            g2d.setColor(new Color(170, 0, 255, (int)(255 * (1 - flash))));
            g2d.fillRect(0, 0, width, height);
        } else {
            // Second Half: Nether
            double netherProgress = (progress - 0.55) / 0.45;
            
            // Background gradient
            GradientPaint bgGradient = new GradientPaint(0, 0, DrawUtils.NETHER_RED.darker(), 0, groundY, DrawUtils.NETHER_RED);
            g2d.setPaint(bgGradient);
            g2d.fillRect(0, 0, width, height);

            // Lava ocean with sine wave
            int lavaY = groundY + 3 * bs;
            g2d.setColor(DrawUtils.LAVA_ORANGE);
            for (int x = 0; x < width; x += 10) {
                int waveY = (int) (Math.sin(x * 0.05 + netherProgress * 10) * 5);
                g2d.fillRect(x, lavaY + waveY, 10, height - (lavaY + waveY));
            }
            MidpointDrawing.fillEllipseGlow(g2d, width/2, height, width, 100, DrawUtils.LAVA_ORANGE, Color.RED);

            // Netherrack ground pieces
            for(int i = 0; i < width / bs; i++) {
                if (i < 5 || i > 15) {
                    DrawUtils.drawBlock(g2d, i * bs, groundY, bs, DrawUtils.NETHER_RED.darker());
                    DrawUtils.drawBlock(g2d, i * bs, groundY + bs, bs, DrawUtils.NETHER_RED.darker());
                }
            }

            // Glowstone ceiling
            for (int i = 0; i < 5; i++) {
                int gx = 100 + i * 80;
                DrawUtils.drawBlock(g2d, gx, 0, bs, DrawUtils.GOLD_YELLOW);
                MidpointDrawing.fillCircleGlow(g2d, gx + bs/2, bs/2, bs*2, new Color(255,255,0,100), new Color(255,255,0,0));
            }

            // Nether fortress in distance
            g2d.setColor(new Color(60, 20, 20)); // dark red brick
            g2d.fillRect(300, groundY - 4*bs, 2*bs, 5*bs);
            g2d.fillRect(350, groundY - 6*bs, 3*bs, 7*bs);
            g2d.fillRect(250, groundY - 5*bs, 6*bs, bs); // bridge

            // Ghast floating
            int ghastX = (int) (400 - netherProgress * 50);
            int ghastY = 100 + (int)(Math.sin(netherProgress * 8) * 15);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(ghastX, ghastY, 60, 60);
            g2d.setColor(Color.RED);
            g2d.fillRect(ghastX + 10, ghastY + 15, 8, 8); // eyes
            g2d.fillRect(ghastX + 30, ghastY + 15, 8, 8);
            g2d.setColor(Color.WHITE);
            for(int i=0; i<5; i++) {
                g2d.fillRect(ghastX + i*12, ghastY + 60, 10, 20 + (int)(Math.sin(netherProgress*10 + i)*5));
            }

            // Steve
            int steveX = (int) (50 + netherProgress * 100);
            DrawUtils.drawSteve(g2d, steveX, groundY, 1, true);

            // Fire particles
            g2d.setColor(Color.ORANGE);
            for(int i=0; i<20; i++) {
                int px = random.nextInt(width);
                int py = groundY - random.nextInt(100) - (int)(netherProgress * 200 % 100);
                g2d.fillRect(px, py, 3, 3);
            }

            // HUD
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, 40);
        }
    }
}
