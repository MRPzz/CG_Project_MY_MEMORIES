import java.awt.*;
import java.util.Random;

/**
 * Scene 5: Mining - Deep Underground Cave.
 * The entire top is solid stone layers from row 0 down.
 * Steve digs down through the solid rock into a natural block cavern,
 * and discovers diamond ore.
 */
public class MiningScene extends Scene {

    // 30 columns x 30 rows of 20x20 blocks
    private static final int COLS = 30;
    private static final int ROWS = 30;
    private static final int BS = DrawUtils.BLOCK_SIZE;

    // Block types
    private static final byte AIR = 0;
    private static final byte STONE = 1;
    private static final byte COBBLE = 2;
    private static final byte COAL_ORE = 3;
    private static final byte IRON_ORE = 4;
    private static final byte GOLD_ORE = 5;
    private static final byte DIAMOND_ORE = 6;
    private static final byte WATER = 7;
    private static final byte DRIPSTONE = 8;

    private final byte[][] world = new byte[ROWS][COLS];

    public MiningScene(String name, int durationMs) {
        super(name, durationMs);
        initWorld();
    }

    private void initWorld() {
        Random r = new Random(456);

        // 1. Define natural contiguous ceiling and floor profiles for the cave (Cols 4..25)
        // Ceiling row for each column: solid stone is at or above this row
        int[] ceilRow = {
            30, 30, 30, 30,  // Cols 0..3: solid wall to bottom
            16, 15, 14, 14, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 14, 14, 15, 15, 16, 17, // Cols 4..23
            30, 30, 30, 30, 30, 30 // Cols 24..29: solid right wall
        };

        // Floor row for each column: solid stone is at or below this row
        int[] floorRow = {
            0, 0, 0, 0,      // Cols 0..3: solid wall
            24, 24, 25, 26, 26, 26, 26, 25, 24, // Cols 4..12
            22, 22, 22,      // Cols 13..15: Landing ledge (Row 22)
            23, 23,          // Cols 16..17: Step 1 (Row 23)
            24, 24,          // Cols 18..19: Step 2 (Row 24)
            25, 25, 25, 25,  // Cols 20..23: Lower floor (Row 25)
            0, 0, 0, 0, 0, 0 // Cols 24..29: solid right wall
        };

        // 2. Build the world grid with solid stone layers from row 0 to bottom
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                // Check if within open cave chamber
                boolean isInsideCave = (col >= 4 && col <= 23 && row >= ceilRow[col] && row < floorRow[col]);

                if (isInsideCave) {
                    world[row][col] = AIR;
                } else {
                    // Solid stone layers with natural ore veins from top to bottom
                    double roll = r.nextDouble();
                    if (roll < 0.05 && row > 4) {
                        world[row][col] = COAL_ORE;
                    } else if (roll < 0.085 && row > 8) {
                        world[row][col] = IRON_ORE;
                    } else if (roll < 0.10 && row > 16) {
                        world[row][col] = GOLD_ORE;
                    } else if (roll < 0.18) {
                        world[row][col] = COBBLE;
                    } else {
                        world[row][col] = STONE;
                    }
                }
            }
        }

        // 3. Underground water pool (Cols 7..10, Row 26)
        for (int c = 7; c <= 10; c++) {
            world[26][c] = WATER;
        }

        // 4. Diamond Ore Vein embedded in the solid right wall (Rows 23..24, Cols 24..25)
        world[23][24] = DIAMOND_ORE;
        world[23][25] = DIAMOND_ORE;
        world[24][24] = DIAMOND_ORE;
        world[24][25] = DIAMOND_ORE;

        // 5. Anchored Dripstone Stalactites (Hanging directly from solid ceiling)
        world[14][7] = DRIPSTONE;
        world[15][7] = DRIPSTONE;
        world[14][18] = DRIPSTONE;
        world[15][18] = DRIPSTONE;
        world[16][18] = DRIPSTONE;

        // 6. Anchored Dripstone Stalagmites (Standing directly on solid floor)
        world[23][5] = DRIPSTONE;
        world[24][11] = DRIPSTONE;
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shaft columns (Cols 13 & 14)
        int shaftCol1 = 13;
        int shaftCol2 = 14;
        int maxDigRow = 13; // Break through into cave ceiling at row 13

        // Phase Timing:
        // 0.00 - 0.38: Digging down shaft block by block through solid stone
        // 0.38 - 0.46: Steve drops from shaft ceiling onto cave ledge
        // 0.46 - 0.58: Steve walks along stepped ledges, mounts torch
        // 0.58 - 0.66: Spots diamond ore ("!")
        // 0.66 - 0.78: Mines diamond ore with pickaxe
        // 0.78 - 1.00: Diamond pops out, celebration jump & achievement
        double digP = Math.min(1.0, progress / 0.38);
        int currentDugRow = (int) (digP * maxDigRow);
        double rowFraction = (digP * maxDigRow) - currentDugRow;

        // === 1. Render Background Deep Cave Darkness ===
        g2d.setColor(new Color(15, 12, 18));
        g2d.fillRect(0, 0, width, height);

        // === 2. Render Voxel Block Grid ===
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int bx = c * BS;
                int by = r * BS;

                // Check if shaft has been excavated
                boolean isShaftDug = (c == shaftCol1 || c == shaftCol2) && r <= currentDugRow;
                if (isShaftDug) {
                    continue; // Excavated air
                }

                // If diamond ore is mined after 0.78, clear it
                if (progress >= 0.78 && (r == 23 || r == 24) && (c == 24 || c == 25)) {
                    continue;
                }

                byte blockType = world[r][c];
                switch (blockType) {
                    case STONE:
                        DrawUtils.drawStoneBlock(g2d, bx, by, BS);
                        break;
                    case COBBLE:
                        DrawUtils.drawCobblestoneBlock(g2d, bx, by, BS);
                        break;
                    case COAL_ORE:
                        DrawUtils.drawOreBlock(g2d, bx, by, BS, "coal");
                        break;
                    case IRON_ORE:
                        DrawUtils.drawOreBlock(g2d, bx, by, BS, "iron");
                        break;
                    case GOLD_ORE:
                        DrawUtils.drawOreBlock(g2d, bx, by, BS, "gold");
                        break;
                    case DIAMOND_ORE:
                        DrawUtils.drawOreBlock(g2d, bx, by, BS, "diamond");
                        break;
                    case WATER:
                        DrawUtils.drawWaterBlock(g2d, bx, by, BS, progress * 10);
                        break;
                    case DRIPSTONE:
                        // Pointed dripstone block anchored to stone
                        g2d.setColor(new Color(145, 125, 100));
                        g2d.fillRect(bx + 4, by, BS - 8, BS);
                        g2d.setColor(new Color(110, 95, 75));
                        g2d.drawRect(bx + 4, by, BS - 8, BS);
                        break;
                    default:
                        break;
                }
            }
        }

        // === 3. Block Breaking Cracks & Dust Particles in Shaft ===
        if (progress < 0.38 && currentDugRow < maxDigRow) {
            int breakY = currentDugRow * BS;
            int breakX = shaftCol1 * BS;

            // Crack lines
            g2d.setColor(new Color(0, 0, 0, (int) (rowFraction * 230)));
            if (rowFraction > 0.20) {
                g2d.drawLine(breakX + 4, breakY + 3, breakX + 18, breakY + 17);
                g2d.drawLine(breakX + 24, breakY + 2, breakX + 36, breakY + 16);
            }
            if (rowFraction > 0.50) {
                g2d.drawLine(breakX + 18, breakY + 2, breakX + 5, breakY + 18);
                g2d.drawLine(breakX + 38, breakY + 3, breakX + 22, breakY + 17);
            }
            if (rowFraction > 0.75) {
                g2d.drawLine(breakX + 10, breakY, breakX + 10, breakY + BS);
                g2d.drawLine(breakX + 30, breakY, breakX + 30, breakY + BS);
            }

            // Stone breaking chips
            Random chipRand = new Random((long) (progress * 500));
            for (int i = 0; i < 6; i++) {
                int px = breakX + chipRand.nextInt(BS * 2);
                int py = breakY + BS + chipRand.nextInt(10);
                g2d.setColor(new Color(125 + chipRand.nextInt(30), 120, 115));
                g2d.fillRect(px, py, 3, 3);
            }
        }

        // === 4. Wall Torch ===
        int torchBlockX = 15 * BS;
        int torchBlockY = 21 * BS;
        if (progress > 0.44) {
            DrawUtils.drawTorch(g2d, torchBlockX, torchBlockY);
        }

        // Diamond Ore Position
        int diamondPX = 24 * BS;
        int diamondPY = 23 * BS;

        // === 6. Steve Animation Lifecycle ===
        int steveX, steveY;
        double swing = 0;
        int ledgeLandingY = 22 * BS - 64; // Steve standing on row 22 ledge (y = 376)

        if (progress < 0.38) {
            // Digging down the vertical shaft through solid rock
            steveX = shaftCol1 * BS + 4;
            steveY = currentDugRow * BS - 64;
            swing = Math.sin(progress * 50) * Math.PI / 4;
            DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "pickaxe", swing, false, 0);
        } else if (progress < 0.46) {
            // Gravity drop from shaft ceiling onto cave ledge
            double dropT = (progress - 0.38) / 0.08;
            double grav = dropT * dropT;
            int startY = maxDigRow * BS - 64;
            steveX = shaftCol1 * BS + 4;
            steveY = (int) (startY + grav * (ledgeLandingY - startY));
            DrawUtils.drawSteveStanding(g2d, steveX, steveY, 1, true);
        } else if (progress < 0.58) {
            // Walking down the solid block staircase toward diamond wall
            double walkT = DrawUtils.easeInOut((progress - 0.46) / 0.12);
            int targetX = diamondPX - 45;
            steveX = shaftCol1 * BS + 4 + (int) (walkT * (targetX - (shaftCol1 * BS + 4)));
            // Steps down from row 22 to row 25 (3 blocks down)
            steveY = ledgeLandingY + (int) (walkT * (3 * BS));
            DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "hand", 0, true, steveX * 0.35);
        } else if (progress < 0.66) {
            // Spots Diamond Ore ("!")
            steveX = diamondPX - 45;
            steveY = ledgeLandingY + 3 * BS;
            DrawUtils.drawSteveStanding(g2d, steveX, steveY, 1, true);
            DrawUtils.drawMinecraftText(g2d, "!", steveX + 12, steveY - 14, 22, Color.WHITE);
        } else if (progress < 0.78) {
            // Mining Diamond Ore Block
            steveX = diamondPX - 45;
            steveY = ledgeLandingY + 3 * BS;
            swing = Math.sin((progress - 0.66) * 35) * Math.PI / 4;
            DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "pickaxe", swing, false, 0);

            // Diamond block crack overlay
            double mineFrac = (progress - 0.66) / 0.12;
            g2d.setColor(new Color(0, 0, 0, (int) (mineFrac * 220)));
            g2d.drawLine(diamondPX + 4, diamondPY + 4, diamondPX + BS * 2 - 4, diamondPY + BS * 2 - 4);
            g2d.drawLine(diamondPX + BS * 2 - 4, diamondPY + 4, diamondPX + 4, diamondPY + BS * 2 - 4);
        } else {
            // Celebration & Diamond item drop + XP Orbs
            steveX = diamondPX - 45;
            steveY = ledgeLandingY + 3 * BS;
            double jumpT = Math.sin((progress - 0.78) * Math.PI * 6);
            if (jumpT > 0) steveY -= (int) (jumpT * 16);
            DrawUtils.drawSteveStanding(g2d, steveX, steveY, 1, true);

            // Floating 3D Diamond Gem (bobbing)
            int gemY = diamondPY + 15 + (int) (Math.sin(progress * 16) * 5);
            g2d.setColor(DrawUtils.DIAMOND_BLUE);
            g2d.fillRect(diamondPX + 12, gemY, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(diamondPX + 14, gemY + 2, 3, 3);
            g2d.setColor(DrawUtils.DIAMOND_DARK);
            g2d.drawRect(diamondPX + 12, gemY, 10, 10);

            // XP Orbs popping and collecting
            Random xpRand = new Random(777);
            for (int i = 0; i < 6; i++) {
                double angle = xpRand.nextDouble() * 2 * Math.PI;
                int ox = diamondPX + 15 + (int) (Math.cos(angle) * (progress - 0.78) * 120);
                int oy = diamondPY + 15 + (int) (Math.sin(angle) * (progress - 0.78) * 120);
                MidpointDrawing.fillCircle(g2d, ox, oy, 3, DrawUtils.XP_GREEN);
            }
        }

        // === 7. Cave Ambient Darkness ===
        DrawUtils.drawCaveDarkness(g2d, width, height, torchBlockX, torchBlockY, 130);

        // === 8. Achievement ===
        if (progress >= 0.78) {
            DrawUtils.drawAchievement(g2d, width, "DIAMONDS!", (progress - 0.78) / 0.22);
        }

        // === 9. HUD ===
        DrawUtils.drawHUD(g2d, width, 10, 10, 8, 20 + (int) (progress * 40));
    }
}
