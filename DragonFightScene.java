import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

/**
 * Scene 10: The End Dimension - Comedic Boss Fight.
 * Steve shoots 4 arrows with the bow and misses all of them, gets annoyed,
 * pulls out an AK-47 assault rifle, shoots the Ender Dragon directly with full-auto fire,
 * destroys the boss, and celebrates an epic victory.
 */
public class DragonFightScene extends Scene {
    private final Random random = new Random(800);

    public DragonFightScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        random.setSeed(800 + (long) (progress * 100));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Dark Void Sky of The End Dimension
        g2d.setColor(new Color(10, 6, 16));
        g2d.fillRect(0, 0, width, height);

        // End static purple haze
        g2d.setColor(new Color(45, 12, 65, 35));
        g2d.fillRect(0, 0, width, height);

        int bs = 24; // 24px block scale
        int groundY = 440;

        // 2. Central End Island Terrain (Authentic End Stone Layers)
        for (int c = 2; c < width / bs - 1; c++) {
            int bx = c * bs;
            for (int y = groundY; y < height; y += bs) {
                DrawUtils.drawEndStoneBlock(g2d, bx, y, bs);
            }
        }
        // Stepped natural edges on left & right of End island
        DrawUtils.drawEndStoneBlock(g2d, bs, groundY + bs, bs);
        DrawUtils.drawEndStoneBlock(g2d, (width / bs - 2) * bs, groundY + bs, bs);

        // 3. Central Bedrock Exit Portal Fountain (Center of Island: Cols 11..13)
        int fountainX = 11 * bs; // x = 264
        int fountainY = groundY - bs;
        DrawUtils.drawBedrockBlock(g2d, fountainX, fountainY, bs);
        DrawUtils.drawBedrockBlock(g2d, fountainX + bs, fountainY, bs);
        DrawUtils.drawBedrockBlock(g2d, fountainX + 2 * bs, fountainY, bs);
        // Central torch pillar on fountain
        DrawUtils.drawBedrockBlock(g2d, fountainX + bs, fountainY - bs, bs);
        DrawUtils.drawTorch(g2d, fountainX + bs + 4, fountainY - 2 * bs + 8);

        // Dragon Egg resting on the Bedrock fountain after dragon dies (progress >= 0.75)
        if (progress >= 0.75) {
            int eggX = fountainX + bs + 4;
            int eggY = fountainY - 2 * bs - 4;
            g2d.setColor(new Color(25, 20, 30));
            g2d.fillRect(eggX, eggY, 12, 14);
            g2d.fillRect(eggX + 2, eggY - 3, 8, 4);
            g2d.setColor(DrawUtils.ENDER_PURPLE);
            g2d.fillRect(eggX + 3, eggY + 3, 3, 3);
            g2d.fillRect(eggX + 7, eggY + 7, 3, 3);
        }

        // 4. Towering Obsidian Pillars with Bedrock Tops & End Crystals
        int[][] pillarData = {
            {4 * bs, groundY - 6 * bs, 2 * bs, 6 * bs},  // Left Pillar
            {16 * bs, groundY - 8 * bs, 2 * bs, 8 * bs}, // Right Tall Pillar
            {21 * bs, groundY - 5 * bs, 2 * bs, 5 * bs}  // Far Right Pillar
        };

        for (int i = 0; i < pillarData.length; i++) {
            int px = pillarData[i][0];
            int py = pillarData[i][1];
            int pw = pillarData[i][2];
            int ph = pillarData[i][3];

            // Obsidian shaft
            for (int bx = px; bx < px + pw; bx += bs) {
                for (int by = py; by < py + ph; by += bs) {
                    DrawUtils.drawObsidianBlock(g2d, bx, by, bs);
                }
            }

            // Bedrock block top
            DrawUtils.drawBedrockBlock(g2d, px, py - bs, bs);
            DrawUtils.drawBedrockBlock(g2d, px + bs, py - bs, bs);

            // End Crystal on Bedrock top
            int cx = px + pw / 2;
            int cy = py - 2 * bs + 4;

            // Floating Crystal Base
            g2d.setColor(new Color(40, 40, 40));
            g2d.fillRect(cx - 6, cy + 8, 12, 4);

            // Rotating Glass Crystal Box
            AffineTransform crystalOld = g2d.getTransform();
            g2d.translate(cx, cy);
            g2d.rotate(progress * Math.PI * 4);
            g2d.setColor(new Color(255, 140, 240, 180));
            g2d.drawRect(-6, -6, 12, 12);
            g2d.setColor(new Color(255, 80, 220));
            g2d.fillRect(-3, -3, 6, 6);
            g2d.setTransform(crystalOld);

            // Healing beam to Ender Dragon (0.00 - 0.28)
            if (progress < 0.28 && i == 1) {
                g2d.setColor(new Color(255, 120, 255, 160));
                int dx = (int) (300 + Math.sin(progress * Math.PI * 4) * 120);
                int dy = (int) (130 + Math.cos(progress * Math.PI * 2) * 40);
                g2d.drawLine(cx, cy, dx, dy);
                g2d.setColor(Color.WHITE);
                g2d.fillRect((cx + dx) / 2, (cy + dy) / 2, 3, 3);
            }
        }

        // 5. Steve Weapon Progression & Comedic Narrative
        int steveX = (int) (110 + Math.min(progress, 0.70) * 40);
        int steveY = groundY - 64;

        boolean isFiringAK = (progress >= 0.30 && progress <= 0.65);
        double akRecoil = isFiringAK ? (Math.sin(progress * 120) * 0.08) : 0;

        if (progress < 0.22) {
            // Beat 1: Steve shooting 4 consecutive arrows with bow (and missing all of them!)
            DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "bow", 0, false, 0);

            // 4 Consecutive Arrow Shots:
            // Shot 1 (0.02 - 0.07): Flies too high
            if (progress >= 0.02 && progress < 0.07) {
                double aP = (progress - 0.02) / 0.05;
                int ax = (int) (steveX + 16 + aP * 240);
                int ay = (int) (steveY + 16 - aP * 120);
                g2d.setColor(Color.WHITE);
                g2d.drawLine(ax, ay, ax + 8, ay - 4);
            }
            // Shot 2 (0.07 - 0.12): Flies too low
            if (progress >= 0.07 && progress < 0.12) {
                double aP = (progress - 0.07) / 0.05;
                int ax = (int) (steveX + 16 + aP * 220);
                int ay = (int) (steveY + 16 - Math.sin(aP * Math.PI) * 40 + aP * aP * 70);
                g2d.setColor(Color.WHITE);
                g2d.drawLine(ax, ay, ax + 8, ay - 2);
            }
            // Shot 3 (0.12 - 0.17): Flies wide right into the void
            if (progress >= 0.12 && progress < 0.17) {
                double aP = (progress - 0.12) / 0.05;
                int ax = (int) (steveX + 16 + aP * 280);
                int ay = (int) (steveY + 16 - Math.sin(aP * Math.PI) * 60 - aP * 20);
                g2d.setColor(Color.WHITE);
                g2d.drawLine(ax, ay, ax + 8, ay - 3);
            }
            // Shot 4 (0.17 - 0.22): Drops short with wobbly physics
            if (progress >= 0.17 && progress < 0.22) {
                double aP = (progress - 0.17) / 0.05;
                int ax = (int) (steveX + 16 + aP * 140);
                int ay = (int) (steveY + 16 - Math.sin(aP * Math.PI) * 30 + aP * aP * 60);
                g2d.setColor(Color.WHITE);
                g2d.drawLine(ax, ay, ax + 7, ay - 1);
            }

            // Comedic "Missed! (0/4)" text
            if (progress >= 0.16) {
                DrawUtils.drawMinecraftText(g2d, "Miss x4...?", steveX - 4, steveY - 14, 16, Color.YELLOW);
            }

        } else if (progress < 0.30) {
            // Beat 2: Comedic reaction - Steve puts down bow, gets frustrated, pulls out AK-47!
            if (progress < 0.26) {
                DrawUtils.drawSteveStanding(g2d, steveX, steveY, 1, true);
                DrawUtils.drawMinecraftText(g2d, "...?", steveX + 6, steveY - 14, 18, Color.YELLOW);
            } else {
                // Steve reaches behind and whips out AK-47!
                DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "ak", 0, false, 0);
                DrawUtils.drawMinecraftText(g2d, "!", steveX + 10, steveY - 14, 22, Color.RED);
            }

        } else if (progress < 0.85) {
            // Beat 3: Full-Auto AK-47 spraying directly at the Ender Dragon!
            DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "ak", akRecoil, false, 0);

        } else {
            // Beat 4: Victory pose - Steve raising AK-47 proudly in the air!
            DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "ak", -0.4, false, 0);
        }

        // 6. AK-47 Muzzle Flash, Ejected Shell Casings & High-Velocity Bullet Tracers
        if (isFiringAK) {
            int muzzleX = steveX + 24;
            int muzzleY = steveY + 12;

            // Flash effect & casings
            Random gunRand = new Random((long) (progress * 800));
            if (gunRand.nextDouble() > 0.20) {
                // Starburst yellow/orange muzzle flash
                g2d.setColor(Color.YELLOW);
                g2d.fillRect(muzzleX + 2, muzzleY - 3, 8, 6);
                g2d.fillRect(muzzleX + 4, muzzleY - 5, 4, 10);
                g2d.setColor(DrawUtils.LAVA_ORANGE);
                g2d.fillRect(muzzleX + 1, muzzleY - 2, 6, 4);

                // Flying brass shell casing
                int shellX = steveX + 6 - gunRand.nextInt(12);
                int shellY = steveY + 10 + gunRand.nextInt(16);
                g2d.setColor(DrawUtils.GOLD_YELLOW);
                g2d.fillRect(shellX, shellY, 3, 2);
            }

            // Stream of High-Velocity Bullet Tracers directly targeting the Ender Dragon!
            int targetX = 300;
            int targetY = 130;
            for (int b = 0; b < 6; b++) {
                double bProg = ((progress * 45 + b * 0.16) % 1.0);
                int bx = (int) (muzzleX + bProg * (targetX - muzzleX) + (gunRand.nextDouble() - 0.5) * 20);
                int by = (int) (muzzleY + bProg * (targetY - muzzleY) + (gunRand.nextDouble() - 0.5) * 20);
                g2d.setColor(Color.YELLOW);
                g2d.drawLine(bx, by, bx + 10, by - 4);
                g2d.setColor(Color.WHITE);
                g2d.drawLine(bx + 3, by - 1, bx + 7, by - 3);
            }
        }

        // 7. Ender Dragon Boss
        if (progress < 0.95) {
            int dx = (int) (300 + Math.sin(progress * Math.PI * 3) * 120);
            int dy = (int) (130 + Math.cos(progress * Math.PI * 2) * 30);

            if (progress > 0.30 && progress < 0.65) {
                // Dragon taking heavy damage, shaking and recoiling under AK-47 bullet impacts
                dx += (int) (Math.sin(progress * 45) * 18);
                dy += (int) (Math.cos(progress * 35) * 14);
            } else if (progress >= 0.65) {
                // Death sequence: Dragon centered directly above bedrock fountain
                double deathApproach = Math.min(1.0, (progress - 0.65) / 0.05);
                dx = (int) (dx + (300 - dx) * deathApproach);
                dy = (int) (dy + (130 - dy) * deathApproach);
            }

            AffineTransform oldDragonTrans = g2d.getTransform();
            g2d.translate(dx, dy);

            if (progress >= 0.65) {
                // Radiant Purple Death Rays Shooting Outward (Minecraft Boss Death)
                double deathP = (progress - 0.65) / 0.18;
                Random rayRand = new Random(777);
                for (int r = 0; r < 20; r++) {
                    double angle = rayRand.nextDouble() * 2 * Math.PI;
                    int rayLen = (int) (deathP * 180);
                    int rx = (int) (Math.cos(angle) * rayLen);
                    int ry = (int) (Math.sin(angle) * rayLen);
                    g2d.setColor(new Color(255, 130 + rayRand.nextInt(100), 255, (int) ((1.0 - Math.min(1.0, deathP)) * 230)));
                    g2d.setStroke(new BasicStroke(3.5f));
                    g2d.drawLine(0, 0, rx, ry);
                }
                g2d.setStroke(new BasicStroke(1.0f));

                // Dragon disintegration fragments
                g2d.setColor(DrawUtils.DRAGON_BLACK);
                for (int i = 0; i < 10; i++) {
                    int fx = (int) ((rayRand.nextDouble() - 0.5) * deathP * 140);
                    int fy = (int) ((rayRand.nextDouble() - 0.5) * deathP * 140);
                    g2d.fillRect(fx, fy, 8, 8);
                }
            } else {
                // Draw Authentic Voxel Ender Dragon
                // Bullet hit damage red tint when being sprayed by AK-47
                boolean hitFlash = (isFiringAK && progress >= 0.30 && random.nextBoolean());
                Color dragonColor = hitFlash ? new Color(180, 40, 40) : DrawUtils.DRAGON_BLACK;

                // Main Body
                g2d.setColor(dragonColor);
                g2d.fillRect(-28, -10, 56, 20);
                g2d.fillRect(28, -6, 20, 12);  // Neck
                g2d.fillRect(48, -12, 18, 16); // Head
                g2d.fillRect(-46, -6, 18, 12); // Tail

                // Dragon Horns
                g2d.setColor(DrawUtils.DRAGON_GRAY);
                g2d.fillRect(52, -16, 4, 5);
                g2d.fillRect(58, -16, 4, 5);

                // Glowing Purple Eyes
                g2d.setColor(DrawUtils.ENDER_PURPLE);
                g2d.fillRect(58, -8, 4, 4);

                // Flapping Dragon Wings
                double flap = Math.sin(progress * Math.PI * 18) * 28;
                GeneralPath wing1 = new GeneralPath();
                wing1.moveTo(-12, -10);
                wing1.quadTo(0, -36 - flap, 36, -18 - flap / 2);
                wing1.quadTo(10, -10, 12, -10);
                wing1.closePath();
                g2d.setColor(dragonColor);
                g2d.fill(wing1);

                GeneralPath wing2 = new GeneralPath();
                wing2.moveTo(-12, 10);
                wing2.quadTo(0, 36 + flap, 36, 18 + flap / 2);
                wing2.quadTo(10, 10, 12, 10);
                wing2.closePath();
                g2d.fill(wing2);
            }
            g2d.setTransform(oldDragonTrans);
        }

        // 8. Massive XP Orbs Fountain from Dragon Death (0.68 - 1.00)
        if (progress >= 0.68) {
            double t = (progress - 0.68) / 0.32;
            Random xpRand = new Random(999);
            for (int i = 0; i < 45; i++) {
                double angle = xpRand.nextDouble() * 2 * Math.PI;
                double speed = 2 + xpRand.nextDouble() * 6;

                // Expand outward from dragon center
                double orbX = 300 + Math.cos(angle) * speed * t * 110;
                double orbY = 130 + Math.sin(angle) * speed * t * 110;

                // Accelerating magnetic attraction towards Steve
                if (t > 0.30) {
                    double attr = (t - 0.30) / 0.70;
                    attr = attr * attr;
                    orbX = orbX + (steveX + 16 - orbX) * attr;
                    orbY = orbY + (steveY + 32 - orbY) * attr;
                }

                // XP Green & Yellow Orbs
                g2d.setColor(i % 2 == 0 ? DrawUtils.XP_GREEN : Color.YELLOW);
                g2d.fillRect((int) orbX, (int) orbY, 4, 4);
            }
        }

        // 9. Grand Victory Banners & Achievements
        if (progress >= 0.72) {
            DrawUtils.drawAchievement(g2d, width, "Free the End", (progress - 0.72) / 0.20);
        }

        if (progress >= 0.82) {
            // "VICTORY ACHIEVED" / "THE END" Grand Banner
            double bannerP = Math.min(1.0, (progress - 0.82) / 0.08);
            int bannerAlpha = (int) (bannerP * 255);
            g2d.setColor(new Color(255, 215, 0, bannerAlpha));
            DrawUtils.drawMinecraftText(g2d, "THE END - VICTORY ACHIEVED!", width / 2 - 180, 80, 20, new Color(255, 215, 0, bannerAlpha));
            DrawUtils.drawMinecraftText(g2d, "Thanks for watching!", width / 2 - 100, 110, 16, new Color(220, 220, 220, bannerAlpha));
        }

        // 10. HUD (XP level increases rapidly to 100 upon dragon defeat)
        int currentXP = (progress < 0.68) ? 50 : (int) (50 + (progress - 0.68) / 0.32 * 50);
        DrawUtils.drawHUD(g2d, width, 10, 10, 8, currentXP);
    }
}
