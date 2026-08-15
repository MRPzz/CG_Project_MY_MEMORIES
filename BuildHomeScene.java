import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

/**
 * Scene 6: Building a home.
 * Shows a time-lapse animation of a Minecraft house being built during a sunset.
 */
public class BuildHomeScene extends Scene {
    private final Random random = new Random(600);

    public BuildHomeScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sunset sky transitioning
        double timeOfDay = 0.2 + (progress * 0.1);
        DrawUtils.drawSky(g2d, width, height, timeOfDay);

        // Drifting clouds
        int cloudX = (int) (width - progress * 400);
        DrawUtils.drawCloud(g2d, cloudX, 100, 100);
        DrawUtils.drawCloud(g2d, cloudX + 250, 150, 120);
        DrawUtils.drawCloud(g2d, cloudX - 200, 80, 90);

        // Ground
        DrawUtils.drawGround(g2d, width, height, 420);
        
        // Base coordinates for house
        int hx = 200;
        int hy = 420;
        int bs = DrawUtils.BLOCK_SIZE;

        // Foundation (0 - 0.15)
        if (progress > 0) {
            double pFoundation = Math.min(1.0, progress / 0.15);
            for (int i = 0; i < 5; i++) {
                if (pFoundation > i * 0.2) {
                    drawPopBlock(g2d, hx + i * bs, hy - bs, bs, DrawUtils.STONE_GRAY, (pFoundation - i * 0.2) * 5);
                }
            }
        }

        // Walls (0.15 - 0.4)
        if (progress > 0.15) {
            double pWalls = Math.min(1.0, (progress - 0.15) / 0.25);
            for (int row = 1; row <= 4; row++) {
                for (int col = 0; col < 5; col++) {
                    if (col == 0 || col == 4 || row == 4) { // Hollow inside for front wall
                        double delay = (row * 5 + col) / 25.0;
                        if (pWalls > delay) {
                            drawPopBlock(g2d, hx + col * bs, hy - bs - row * bs, bs, DrawUtils.WOOD_BROWN, (pWalls - delay) * 5);
                        }
                    }
                }
            }
        }

        // Roof (0.4 - 0.55)
        if (progress > 0.4) {
            double pRoof = Math.min(1.0, (progress - 0.4) / 0.15);
            int[] roofX = {hx - bs, hx, hx + bs, hx + 2*bs, hx + 3*bs, hx + 4*bs, hx + 5*bs};
            int[] roofY = {hy - 5*bs, hy - 6*bs, hy - 7*bs, hy - 8*bs, hy - 7*bs, hy - 6*bs, hy - 5*bs};
            for (int i = 0; i < roofX.length; i++) {
                double delay = i / (double)roofX.length;
                if (pRoof > delay) {
                    drawPopBlock(g2d, roofX[i], roofY[i], bs, DrawUtils.LOG_BROWN, (pRoof - delay) * 5);
                    // Fill under roof
                    if (i > 0 && i < roofX.length - 1) {
                         drawPopBlock(g2d, roofX[i], roofY[i]+bs, bs, DrawUtils.WOOD_BROWN, (pRoof - delay) * 5);
                    }
                }
            }
        }

        // Door (0.55 - 0.65)
        if (progress > 0.55) {
            double pDoor = Math.min(1.0, (progress - 0.55) / 0.1);
            if (pDoor > 0.5) {
                g2d.setColor(DrawUtils.LOG_BROWN);
                g2d.fillRect(hx + 2 * bs, hy - 3 * bs, bs, bs * 2);
            }
        }

        // Windows (0.65 - 0.75)
        if (progress > 0.65) {
            double pWindow = Math.min(1.0, (progress - 0.65) / 0.1);
            if (pWindow > 0.5) {
                g2d.setColor(new Color(150, 200, 255, 180));
                g2d.fillRect(hx + bs, hy - 3 * bs, bs, bs);
                g2d.fillRect(hx + 3 * bs, hy - 3 * bs, bs, bs);
            }
        }

        // Decorations (0.75 - 0.85)
        if (progress > 0.75) {
            double pDeco = Math.min(1.0, (progress - 0.75) / 0.1);
            if (pDeco > 0.5) {
                // Path
                DrawUtils.drawBlock(g2d, hx + 2 * bs, hy, bs, DrawUtils.DIRT_BROWN);
                DrawUtils.drawBlock(g2d, hx + 2 * bs, hy + bs, bs, DrawUtils.DIRT_BROWN);
                
                // Flowers
                g2d.setColor(Color.RED);
                g2d.fillRect(hx - bs, hy - bs/2, 4, 4);
                g2d.setColor(Color.YELLOW);
                g2d.fillRect(hx + 6 * bs, hy - bs/2, 4, 4);
            }
        }

        // Farm area (0.85 - 0.95)
        if (progress > 0.85) {
            double pFarm = Math.min(1.0, (progress - 0.85) / 0.1);
            if (pFarm > 0.5) {
                int fx = hx + 7 * bs;
                // Farm land
                DrawUtils.drawBlock(g2d, fx, hy, bs, DrawUtils.DIRT_BROWN);
                DrawUtils.drawBlock(g2d, fx + bs, hy, bs, DrawUtils.DIRT_BROWN);
                // Wheat
                g2d.setColor(DrawUtils.GOLD_YELLOW);
                for(int i=0; i<4; i++) g2d.drawLine(fx + i*5 + 2, hy, fx + i*5 + 2, hy - 15);
                for(int i=0; i<4; i++) g2d.drawLine(fx + bs + i*5 + 2, hy, fx + bs + i*5 + 2, hy - 15);
            }
        }

        // Steve (0.95 - 1.0)
        if (progress > 0.95) {
            DrawUtils.drawSteve(g2d, hx + 2 * bs + 10, hy - 10, 1, false);
        }

        // HUD
        DrawUtils.drawHUD(g2d, width, 10, 10, 10, 30);
    }

    private void drawPopBlock(Graphics2D g2d, int x, int y, int size, Color color, double p) {
        if (p > 1.0) p = 1.0;
        int currentSize = (int)(size * p);
        int offset = (size - currentSize) / 2;
        if (currentSize > 0) {
            DrawUtils.drawBlock(g2d, x + offset, y + offset + (size-currentSize), currentSize, color);
        }
    }
}
