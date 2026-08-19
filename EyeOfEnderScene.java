import java.awt.*;
import java.util.Random;

/**
 * Scene 9: Eye of Ender & Stronghold End Portal.
 * Part 1: Steve throws the Eye of Ender in the Overworld to locate the Stronghold.
 * Part 2: Stronghold End Portal Room with Stone Bricks, Lava pool, End Portal Frame,
 * inserting the final Eye of Ender, activating the cosmic void portal, and jumping in.
 */
public class EyeOfEnderScene extends Scene {
    private final Random random = new Random(999);

    public EyeOfEnderScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        random.setSeed(999 + (long) (progress * 100));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int bs = 24; // 24px block scale

        if (progress < 0.50) {
            // ==========================================
            // PART 1: OVERWORLD - THROWING EYE OF ENDER
            // ==========================================
            double overworldP = progress * 2.0;

            // Afternoon Sky & Sun
            DrawUtils.drawSky(g2d, width, height, 0.25);
            DrawUtils.drawMinecraftSun(g2d, 480, 90, 36);
            DrawUtils.drawCloud(g2d, (int) (80 + overworldP * 50), 90, 110);
            DrawUtils.drawCloud(g2d, (int) (320 + overworldP * 60), 130, 90);

            // Ground & Trees
            int groundY = 440;
            DrawUtils.drawGround(g2d, width, height, groundY, bs);
            DrawUtils.drawTree(g2d, 380, groundY - 144, 3);
            DrawUtils.drawTree(g2d, 40, groundY - 144, 3);

            // Steve
            int steveX = 120 + (int) (overworldP * 70);
            int steveY = groundY - 64;

            // Eye of Ender Thrown in the Sky
            if (overworldP > 0.15 && overworldP < 0.90) {
                double eyeT = (overworldP - 0.15) / 0.75;
                int eyeX = steveX + 24 + (int) (eyeT * 260);
                // Parabolic flight arc
                int eyeY = (groundY - 50) - (int) (Math.sin(eyeT * Math.PI) * 160) - (int) (eyeT * 30);

                // Draw Eye of Ender Pearl (Teal pearl with black slit pupil)
                g2d.setColor(new Color(20, 150, 120));
                g2d.fillRect(eyeX - 4, eyeY - 4, 8, 8);
                g2d.setColor(new Color(85, 235, 205));
                g2d.fillRect(eyeX - 3, eyeY - 3, 3, 3);
                g2d.setColor(Color.BLACK);
                g2d.fillRect(eyeX - 1, eyeY - 3, 2, 6);

                // Trailing Ender particle sparks
                g2d.setColor(DrawUtils.PORTAL_PURPLE);
                for (int i = 0; i < 4; i++) {
                    int px = eyeX - 6 - random.nextInt(12);
                    int py = eyeY + 2 + random.nextInt(8);
                    g2d.fillRect(px, py, 3, 3);
                }

                DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "hand", 0.3, false, 0);
            } else {
                DrawUtils.drawSteveStanding(g2d, steveX, steveY, 1, true);
            }

            // HUD
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, 48);

        } else if (progress < 0.54) {
            // Transition flash into Stronghold
            double flash = (progress - 0.50) / 0.04;
            g2d.setColor(new Color(0, 0, 0, (int) (255 * (1.0 - flash))));
            g2d.fillRect(0, 0, width, height);

        } else {
            // ==========================================
            // PART 2: STRONGHOLD END PORTAL ROOM
            // ==========================================
            double portalP = (progress - 0.54) / 0.46;

            // Background Deep Cave Darkness
            g2d.setColor(new Color(12, 10, 15));
            g2d.fillRect(0, 0, width, height);

            int chamberFloorY = 440;

            // 1. Stronghold Stone Brick Walls, Floor & Ceiling
            for (int c = 0; c < width / bs + 1; c++) {
                int bx = c * bs;
                // Floor
                DrawUtils.drawStoneBrickBlock(g2d, bx, chamberFloorY, bs, (c % 5 == 0) ? 1 : (c % 7 == 0 ? 2 : 0));
                DrawUtils.drawStoneBrickBlock(g2d, bx, chamberFloorY + bs, bs, 0);
                // Ceiling
                DrawUtils.drawStoneBrickBlock(g2d, bx, 0, bs, (c % 4 == 0) ? 1 : 0);
                DrawUtils.drawStoneBrickBlock(g2d, bx, bs, bs, 0);
                // Side walls
                if (c < 3 || c > 21) {
                    for (int r = 2; r < chamberFloorY / bs; r++) {
                        DrawUtils.drawStoneBrickBlock(g2d, bx, r * bs, bs, (r % 3 == 0) ? 1 : (r % 4 == 0 ? 2 : 0));
                    }
                }
            }

            // 2. Stronghold Wall Torches
            DrawUtils.drawTorch(g2d, 3 * bs + 4, chamberFloorY - 4 * bs);
            DrawUtils.drawTorch(g2d, 21 * bs - 4, chamberFloorY - 4 * bs);

            // 3. Central End Portal Structure
            // Portal is elevated on a 3x3 platform above a pool of lava (Cols 9..15)
            int portalBaseX = 9 * bs; // x = 216
            int portalBaseY = chamberFloorY - 2 * bs; // y = 392
            int portalW = 7 * bs; // 168px

            // Molten Lava Pool beneath portal (Cols 10..14)
            for (int c = 10; c <= 14; c++) {
                DrawUtils.drawLavaBlock(g2d, c * bs, chamberFloorY - bs, bs, portalP * 10 + c);
            }

            // End Portal Frame Blocks (Cross-section side view: Left frame, Right frame)
            boolean lastEyePlaced = (portalP >= 0.35);

            // Left Portal Frame Block (has Eye)
            DrawUtils.drawEndPortalFrame(g2d, portalBaseX + bs, portalBaseY, bs, true);
            // Right Portal Frame Block (gets 12th Eye placed by Steve at 0.35)
            DrawUtils.drawEndPortalFrame(g2d, portalBaseX + 5 * bs, portalBaseY, bs, lastEyePlaced);

            // 4. Active End Portal Cosmic Void Plane (Activates when 12th Eye is placed)
            if (lastEyePlaced) {
                int voidX = portalBaseX + 2 * bs;
                int voidY = portalBaseY;
                int voidW = 3 * bs;
                int voidH = bs;
                DrawUtils.drawEndPortalPlane(g2d, voidX, voidY, voidW, voidH, portalP);

                // Void particles floating upwards
                g2d.setColor(new Color(25, 145, 115));
                for (int i = 0; i < 6; i++) {
                    int ppx = voidX + random.nextInt(voidW);
                    int ppy = voidY + random.nextInt(voidH) - (int) ((portalP * 40 + i * 8) % 30);
                    g2d.fillRect(ppx, ppy, 3, 3);
                }
            }

            // 5. Steve Animation (Approaches, places final Eye, jumps into portal)
            int steveApproachX = portalBaseX + 5 * bs + 24;
            int steveY = chamberFloorY - 64;

            if (portalP < 0.35) {
                // Steve standing holding Eye of Ender ready to place
                DrawUtils.drawSteveWithTool(g2d, steveApproachX, steveY, 1, false, "hand", 0, false, 0);
            } else if (portalP < 0.65) {
                // Steve stands looking at activated portal
                DrawUtils.drawSteveStanding(g2d, steveApproachX, steveY, 1, false);
            } else {
                // Steve jumps into the End Portal!
                double jumpT = (portalP - 0.65) / 0.35;
                int startX = steveApproachX;
                int targetX = portalBaseX + 3 * bs;
                int jumpX = (int) (startX - jumpT * (startX - targetX));
                int jumpY = (int) (steveY - Math.sin(jumpT * Math.PI) * 40 + jumpT * jumpT * 40);

                // Alpha fade as Steve enters the cosmic void
                float steveAlpha = (float) Math.max(0.0, 1.0 - jumpT * 1.6);
                Composite origComp = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, steveAlpha));
                DrawUtils.drawSteve(g2d, jumpX, jumpY, 1, false);
                g2d.setComposite(origComp);
            }

            // HUD
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, 50);
        }
    }
}
