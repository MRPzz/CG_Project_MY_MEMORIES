import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * Scene 4: First Night - 2D Cross-Section Dirt Shelter.
 * Steve builds a 2D dirt shelter with background walls, grass roof, and 2-block high room.
 * He places the wooden door, enters the warm torch-lit interior, and the Creeper comes to look outside.
 */
public class FirstNightScene extends Scene {

    public FirstNightScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Day to Night Cycle
        double timeOfDay = 0.15 + (progress * 0.40); // 0.15 (sunset) -> 0.55 (night)
        DrawUtils.drawSky(g2d, width, height, timeOfDay);

        // Sun descends on left
        double sunT = DrawUtils.easeInOut(Math.min(1.0, progress / 0.35));
        int sunY = (int) (90 + sunT * 400);
        if (progress < 0.35) {
            DrawUtils.drawMinecraftSun(g2d, 80, sunY, 36);
        }

        // Moon ascends on right
        if (progress > 0.25) {
            double moonT = DrawUtils.easeInOut(Math.min(1.0, (progress - 0.25) / 0.50));
            int moonY = (int) (420 - moonT * 320);
            DrawUtils.drawMinecraftMoon(g2d, 500, moonY, 36);
        }

        // Twinkling stars in night sky
        if (progress > 0.30) {
            DrawUtils.drawStars(g2d, width, height, 50, 12345, progress);
        }

        // 2D Block Scale: 1 block = 32px (half Steve), 2 blocks = 64px (full Steve height)
        int bs = 32;
        int groundY = 450;

        // Ground (Grass & Dirt layers with identical 32px block textures)
        DrawUtils.drawGround(g2d, width, height, groundY, bs);

        // 2D Dirt House Layout:
        // - 5 blocks wide, 3 blocks high (Room interior: 2 blocks high = 64px)
        // - Col 0 (x = 80): Left exterior wall (2 blocks high)
        // - Col 1 & 2 (x = 112..176): Interior room with shaded back wall
        // - Col 3 (x = 176): Doorway & 2-block Wooden Door
        // - Col 4 (x = 208): Right roof overhang
        int hutX = 80;
        int hutY = groundY - 3 * bs; // y = 354
        int doorX = hutX + 3 * bs;   // x = 176
        int doorY = groundY - 2 * bs; // y = 386
        int doorW = bs;               // 32px
        int doorH = 2 * bs;           // 64px

        // === 1. 2D Interior Background Wall (Back Wall) ===
        if (progress > 0.15) {
            double bgP = Math.min(1.0, (progress - 0.15) / 0.15);
            int bgAlpha = (int) (bgP * 255);
            g2d.setColor(new Color(75, 52, 34, bgAlpha));
            g2d.fillRect(hutX + bs, groundY - 2 * bs, 2 * bs, 2 * bs);

            // Shaded 2D background tile grid lines
            g2d.setColor(new Color(55, 38, 24, bgAlpha));
            g2d.drawLine(hutX + 2 * bs, groundY - 2 * bs, hutX + 2 * bs, groundY);
            g2d.drawLine(hutX + bs, groundY - bs, hutX + 3 * bs, groundY - bs);
            g2d.drawRect(hutX + bs, groundY - 2 * bs, 2 * bs, 2 * bs);
        }

        // === 2. 2D Dirt House Building Sequence (0.00 - 0.35) ===
        // Ordered 2D block placement:
        // 1-2: Left wall blocks (bottom-to-top)
        // 3-7: Roof blocks (left-to-right) with grass top
        int[][] buildOrder = {
            {0, 2}, // Left wall lower (y = 418)
            {0, 1}, // Left wall upper (y = 386)
            {0, 0}, // Roof 0 (y = 354)
            {1, 0}, // Roof 1
            {2, 0}, // Roof 2
            {3, 0}, // Roof 3 (above door)
            {4, 0}  // Roof 4 (overhang)
        };

        double buildP = Math.min(1.0, progress / 0.32);
        int totalBlocks = buildOrder.length;
        int blocksBuilt = (int) (buildP * totalBlocks);

        for (int i = 0; i < blocksBuilt; i++) {
            int c = buildOrder[i][0];
            int r = buildOrder[i][1];
            int bx = hutX + c * bs;
            int by = hutY + r * bs;

            if (r == 0) {
                // Top roof blocks have green grassy surface
                DrawUtils.drawGrassBlock(g2d, bx, by, bs);
            } else {
                // Wall blocks are pure dirt
                DrawUtils.drawDirtBlock(g2d, bx, by, bs);
            }
        }

        // Placement particle puffs for the currently placing block
        if (progress < 0.32 && blocksBuilt > 0 && blocksBuilt <= totalBlocks) {
            int curIdx = blocksBuilt - 1;
            int px = hutX + buildOrder[curIdx][0] * bs;
            int py = hutY + buildOrder[curIdx][1] * bs;
            Random pr = new Random((long) (progress * 400));
            g2d.setColor(new Color(150, 110, 75, 160));
            for (int k = 0; k < 3; k++) {
                g2d.fillRect(px + pr.nextInt(bs), py + pr.nextInt(bs), 3, 3);
            }
        }

        // === 3. Interior Wall Torch ===
        int torchX = hutX + bs + 4;
        int torchY = groundY - 2 * bs + 8;
        if (progress > 0.46) {
            DrawUtils.drawTorch(g2d, torchX, torchY);
        }

        // === 4. Wooden Door Placement & States ===
        // - Not placed: progress < 0.38
        // - Placed & OPEN: progress 0.38 - 0.54 (Steve walks through)
        // - Placed & CLOSED: progress >= 0.54 (Steve safe inside)
        boolean doorPlaced = (progress >= 0.38);
        boolean doorOpen = (progress >= 0.38 && progress < 0.54);

        if (doorPlaced) {
            DrawUtils.drawWoodenDoor(g2d, doorX, doorY, doorW, doorH, doorOpen);

            // Door placement smoke puff particles (0.38 - 0.44)
            if (progress < 0.44) {
                double puffT = (progress - 0.38) / 0.06;
                Random puffRand = new Random(777);
                for (int i = 0; i < 6; i++) {
                    int px = doorX + puffRand.nextInt(doorW) + (int) ((puffRand.nextDouble() - 0.5) * puffT * 16);
                    int py = doorY + puffRand.nextInt(doorH) - (int) (puffT * 10);
                    int alpha = (int) ((1.0 - puffT) * 180);
                    g2d.setColor(new Color(220, 220, 220, Math.max(0, alpha)));
                    g2d.fillRect(px, py, 3, 3);
                }
            }
        }

        // === 5. Steve Animation Lifecycle ===
        int outsideStandX = hutX + 5 * bs + 25; // x = 265
        int insideRoomX = hutX + bs + 6;        // x = 118 (cozy inside 2-block room)

        if (progress < 0.34) {
            // Phase A: Standing outside, holding a dirt block in hand, watching 2D house build
            DrawUtils.drawSteveWithTool(g2d, outsideStandX, groundY - 64, 1, false, "dirt", 0, false, 0);
        } else if (progress < 0.42) {
            // Phase B: Walking to doorway holding wooden door
            double t = DrawUtils.easeInOut((progress - 0.34) / 0.08);
            int steveX = (int) (outsideStandX - t * (outsideStandX - (doorX + 20)));
            DrawUtils.drawSteveWithTool(g2d, steveX, groundY - 64, 1, false, "door", 0, true, (outsideStandX - steveX) * 0.4);
        } else if (progress < 0.54) {
            // Phase C: Walking through 2D doorway into the cozy room
            double t = DrawUtils.easeInOut((progress - 0.42) / 0.12);
            int steveX = (int) ((doorX + 20) - t * ((doorX + 20) - insideRoomX));
            DrawUtils.drawSteveWithTool(g2d, steveX, groundY - 64, 1, false, "hand", 0, true, t * 8);
        } else {
            // Phase D: Safe inside the 2-block room, standing comfortably under torchlight, facing right
            DrawUtils.drawSteveStanding(g2d, insideRoomX, groundY - 64, 1, true);
        }

        // Re-draw closed door so it is visibly in front of doorway
        if (doorPlaced && !doorOpen) {
            DrawUtils.drawWoodenDoor(g2d, doorX, doorY, doorW, doorH, false);
        }

        // === 6. Creeper walks up and looks beside the house (0.58 - 1.00) ===
        if (progress >= 0.58) {
            double cProg = Math.min(1.0, (progress - 0.58) / 0.22);
            double walkEased = DrawUtils.easeInOut(cProg);
            int creeperStartX = 560;
            int creeperStopX = hutX + 5 * bs + 20; // x = 260 (right beside the house)
            int creeperX = (int) (creeperStartX - walkEased * (creeperStartX - creeperStopX));

            // Draw Creeper walking/standing beside the house
            DrawUtils.drawCreeper(g2d, creeperX, groundY - 52, 1);

            // Creeper looking curiously at the house (subtle head tilt/curious gaze)
            if (cProg >= 1.0) {
                int gazeTick = (int) (Math.sin(progress * 8) * 2);
                g2d.setColor(Color.BLACK);
                g2d.fillRect(creeperX + 9, groundY - 50 + gazeTick, 2, 2);
            }
        }

        // HUD
        DrawUtils.drawHUD(g2d, width, 10, 10, 8, 20);
    }
}
