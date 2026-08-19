import java.awt.*;
import java.util.Random;

/**
 * Scene 7: Nether Portal Activation & Journey into the Nether Dimension.
 * Overworld: Steve lights the Obsidian frame, activating the rectangular Minecraft Nether Portal.
 * Nether Dimension: Authentic Netherrack terrain, Glowstone clusters, lava falls,
 * Nether fortress bridge in distance, Ghast floating, and rising flame embers.
 */
public class NetherPortalScene extends Scene {
    private final Random random = new Random(700);

    private static final int[][] FRAME_POSITIONS = {
        {0, 0}, {1, 0}, {2, 0}, {3, 0},
        {0, -1}, {3, -1},
        {0, -2}, {3, -2},
        {0, -3}, {3, -3},
        {0, -4}, {1, -4}, {2, -4}, {3, -4}
    };

    public NetherPortalScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        random.setSeed(700 + (long) (progress * 100));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int groundY = 430;
        int bs = 24; // 24px block size for landscape detail

        if (progress < 0.50) {
            // ==========================================
            // FIRST HALF: OVERWORLD
            // ==========================================
            double overworldProgress = progress * 2.0;

            // Night Sky
            g2d.setColor(DrawUtils.NIGHT_SKY);
            g2d.fillRect(0, 0, width, groundY);
            DrawUtils.drawStars(g2d, width, groundY, 50, 12345, overworldProgress);
            DrawUtils.drawMinecraftMoon(g2d, 500, 90, 36);

            // Ground
            DrawUtils.drawGround(g2d, width, height, groundY, bs);

            // Portal Frame base coords
            int px = width / 2 - 2 * bs;
            int py = groundY - bs;

            for (int i = 0; i < FRAME_POSITIONS.length; i++) {
                double appearTime = i * (0.2 / FRAME_POSITIONS.length);
                if (overworldProgress > appearTime) {
                    DrawUtils.drawObsidianBlock(g2d, px + FRAME_POSITIONS[i][0] * bs, py + FRAME_POSITIONS[i][1] * bs, bs);
                }
            }

            // Steve standing and lighting the portal
            DrawUtils.drawSteve(g2d, px - 3 * bs, groundY - 64, 1, true);

            // Flint & Steel Spark before activation (0.4 - 0.6)
            if (overworldProgress > 0.4 && overworldProgress < 0.6) {
                g2d.setColor(Color.YELLOW);
                for (int i = 0; i < 5; i++) {
                    g2d.fillRect(px + bs + random.nextInt(bs * 2), py - bs - random.nextInt(bs * 2), 3, 3);
                }
            }

            // Portal Activation (0.6 - 1.0 of overworld)
            if (overworldProgress > 0.6) {
                int portalW = 2 * bs;
                int portalH = 3 * bs;
                int portalX = px + bs;
                int portalY = py - 3 * bs;

                // Draw rectangular Minecraft Nether Portal texture
                DrawUtils.drawNetherPortalTexture(g2d, portalX, portalY, portalW, portalH, overworldProgress);

                // Purple portal square particles drifting upwards
                g2d.setColor(DrawUtils.PORTAL_PURPLE);
                for (int i = 0; i < 8; i++) {
                    int partX = portalX + random.nextInt(portalW);
                    int partY = portalY + random.nextInt(portalH);
                    g2d.fillRect(partX, partY, 3, 3);
                }
            }

            // HUD
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, 40);

        } else if (progress < 0.54) {
            // Transition flash into Nether
            double flash = (progress - 0.50) / 0.04;
            g2d.setColor(new Color(130, 20, 190, (int) (255 * (1.0 - flash))));
            g2d.fillRect(0, 0, width, height);

        } else {
            // ==========================================
            // SECOND HALF: AUTHENTIC NETHER LANDSCAPE
            // ==========================================
            double netherProgress = (progress - 0.54) / 0.46;

            // 1. Deep Crimson Nether Atmospheric Fog
            for (int y = 0; y < height; y += 4) {
                double t = (double) y / height;
                g2d.setColor(DrawUtils.lerpColor(new Color(45, 8, 12), new Color(110, 22, 22), t));
                g2d.fillRect(0, y, width, 4);
            }

            // 2. Hanging Netherrack Ceiling & Stalactites (Rows 0..3)
            for (int col = 0; col < width / bs + 1; col++) {
                int bx = col * bs;
                DrawUtils.drawNetherrackBlock(g2d, bx, 0, bs);
                DrawUtils.drawNetherrackBlock(g2d, bx, bs, bs);
                if (col % 3 == 0 || col % 5 == 0) {
                    DrawUtils.drawNetherrackBlock(g2d, bx, 2 * bs, bs);
                }
            }

            // 3. Hanging Glowstone Clusters from Ceiling
            int[][] glowClusters = {
                {4, 2}, {5, 2}, {5, 3}, {12, 2}, {13, 2}, {13, 3}, {14, 2}, {20, 2}, {21, 2}
            };
            for (int[] gc : glowClusters) {
                DrawUtils.drawGlowstoneBlock(g2d, gc[0] * bs, gc[1] * bs, bs);
            }

            // 4. Cascading Lava Fall from Ceiling (Cols 17..18)
            int lavaFallX = 17 * bs;
            for (int y = 2 * bs; y < groundY + 2 * bs; y += bs) {
                DrawUtils.drawLavaBlock(g2d, lavaFallX, y, bs, netherProgress * 15 + y);
            }

            // 5. Nether Fortress in the Distance (Across Lava Lake)
            int fortX = 11 * bs;
            int fortY = groundY - 4 * bs;
            // Fortress pillars
            for (int r = 0; r < 6; r++) {
                DrawUtils.drawNetherBrickBlock(g2d, fortX, fortY + r * bs, bs);
                DrawUtils.drawNetherBrickBlock(g2d, fortX + 3 * bs, fortY + r * bs, bs);
                DrawUtils.drawNetherBrickBlock(g2d, fortX + 7 * bs, fortY + r * bs, bs);
            }
            // Fortress Bridge walkway & battlements
            for (int c = 0; c < 9; c++) {
                DrawUtils.drawNetherBrickBlock(g2d, fortX + c * bs, fortY, bs);
                // Battlements
                if (c % 2 == 0) {
                    DrawUtils.drawNetherBrickBlock(g2d, fortX + c * bs, fortY - bs, bs);
                }
            }

            // 6. Vast Molten Lava Ocean (Right Valley)
            int lavaLakeY = groundY + 2 * bs;
            for (int x = 6 * bs; x < width; x += bs) {
                for (int y = lavaLakeY; y < height; y += bs) {
                    DrawUtils.drawLavaBlock(g2d, x, y, bs, netherProgress * 12 + x * 0.1);
                }
            }

            // 7. Left Netherrack Cliff & Ledge Terrain
            for (int c = 0; c < 7; c++) {
                int bx = c * bs;
                for (int y = groundY; y < height; y += bs) {
                    DrawUtils.drawNetherrackBlock(g2d, bx, y, bs);
                }
            }
            // Netherrack cliff steps
            DrawUtils.drawNetherrackBlock(g2d, 0, groundY - bs, bs);
            DrawUtils.drawNetherrackBlock(g2d, bs, groundY - bs, bs);
            DrawUtils.drawNetherrackBlock(g2d, 2 * bs, groundY - bs, bs);

            // 8. Nether Portal frame standing on Netherrack cliff
            int npx = bs;
            int npy = groundY - bs;
            for (int i = 0; i < FRAME_POSITIONS.length; i++) {
                DrawUtils.drawObsidianBlock(g2d, npx + FRAME_POSITIONS[i][0] * bs, npy + FRAME_POSITIONS[i][1] * bs, bs);
            }
            DrawUtils.drawNetherPortalTexture(g2d, npx + bs, npy - 3 * bs, 2 * bs, 3 * bs, netherProgress);

            // 9. Floating Ghast in Nether Sky
            int ghastX = (int) (420 - netherProgress * 60);
            int ghastY = 110 + (int) (Math.sin(netherProgress * 8) * 14);
            DrawUtils.drawGhast(g2d, ghastX, ghastY, 2);

            // 10. Steve stepping out of portal onto Netherrack ledge
            int steveX = (int) (npx + 3 * bs + netherProgress * 45);
            DrawUtils.drawSteveWithTool(g2d, steveX, groundY - 64, 1, true, "sword", 0);

            // 11. Rising Flame Embers & Smoke
            g2d.setColor(new Color(255, 120, 20, 200));
            for (int i = 0; i < 20; i++) {
                int fx = random.nextInt(width);
                int fy = groundY + random.nextInt(100) - (int) ((netherProgress * 150 + i * 25) % (height - 50));
                g2d.fillRect(fx, fy, 3, 3);
            }

            // HUD
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, 40);
        }
    }
}
