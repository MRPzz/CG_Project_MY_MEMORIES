import java.awt.*;
import java.util.Random;

/**
 * Scene 6: Building a Wooden Home.
 * Steve stands outside holding a wood block, watching his wooden house being built during sunset,
 * then walks over, enters through the wooden door into his new home.
 */
public class BuildHomeScene extends Scene {

    public BuildHomeScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Sunset sky transitioning
        double timeOfDay = 0.22 + (progress * 0.12);
        DrawUtils.drawSky(g2d, width, height, timeOfDay);

        // Sunset Sun setting behind clouds
        int sunX = (int) (490 - progress * 90);
        int sunY = (int) (110 + progress * 130);
        DrawUtils.drawMinecraftSun(g2d, sunX, sunY, 36);

        // Drifting blocky clouds
        int cloudX = (int) (progress * 100);
        DrawUtils.drawCloud(g2d, 30 + cloudX, 80, 110);
        DrawUtils.drawCloud(g2d, 270 + cloudX, 120, 130);
        DrawUtils.drawCloud(g2d, 470 + cloudX, 65, 95);

        // Ground (32px block scale matching Steve)
        int bs = 32;
        int groundY = 440;
        DrawUtils.drawGround(g2d, width, height, groundY, bs);

        // Base coordinates for wooden house (6 blocks wide)
        int hx = 60;
        int hy = groundY;
        int doorX = hx + 2 * bs; // x = 124
        int doorY = groundY - 2 * bs; // y = 376
        int doorW = bs; // 32px
        int doorH = 2 * bs; // 64px (equal to Steve)

        // === 1. Cobblestone Foundation (0 - 0.14) ===
        if (progress > 0) {
            double pFoundation = Math.min(1.0, progress / 0.14);
            for (int i = 0; i < 6; i++) {
                if (pFoundation > i * (1.0 / 6)) {
                    DrawUtils.drawCobblestoneBlock(g2d, hx + i * bs, hy - bs, bs);
                }
            }
        }

        // === 2. Oak Log Pillars & Wood Plank Walls + Windows (0.14 - 0.38) ===
        if (progress > 0.14) {
            double pWalls = Math.min(1.0, (progress - 0.14) / 0.24);
            for (int row = 1; row <= 2; row++) {
                for (int col = 0; col < 6; col++) {
                    double delay = (row * 6 + col) / 18.0;
                    if (pWalls > delay) {
                        int bx = hx + col * bs;
                        int by = hy - bs - row * bs;

                        if (col == 0 || col == 5) {
                            // Corner Oak Log pillars
                            DrawUtils.drawOakLog(g2d, bx, by, bs);
                        } else if (col == 2) {
                            // Doorway opening (leave open for door)
                        } else if (col == 1 || col == 4) {
                            // Glass window pane
                            g2d.setColor(new Color(175, 220, 255, 190));
                            g2d.fillRect(bx, by, bs, bs);
                            g2d.setColor(Color.WHITE);
                            g2d.drawLine(bx + 4, by + 4, bx + 12, by + 4);
                            g2d.setColor(new Color(120, 160, 200));
                            g2d.drawRect(bx, by, bs, bs);
                        } else {
                            // Wood planks
                            DrawUtils.drawBlock(g2d, bx, by, bs, DrawUtils.WOOD_BROWN);
                        }
                    }
                }
            }
        }

        // === 3. Sloped Cobblestone Roof (0.38 - 0.55) ===
        if (progress > 0.38) {
            double pRoof = Math.min(1.0, (progress - 0.38) / 0.17);
            // 6 roof steps across top
            for (int col = 0; col < 6; col++) {
                double delay = col / 6.0;
                if (pRoof > delay) {
                    int rx = hx + col * bs;
                    int ry = hy - 3 * bs - (col >= 1 && col <= 4 ? (col == 2 || col == 3 ? bs : bs / 2) : 0);
                    DrawUtils.drawCobblestoneBlock(g2d, rx, ry, bs);
                }
            }
        }

        // === 4. Wooden Door Placement & Animation (0.55 - 1.00) ===
        // Door is closed until Steve approaches at 0.78, opens (0.78 - 0.88), then closes (0.88 - 1.00)
        boolean doorPlaced = (progress >= 0.55);
        boolean doorOpen = (progress >= 0.78 && progress < 0.88);

        if (doorPlaced) {
            DrawUtils.drawWoodenDoor(g2d, doorX, doorY, doorW, doorH, doorOpen);

            // Door placement smoke puff
            if (progress < 0.60) {
                double puffT = (progress - 0.55) / 0.05;
                Random puffRand = new Random(777);
                for (int i = 0; i < 5; i++) {
                    int px = doorX + puffRand.nextInt(doorW);
                    int py = doorY + puffRand.nextInt(doorH) - (int) (puffT * 10);
                    int alpha = (int) ((1.0 - puffT) * 180);
                    g2d.setColor(new Color(220, 220, 220, Math.max(0, alpha)));
                    g2d.fillRect(px, py, 3, 3);
                }
            }
        }

        // === 5. Wall Torch (0.60 - 1.00) ===
        int tx = hx + 3 * bs + 4;
        int ty = hy - 2 * bs + 8;
        if (progress > 0.60) {
            DrawUtils.drawTorch(g2d, tx, ty);
        }

        // === 6. Garden & Wheat Farm (0.64 - 0.75) ===
        if (progress > 0.64) {
            int fx = hx + 7 * bs;
            // Farmland
            DrawUtils.drawDirtBlock(g2d, fx, hy, bs);
            DrawUtils.drawDirtBlock(g2d, fx + bs, hy, bs);

            // Wheat crops
            g2d.setColor(DrawUtils.GOLD_YELLOW);
            for (int b = 0; b < 2; b++) {
                int cx = fx + b * bs;
                for (int i = 0; i < 3; i++) {
                    int wx = cx + 4 + i * 9;
                    g2d.fillRect(wx, hy - 16, 2, 16);
                    g2d.fillRect(wx - 2, hy - 14, 6, 3);
                    g2d.fillRect(wx - 1, hy - 8, 4, 3);
                }
            }

            // Red & Yellow Flowers by house entrance
            g2d.setColor(new Color(225, 30, 30));
            g2d.fillRect(doorX - 18, hy - 12, 6, 6);
            g2d.setColor(new Color(45, 140, 25));
            g2d.fillRect(doorX - 16, hy - 6, 2, 6);

            g2d.setColor(DrawUtils.GOLD_YELLOW);
            g2d.fillRect(doorX + doorW + 12, hy - 10, 6, 6);
            g2d.setColor(new Color(45, 140, 25));
            g2d.fillRect(doorX + doorW + 14, hy - 4, 2, 4);
        }

        // === 7. Steve Animation Lifecycle ===
        int outsideWatchX = 400;
        int insideHouseX = doorX - 10; // Inside house behind door

        if (progress < 0.70) {
            // Phase A: Standing outside, holding a wood block in hand, watching house build
            DrawUtils.drawSteveWithTool(g2d, outsideWatchX, hy - 64, 1, false, "wood", 0, false, 0);
        } else if (progress < 0.86) {
            // Phase B: Walking over to the house and entering through the open door
            double t = DrawUtils.easeInOut((progress - 0.70) / 0.16);
            int steveX = (int) (outsideWatchX - t * (outsideWatchX - insideHouseX));
            DrawUtils.drawSteveWithTool(g2d, steveX, hy - 64, 1, false, "hand", 0, true, (outsideWatchX - steveX) * 0.35);
        } else {
            // Phase C: Safely inside his completed wooden home, standing proudly
            DrawUtils.drawSteveStanding(g2d, insideHouseX, hy - 64, 1, true);
        }

        // Re-draw closed door over doorway when door is closed
        if (doorPlaced && !doorOpen && progress >= 0.88) {
            DrawUtils.drawWoodenDoor(g2d, doorX, doorY, doorW, doorH, false);
        }

        // HUD
        DrawUtils.drawHUD(g2d, width, 10, 10, 10, 30);
    }
}
