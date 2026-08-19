import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;

/**
 * Scene 3: First Day - Steve spawns into the new world, looks around,
 * turns towards the oak tree, walks over to chop down wood, and crafts a workbench.
 */
public class FirstDayScene extends Scene {

    public FirstDayScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Day/Night cycle
        double timeOfDay = 0.0 + (progress * 0.1);
        DrawUtils.drawSky(g2d, width, height, timeOfDay);

        // Authentic Square Minecraft Sun
        int sunX = 80 + (int) (progress * 420);
        int sunY = 70 + (int) (Math.sin(progress * Math.PI) * -30);
        DrawUtils.drawMinecraftSun(g2d, sunX, sunY, 38);

        // Drifting blocky clouds
        DrawUtils.drawCloud(g2d, (int) (120 + progress * 70), 80, 110);
        DrawUtils.drawCloud(g2d, (int) (340 + progress * 90), 120, 90);

        // Ground (Grass + Dirt layers with 32px block scale)
        int bs = 32;
        int groundY = 450;
        DrawUtils.drawGround(g2d, width, height, groundY, bs);

        // Background decorative trees (planted firmly on ground)
        int treeScale = 3;
        int treeBlockSize = 8 * treeScale; // 24px
        int treeH = 6 * treeBlockSize;      // 144px
        int treeGroundY = groundY - treeH;  // 306px

        DrawUtils.drawTree(g2d, 430, treeGroundY, treeScale);
        DrawUtils.drawTree(g2d, 50, treeGroundY, treeScale);

        // Main Tree to be punched
        int treeX = 230;
        int trunkX = treeX + 2 * treeBlockSize; // 230 + 48 = 278
        int trunkBottomY = groundY - treeBlockSize; // 450 - 24 = 426

        // 1. Draw Main Tree
        if (progress < 0.60) {
            // Full tree standing firmly on the ground
            DrawUtils.drawTree(g2d, treeX, treeGroundY, treeScale);

            // Progressive block breaking cracks on the bottom-most trunk block (0.34 - 0.60)
            if (progress >= 0.34) {
                double punchProg = (progress - 0.34) / 0.26;
                int bx = trunkX;
                int by = trunkBottomY;
                int tbs = treeBlockSize;

                // Authentic Minecraft 4-stage block crack lines
                g2d.setColor(new Color(0, 0, 0, (int) (punchProg * 220)));

                if (punchProg > 0.15) {
                    g2d.drawLine(bx + 4, by + 3, bx + tbs - 6, by + tbs - 4);
                    g2d.drawLine(bx + tbs - 5, by + 4, bx + 5, by + tbs - 5);
                }
                if (punchProg > 0.40) {
                    g2d.drawLine(bx + tbs / 2, by + 2, bx + tbs / 2, by + tbs - 2);
                    g2d.drawLine(bx + 3, by + tbs / 2, bx + tbs - 3, by + tbs / 2);
                    g2d.drawLine(bx + 6, by + 6, bx + 16, by + 12);
                }
                if (punchProg > 0.70) {
                    g2d.drawLine(bx + 2, by + 8, bx + 18, by + 2);
                    g2d.drawLine(bx + 8, by + tbs - 2, bx + tbs - 2, by + 8);
                }

                // Wood breaking chips
                Random woodRand = new Random((long) (progress * 400));
                for (int i = 0; i < 5; i++) {
                    int px = bx + woodRand.nextInt(tbs);
                    int py = by + woodRand.nextInt(tbs);
                    g2d.setColor(new Color(115 + woodRand.nextInt(30), 85, 50));
                    g2d.fillRect(px, py, 3, 3);
                }
            }
        } else {
            // Upper tree leaves and remaining trunk (classic Minecraft floating tree!)
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 5; col++) {
                    DrawUtils.drawLeavesBlock(g2d, treeX + col * treeBlockSize, treeGroundY + row * treeBlockSize + treeBlockSize, treeBlockSize);
                }
            }
            for (int col = 1; col < 4; col++) {
                DrawUtils.drawLeavesBlock(g2d, treeX + col * treeBlockSize, treeGroundY, treeBlockSize);
            }
            // Trunk above the broken bottom block
            DrawUtils.drawOakLog(g2d, trunkX, treeGroundY + 2 * treeBlockSize, treeBlockSize);
            DrawUtils.drawOakLog(g2d, trunkX, treeGroundY + 3 * treeBlockSize, treeBlockSize);
            DrawUtils.drawOakLog(g2d, trunkX, treeGroundY + 4 * treeBlockSize, treeBlockSize);

            // Dropped Wood Item (pops out and bobs on floor, collected by Steve)
            if (progress < 0.70) {
                int dropY = trunkBottomY + 4 + (int) (Math.sin(progress * 20) * 3);
                DrawUtils.drawOakLog(g2d, trunkX + 4, dropY, 16);
            }
        }

        // 2. Steve Animation Phases (Spawn -> Look Around -> Turn to Tree -> Chop Wood)
        int spawnX = 130;
        int punchTargetX = trunkX - 24; // x = 254

        if (progress < 0.12) {
            // Phase A1: Steve spawns floating 1 block (32px) in the air and drops with gravity
            double spawnProg = progress / 0.12;
            float alpha = (float) Math.min(1.0, spawnProg * 2.0);

            // Spawn height: starts 1 block (32px) above ground, drops with gravity acceleration
            int airHeight = bs; // 32px (1 block)
            double fallT = Math.max(0.0, (spawnProg - 0.25) / 0.75);
            fallT = fallT * fallT; // Accelerating gravity fall
            int steveY = (int) ((groundY - 64 - airHeight) + fallT * airHeight);

            // Swirling Cyan/White Minecraft Spawn Particles in mid-air
            Random spRand = new Random((long) (progress * 600));
            for (int i = 0; i < 12; i++) {
                int ppx = spawnX + 8 + (int) ((spRand.nextDouble() - 0.5) * 44 * (1.0 - spawnProg));
                int ppy = (groundY - 64 - airHeight / 2) + (int) ((spRand.nextDouble() - 0.5) * 60);
                g2d.setColor(i % 2 == 0 ? new Color(120, 240, 255, 220) : new Color(255, 255, 255, 240));
                g2d.fillRect(ppx, ppy, 3, 3);
            }

            // Landing grass dust puff when hitting ground
            if (spawnProg > 0.85) {
                double landT = (spawnProg - 0.85) / 0.15;
                for (int i = 0; i < 6; i++) {
                    int lx = spawnX - 8 + i * 8 + (int) ((spRand.nextDouble() - 0.5) * landT * 16);
                    int ly = groundY - 4 - (int) (landT * 6);
                    g2d.setColor(new Color(110, 185, 60, (int) ((1.0 - landT) * 200)));
                    g2d.fillRect(lx, ly, 3, 3);
                }
            }

            // Draw Steve materializing in mid-air and falling to ground
            Composite origComp = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            DrawUtils.drawSteveStanding(g2d, spawnX, steveY, 1, false);
            g2d.setComposite(origComp);

        } else if (progress < 0.20) {
            // Phase A2: Steve landed on ground, looks around, turns RIGHT facing the oak tree!
            boolean facingTree = (progress >= 0.15);
            DrawUtils.drawSteveStanding(g2d, spawnX, groundY - 64, 1, facingTree);

        } else if (progress < 0.34) {
            // Phase B: Steve walks over from spawn position to the oak tree
            double t = DrawUtils.easeInOut((progress - 0.20) / 0.14);
            int steveX = (int) (spawnX + t * (punchTargetX - spawnX));
            DrawUtils.drawSteveWithTool(g2d, steveX, groundY - 64, 1, true, "hand", 0, true, (steveX - spawnX) * 0.4);

        } else if (progress < 0.60) {
            // Phase C: Standing still and punching tree vigorously
            double swingAngle = Math.sin((progress - 0.34) * 48) * Math.PI / 4;
            DrawUtils.drawSteveWithTool(g2d, punchTargetX, groundY - 64, 1, true, "hand", swingAngle, false, 0);

        } else if (progress < 0.70) {
            // Phase D: Stepping forward to pick up dropped wood
            double t = DrawUtils.easeInOut((progress - 0.60) / 0.10);
            int steveX = punchTargetX + (int) (t * 16);
            DrawUtils.drawSteveWithTool(g2d, steveX, groundY - 64, 1, true, "hand", 0, true, (steveX - punchTargetX) * 0.4);

        } else {
            // Phase E: Standing completely still and admiring the Crafting Table
            int steveX = punchTargetX + 16;
            DrawUtils.drawSteveStanding(g2d, steveX, groundY - 64, 1, true);
        }

        // 3. Crafting Table placed on ground (from 0.70 onward)
        int tableX = trunkX + 38;
        int tableY = groundY - bs;
        if (progress >= 0.70) {
            DrawUtils.drawCraftingTable(g2d, tableX, tableY, bs);

            // Placement smoke/dust puff particles (from 0.70 to 0.78)
            if (progress < 0.78) {
                double puffT = (progress - 0.70) / 0.08;
                Random puffRand = new Random(888);
                for (int i = 0; i < 8; i++) {
                    int px = tableX + puffRand.nextInt(bs) + (int) ((puffRand.nextDouble() - 0.5) * puffT * 20);
                    int py = tableY + puffRand.nextInt(bs) - (int) (puffT * 12);
                    int alpha = (int) ((1.0 - puffT) * 180);
                    g2d.setColor(new Color(220, 220, 220, Math.max(0, alpha)));
                    g2d.fillRect(px, py, 3, 3);
                }
            }
        }

        // HUD & Achievement
        DrawUtils.drawHUD(g2d, width, 10, 10, 10, (int) (progress * 10));
    }
}
