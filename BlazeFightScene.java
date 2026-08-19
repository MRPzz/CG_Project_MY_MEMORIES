import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

/**
 * Scene 8: Blaze Fight in the Nether Fortress.
 * Blaze shoots fireballs, and after 1 second, Steve dashes at hyper-speed
 * and unleashes a rapid diamond sword slash flurry to defeat the Blaze.
 */
public class BlazeFightScene extends Scene {
    private final Random random = new Random(850);

    public BlazeFightScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        random.setSeed(850 + (long) (progress * 100));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int bs = 24; // 24px block scale
        int bridgeY = 410; // Fortress bridge walkway level

        // 1. Dark Crimson Nether Atmosphere
        for (int y = 0; y < height; y += 4) {
            double t = (double) y / height;
            g2d.setColor(DrawUtils.lerpColor(new Color(40, 6, 10), new Color(100, 18, 18), t));
            g2d.fillRect(0, y, width, 4);
        }

        // 2. Hanging Netherrack Ceiling (Rows 0..2)
        for (int c = 0; c < width / bs + 1; c++) {
            DrawUtils.drawNetherrackBlock(g2d, c * bs, 0, bs);
            if (c % 2 == 0 || c % 3 == 0) {
                DrawUtils.drawNetherrackBlock(g2d, c * bs, bs, bs);
            }
        }

        // 3. Hanging Glowstone Clusters on Ceiling
        DrawUtils.drawGlowstoneBlock(g2d, 4 * bs, bs, bs);
        DrawUtils.drawGlowstoneBlock(g2d, 5 * bs, bs, bs);
        DrawUtils.drawGlowstoneBlock(g2d, 19 * bs, bs, bs);
        DrawUtils.drawGlowstoneBlock(g2d, 20 * bs, bs, bs);

        // 4. Molten Lava Ocean under the fortress
        int lavaY = bridgeY + 3 * bs;
        for (int x = 0; x < width; x += bs) {
            for (int y = lavaY; y < height; y += bs) {
                DrawUtils.drawLavaBlock(g2d, x, y, bs, progress * 10 + x * 0.1);
            }
        }

        // 5. Nether Fortress Bridge Structure (Authentic Nether Brick)
        // Tall Fortress Support Pillars reaching down to lava
        int[] pillarCols = {3, 11, 20};
        for (int pc : pillarCols) {
            for (int y = 2 * bs; y < height; y += bs) {
                DrawUtils.drawNetherBrickBlock(g2d, pc * bs, y, bs);
                DrawUtils.drawNetherBrickBlock(g2d, (pc + 1) * bs, y, bs);
            }
        }

        // Horizontal Bridge Walkway
        for (int c = 0; c < width / bs + 1; c++) {
            DrawUtils.drawNetherBrickBlock(g2d, c * bs, bridgeY, bs);
            DrawUtils.drawNetherBrickBlock(g2d, c * bs, bridgeY + bs, bs);

            // Fortress Battlements along walkway
            if (c % 2 == 0) {
                DrawUtils.drawNetherBrickBlock(g2d, c * bs, bridgeY - bs, bs);
            }
        }

        // 6. Combat Entities Coordinates
        int blazeX = 430;
        int blazeY = bridgeY - 80 + (int) (Math.sin(progress * Math.PI * 8) * 14);
        int steveY = bridgeY - 64;

        // 7. Blaze Fireballs (0.04 - 0.26)
        if (progress >= 0.04 && progress < 0.28) {
            for (int f = 0; f < 3; f++) {
                double fStart = 0.04 + f * 0.05;
                if (progress >= fStart) {
                    double fbP = Math.min(1.0, (progress - fStart) / 0.14);
                    int startX = blazeX - 20;
                    int startY = blazeY + 15 + f * 10;
                    int targetX = 80;
                    int targetY = bridgeY - 30 + f * 15;

                    int fbX = (int) (startX - fbP * (startX - targetX));
                    int fbY = (int) (startY + Math.sin(fbP * Math.PI) * -20 + fbP * (targetY - startY));

                    // Fireball core & flame
                    g2d.setColor(DrawUtils.GOLD_YELLOW);
                    g2d.fillRect(fbX, fbY, 8, 8);
                    g2d.setColor(DrawUtils.LAVA_ORANGE);
                    g2d.drawRect(fbX - 1, fbY - 1, 10, 10);

                    // Smoke trail
                    g2d.setColor(new Color(60, 60, 60, 160));
                    g2d.fillRect(fbX + 8, fbY + 2, 4, 4);
                }
            }
        }

        // 8. Steve Position & Dash / Attack Choreography
        int steveStartX = 130;
        int steveAttackX = blazeX - 42; // x = 388 (right in front of Blaze)
        int currentSteveX;

        if (progress < 0.20) {
            // Stage 1 (0 to 1 sec): Steve standing ready with diamond sword
            currentSteveX = steveStartX;
            DrawUtils.drawSteveWithTool(g2d, currentSteveX, steveY, 1, true, "sword", 0, false, 0);

        } else if (progress < 0.28) {
            // Stage 2 (1 sec mark): HYPER-SPEED DASH towards the Blaze!
            double dashT = DrawUtils.easeInOut((progress - 0.20) / 0.08);
            currentSteveX = (int) (steveStartX + dashT * (steveAttackX - steveStartX));

            // Dash takeoff dust & speedlines
            g2d.setColor(new Color(255, 160, 60, 180));
            g2d.fillRect(steveStartX - 6, bridgeY - 10, 14, 6);

            // Motion Blur Ghost Trails
            Composite origComp = g2d.getComposite();
            for (int g = 1; g <= 3; g++) {
                int ghostX = (int) (currentSteveX - g * 22 * (1.0 - dashT * 0.5));
                float gAlpha = (float) (0.35 - g * 0.10);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, gAlpha)));
                DrawUtils.drawSteveWithTool(g2d, ghostX, steveY, 1, true, "sword", 0.4, false, 0);
            }
            g2d.setComposite(origComp);

            // Draw real Steve dashing forward with lunging sword
            DrawUtils.drawSteveWithTool(g2d, currentSteveX, steveY, 1, true, "sword", 0.6, false, 0);

        } else if (progress < 0.58) {
            // Stage 3: RAPID 360-DEGREE DIAMOND SWORD FLURRY SLASH (หมุนดาบ 360 องศา)
            currentSteveX = steveAttackX;
            double rapidSwing = (progress - 0.28) * Math.PI * 28; // Continuous rapid 360-degree sword spin rotations
            DrawUtils.drawSteveWithTool(g2d, currentSteveX, steveY, 1, true, "sword", rapidSwing, false, 0);

            // Diamond Blue & White Slash Arcs (Swoosh effect)
            int slashCX = blazeX - 10;
            int slashCY = blazeY + 15;
            double slashAngle = progress * 40;
            g2d.setColor(new Color(120, 240, 255, 220));
            g2d.setStroke(new BasicStroke(3.0f));
            int arcOffset = (int) (Math.sin(slashAngle) * 20);
            g2d.drawLine(slashCX - 15, slashCY + arcOffset, slashCX + 25, slashCY - arcOffset);
            g2d.setColor(Color.WHITE);
            g2d.drawLine(slashCX - 10, slashCY + arcOffset - 2, slashCX + 20, slashCY - arcOffset - 2);
            g2d.setStroke(new BasicStroke(1.0f));

            // Critical Hit Star Sparks
            Random critRand = new Random((long) (progress * 500));
            for (int k = 0; k < 4; k++) {
                int sparkX = blazeX + critRand.nextInt(24) - 12;
                int sparkY = blazeY + critRand.nextInt(30) - 10;
                g2d.setColor(critRand.nextBoolean() ? DrawUtils.GOLD_YELLOW : Color.WHITE);
                g2d.fillRect(sparkX, sparkY, 3, 3);
            }

        } else if (progress < 0.75) {
            // Stage 4: Finishing pose as Blaze explodes
            currentSteveX = steveAttackX;
            DrawUtils.drawSteveWithTool(g2d, currentSteveX, steveY, 1, true, "sword", 0.2, false, 0);

        } else {
            // Stage 5: Steve collects Blaze Rod
            double collectT = DrawUtils.easeInOut((progress - 0.75) / 0.25);
            int rodX = blazeX - 10;
            currentSteveX = (int) (steveAttackX + collectT * (rodX - steveAttackX));
            DrawUtils.drawSteveWithTool(g2d, currentSteveX, steveY, 1, true, "sword", 0, true, (currentSteveX - steveAttackX) * 0.4);
        }

        // 9. Blaze Rendering & Death Explosion
        if (progress < 0.58) {
            // Blaze taking damage flash
            boolean isHit = (progress >= 0.28 && random.nextBoolean());
            if (isHit) {
                // Red damage tint
                g2d.setColor(new Color(255, 60, 60, 160));
                g2d.fillRect(blazeX - 16, blazeY - 16, 32, 48);
            }
            DrawUtils.drawBlaze(g2d, blazeX, blazeY, 2, progress);

        } else if (progress < 0.72) {
            // Blaze Death smoke and fire puff
            double deathP = (progress - 0.58) / 0.14;
            Random dRand = new Random(888);
            for (int i = 0; i < 18; i++) {
                int px = blazeX + (int) ((dRand.nextDouble() - 0.5) * deathP * 80);
                int py = blazeY + (int) ((dRand.nextDouble() - 0.5) * deathP * 80);
                g2d.setColor(dRand.nextBoolean() ? DrawUtils.LAVA_ORANGE : new Color(60, 60, 60));
                g2d.fillRect(px, py, 6, 6);
            }
        } else {
            // Dropped Blaze Rod Item (floating and bobbing)
            if (progress < 0.90) {
                int rodX = blazeX - 10;
                int rodY = bridgeY - 14 + (int) (Math.sin(progress * 15) * 3);
                g2d.setColor(DrawUtils.GOLD_YELLOW);
                g2d.fillRect(rodX, rodY, 16, 5);
                g2d.setColor(DrawUtils.LAVA_ORANGE);
                g2d.drawRect(rodX, rodY, 16, 5);
            }
        }

        // 10. Achievement
        if (progress >= 0.70) {
            DrawUtils.drawAchievement(g2d, width, "Into Fire", (progress - 0.70) / 0.25);
        }

        // 11. Rising Nether Embers
        g2d.setColor(new Color(255, 120, 20, 180));
        for (int i = 0; i < 15; i++) {
            int ex = random.nextInt(width);
            int ey = bridgeY + 20 - (int) ((progress * 200 + i * 30) % 300);
            g2d.fillRect(ex, ey, 3, 3);
        }

        // HUD
        DrawUtils.drawHUD(g2d, width, 8, 10, 8, 45);
    }
}
