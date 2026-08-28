import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class Assignment1_67050476_67050613 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("MINECRAFT - MY MEMORIES");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 600);
            frame.setResizable(false);
            frame.add(new AnimationPanel());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // =========================================================================
    // ANIMATION PANEL & SCENE MANAGER
    // =========================================================================
    static class AnimationPanel extends JPanel implements ActionListener {
        private final List<Scene> scenes = new ArrayList<>();
        private final Timer timer;
        private long startTime = -1;
        private final int TRANSITION_MS = 800;
        private final PixelCanvas canvas = new PixelCanvas(600, 600);

        public AnimationPanel() {
            scenes.add(new TitleScene("Title", 4000));
            scenes.add(new CreateWorldScene("CreateWorld", 4000));
            scenes.add(new FirstDayScene("FirstDay", 5000));
            scenes.add(new FirstNightScene("FirstNight", 5000));
            scenes.add(new MiningScene("Mining", 5000));
            scenes.add(new BuildHomeScene("BuildHome", 5000));
            scenes.add(new NetherPortalScene("NetherPortal", 5000));
            scenes.add(new BlazeFightScene("BlazeFight", 5000));
            scenes.add(new EyeOfEnderScene("EyeOfEnder", 7000));
            scenes.add(new DragonFightScene("DragonFight", 13000));
            timer = new Timer(16, this);
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (startTime == -1) startTime = System.currentTimeMillis();
            long elapsed = System.currentTimeMillis() - startTime;
            int idx = 0; long acc = 0;

            while (idx < scenes.size()) {
                int dur = scenes.get(idx).getDurationMs();
                if (elapsed < acc + dur) break;
                acc += dur;
                idx++;
            }

            canvas.clear(Color.BLACK);
            if (idx >= scenes.size()) {
                if (!scenes.isEmpty()) scenes.get(scenes.size() - 1).render(canvas, getWidth(), getHeight(), 1.0);
                g.drawImage(canvas.getBufferedImage(), 0, 0, null);
                return;
            }

            Scene sc = scenes.get(idx);
            long scElapsed = elapsed - acc;
            double prog = (double) scElapsed / sc.getDurationMs();

            sc.render(canvas, getWidth(), getHeight(), prog);
            if (scElapsed > sc.getDurationMs() - TRANSITION_MS && idx < scenes.size() - 1) {
                double tp = (double) (scElapsed - (sc.getDurationMs() - TRANSITION_MS)) / TRANSITION_MS;
                tp = tp * tp * (3 - 2 * tp);
                canvas.setAlpha((float) Math.min(1.0, tp));
                canvas.setColor(Color.BLACK);
                canvas.fillRect(0, 0, getWidth(), getHeight());
                canvas.setAlpha(1.0f);
            }
            g.drawImage(canvas.getBufferedImage(), 0, 0, null);
        }

        @Override
        public void actionPerformed(ActionEvent e) { repaint(); }
    }

    // =========================================================================
    // BASE SCENE
    // =========================================================================
    static abstract class Scene {
        protected final String name;
        protected final int durationMs;

        public Scene(String name, int durationMs) {
            this.name = name;
            this.durationMs = durationMs;
        }
        public int getDurationMs() { return durationMs; }
        public abstract void render(PixelCanvas g2d, int width, int height, double progress);
    }

    // =========================================================================
    // SCENE 1: TITLE SCENE
    // =========================================================================
    static class TitleScene extends Scene {
        public TitleScene(String name, int durationMs) { super(name, durationMs); }

        @Override
        public void render(PixelCanvas g2d, int width, int height, double progress) {
            for (int i = 0; i < height; i += 5) {
                g2d.setColor(DrawUtils.lerpColor(new Color(20, 20, 60), Color.BLACK, (double) i / height));
                g2d.fillRect(0, i, width, 5);
            }

            int creeperX = (int) (width + 40 - progress * (width + 100));
            DrawUtils.drawCreeper(g2d, creeperX, height - 112, 1);

            int titleY = 150 + (int) (Math.sin(progress * Math.PI * 4) * 10);
            DrawUtils.drawMinecraftText(g2d, "MINECRAFT", 111, titleY, 56, DrawUtils.GOLD_YELLOW);

            AffineTransform old = g2d.getTransform();
            g2d.translate(450, titleY - 20);
            g2d.rotate(-Math.PI / 8 + Math.sin(progress * Math.PI * 8) * 0.1);
            DrawUtils.drawMinecraftText(g2d, "Now with Java 2D!", 0, 0, 16, Color.YELLOW);
            g2d.setTransform(old);

            int btnW = 300, btnH = 40, btnX = width / 2 - btnW / 2, btnY = height - 200;
            int spState = (progress >= 0.85) ? 2 : (progress >= 0.70 ? 1 : 0);
            DrawUtils.drawMinecraftButton(g2d, btnX, btnY, btnW, btnH, "Singleplayer", spState);
            DrawUtils.drawMinecraftButton(g2d, btnX, btnY + 50, btnW, btnH, "Multiplayer", 0);
            DrawUtils.drawMinecraftButton(g2d, btnX, btnY + 100, btnW, btnH, "Options...", 0);

            double eased = DrawUtils.easeInOut(Math.min(1.0, progress / 0.70));
            int curX = (int) (400 + (btnX + btnW / 2 - 400) * eased);
            int curY = (int) (100 + (btnY + btnH / 2 - 100) * eased);
            DrawUtils.drawMinecraftCursor(g2d, curX, curY);
        }
    }

    // =========================================================================
    // SCENE 2: CREATE WORLD SCENE
    // =========================================================================
    static class CreateWorldScene extends Scene {
        public CreateWorldScene(String name, int durationMs) { super(name, durationMs); }

        @Override
        public void render(PixelCanvas g2d, int width, int height, double progress) {
            g2d.setColor(new Color(30, 30, 30));
            g2d.fillRect(0, 0, width, height);

            if (progress < 0.55) {
                DrawUtils.drawMinecraftText(g2d, "Create New World", 156, 45, 24, Color.WHITE);
                g2d.setColor(Color.BLACK);
                g2d.fillRect(150, 80, 300, 40);
                g2d.setColor(Color.GRAY);
                g2d.drawRect(150, 80, 300, 40);

                String fullText = "New World";
                int chars = (int) Math.min(fullText.length(), (progress / 0.4) * fullText.length());
                DrawUtils.drawMinecraftText(g2d, fullText.substring(0, chars) + (progress % 0.1 < 0.05 && progress < 0.4 ? "_" : ""), 160, 93, 16, Color.WHITE);

                int btnW = 300, btnH = 40, btnX = 150;
                DrawUtils.drawMinecraftButton(g2d, btnX, 140, btnW, btnH, "Game Mode: Survival", 0);
                DrawUtils.drawMinecraftButton(g2d, btnX, 190, btnW, btnH, "Difficulty: Normal", 0);
                DrawUtils.drawMinecraftButton(g2d, btnX, 240, btnW, btnH, "Allow Cheats: OFF", 0);
                int createState = (progress >= 0.5) ? 2 : (progress >= 0.45 ? 1 : 0);
                DrawUtils.drawMinecraftButton(g2d, btnX, height - 100, btnW, btnH, "Create New World", createState);
            } else {
                double lp = Math.min(1.0, (progress - 0.55) / 0.4);
                DrawUtils.drawMinecraftText(g2d, "Building terrain...", 196, 250, 18, Color.WHITE);
                g2d.setColor(Color.BLACK);
                g2d.fillRect(100, 300, 400, 20);
                g2d.setColor(DrawUtils.XP_GREEN);
                g2d.fillRect(100, 300, (int) (400 * lp), 20);
                String pctStr = (int) (lp * 100) + "%";
                DrawUtils.drawMinecraftText(g2d, pctStr, width / 2 - pctStr.length() * 6, 350, 18, Color.WHITE);
            }

            if (progress >= 0.95) {
                g2d.setAlpha((float) Math.min(1.0, (progress - 0.95) / 0.05));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, width, height);
                g2d.setAlpha(1.0f);
            }
        }
    }

    // =========================================================================
    // SCENE 3: FIRST DAY SCENE
    // =========================================================================
    static class FirstDayScene extends Scene {
        public FirstDayScene(String name, int durationMs) { super(name, durationMs); }

        @Override
        public void render(PixelCanvas g2d, int width, int height, double progress) {
            DrawUtils.drawSky(g2d, width, height, progress * 0.1);
            DrawUtils.drawMinecraftSun(g2d, 80 + (int) (progress * 420), 70 + (int) (Math.sin(progress * Math.PI) * -30), 38);

            int bs = 32, groundY = 450, tbs = 24;
            DrawUtils.drawGround(g2d, width, height, groundY, bs);

            int treeX = 230, trunkX = treeX + 48, treeGroundY = groundY - 144;

            if (progress < 0.60) {
                DrawUtils.drawTree(g2d, treeX, treeGroundY, 3);
                if (progress >= 0.34) {
                    double punchProg = (progress - 0.34) / 0.26;
                    g2d.setColor(new Color(0, 0, 0, (int) (punchProg * 220)));
                    g2d.drawLine(trunkX + 4, groundY - tbs + 3, trunkX + tbs - 6, groundY - 4);
                    g2d.drawLine(trunkX + tbs / 2, groundY - tbs + 2, trunkX + tbs / 2, groundY - 2);
                }
            } else {
                for (int r = 0; r < 2; r++) {
                    for (int c = 0; c < 5; c++) DrawUtils.drawBlock(g2d, treeX + c * tbs, treeGroundY + (r + 1) * tbs, tbs, "leaves");
                }
                for (int c = 1; c < 4; c++) DrawUtils.drawBlock(g2d, treeX + c * tbs, treeGroundY, tbs, "leaves");
                for (int r = 2; r <= 4; r++) DrawUtils.drawBlock(g2d, trunkX, treeGroundY + r * tbs, tbs, "log");

                if (progress < 0.70) {
                    int dropY = groundY - tbs + 4 + (int) (Math.sin(progress * 20) * 3);
                    DrawUtils.drawBlock(g2d, trunkX + 4, dropY, 16, "log");
                }
            }

            int spawnX = 130, punchTargetX = trunkX - 24;
            if (progress < 0.12) {
                double fallT = Math.max(0.0, (progress / 0.12 - 0.25) / 0.75);
                DrawUtils.drawSteveStanding(g2d, spawnX, (int) ((groundY - 64 - bs) + (fallT * fallT) * bs), 1, false);
            } else if (progress < 0.20) {
                DrawUtils.drawSteveStanding(g2d, spawnX, groundY - 64, 1, progress >= 0.15);
            } else if (progress < 0.34) {
                double t = DrawUtils.easeInOut((progress - 0.20) / 0.14);
                int sx = (int) (spawnX + t * (punchTargetX - spawnX));
                DrawUtils.drawSteveWithTool(g2d, sx, groundY - 64, 1, true, "hand", 0, true, (sx - spawnX) * 0.4);
            } else if (progress < 0.60) {
                DrawUtils.drawSteveWithTool(g2d, punchTargetX, groundY - 64, 1, true, "hand", Math.sin((progress - 0.34) * 48) * Math.PI / 4, false, 0);
            } else if (progress < 0.70) {
                double t = DrawUtils.easeInOut((progress - 0.60) / 0.10);
                int sx = punchTargetX + (int) (t * 16);
                DrawUtils.drawSteveWithTool(g2d, sx, groundY - 64, 1, true, "hand", 0, true, (sx - punchTargetX) * 0.4);
            } else {
                DrawUtils.drawSteveStanding(g2d, punchTargetX + 16, groundY - 64, 1, true);
            }

            if (progress >= 0.70) DrawUtils.drawBlock(g2d, trunkX + 38, groundY - bs, bs, "crafting_table");
            DrawUtils.drawHUD(g2d, width, 10, 10, 10, (int) (progress * 10));
            if (progress >= 0.62) DrawUtils.drawAchievement(g2d, width, "Getting Wood", (progress - 0.62) / 0.25);
        }
    }

    // =========================================================================
    // SCENE 4: FIRST NIGHT SCENE
    // =========================================================================
    static class FirstNightScene extends Scene {
        public FirstNightScene(String name, int durationMs) { super(name, durationMs); }

        @Override
        public void render(PixelCanvas g2d, int width, int height, double progress) {
            DrawUtils.drawSky(g2d, width, height, 0.15 + (progress * 0.40));
            if (progress < 0.35) DrawUtils.drawMinecraftSun(g2d, 80, (int) (90 + DrawUtils.easeInOut(progress / 0.35) * 400), 36);
            if (progress > 0.25) DrawUtils.drawMinecraftMoon(g2d, 500, (int) (420 - DrawUtils.easeInOut(Math.min(1.0, (progress - 0.25) / 0.50)) * 320), 36);
            if (progress > 0.30) DrawUtils.drawStars(g2d, width, height, 50, 12345, progress);

            int bs = 32, groundY = 450;
            DrawUtils.drawGround(g2d, width, height, groundY, bs);

            if (progress >= 0.50) {
                double zp = Math.min(1.0, (progress - 0.50) / 0.40);
                DrawUtils.drawZombie(g2d, (int) (-20 + zp * 220), groundY - 64, 1, progress * 10);
            }

            int hutX = 80, hutY = groundY - 3 * bs, doorX = hutX + 3 * bs, doorY = groundY - 2 * bs;
            if (progress > 0.15) {
                g2d.setColor(new Color(75, 52, 34));
                g2d.fillRect(hutX + bs, groundY - 2 * bs, 2 * bs, 2 * bs);
            }

            int[][] order = {{0, 2}, {0, 1}, {0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}};
            int built = (int) (Math.min(1.0, progress / 0.32) * order.length);
            for (int i = 0; i < built; i++) {
                DrawUtils.drawBlock(g2d, hutX + order[i][0] * bs, hutY + order[i][1] * bs, bs, order[i][1] == 0 ? "grass" : "dirt");
            }

            if (progress > 0.46) DrawUtils.drawTorch(g2d, hutX + bs + 4, groundY - 2 * bs + 8);

            boolean doorPlaced = (progress >= 0.38), doorOpen = (progress >= 0.38 && progress < 0.54);
            if (doorPlaced) DrawUtils.drawWoodenDoor(g2d, doorX, doorY, bs, 2 * bs, doorOpen);

            int outX = hutX + 5 * bs + 25, inX = hutX + bs + 6;
            if (progress < 0.34) {
                DrawUtils.drawSteveWithTool(g2d, outX, groundY - 64, 1, false, "dirt", 0, false, 0);
            } else if (progress < 0.42) {
                double t = DrawUtils.easeInOut((progress - 0.34) / 0.08);
                int sx = (int) (outX - t * (outX - (doorX + 20)));
                DrawUtils.drawSteveWithTool(g2d, sx, groundY - 64, 1, false, "door", 0, true, (outX - sx) * 0.4);
            } else if (progress < 0.54) {
                double t = DrawUtils.easeInOut((progress - 0.42) / 0.12);
                DrawUtils.drawSteveWithTool(g2d, (int) ((doorX + 20) - t * ((doorX + 20) - inX)), groundY - 64, 1, false, "hand", 0, true, t * 8);
            } else {
                DrawUtils.drawSteveStanding(g2d, inX, groundY - 64, 1, true);
            }

            if (doorPlaced && !doorOpen) DrawUtils.drawWoodenDoor(g2d, doorX, doorY, bs, 2 * bs, false);

            if (progress >= 0.58) {
                double cp = Math.min(1.0, (progress - 0.58) / 0.22);
                int cx = (int) (560 - DrawUtils.easeInOut(cp) * 300) + (progress >= 0.75 ? (int) (Math.sin(progress * 80) * 2) : 0);
                DrawUtils.drawCreeper(g2d, cx, groundY - 52, 1);
            }
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, 20);
        }
    }

    // =========================================================================
    // SCENE 5: MINING SCENE
    // =========================================================================
    static class MiningScene extends Scene {
        private static final int COLS = 30, ROWS = 30, BS = DrawUtils.BLOCK_SIZE;
        private final byte[][] world = new byte[ROWS][COLS];

        public MiningScene(String name, int durationMs) {
            super(name, durationMs);
            Random r = new Random(456);
            int[] ceil = {30,30,30,30, 16,15,14,14,13,13,13,13,13,13,13,13,13,13,14,14,15,15,16,17, 30,30,30,30,30,30};
            int[] flr = {0,0,0,0, 24,24,25,26,26,26,26,25,24,22,22,22,23,23,24,24,25,25,25,25, 0,0,0,0,0,0};

            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (col >= 4 && col <= 23 && row >= ceil[col] && row < flr[col]) world[row][col] = 0;
                    else {
                        double roll = r.nextDouble();
                        world[row][col] = (byte) (roll < 0.05 && row > 4 ? 3 : (roll < 0.085 && row > 8 ? 4 : (roll < 0.10 && row > 16 ? 5 : (roll < 0.18 ? 2 : 1))));
                    }
                }
            }
            world[23][24] = 6; world[23][25] = 6; world[24][24] = 6; world[24][25] = 6;
        }

        @Override
        public void render(PixelCanvas g2d, int width, int height, double progress) {
            int currentDug = (int) (Math.min(1.0, progress / 0.38) * 13);
            g2d.setColor(new Color(15, 12, 18));
            g2d.fillRect(0, 0, width, height);

            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLS; c++) {
                    if ((c == 13 || c == 14) && r <= currentDug) continue;
                    if (progress >= 0.78 && (r == 23 || r == 24) && (c == 24 || c == 25)) continue;
                    byte t = world[r][c];
                    if (t == 1) DrawUtils.drawBlock(g2d, c * BS, r * BS, BS, "stone");
                    else if (t == 2) DrawUtils.drawBlock(g2d, c * BS, r * BS, BS, "cobble");
                    else if (t == 3) DrawUtils.drawOreBlock(g2d, c * BS, r * BS, BS, "coal");
                    else if (t == 4) DrawUtils.drawOreBlock(g2d, c * BS, r * BS, BS, "iron");
                    else if (t == 5) DrawUtils.drawOreBlock(g2d, c * BS, r * BS, BS, "gold");
                    else if (t == 6) DrawUtils.drawOreBlock(g2d, c * BS, r * BS, BS, "diamond");
                }
            }

            int torchX = 15 * BS, torchY = 21 * BS, diamondX = 24 * BS, diamondY = 23 * BS, ledgeY = 22 * BS - 64;
            if (progress > 0.44) DrawUtils.drawTorch(g2d, torchX, torchY);

            int sx, sy;
            if (progress < 0.38) {
                DrawUtils.drawSteveWithTool(g2d, 13 * BS + 4, currentDug * BS - 64, 1, true, "iron_pickaxe", Math.sin(progress * 50) * Math.PI / 4, false, 0);
            } else if (progress < 0.46) {
                double dt = (progress - 0.38) / 0.08;
                DrawUtils.drawSteveWithTool(g2d, 13 * BS + 4, (int) ((13 * BS - 64) + dt * dt * (ledgeY - (13 * BS - 64))), 1, true, "iron_pickaxe", 0.2, false, 0);
            } else if (progress < 0.58) {
                double wt = DrawUtils.easeInOut((progress - 0.46) / 0.12);
                sx = 13 * BS + 4 + (int) (wt * (diamondX - 45 - (13 * BS + 4)));
                DrawUtils.drawSteveWithTool(g2d, sx, ledgeY + (int) (wt * (3 * BS)), 1, true, "iron_pickaxe", 0, true, sx * 0.35);
            } else if (progress < 0.66) {
                DrawUtils.drawSteveWithTool(g2d, diamondX - 45, ledgeY + 3 * BS, 1, true, "iron_pickaxe", 0, false, 0);
                DrawUtils.drawMinecraftText(g2d, "!", diamondX - 33, ledgeY + 3 * BS - 14, 22, Color.WHITE);
            } else if (progress < 0.78) {
                DrawUtils.drawSteveWithTool(g2d, diamondX - 45, ledgeY + 3 * BS, 1, true, "iron_pickaxe", Math.sin((progress - 0.66) * 35) * Math.PI / 4, false, 0);
                g2d.setColor(new Color(0, 0, 0, (int) (((progress - 0.66) / 0.12) * 220)));
                g2d.drawLine(diamondX + 4, diamondY + 4, diamondX + BS * 2 - 4, diamondY + BS * 2 - 4);
            } else {
                sy = ledgeY + 3 * BS - (int) (Math.max(0, Math.sin((progress - 0.78) * Math.PI * 6)) * 16);
                DrawUtils.drawSteveWithTool(g2d, diamondX - 45, sy, 1, true, "iron_pickaxe", -0.3, false, 0);

                int gemY = diamondY + 15 + (int) (Math.sin(progress * 16) * 5);
                g2d.setColor(DrawUtils.DIAMOND_BLUE);
                g2d.fillRect(diamondX + 12, gemY, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(diamondX + 14, gemY + 2, 3, 3);

                Random xpRand = new Random(777);
                for (int i = 0; i < 6; i++) {
                    double ang = xpRand.nextDouble() * 2 * Math.PI;
                    MidpointDrawing.fillCircle(g2d, diamondX + 15 + (int) (Math.cos(ang) * (progress - 0.78) * 120), diamondY + 15 + (int) (Math.sin(ang) * (progress - 0.78) * 120), 3, DrawUtils.XP_GREEN);
                }
            }

            DrawUtils.drawCaveDarkness(g2d, width, height, torchX, torchY, 130);
            if (progress >= 0.78) DrawUtils.drawAchievement(g2d, width, "DIAMONDS!", (progress - 0.78) / 0.22);
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, 20 + (int) (progress * 40));
        }
    }

    // =========================================================================
    // SCENE 6: BUILD HOME SCENE
    // =========================================================================
    static class BuildHomeScene extends Scene {
        public BuildHomeScene(String name, int durationMs) { super(name, durationMs); }

        @Override
        public void render(PixelCanvas g2d, int width, int height, double progress) {
            DrawUtils.drawSky(g2d, width, height, 0.22 + (progress * 0.12));
            DrawUtils.drawMinecraftSun(g2d, (int) (490 - progress * 90), (int) (110 + progress * 130), 36);

            int bs = 32, groundY = 440, hx = 60, doorX = hx + 2 * bs, doorY = groundY - 2 * bs;
            DrawUtils.drawGround(g2d, width, height, groundY, bs);

            double pf = Math.min(1.0, progress / 0.14);
            for (int i = 0; i < 6; i++) {
                if (pf > i / 6.0) DrawUtils.drawBlock(g2d, hx + i * bs, groundY - bs, bs, "cobble");
            }

            if (progress > 0.14) {
                double pw = Math.min(1.0, (progress - 0.14) / 0.24);
                for (int r = 1; r <= 2; r++) {
                    for (int c = 0; c < 6; c++) {
                        if (pw > (r * 6 + c) / 18.0) {
                            String type = (c == 0 || c == 5) ? "log" : (c == 1 || c == 4 ? "glass" : "plank");
                            if (c != 2) DrawUtils.drawBlock(g2d, hx + c * bs, groundY - bs - r * bs, bs, type);
                        }
                    }
                }
            }

            if (progress > 0.38) {
                double pr = Math.min(1.0, (progress - 0.38) / 0.17);
                for (int c = 0; c < 6; c++) {
                    if (pr > c / 6.0) DrawUtils.drawBlock(g2d, hx + c * bs, groundY - 3 * bs - (c >= 1 && c <= 4 ? (c == 2 || c == 3 ? bs : bs / 2) : 0), bs, "cobble");
                }
            }

            boolean doorPlaced = (progress >= 0.55), doorOpen = (progress >= 0.78 && progress < 0.88);
            if (doorPlaced) DrawUtils.drawWoodenDoor(g2d, doorX, doorY, bs, 2 * bs, doorOpen);

            if (progress > 0.60) {
                DrawUtils.drawTorch(g2d, hx + 3 * bs + 4, groundY - 2 * bs + 8);
                int chX = hx + 4 * bs + 4, chY = groundY - 4 * bs;
                for (int i = 0; i < 4; i++) {
                    double sp = ((progress * 3.5) + (i * 0.25)) % 1.0;
                    g2d.setColor(new Color(120, 120, 120, (int) ((1.0 - sp) * 160)));
                    g2d.fillRect(chX + (int) (Math.sin(progress * 8 + i) * 6), chY - (int) (sp * 40), 5, 5);
                }
            }

            int outX = 400, inX = doorX - 10;
            if (progress < 0.70) {
                DrawUtils.drawSteveWithTool(g2d, outX, groundY - 64, 1, false, "wood", 0, false, 0);
            } else if (progress < 0.86) {
                double t = DrawUtils.easeInOut((progress - 0.70) / 0.16);
                int sx = (int) (outX - t * (outX - inX));
                DrawUtils.drawSteveWithTool(g2d, sx, groundY - 64, 1, false, "hand", 0, true, (outX - sx) * 0.35);
            } else {
                DrawUtils.drawSteveStanding(g2d, inX, groundY - 64, 1, true);
            }

            if (doorPlaced && !doorOpen && progress >= 0.88) DrawUtils.drawWoodenDoor(g2d, doorX, doorY, bs, 2 * bs, false);
            if (progress >= 0.90) DrawUtils.drawAchievement(g2d, width, "Home Sweet Home", (progress - 0.90) / 0.10);
            DrawUtils.drawHUD(g2d, width, 10, 10, 10, 30);
        }
    }

    // =========================================================================
    // SCENE 7: NETHER PORTAL SCENE
    // =========================================================================
    static class NetherPortalScene extends Scene {
        private final Random rand = new Random(700);
        private static final int[][] FRAME = {{0,0},{1,0},{2,0},{3,0},{0,-1},{3,-1},{0,-2},{3,-2},{0,-3},{3,-3},{0,-4},{1,-4},{2,-4},{3,-4}};

        public NetherPortalScene(String name, int durationMs) { super(name, durationMs); }

        @Override
        public void render(PixelCanvas g2d, int width, int height, double progress) {
            rand.setSeed(700 + (long) (progress * 100));
            int groundY = 430, bs = 24;

            if (progress < 0.50) {
                double op = progress * 2.0;
                g2d.setColor(DrawUtils.NIGHT_SKY);
                g2d.fillRect(0, 0, width, groundY);
                DrawUtils.drawStars(g2d, width, groundY, 50, 12345, op);
                DrawUtils.drawMinecraftMoon(g2d, 500, 90, 36);
                DrawUtils.drawGround(g2d, width, height, groundY, bs);

                int px = width / 2 - 2 * bs, py = groundY - bs;
                for (int i = 0; i < FRAME.length; i++) {
                    if (op > i * (0.2 / FRAME.length)) DrawUtils.drawBlock(g2d, px + FRAME[i][0] * bs, py + FRAME[i][1] * bs, bs, "obsidian");
                }
                DrawUtils.drawSteve(g2d, px - 3 * bs, groundY - 64, 1, true);

                if (op > 0.4 && op < 0.6) {
                    g2d.setColor(Color.YELLOW);
                    for (int i = 0; i < 5; i++) g2d.fillRect(px + bs + rand.nextInt(bs * 2), py - bs - rand.nextInt(bs * 2), 3, 3);
                }
                if (op > 0.6) DrawUtils.drawNetherPortalTexture(g2d, px + bs, py - 3 * bs, 2 * bs, 3 * bs, op);
                DrawUtils.drawHUD(g2d, width, 10, 10, 8, 40);

            } else if (progress < 0.54) {
                g2d.setColor(new Color(130, 20, 190, (int) (255 * (1.0 - (progress - 0.50) / 0.04))));
                g2d.fillRect(0, 0, width, height);

            } else {
                double np = (progress - 0.54) / 0.46;
                for (int y = 0; y < height; y += 4) {
                    g2d.setColor(DrawUtils.lerpColor(new Color(45, 8, 12), new Color(110, 22, 22), (double) y / height));
                    g2d.fillRect(0, y, width, 4);
                }

                for (int c = 0; c < width / bs + 1; c++) {
                    DrawUtils.drawBlock(g2d, c * bs, 0, bs, "netherrack");
                    DrawUtils.drawBlock(g2d, c * bs, bs, bs, "netherrack");
                }
                DrawUtils.drawBlock(g2d, 4 * bs, 2 * bs, bs, "glowstone");
                DrawUtils.drawBlock(g2d, 13 * bs, 2 * bs, bs, "glowstone");

                for (int y = 2 * bs; y < groundY + 2 * bs; y += bs) DrawUtils.drawLavaBlock(g2d, 17 * bs, y, bs, np * 15 + y);
                for (int x = 6 * bs; x < width; x += bs) {
                    for (int y = groundY + 2 * bs; y < height; y += bs) DrawUtils.drawLavaBlock(g2d, x, y, bs, np * 12 + x * 0.1);
                }
                for (int c = 0; c < 7; c++) {
                    for (int y = groundY; y < height; y += bs) DrawUtils.drawBlock(g2d, c * bs, y, bs, "netherrack");
                }

                int npx = bs, npy = groundY - bs;
                for (int[] p : FRAME) DrawUtils.drawBlock(g2d, npx + p[0] * bs, npy + p[1] * bs, bs, "obsidian");
                DrawUtils.drawNetherPortalTexture(g2d, npx + bs, npy - 3 * bs, 2 * bs, 3 * bs, np);

                int gx = (int) (420 - np * 60), gy = 110 + (int) (Math.sin(np * 8) * 14);
                DrawUtils.drawGhast(g2d, gx, gy, 2);

                if (np > 0.5) {
                    int fbX = gx - (int) ((np - 0.5) / 0.5 * 300), fbY = gy + 20;
                    g2d.setColor(Color.YELLOW);
                    g2d.fillRect(fbX, fbY, 8, 8);
                    g2d.setColor(DrawUtils.LAVA_ORANGE);
                    g2d.drawRect(fbX, fbY, 8, 8);
                }

                DrawUtils.drawSteveWithTool(g2d, (int) (npx + 3 * bs + np * 45), groundY - 64, 1, true, "sword", 0);
                if (progress >= 0.60) DrawUtils.drawAchievement(g2d, width, "We Need to Go Deeper", (progress - 0.60) / 0.25);
                DrawUtils.drawHUD(g2d, width, 10, 10, 8, 40);
            }
        }
    }

    // =========================================================================
    // SCENE 8: BLAZE FIGHT SCENE
    // =========================================================================
    static class BlazeFightScene extends Scene {
        public BlazeFightScene(String name, int durationMs) { super(name, durationMs); }

        @Override
        public void render(PixelCanvas g2d, int width, int height, double progress) {
            int bs = 24, bridgeY = 410;
            for (int y = 0; y < height; y += 4) {
                g2d.setColor(DrawUtils.lerpColor(new Color(40, 6, 10), new Color(100, 18, 18), (double) y / height));
                g2d.fillRect(0, y, width, 4);
            }
            for (int x = 0; x < width; x += bs) {
                for (int y = bridgeY + 3 * bs; y < height; y += bs) DrawUtils.drawLavaBlock(g2d, x, y, bs, progress * 10 + x * 0.1);
            }

            int[] pillars = {3, 11, 20};
            for (int p : pillars) {
                for (int y = 2 * bs; y < height; y += bs) DrawUtils.drawBlock(g2d, p * bs, y, bs, "netherbrick");
            }
            for (int c = 0; c < width / bs + 1; c++) {
                DrawUtils.drawBlock(g2d, c * bs, bridgeY, bs, "netherbrick");
                DrawUtils.drawBlock(g2d, c * bs, bridgeY + bs, bs, "netherbrick");
            }

            int blazeX = 430, blazeY = bridgeY - 80 + (int) (Math.sin(progress * Math.PI * 8) * 14);
            int steveY = bridgeY - 64;

            if (progress >= 0.04 && progress < 0.28) {
                for (int f = 0; f < 3; f++) {
                    double fStart = 0.04 + f * 0.05;
                    if (progress >= fStart) {
                        double fbP = Math.min(1.0, (progress - fStart) / 0.14);
                        int fbX = (int) ((blazeX - 20) - fbP * ((blazeX - 20) - 80));
                        int fbY = (int) ((blazeY + 15 + f * 10) + Math.sin(fbP * Math.PI) * -20 + fbP * (bridgeY - 30 - blazeY));
                        g2d.setColor(DrawUtils.GOLD_YELLOW);
                        g2d.fillRect(fbX, fbY, 8, 8);
                        g2d.setColor(DrawUtils.LAVA_ORANGE);
                        g2d.drawRect(fbX, fbY, 8, 8);
                    }
                }
            }

            int startX = 130, attackX = blazeX - 42;
            if (progress < 0.20) {
                DrawUtils.drawSteveWithTool(g2d, startX, steveY, 1, true, "sword", 0, false, 0);
            } else if (progress < 0.28) {
                int sx = (int) (startX + DrawUtils.easeInOut((progress - 0.20) / 0.08) * (attackX - startX));
                DrawUtils.drawSteveWithTool(g2d, sx, steveY, 1, true, "sword", 0.6, false, 0);
            } else if (progress < 0.58) {
                DrawUtils.drawSteveWithTool(g2d, attackX, steveY, 1, true, "sword", (progress - 0.28) * Math.PI * 28, false, 0);
                int scX = blazeX - 10, scY = blazeY + 15, arc = (int) (Math.sin(progress * 40) * 20);
                g2d.setColor(new Color(120, 240, 255, 220));
                g2d.setStrokeWidth(3.0f);
                g2d.drawBezierQuadratic(scX - 25, scY + arc, scX, scY - arc - 15, scX + 25, scY + arc, 16);
                g2d.setStrokeWidth(1.0f);
            } else if (progress < 0.75) {
                DrawUtils.drawSteveWithTool(g2d, attackX, steveY, 1, true, "sword", 0.2, false, 0);
            } else {
                double ct = DrawUtils.easeInOut((progress - 0.75) / 0.25);
                DrawUtils.drawSteveWithTool(g2d, (int) (attackX + ct * (blazeX - 10 - attackX)), steveY, 1, true, "sword", 0, true, ct * 10);
            }

            if (progress < 0.58) {
                DrawUtils.drawBlaze(g2d, blazeX, blazeY, 2, progress);
            } else if (progress < 0.72) {
                double dp = (progress - 0.58) / 0.14;
                Random dr = new Random(888);
                for (int i = 0; i < 15; i++) {
                    g2d.setColor(dr.nextBoolean() ? DrawUtils.LAVA_ORANGE : new Color(60, 60, 60));
                    g2d.fillRect(blazeX + (int) ((dr.nextDouble() - 0.5) * dp * 80), blazeY + (int) ((dr.nextDouble() - 0.5) * dp * 80), 6, 6);
                }
            } else if (progress < 0.90) {
                g2d.setColor(DrawUtils.GOLD_YELLOW);
                g2d.fillRect(blazeX - 10, bridgeY - 14 + (int) (Math.sin(progress * 15) * 3), 16, 5);
            }

            if (progress >= 0.70) DrawUtils.drawAchievement(g2d, width, "Into Fire", (progress - 0.70) / 0.25);
            DrawUtils.drawHUD(g2d, width, 8, 10, 8, 45);
        }
    }

    // =========================================================================
    // SCENE 9: EYE OF ENDER SCENE
    // =========================================================================
    static class EyeOfEnderScene extends Scene {
        public EyeOfEnderScene(String name, int durationMs) { super(name, durationMs); }

        @Override
        public void render(PixelCanvas g2d, int width, int height, double progress) {
            int bs = 24;
            if (progress < 0.50) {
                double op = progress * 2.0;
                DrawUtils.drawSky(g2d, width, height, 0.25);
                DrawUtils.drawMinecraftSun(g2d, 520, 70, 36);
                int groundY = 440, sx = 130 + (int) (op * 40);
                DrawUtils.drawGround(g2d, width, height, groundY, bs);

                if (op > 0.15 && op < 0.90) {
                    double et = (op - 0.15) / 0.75;
                    int eyeX = sx + 24 + (int) (et * 290), eyeY = (groundY - 50) - (int) (Math.sin(et * Math.PI) * 190) - (int) (et * 30);
                    g2d.setColor(new Color(25, 200, 160, 80));
                    g2d.drawBezierQuadratic(sx + 24, groundY - 50, sx + 160, groundY - 260, sx + 314, groundY - 80, 24);

                    g2d.setColor(new Color(20, 150, 120));
                    g2d.fillRect(eyeX - 4, eyeY - 4, 8, 8);
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(eyeX - 1, eyeY - 3, 2, 6);
                    DrawUtils.drawSteveWithTool(g2d, sx, groundY - 64, 1, true, "hand", 0.3, false, 0);
                } else {
                    DrawUtils.drawSteveStanding(g2d, sx, groundY - 64, 1, true);
                }
                DrawUtils.drawHUD(g2d, width, 10, 10, 8, 48);

            } else if (progress < 0.54) {
                g2d.setColor(new Color(0, 0, 0, (int) (255 * (1.0 - (progress - 0.50) / 0.04))));
                g2d.fillRect(0, 0, width, height);

            } else {
                double pp = (progress - 0.54) / 0.46;
                g2d.setColor(new Color(12, 10, 15));
                g2d.fillRect(0, 0, width, height);

                int floorY = 440;
                for (int c = 0; c < width / bs + 1; c++) {
                    DrawUtils.drawStoneBrickBlock(g2d, c * bs, floorY, bs, (c % 5 == 0) ? 1 : 0);
                    DrawUtils.drawStoneBrickBlock(g2d, c * bs, floorY + bs, bs, 0);
                    DrawUtils.drawStoneBrickBlock(g2d, c * bs, 0, bs, 0);
                }
                DrawUtils.drawTorch(g2d, 3 * bs + 4, floorY - 4 * bs);
                DrawUtils.drawTorch(g2d, 21 * bs - 4, floorY - 4 * bs);

                int pbx = 9 * bs, pby = floorY - 2 * bs;
                for (int c = 10; c <= 14; c++) DrawUtils.drawLavaBlock(g2d, c * bs, floorY - bs, bs, pp * 10 + c);

                boolean lastEye = (pp >= 0.35);
                DrawUtils.drawEndPortalFrame(g2d, pbx + bs, pby, bs, true);
                DrawUtils.drawEndPortalFrame(g2d, pbx + 5 * bs, pby, bs, lastEye);

                if (lastEye) DrawUtils.drawEndPortalPlane(g2d, pbx + 2 * bs, pby, 3 * bs, bs, pp);

                int appX = pbx + 5 * bs + 24, sy = floorY - 64;
                if (pp < 0.35) DrawUtils.drawSteveWithTool(g2d, appX, sy, 1, false, "hand", 0, false, 0);
                else if (pp < 0.65) DrawUtils.drawSteveStanding(g2d, appX, sy, 1, false);
                else {
                    double jt = (pp - 0.65) / 0.35;
                    int jx = (int) (appX - jt * (appX - (pbx + 3 * bs)));
                    int jy = (int) (sy - Math.sin(jt * Math.PI) * 40 + jt * jt * 40);
                    g2d.setAlpha((float) Math.max(0.0, 1.0 - jt * 1.6));
                    DrawUtils.drawSteve(g2d, jx, jy, 1, false);
                    g2d.setAlpha(1.0f);
                }

                if (pp >= 0.40) DrawUtils.drawAchievement(g2d, width, "Eye Spy", (pp - 0.40) / 0.25);
                DrawUtils.drawHUD(g2d, width, 10, 10, 8, 50);
            }
        }
    }

    // =========================================================================
    // SCENE 10: DRAGON FIGHT SCENE
    // =========================================================================
    static class DragonFightScene extends Scene {
        public DragonFightScene(String name, int durationMs) { super(name, durationMs); }

        @Override
        public void render(PixelCanvas g2d, int width, int height, double progress) {
            g2d.setColor(new Color(10, 6, 16));
            g2d.fillRect(0, 0, width, height);

            int bs = 24, groundY = 440;
            for (int c = 2; c < width / bs - 1; c++) {
                for (int y = groundY; y < height; y += bs) DrawUtils.drawBlock(g2d, c * bs, y, bs, "end_stone");
            }

            int fX = 11 * bs, fY = groundY - bs;
            DrawUtils.drawBlock(g2d, fX, fY, bs, "bedrock");
            DrawUtils.drawBlock(g2d, fX + bs, fY, bs, "bedrock");
            DrawUtils.drawBlock(g2d, fX + 2 * bs, fY, bs, "bedrock");

            if (progress >= 0.75) {
                g2d.setColor(new Color(25, 20, 30));
                g2d.fillRect(fX + bs + 4, fY - 2 * bs - 4, 12, 14);
                g2d.setColor(DrawUtils.ENDER_PURPLE);
                g2d.fillRect(fX + bs + 7, fY - 2 * bs - 1, 3, 3);
            }

            int[][] pillars = {{4 * bs, groundY - 6 * bs, 2 * bs, 6 * bs}, {16 * bs, groundY - 8 * bs, 2 * bs, 8 * bs}};
            for (int[] p : pillars) {
                for (int bx = p[0]; bx < p[0] + p[2]; bx += bs) {
                    for (int by = p[1]; by < p[1] + p[3]; by += bs) DrawUtils.drawBlock(g2d, bx, by, bs, "obsidian");
                }
                DrawUtils.drawBlock(g2d, p[0], p[1] - bs, bs, "bedrock");
                DrawUtils.drawBlock(g2d, p[0] + bs, p[1] - bs, bs, "bedrock");

                AffineTransform old = g2d.getTransform();
                g2d.translate(p[0] + p[2] / 2, p[1] - 2 * bs + 4);
                g2d.rotate(progress * Math.PI * 4);
                g2d.setColor(new Color(255, 140, 240, 180));
                g2d.drawRect(-6, -6, 12, 12);
                g2d.setColor(new Color(255, 80, 220));
                g2d.fillRect(-3, -3, 6, 6);
                g2d.setTransform(old);
            }

            g2d.setColor(new Color(20, 20, 20));
            g2d.fillRect(204, groundY - 68, 6, 68);
            g2d.fillRect(504, groundY - 68, 6, 68);
            g2d.setColor(DrawUtils.ENDER_PURPLE);
            g2d.fillRect(205, groundY - 64, 2, 2);
            g2d.fillRect(505, groundY - 64, 2, 2);

            int sx = (int) (110 + Math.min(progress, 0.70) * 40), sy = groundY - 64;
            boolean isFiringAK = (progress >= 0.30 && progress <= 0.65);
            double akRecoil = isFiringAK ? (Math.sin(progress * 120) * 0.08) : 0;

            if (progress < 0.22) {
                DrawUtils.drawSteveWithTool(g2d, sx, sy, 1, true, "bow", 0, false, 0);
                for (int s = 0; s < 4; s++) {
                    double sStart = s * 0.05 + 0.02;
                    if (progress >= sStart && progress < sStart + 0.05) {
                        double ap = (progress - sStart) / 0.05;
                        int ax = (int) (sx + 16 + ap * (220 + s * 20));
                        int ay = (int) (sy + 16 - (s == 0 ? ap * 120 : (s == 1 ? -ap * ap * 70 : (s == 2 ? ap * 20 : -ap * ap * 60))));
                        g2d.setColor(Color.WHITE);
                        g2d.drawLine(ax, ay, ax + 8, ay - 2);
                    }
                }
            } else if (progress < 0.30) {
                DrawUtils.drawSteveWithTool(g2d, sx, sy, 1, true, progress < 0.26 ? "hand" : "ak", 0, false, 0);
            } else if (progress < 0.85) {
                DrawUtils.drawSteveWithTool(g2d, sx, sy, 1, true, "ak", akRecoil, false, 0);
            } else {
                DrawUtils.drawSteveWithTool(g2d, sx, sy, 1, true, "ak", -0.4, false, 0);
            }

            if (isFiringAK) {
                int mx = sx + 24, my = sy + 12;
                g2d.setColor(Color.YELLOW);
                g2d.fillRect(mx + 2, my - 3, 8, 6);
                for (int b = 0; b < 6; b++) {
                    double bp = ((progress * 45 + b * 0.16) % 1.0);
                    int bx = (int) (mx + bp * (300 - mx)), by = (int) (my + bp * (130 - my));
                    g2d.drawLine(bx, by, bx + 10, by - 4);
                }
            }

            if (progress < 0.95) {
                int dx = (int) (300 + Math.sin(progress * Math.PI * 3) * 120);
                int dy = (int) (130 + Math.cos(progress * Math.PI * 2) * 30);
                if (progress > 0.30 && progress < 0.65) {
                    dx += (int) (Math.sin(progress * 45) * 18);
                    dy += (int) (Math.cos(progress * 35) * 14);
                }

                AffineTransform old = g2d.getTransform();
                g2d.translate(dx, dy);

                if (progress >= 0.65) {
                    double dp = (progress - 0.65) / 0.18;
                    Random rr = new Random(777);
                    for (int r = 0; r < 16; r++) {
                        double ang = rr.nextDouble() * 2 * Math.PI, rlen = dp * 180;
                        g2d.setColor(new Color(255, 150, 255, (int) ((1.0 - Math.min(1.0, dp)) * 230)));
                        g2d.setStrokeWidth(3.0f);
                        g2d.drawLine(0, 0, (int) (Math.cos(ang) * rlen), (int) (Math.sin(ang) * rlen));
                    }
                    g2d.setStrokeWidth(1.0f);
                } else {
                    Color dcol = (isFiringAK && (int) (progress * 100) % 2 == 0) ? new Color(180, 40, 40) : DrawUtils.DRAGON_BLACK;
                    g2d.setColor(dcol);
                    g2d.fillRect(-28, -10, 56, 20);
                    g2d.fillRect(28, -6, 20, 12);
                    g2d.fillRect(48, -12, 18, 16);
                    g2d.fillRect(-46, -6, 18, 12);
                    g2d.setColor(DrawUtils.ENDER_PURPLE);
                    g2d.fillRect(58, -8, 4, 4);

                    double flap = Math.sin(progress * Math.PI * 18) * 28;
                    g2d.setColor(dcol);
                    g2d.fillBezierWing(-12, -10, 0, -36 - flap, 36, -18 - flap / 2, 10, -10);
                    g2d.fillBezierWing(-12, 10, 0, 36 + flap, 36, 18 + flap / 2, 10, 10);
                }
                g2d.setTransform(old);
            }

            if (progress >= 0.68) {
                double t = (progress - 0.68) / 0.32;
                Random xr = new Random(999);
                for (int i = 0; i < 30; i++) {
                    double ang = xr.nextDouble() * 2 * Math.PI, spd = 2 + xr.nextDouble() * 6;
                    double ox = 300 + Math.cos(ang) * spd * t * 110, oy = 130 + Math.sin(ang) * spd * t * 110;
                    if (t > 0.30) {
                        double at = Math.pow((t - 0.30) / 0.70, 2);
                        ox += (sx + 16 - ox) * at; oy += (sy + 32 - oy) * at;
                    }
                    g2d.setColor(i % 2 == 0 ? DrawUtils.XP_GREEN : Color.YELLOW);
                    g2d.fillRect((int) ox, (int) oy, 4, 4);
                }
            }

            if (progress >= 0.72) DrawUtils.drawAchievement(g2d, width, "Free the End", (progress - 0.72) / 0.20);
            if (progress >= 0.82) {
                DrawUtils.drawMinecraftText(g2d, "THE END - VICTORY ACHIEVED!", width / 2 - 162, 80, 20, new Color(255, 215, 0));
                DrawUtils.drawMinecraftText(g2d, "Thanks for watching!", width / 2 - 120, 110, 16, new Color(220, 220, 220));
            }
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, progress < 0.68 ? 50 : (int) (50 + (progress - 0.68) / 0.32 * 50));
        }
    }

    // =========================================================================
    // DRAWUTILS - PROCEDURAL RENDERING UTILITIES
    // =========================================================================
    static class DrawUtils {
        public static final Color SKY_BLUE = new Color(135, 206, 235), GRASS_TOP = new Color(105, 178, 55), GRASS_SIDE = new Color(92, 160, 48);
        public static final Color DIRT_BROWN = new Color(134, 96, 67), DIRT_DARK = new Color(107, 74, 49), STONE_GRAY = new Color(128, 128, 128);
        public static final Color STONE_DARK = new Color(95, 95, 95), WOOD_BROWN = new Color(162, 130, 78), WOOD_DARK = new Color(125, 98, 55);
        public static final Color LOG_SIDE = new Color(108, 85, 52), LEAVES_GREEN = new Color(46, 115, 25), LEAVES_DARK = new Color(32, 85, 18);
        public static final Color DIAMOND_BLUE = new Color(80, 225, 235), NIGHT_SKY = new Color(10, 10, 32), SUNSET_ORANGE = new Color(255, 135, 45);
        public static final Color SUNSET_PINK = new Color(245, 90, 120), STEVE_SKIN = new Color(188, 142, 105), STEVE_SHIRT = new Color(45, 165, 180);
        public static final Color STEVE_PANTS = new Color(55, 55, 165), STEVE_SHOES = new Color(75, 75, 75), CREEPER_GREEN = new Color(75, 145, 45);
        public static final Color CREEPER_DARK = new Color(45, 95, 28), OBSIDIAN = new Color(22, 18, 35), GOLD_YELLOW = new Color(255, 205, 45);
        public static final Color LAVA_ORANGE = new Color(215, 95, 18), HEART_RED = new Color(195, 25, 25), XP_GREEN = new Color(115, 225, 45);
        public static final Color ENDER_PURPLE = new Color(85, 0, 130), DRAGON_BLACK = new Color(28, 28, 28);
        public static final int BLOCK_SIZE = 20;

        public static void drawBlock(PixelCanvas g, int x, int y, int size, String type) {
            Color base = DIRT_BROWN;
            if ("grass".equals(type)) {
                drawBlock(g, x, y, size, "dirt");
                g.setColor(GRASS_TOP); g.fillRect(x, y, size, size / 4);
                g.setColor(GRASS_SIDE);
                for (int i = 0; i < 4; i++) g.fillRect(x + i * (size / 4), y + size / 4, size / 4, i % 2 == 0 ? 3 : 5);
                return;
            } else if ("dirt".equals(type)) base = DIRT_BROWN;
            else if ("stone".equals(type)) base = STONE_GRAY;
            else if ("cobble".equals(type)) {
                g.setColor(STONE_GRAY); g.fillRect(x, y, size, size);
                g.setColor(STONE_DARK); g.fillRect(x + 1, y + size / 3, size - 2, 2); g.fillRect(x + 1, y + 2 * size / 3, size - 2, 2);
                g.setColor(new Color(0, 0, 0, 50)); g.drawRect(x, y, size, size);
                return;
            } else if ("log".equals(type)) {
                g.setColor(LOG_SIDE); g.fillRect(x, y, size, size);
                g.setColor(new Color(65, 48, 28)); g.fillRect(x + 3, y, 2, size); g.fillRect(x + size - 6, y, 2, size);
                g.setColor(new Color(0, 0, 0, 50)); g.drawRect(x, y, size, size);
                return;
            } else if ("leaves".equals(type)) base = LEAVES_GREEN;
            else if ("plank".equals(type)) {
                g.setColor(new Color(160, 115, 68)); g.fillRect(x, y, size, size);
                g.setColor(new Color(110, 75, 40)); g.fillRect(x, y + size / 2, size, 1);
                g.setColor(new Color(0, 0, 0, 45)); g.drawRect(x, y, size, size);
                return;
            } else if ("glass".equals(type)) {
                g.setColor(new Color(200, 235, 255, 60)); g.fillRect(x, y, size, size);
                g.setColor(Color.WHITE); g.fillRect(x + 2, y + 2, 3, 2);
                g.setColor(new Color(150, 195, 225, 200)); g.drawRect(x, y, size, size);
                return;
            } else if ("crafting_table".equals(type)) {
                g.setColor(new Color(160, 115, 68)); g.fillRect(x, y, size, size);
                g.setColor(new Color(195, 155, 95)); g.fillRect(x, y, size, 4);
                g.setColor(new Color(110, 75, 40)); g.fillRect(x + 3, y + 6, size - 6, size - 8);
                g.setColor(new Color(0, 0, 0, 50)); g.drawRect(x, y, size, size);
                return;
            } else if ("obsidian".equals(type)) base = OBSIDIAN;
            else if ("netherrack".equals(type)) base = new Color(112, 34, 34);
            else if ("netherbrick".equals(type)) base = new Color(48, 22, 26);
            else if ("glowstone".equals(type)) {
                g.setColor(new Color(215, 155, 45)); g.fillRect(x, y, size, size);
                g.setColor(new Color(255, 240, 120)); g.fillRect(x + 2, y + 2, size / 3, size / 3);
                g.setColor(new Color(0, 0, 0, 40)); g.drawRect(x, y, size, size);
                return;
            } else if ("end_stone".equals(type)) base = new Color(222, 222, 168);
            else if ("bedrock".equals(type)) base = new Color(35, 35, 35);

            g.setColor(base); g.fillRect(x, y, size, size);
            g.setColor(new Color(0, 0, 0, 45)); g.drawRect(x, y, size, size);
        }

        public static void drawOreBlock(PixelCanvas g, int x, int y, int size, String ore) {
            drawBlock(g, x, y, size, "stone");
            Color c = "diamond".equals(ore) ? DIAMOND_BLUE : ("gold".equals(ore) ? GOLD_YELLOW : ("iron".equals(ore) ? new Color(195, 185, 175) : new Color(38, 38, 38)));
            g.setColor(c);
            g.fillRect(x + 4, y + 3, 5, 4); g.fillRect(x + size - 8, y + size - 8, 5, 4);
        }

        public static void drawLavaBlock(PixelCanvas g, int x, int y, int size, double wave) {
            g.setColor(new Color(230, 95, 15)); g.fillRect(x, y, size, size);
            g.setColor(new Color(255, 210, 35)); g.fillRect(x, y + ((int) (wave * 4) % size), size, 3);
        }

        public static void drawStoneBrickBlock(PixelCanvas g, int x, int y, int size, int var) {
            g.setColor(new Color(125, 125, 125)); g.fillRect(x, y, size, size);
            g.setColor(new Color(65, 65, 65)); g.fillRect(x, y + size / 2, size, 1);
            if (var == 1) { g.setColor(new Color(55, 115, 45)); g.fillRect(x + 4, y + 2, 4, 4); }
            g.setColor(new Color(0, 0, 0, 50)); g.drawRect(x, y, size, size);
        }

        public static void drawEndPortalFrame(PixelCanvas g, int x, int y, int size, boolean eye) {
            g.setColor(new Color(75, 110, 92)); g.fillRect(x, y, size, size);
            g.setColor(new Color(35, 52, 44)); g.fillRect(x + 3, y + 3, size - 6, size - 6);
            if (eye) {
                g.setColor(new Color(25, 145, 115)); g.fillRect(x + 5, y + 5, size - 10, size - 10);
                g.setColor(Color.BLACK); g.fillRect(x + size / 2 - 1, y + 6, 2, size - 12);
            }
            g.setColor(new Color(0, 0, 0, 60)); g.drawRect(x, y, size, size);
        }

        public static void drawEndPortalPlane(PixelCanvas g, int x, int y, int w, int h, double t) {
            g.setColor(new Color(8, 6, 14)); g.fillRect(x, y, w, h);
            Random r = new Random(333);
            for (int i = 0; i < 15; i++) {
                g.setColor(i % 2 == 0 ? new Color(90, 180, 255) : Color.WHITE);
                g.fillRect(x + r.nextInt(Math.max(1, w - 4)), y + (int) ((r.nextInt(Math.max(1, h)) + t * 10) % h), 2, 2);
            }
            g.setColor(new Color(25, 145, 115, 200)); g.drawRect(x, y, w - 1, h - 1);
        }

        public static void drawTree(PixelCanvas g, int x, int y, int scale) {
            int bs = 8 * scale;
            for (int r = 0; r < 4; r++) drawBlock(g, x + 2 * bs, y + (2 + r) * bs, bs, "log");
            for (int r = 0; r < 2; r++) {
                for (int c = 0; c < 5; c++) drawBlock(g, x + c * bs, y + (r + 1) * bs, bs, "leaves");
            }
            for (int c = 1; c < 4; c++) drawBlock(g, x + c * bs, y, bs, "leaves");
        }

        public static void drawTorch(PixelCanvas g, int x, int y) {
            g.setColor(new Color(110, 85, 50)); g.fillRect(x + 2, y + 4, 4, 12);
            g.setColor(Color.BLACK); g.fillRect(x + 1, y + 2, 6, 4);
            g.setColor(GOLD_YELLOW); g.fillRect(x + 2, y - 2, 4, 4);
        }

        public static void drawWoodenDoor(PixelCanvas g, int x, int y, int w, int h, boolean open) {
            if (!open) {
                g.setColor(new Color(135, 95, 52)); g.fillRect(x, y, w, h);
                g.setColor(new Color(95, 65, 35)); g.drawRect(x, y, w, h);
                g.setColor(new Color(55, 38, 20)); g.fillRect(x + 3, y + 4, w / 2 - 4, h / 3); g.fillRect(x + w / 2 + 1, y + 4, w / 2 - 4, h / 3);
                g.setColor(GOLD_YELLOW); g.fillRect(x + w - 5, y + h / 2 - 2, 3, 4);
            } else {
                g.setColor(new Color(110, 75, 40)); g.fillRect(x, y, 4, h);
            }
        }

        public static void drawNetherPortalTexture(PixelCanvas g, int x, int y, int w, int h, double t) {
            g.setColor(new Color(85, 20, 140, 230)); g.fillRect(x, y, w, h);
            Random pr = new Random(101);
            for (int i = 0; i < 8; i++) {
                g.setColor(new Color(210, 120, 255, 200));
                g.fillRect(x + pr.nextInt(Math.max(1, w - 4)), y + (int) ((pr.nextInt(Math.max(1, h)) + t * 30) % h), 4, 3);
            }
            g.setColor(new Color(160, 60, 230, 180)); g.drawRect(x, y, w - 1, h - 1);
        }

        public static void drawMinecraftSun(PixelCanvas g, int x, int y, int s) {
            MidpointDrawing.fillCircleGlow(g, x + s / 2, y + s / 2, s + 15, new Color(255, 240, 150, 60), new Color(255, 200, 50, 0));
            g.setColor(Color.WHITE); g.fillRect(x, y, s, s);
        }

        public static void drawMinecraftMoon(PixelCanvas g, int x, int y, int s) {
            MidpointDrawing.fillCircleGlow(g, x + s / 2, y + s / 2, s + 12, new Color(220, 230, 255, 45), new Color(150, 180, 255, 0));
            g.setColor(new Color(240, 240, 245)); g.fillRect(x, y, s, s);
        }

        public static void drawMinecraftCursor(PixelCanvas g, int x, int y) {
            int[] xp = {x, x, x + 4, x + 6, x + 8, x + 5, x + 10};
            int[] yp = {y, y + 14, y + 11, y + 16, y + 14, y + 10, y + 10};
            g.setColor(Color.WHITE); g.fillPolygon(xp, yp, 7);
            g.setColor(Color.BLACK); g.drawPolygon(xp, yp, 7);
        }

        public static void drawSteve(PixelCanvas g, int x, int y, int scale, boolean facingRight) {
            drawSteveWithTool(g, x, y, scale, facingRight, "hand", 0, false, 0);
        }

        public static void drawSteveStanding(PixelCanvas g, int x, int y, int scale, boolean facingRight) {
            drawSteveWithTool(g, x, y, scale, facingRight, "hand", 0, false, 0);
        }

        public static void drawSteveWithTool(PixelCanvas g, int x, int y, int scale, boolean facingRight, String tool, double swing) {
            drawSteveWithTool(g, x, y, scale, facingRight, tool, swing, swing == 0, x * 0.28);
        }

        public static void drawSteveWithTool(PixelCanvas g, int x, int y, int sc, boolean right, String tool, double swing, boolean walk, double phase) {
            int px = 2 * sc;
            double lSwing = walk ? Math.sin(phase) * 0.55 : 0;
            if (!right) lSwing = -lSwing;

            AffineTransform old = g.getTransform();

            // Back Arm & Leg
            g.translate(x + 8 * px, y + 8 * px); g.rotate(-lSwing);
            g.setColor(STEVE_SHIRT); g.fillRect(-2 * px, 0, 4 * px, 4 * px);
            g.setColor(STEVE_SKIN); g.fillRect(-2 * px, 4 * px, 4 * px, 8 * px);
            g.setTransform(old);

            g.translate(x + 8 * px, y + 20 * px); g.rotate(lSwing);
            g.setColor(STEVE_PANTS); g.fillRect(-2 * px, 0, 4 * px, 10 * px);
            g.setColor(STEVE_SHOES); g.fillRect(-2 * px, 10 * px, 4 * px, 2 * px);
            g.setTransform(old);

            // Body & Front Leg
            g.setColor(STEVE_SHIRT); g.fillRect(x + 4 * px, y + 8 * px, 8 * px, 12 * px);
            g.setColor(STEVE_SKIN); g.fillRect(x + 7 * px, y + 8 * px, 2 * px, 2 * px);

            g.translate(x + 8 * px, y + 20 * px); g.rotate(-lSwing);
            g.setColor(STEVE_PANTS); g.fillRect(-2 * px, 0, 4 * px, 10 * px);
            g.setColor(STEVE_SHOES); g.fillRect(-2 * px, 10 * px, 4 * px, 2 * px);
            g.setTransform(old);

            // Head
            g.setColor(STEVE_SKIN); g.fillRect(x + 4 * px, y, 8 * px, 8 * px);
            g.setColor(new Color(60, 35, 15)); g.fillRect(x + 4 * px, y, 8 * px, 2 * px);
            g.fillRect(right ? x + 4 * px : x + 10 * px, y + 2 * px, 2 * px, 3 * px);

            int ex = right ? x + 8 * px : x + 6 * px;
            g.setColor(Color.WHITE); g.fillRect(ex, y + 4 * px, 2 * px, px);
            g.setColor(new Color(0, 50, 180)); g.fillRect(ex + (right ? px : 0), y + 4 * px, px, px);
            g.setColor(new Color(135, 80, 50)); g.fillRect(ex, y + 6 * px, 2 * px, px);

            // Front Arm & Tool
            g.translate(x + 8 * px, y + 8 * px);
            double armRot = (right ? swing : -swing) + (tool == null || "hand".equals(tool) ? lSwing : (right ? -Math.PI / 4 : Math.PI / 4));
            g.rotate(armRot);
            g.setColor(STEVE_SHIRT); g.fillRect(-2 * px, 0, 4 * px, 4 * px);
            g.setColor(STEVE_SKIN); g.fillRect(-2 * px, 4 * px, 4 * px, 8 * px);

            if (tool != null && tool.contains("pickaxe")) {
                g.setColor(WOOD_BROWN); g.fillRect(-px, 10 * px, 2 * px, 14 * px);
                g.setColor(tool.contains("diamond") ? DIAMOND_BLUE : Color.LIGHT_GRAY);
                g.fillRect(-6 * px, 22 * px, 13 * px, 3 * px); g.fillRect(4 * px, 19 * px, 3 * px, 4 * px); g.fillRect(-7 * px, 19 * px, 2 * px, 4 * px);
            } else if ("sword".equals(tool)) {
                g.setColor(WOOD_BROWN); g.fillRect(-px, 10 * px, 2 * px, 4 * px);
                g.setColor(DIAMOND_BLUE); g.fillRect(-px, 14 * px, 2 * px, 18 * px);
            } else if ("bow".equals(tool)) {
                g.setColor(WOOD_DARK); g.fillRect(-px, 8 * px, 2 * px, 12 * px);
                g.setColor(Color.WHITE); g.drawLine(-2 * px, 6 * px, -2 * px, 22 * px);
            } else if (tool != null && (tool.contains("ak") || "gun".equals(tool))) {
                g.setColor(new Color(130, 80, 40)); g.fillRect(-6 * px, 9 * px, 5 * px, 3 * px);
                g.setColor(new Color(45, 45, 45)); g.fillRect(-2 * px, 9 * px, 8 * px, 3 * px);
                g.setColor(new Color(60, 60, 60)); g.fillRect(6 * px, 8 * px, 12 * px, 2 * px);
            } else if ("dirt".equals(tool) || "wood".equals(tool)) {
                drawBlock(g, -3 * px, 7 * px, 6 * px, "dirt".equals(tool) ? "dirt" : "plank");
            } else if ("door".equals(tool)) {
                g.setColor(new Color(135, 95, 52)); g.fillRect(-2 * px, 5 * px, 4 * px, 10 * px);
            }
            g.setTransform(old);
        }

        public static void drawZombie(PixelCanvas g, int x, int y, int scale, double prog) {
            int px = 2 * scale;
            AffineTransform old = g.getTransform();
            g.translate(x + 8 * px, y + 8 * px); g.rotate(-Math.PI / 2 + Math.sin(prog * 10) * 0.25);
            g.setColor(new Color(0, 145, 95)); g.fillRect(-2 * px, 0, 4 * px, 12 * px);
            g.setTransform(old);
            g.setColor(new Color(0, 120, 145)); g.fillRect(x + 4 * px, y + 8 * px, 8 * px, 12 * px);
            g.setColor(new Color(45, 45, 145)); g.fillRect(x + 4 * px, y + 20 * px, 8 * px, 12 * px);
            g.setColor(new Color(0, 145, 95)); g.fillRect(x + 4 * px, y, 8 * px, 8 * px);
            g.setColor(Color.BLACK); g.fillRect(x + 5 * px, y + 4 * px, 2 * px, px); g.fillRect(x + 9 * px, y + 4 * px, 2 * px, px);
        }

        public static void drawCreeper(PixelCanvas g, int x, int y, int scale) {
            int px = 2 * scale;
            g.setColor(CREEPER_DARK); g.fillRect(x + 3 * px, y + 20 * px, 4 * px, 6 * px);
            g.setColor(CREEPER_GREEN); g.fillRect(x + 9 * px, y + 20 * px, 4 * px, 6 * px);
            g.fillRect(x + 4 * px, y + 8 * px, 8 * px, 12 * px);
            g.fillRect(x + 4 * px, y, 8 * px, 8 * px);
            g.setColor(Color.BLACK);
            g.fillRect(x + 5 * px, y + 2 * px, 2 * px, 2 * px); g.fillRect(x + 9 * px, y + 2 * px, 2 * px, 2 * px);
            g.fillRect(x + 7 * px, y + 4 * px, 2 * px, 3 * px); g.fillRect(x + 6 * px, y + 5 * px, px, 3 * px); g.fillRect(x + 9 * px, y + 5 * px, px, 3 * px);
        }

        public static void drawGhast(PixelCanvas g, int x, int y, int scale) {
            int px = 2 * scale;
            g.setColor(new Color(245, 245, 245)); g.fillRect(x, y, 16 * px, 16 * px);
            g.setColor(Color.BLACK);
            g.fillRect(x + 3 * px, y + 6 * px, 3 * px, px); g.fillRect(x + 10 * px, y + 6 * px, 3 * px, px);
            g.fillRect(x + 6 * px, y + 9 * px, 4 * px, 3 * px);
            for (int i = 0; i < 5; i++) {
                g.setColor(new Color(230, 230, 230)); g.fillRect(x + (1 + i * 3) * px, y + 16 * px, 2 * px, 6 * px);
            }
        }

        public static void drawBlaze(PixelCanvas g, int x, int y, int scale, double prog) {
            int px = 2 * scale;
            g.setColor(GOLD_YELLOW); g.fillRect(x + 4 * px, y, 8 * px, 8 * px);
            g.setColor(Color.BLACK); g.fillRect(x + 5 * px, y + 3 * px, 2 * px, 2 * px); g.fillRect(x + 9 * px, y + 3 * px, 2 * px, 2 * px);
            double ab = prog * Math.PI * 4;
            for (int i = 0; i < 6; i++) {
                double a = ab + (i * Math.PI / 3);
                int rx = x + 8 * px + (int) (Math.cos(a) * 11 * px), ry = y + 10 * px + (int) (Math.sin(a) * 3 * px);
                g.setColor(GOLD_YELLOW); g.fillRect(rx - px, ry - 4 * px, 2 * px, 8 * px);
                g.setColor(LAVA_ORANGE); g.fillRect(rx - px, ry, 2 * px, 2 * px);
            }
        }

        public static void drawHUD(PixelCanvas g, int width, int hearts, int maxHearts, int hunger, int xp) {
            for (int i = 0; i < maxHearts; i++) {
                int hx = 10 + i * 15;
                g.setColor(i < hearts ? HEART_RED : Color.DARK_GRAY); g.fillRect(hx + 1, 11, 7, 7);
                g.setColor(Color.BLACK); g.drawRect(hx, 10, 8, 8);
            }
            for (int i = 0; i < 10; i++) {
                int hx = width - 150 + i * 12;
                g.setColor(i < hunger ? new Color(160, 80, 20) : Color.DARK_GRAY); g.fillRect(hx + 1, 11, 7, 7);
                g.setColor(Color.BLACK); g.drawRect(hx, 10, 8, 8);
            }
        }

        public static void drawMinecraftButton(PixelCanvas g, int x, int y, int w, int h, String text, int state) {
            g.setColor(Color.BLACK); g.fillRect(x - 2, y - 2, w + 4, h + 4);
            g.setColor(state == 2 ? new Color(90, 90, 90) : (state == 1 ? new Color(175, 175, 175) : new Color(130, 130, 130)));
            g.fillRect(x, y, w, h);
            g.setColor(state == 2 ? new Color(60, 60, 60) : (state == 1 ? new Color(220, 220, 220) : new Color(190, 190, 190)));
            g.fillRect(x, y, w - 2, 2); g.fillRect(x, y, 2, h - 2);
            int scale = Math.max(1, 16 / 8);
            drawMinecraftText(g, text, x + (w - text.length() * 6 * scale) / 2, y + (h - 7 * scale) / 2 + (state == 2 ? 1 : 0), 16, state == 1 ? new Color(255, 255, 160) : Color.WHITE);
        }

        public static void drawAchievement(PixelCanvas g, int width, String title, double p) {
            if (p <= 0 || p >= 1) return;
            int yOff = (p < 0.3) ? (int) (-50 + 70 * easeInOut(p / 0.3)) : (p > 0.7 ? (int) (20 - 70 * easeInOut((p - 0.7) / 0.3)) : 20);
            int w = 260, h = 42, x = width / 2 - w / 2;
            g.setColor(new Color(35, 35, 35)); g.fillRect(x, yOff, w, h);
            g.setColor(GOLD_YELLOW); g.drawRect(x, yOff, w, h);
            drawMinecraftText(g, "Achievement Get!", x + 12, yOff + 8, 12, GOLD_YELLOW);
            drawMinecraftText(g, title, x + 12, yOff + 22, 14, Color.WHITE);
        }

        public static Color lerpColor(Color a, Color b, double t) {
            t = Math.max(0, Math.min(1, t));
            return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t), (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t), (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t), (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t));
        }

        public static double easeInOut(double t) { return t * t * (3 - 2 * t); }

        public static void drawSky(PixelCanvas g, int width, int height, double t) {
            Color top = t < 0.2 ? SKY_BLUE : (t < 0.4 ? lerpColor(SKY_BLUE, SUNSET_PINK, (t - 0.2) / 0.2) : (t < 0.6 ? lerpColor(SUNSET_PINK, NIGHT_SKY, (t - 0.4) / 0.2) : lerpColor(NIGHT_SKY, SKY_BLUE, (t - 0.6) / 0.4)));
            Color bot = t < 0.2 ? new Color(185, 230, 255) : (t < 0.4 ? lerpColor(new Color(185, 230, 255), SUNSET_ORANGE, (t - 0.2) / 0.2) : (t < 0.6 ? lerpColor(SUNSET_ORANGE, Color.BLACK, (t - 0.4) / 0.2) : lerpColor(Color.BLACK, new Color(185, 230, 255), (t - 0.6) / 0.4)));
            for (int i = 0; i < height; i += 4) {
                g.setColor(lerpColor(top, bot, (double) i / height));
                g.fillRect(0, i, width, 4);
            }
        }

        public static void drawGround(PixelCanvas g, int width, int height, int groundY, int bs) {
            for (int x = 0; x < width; x += bs) {
                drawBlock(g, x, groundY, bs, "grass");
                for (int y = groundY + bs; y < height; y += bs) drawBlock(g, x, y, bs, "dirt");
            }
        }

        public static void drawStars(PixelCanvas g, int width, int height, int count, long seed, double tw) {
            Random rand = new Random(seed);
            for (int i = 0; i < count; i++) {
                int a = Math.max(0, Math.min(255, (int) (155 + 100 * Math.sin(tw * Math.PI * 2 + rand.nextDouble() * 10))));
                MidpointDrawing.fillCircle(g, rand.nextInt(width), rand.nextInt(height / 2), rand.nextInt(2) + 1, new Color(255, 255, 255, a));
            }
        }

        public static void drawCaveDarkness(PixelCanvas g, int width, int height, int lx, int ly, int rad) {
            for (int i = 0; i < 4; i++) {
                int a = 30 - i * 5;
                if (a <= 0) break;
                g.setColor(new Color(0, 0, 0, a));
                g.fillRect(0, 0, width, i * 40); g.fillRect(0, height - i * 40, width, i * 40);
                g.fillRect(0, 0, i * 40, height); g.fillRect(width - i * 40, 0, i * 40, height);
            }
            if (rad > 0) MidpointDrawing.fillCircleGlow(g, lx, ly, rad, new Color(255, 200, 80, 25), new Color(0, 0, 0, 0));
        }

        public static void drawMinecraftText(PixelCanvas g, String text, int x, int y, int size, Color color) {
            g.drawMinecraftText(text, x, y, size, color);
        }
    }

    // =========================================================================
    // MIDPOINT DRAWING ALGORITHMS
    // =========================================================================
    static class MidpointDrawing {
        public static void drawCircle(PixelCanvas g, int cx, int cy, int r, Color c) { g.setColor(c); g.drawCircle(cx, cy, r); }
        public static void fillCircle(PixelCanvas g, int cx, int cy, int r, Color c) { g.setColor(c); g.fillCircle(cx, cy, r); }
        public static void drawEllipse(PixelCanvas g, int cx, int cy, int rx, int ry, Color c) { g.setColor(c); g.drawEllipse(cx, cy, rx, ry); }
        public static void fillEllipse(PixelCanvas g, int cx, int cy, int rx, int ry, Color c) { g.setColor(c); g.fillEllipse(cx, cy, rx, ry); }
        public static void fillCircleGlow(PixelCanvas g, int cx, int cy, int r, Color cc, Color ec) { g.fillCircleGlow(cx, cy, r, cc, ec); }
        public static void fillEllipseGlow(PixelCanvas g, int cx, int cy, int rx, int ry, Color cc, Color ec) { g.fillEllipseGlow(cx, cy, rx, ry, cc, ec); }
    }

    // =========================================================================
    // PIXELCANVAS - CUSTOM COMPUTER GRAPHICS ENGINE
    // =========================================================================
    static class PixelCanvas {
        private final int width, height;
        private final BufferedImage image;
        private final int[] pixels;
        private Color currentColor = Color.WHITE;
        private float currentAlpha = 1.0f, strokeWidth = 1.0f;
        private AffineTransform currentTransform = new AffineTransform();
        private final Stack<AffineTransform> transformStack = new Stack<>();

        public PixelCanvas(int width, int height) {
            this.width = width; this.height = height;
            this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        }

        public BufferedImage getBufferedImage() { return image; }
        public void clear(Color c) { Arrays.fill(pixels, (0xFF << 24) | (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue()); }
        public void setColor(Color c) { if (c != null) this.currentColor = c; }
        public void setAlpha(float a) { this.currentAlpha = Math.max(0f, Math.min(1f, a)); }
        public void setStrokeWidth(float w) { this.strokeWidth = w; }
        public void translate(double tx, double ty) { currentTransform.translate(tx, ty); }
        public void rotate(double th) { currentTransform.rotate(th); }
        public void scale(double sx, double sy) { currentTransform.scale(sx, sy); }
        public void pushTransform() { transformStack.push(new AffineTransform(currentTransform)); }
        public void popTransform() { if (!transformStack.isEmpty()) currentTransform = transformStack.pop(); }
        public AffineTransform getTransform() { return new AffineTransform(currentTransform); }
        public void setTransform(AffineTransform at) { currentTransform = (at != null) ? new AffineTransform(at) : new AffineTransform(); }

        private Point2D.Double transformPoint(double x, double y) {
            if (currentTransform.isIdentity()) return new Point2D.Double(x, y);
            Point2D.Double dst = new Point2D.Double();
            currentTransform.transform(new Point2D.Double(x, y), dst);
            return dst;
        }

        public void setPixel(int x, int y, Color color) {
            if (x < 0 || x >= width || y < 0 || y >= height || color == null) return;
            int sa = (int) (color.getAlpha() * currentAlpha);
            if (sa <= 0) return;
            int idx = y * width + x, sr = color.getRed(), sg = color.getGreen(), sb = color.getBlue();
            if (sa >= 255) pixels[idx] = (0xFF << 24) | (sr << 16) | (sg << 8) | sb;
            else {
                int dst = pixels[idx], da = (dst >>> 24) & 0xFF, dr = (dst >>> 16) & 0xFF, dg = (dst >>> 8) & 0xFF, db = dst & 0xFF, invA = 255 - sa;
                pixels[idx] = ((sa + (da * invA) / 255) << 24) | (((sr * sa + dr * invA) / 255) << 16) | (((sg * sa + dg * invA) / 255) << 8) | ((sb * sa + db * invA) / 255);
            }
        }

        // --- 1. BRESENHAM'S LINE ALGORITHM ---
        public void drawLine(int x0, int y0, int x1, int y1) {
            Point2D.Double p0 = transformPoint(x0, y0), p1 = transformPoint(x1, y1);
            drawLineRaw((int) Math.round(p0.x), (int) Math.round(p0.y), (int) Math.round(p1.x), (int) Math.round(p1.y), currentColor);
        }

        public void drawLineRaw(int x0, int y0, int x1, int y1, Color c) {
            int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0), sx = x0 < x1 ? 1 : -1, sy = y0 < y1 ? 1 : -1, err = dx - dy;
            while (true) {
                if (strokeWidth > 1.5f) {
                    int r = (int) Math.round(strokeWidth / 2.0);
                    for (int oy = -r; oy <= r; oy++) {
                        for (int ox = -r; ox <= r; ox++) setPixel(x0 + ox, y0 + oy, c);
                    }
                } else setPixel(x0, y0, c);

                if (x0 == x1 && y0 == y1) break;
                int e2 = 2 * err;
                if (e2 > -dy) { err -= dy; x0 += sx; }
                if (e2 < dx) { err += dx; y0 += sy; }
            }
        }

        // --- 2. SCANLINE FILL ALGORITHM ---
        public void drawHLine(int x1, int x2, int y, Color c) {
            if (y < 0 || y >= height || c == null) return;
            int minX = Math.max(0, Math.min(x1, x2)), maxX = Math.min(width - 1, Math.max(x1, x2));
            if (minX > maxX) return;
            int sa = (int) (c.getAlpha() * currentAlpha);
            if (sa <= 0) return;
            int sr = c.getRed(), sg = c.getGreen(), sb = c.getBlue(), rowStart = y * width;
            if (sa >= 255) {
                int rgb = (0xFF << 24) | (sr << 16) | (sg << 8) | sb;
                for (int x = minX; x <= maxX; x++) pixels[rowStart + x] = rgb;
            } else {
                int invA = 255 - sa;
                for (int x = minX; x <= maxX; x++) {
                    int idx = rowStart + x, dst = pixels[idx];
                    pixels[idx] = ((sa + (((dst >>> 24) & 0xFF) * invA) / 255) << 24) | (((sr * sa + ((dst >>> 16) & 0xFF) * invA) / 255) << 16) | (((sg * sa + ((dst >>> 8) & 0xFF) * invA) / 255) << 8) | ((sb * sa + (dst & 0xFF) * invA) / 255);
                }
            }
        }

        public void fillRect(int x, int y, int w, int h) {
            if (w <= 0 || h <= 0) return;
            if (currentTransform.isIdentity()) {
                for (int cy = y; cy < y + h; cy++) drawHLine(x, x + w - 1, cy, currentColor);
            } else fillPolygon(new int[]{x, x + w, x + w, x}, new int[]{y, y, y + h, y + h}, 4);
        }

        public void drawRect(int x, int y, int w, int h) {
            if (w <= 0 || h <= 0) return;
            if (currentTransform.isIdentity()) {
                drawLineRaw(x, y, x + w - 1, y, currentColor); drawLineRaw(x, y + h - 1, x + w - 1, y + h - 1, currentColor);
                drawLineRaw(x, y, x, y + h - 1, currentColor); drawLineRaw(x + w - 1, y, x + w - 1, y + h - 1, currentColor);
            } else drawPolygon(new int[]{x, x + w - 1, x + w - 1, x}, new int[]{y, y, y + h - 1, y + h - 1}, 4);
        }

        // --- 3. SCANLINE POLYGON FILL & OUTLINE ---
        public void drawPolygon(int[] xp, int[] yp, int np) {
            for (int i = 0; i < np; i++) drawLine(xp[i], yp[i], xp[(i + 1) % np], yp[(i + 1) % np]);
        }

        public void fillPolygon(int[] xp, int[] yp, int np) {
            if (np < 3) return;
            Point2D.Double[] pts = new Point2D.Double[np];
            int minY = height, maxY = -1;
            for (int i = 0; i < np; i++) {
                pts[i] = transformPoint(xp[i], yp[i]);
                int py = (int) Math.round(pts[i].y);
                if (py < minY) minY = py; if (py > maxY) maxY = py;
            }
            minY = Math.max(0, minY); maxY = Math.min(height - 1, maxY);
            List<Integer> nodeX = new ArrayList<>();

            for (int scanY = minY; scanY <= maxY; scanY++) {
                nodeX.clear();
                double yPos = scanY + 0.5;
                for (int i = 0; i < np; i++) {
                    int j = (i + 1) % np;
                    if ((pts[i].y < yPos && pts[j].y >= yPos) || (pts[j].y < yPos && pts[i].y >= yPos)) {
                        nodeX.add((int) Math.round(pts[i].x + (yPos - pts[i].y) / (pts[j].y - pts[i].y) * (pts[j].x - pts[i].x)));
                    }
                }
                if (nodeX.size() >= 2) {
                    nodeX.sort(Integer::compareTo);
                    for (int k = 0; k < nodeX.size() - 1; k += 2) drawHLine(nodeX.get(k), nodeX.get(k + 1), scanY, currentColor);
                }
            }
        }

        // --- 4. MIDPOINT CIRCLE ALGORITHM ---
        public void drawCircle(int cx, int cy, int radius) {
            Point2D.Double c = transformPoint(cx, cy);
            int icx = (int) Math.round(c.x), icy = (int) Math.round(c.y), x = radius, y = 0, p = 1 - radius;
            while (x >= y) {
                setPixel(icx + x, icy + y, currentColor); setPixel(icx - x, icy + y, currentColor);
                setPixel(icx + x, icy - y, currentColor); setPixel(icx - x, icy - y, currentColor);
                setPixel(icx + y, icy + x, currentColor); setPixel(icx - y, icy + x, currentColor);
                setPixel(icx + y, icy - x, currentColor); setPixel(icx - y, icy - x, currentColor);
                y++;
                if (p <= 0) p += 2 * y + 1;
                else { x--; p += 2 * y - 2 * x + 1; }
            }
        }

        public void fillCircle(int cx, int cy, int radius) {
            Point2D.Double c = transformPoint(cx, cy);
            int icx = (int) Math.round(c.x), icy = (int) Math.round(c.y), x = radius, y = 0, p = 1 - radius;
            while (x >= y) {
                drawHLine(icx - x, icx + x, icy + y, currentColor); drawHLine(icx - x, icx + x, icy - y, currentColor);
                drawHLine(icx - y, icx + y, icy + x, currentColor); drawHLine(icx - y, icx + y, icy - x, currentColor);
                y++;
                if (p <= 0) p += 2 * y + 1;
                else { x--; p += 2 * y - 2 * x + 1; }
            }
        }

        public void fillCircleGlow(int cx, int cy, int radius, Color cc, Color ec) {
            int step = Math.max(1, radius / 20);
            for (int r = radius; r > 0; r -= step) {
                setColor(DrawUtils.lerpColor(ec, cc, 1.0 - ((double) r / radius)));
                fillCircle(cx, cy, r);
            }
            setColor(cc); fillCircle(cx, cy, Math.max(1, step));
        }

        // --- 5. MIDPOINT ELLIPSE ALGORITHM ---
        public void drawEllipse(int cx, int cy, int rx, int ry) {
            Point2D.Double c = transformPoint(cx, cy);
            int icx = (int) Math.round(c.x), icy = (int) Math.round(c.y);
            long rx2 = (long) rx * rx, ry2 = (long) ry * ry, twoRx2 = 2 * rx2, twoRy2 = 2 * ry2;
            int x = 0, y = ry; long px = 0, py = twoRx2 * y;

            double p = ry2 - (rx2 * ry) + (0.25 * rx2);
            while (px < py) {
                setPixel(icx + x, icy + y, currentColor); setPixel(icx - x, icy + y, currentColor);
                setPixel(icx + x, icy - y, currentColor); setPixel(icx - x, icy - y, currentColor);
                x++; px += twoRy2;
                if (p < 0) p += ry2 + px;
                else { y--; py -= twoRx2; p += ry2 + px - py; }
            }
            p = ry2 * (x + 0.5) * (x + 0.5) + rx2 * (y - 1.0) * (y - 1.0) - rx2 * ry2;
            while (y >= 0) {
                setPixel(icx + x, icy + y, currentColor); setPixel(icx - x, icy + y, currentColor);
                setPixel(icx + x, icy - y, currentColor); setPixel(icx - x, icy - y, currentColor);
                y--; py -= twoRx2;
                if (p > 0) p += rx2 - py;
                else { x++; px += twoRy2; p += rx2 - py + px; }
            }
        }

        public void fillEllipse(int cx, int cy, int rx, int ry) {
            Point2D.Double c = transformPoint(cx, cy);
            int icx = (int) Math.round(c.x), icy = (int) Math.round(c.y);
            long rx2 = (long) rx * rx, ry2 = (long) ry * ry, twoRx2 = 2 * rx2, twoRy2 = 2 * ry2;
            int x = 0, y = ry; long px = 0, py = twoRx2 * y;

            double p = ry2 - (rx2 * ry) + (0.25 * rx2);
            while (px < py) {
                drawHLine(icx - x, icx + x, icy + y, currentColor); drawHLine(icx - x, icx + x, icy - y, currentColor);
                x++; px += twoRy2;
                if (p < 0) p += ry2 + px;
                else { y--; py -= twoRx2; p += ry2 + px - py; }
            }
            p = ry2 * (x + 0.5) * (x + 0.5) + rx2 * (y - 1.0) * (y - 1.0) - rx2 * ry2;
            while (y >= 0) {
                drawHLine(icx - x, icx + x, icy + y, currentColor); drawHLine(icx - x, icx + x, icy - y, currentColor);
                y--; py -= twoRx2;
                if (p > 0) p += rx2 - py;
                else { x++; px += twoRy2; p += rx2 - py + px; }
            }
        }

        public void fillEllipseGlow(int cx, int cy, int rx, int ry, Color cc, Color ec) {
            int maxR = Math.max(rx, ry), step = Math.max(1, maxR / 20);
            for (int r = maxR; r > 0; r -= step) {
                int curRx = (int) (rx * ((double) r / maxR)), curRy = (int) (ry * ((double) r / maxR));
                if (curRx > 0 && curRy > 0) {
                    setColor(DrawUtils.lerpColor(ec, cc, 1.0 - ((double) r / maxR)));
                    fillEllipse(cx, cy, curRx, curRy);
                }
            }
            setColor(cc); fillCircle(cx, cy, Math.max(1, step));
        }

        // --- 6. BEZIER CURVE ALGORITHM ---
        public void drawBezierQuadratic(double x0, double y0, double cx, double cy, double x1, double y1, int segs) {
            Point2D.Double p0 = transformPoint(x0, y0), pc = transformPoint(cx, cy), p1 = transformPoint(x1, y1);
            double px = p0.x, py = p0.y;
            for (int i = 1; i <= segs; i++) {
                double t = (double) i / segs, omt = 1.0 - t;
                double curX = omt * omt * p0.x + 2 * omt * t * pc.x + t * t * p1.x;
                double curY = omt * omt * p0.y + 2 * omt * t * pc.y + t * t * p1.y;
                drawLineRaw((int) Math.round(px), (int) Math.round(py), (int) Math.round(curX), (int) Math.round(curY), currentColor);
                px = curX; py = curY;
            }
        }

        public void drawBezierCubic(double x0, double y0, double cx1, double cy1, double cx2, double cy2, double x1, double y1, int segs) {
            Point2D.Double p0 = transformPoint(x0, y0), pc1 = transformPoint(cx1, cy1), pc2 = transformPoint(cx2, cy2), p1 = transformPoint(x1, y1);
            double px = p0.x, py = p0.y;
            for (int i = 1; i <= segs; i++) {
                double t = (double) i / segs, omt = 1.0 - t;
                double curX = omt * omt * omt * p0.x + 3 * omt * omt * t * pc1.x + 3 * omt * t * t * pc2.x + t * t * t * p1.x;
                double curY = omt * omt * omt * p0.y + 3 * omt * omt * t * pc1.y + 3 * omt * t * t * pc2.y + t * t * t * p1.y;
                drawLineRaw((int) Math.round(px), (int) Math.round(py), (int) Math.round(curX), (int) Math.round(curY), currentColor);
                px = curX; py = curY;
            }
        }

        public void fillBezierWing(double x0, double y0, double cx1, double cy1, double x1, double y1, double cx2, double cy2) {
            int segs = 16, total = segs * 2, idx = 0;
            int[] xp = new int[total], yp = new int[total];
            for (int i = 0; i < segs; i++) {
                double t = (double) i / segs, omt = 1.0 - t;
                xp[idx] = (int) Math.round(omt * omt * x0 + 2 * omt * t * cx1 + t * t * x1);
                yp[idx] = (int) Math.round(omt * omt * y0 + 2 * omt * t * cy1 + t * t * y1);
                idx++;
            }
            for (int i = 0; i < segs; i++) {
                double t = (double) i / segs, omt = 1.0 - t;
                xp[idx] = (int) Math.round(omt * omt * x1 + 2 * omt * t * cx2 + t * t * x0);
                yp[idx] = (int) Math.round(omt * omt * y1 + 2 * omt * t * cy2 + t * t * y0);
                idx++;
            }
            fillPolygon(xp, yp, total);
        }

        // --- 7. QUEUE-BASED 4-WAY FLOOD FILL ALGORITHM ---
        public void floodFill(int sx, int sy, Color tc, Color fc) {
            if (sx < 0 || sx >= width || sy < 0 || sy >= height) return;
            int trgb = (0xFF << 24) | (tc.getRed() << 16) | (tc.getGreen() << 8) | tc.getBlue();
            int frgb = (0xFF << 24) | (fc.getRed() << 16) | (fc.getGreen() << 8) | fc.getBlue();
            if (trgb == frgb || pixels[sy * width + sx] != trgb) return;

            int[] queue = new int[width * height];
            int head = 0, tail = 0;
            queue[tail++] = (sy << 16) | (sx & 0xFFFF);
            pixels[sy * width + sx] = frgb;
            int[] dx = {1, -1, 0, 0}, dy = {0, 0, 1, -1};

            while (head < tail) {
                int pos = queue[head++], qy = pos >>> 16, qx = pos & 0xFFFF;
                for (int d = 0; d < 4; d++) {
                    int nx = qx + dx[d], ny = qy + dy[d];
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height && pixels[ny * width + nx] == trgb) {
                        pixels[ny * width + nx] = frgb;
                        queue[tail++] = (ny << 16) | (nx & 0xFFFF);
                    }
                }
            }
        }

        // --- 8. CUSTOM 5x7 MINECRAFT PIXEL / BITMAP FONT ENGINE ---
        public void drawMinecraftText(String text, int x, int y, int fontSize, Color color) {
            if (text == null || text.isEmpty()) return;
            int scale = Math.max(1, fontSize / 8), curX = x;
            Color shadow = new Color(30, 30, 30, (int) (color.getAlpha() * 0.85f));
            for (int i = 0; i < text.length(); i++) { drawGlyph(text.charAt(i), curX + scale, y + scale, scale, shadow); curX += 6 * scale; }
            curX = x;
            for (int i = 0; i < text.length(); i++) { drawGlyph(text.charAt(i), curX, y, scale, color); curX += 6 * scale; }
        }

        private void drawGlyph(char ch, int gx, int gy, int scale, Color col) {
            byte[] cols = (ch >= 0 && ch < 128) ? GLYPH_MAP[ch] : GLYPH_MAP['?'];
            if (cols == null) return;
            Color old = currentColor; currentColor = col;
            for (int c = 0; c < 5; c++) {
                int bits = cols[c] & 0xFF;
                for (int r = 0; r < 7; r++) {
                    if ((bits & (1 << r)) != 0) fillRect(gx + c * scale, gy + r * scale, scale, scale);
                }
            }
            currentColor = old;
        }

        private static final byte[][] GLYPH_MAP = new byte[128][5];
        private static void g(char c, int c0, int c1, int c2, int c3, int c4) { GLYPH_MAP[c] = new byte[]{(byte)c0, (byte)c1, (byte)c2, (byte)c3, (byte)c4}; }
        static {
            g(' ',0,0,0,0,0); g('!',0,0,0x5F,0,0); g('"',0,7,0,7,0); g('#',0x14,0x7F,0x14,0x7F,0x14); g('$',0x24,0x2A,0x7F,0x2A,0x12);
            g('%',0x23,0x13,8,0x64,0x62); g('&',0x36,0x49,0x55,0x22,0x50); g('\'',0,5,3,0,0); g('(',0,0x1C,0x22,0x41,0); g(')',0,0x41,0x22,0x1C,0);
            g('*',0x14,8,0x3E,8,0x14); g('+',8,8,0x3E,8,8); g(',',0,0x50,0x30,0,0); g('-',8,8,8,8,8); g('.',0,0x60,0x60,0,0); g('/',0x20,0x10,8,4,2);
            g('0',0x3E,0x51,0x49,0x45,0x3E); g('1',0,0x42,0x7F,0x40,0); g('2',0x42,0x61,0x51,0x49,0x46); g('3',0x21,0x41,0x45,0x4B,0x31);
            g('4',0x18,0x14,0x12,0x7F,0x10); g('5',0x27,0x45,0x45,0x45,0x39); g('6',0x3C,0x4A,0x49,0x49,0x30); g('7',1,0x71,9,5,3);
            g('8',0x36,0x49,0x49,0x49,0x36); g('9',6,0x49,0x49,0x29,0x1E); g(':',0,0x36,0x36,0,0); g(';',0,0x56,0x36,0,0);
            g('<',8,0x14,0x22,0x41,0); g('=',0x14,0x14,0x14,0x14,0x14); g('>',0,0x41,0x22,0x14,8); g('?',2,1,0x51,9,6); g('@',0x32,0x49,0x79,0x41,0x3E);
            g('A',0x7E,0x11,0x11,0x11,0x7E); g('B',0x7F,0x49,0x49,0x49,0x36); g('C',0x3E,0x41,0x41,0x41,0x22); g('D',0x7F,0x41,0x41,0x22,0x1C);
            g('E',0x7F,0x49,0x49,0x49,0x41); g('F',0x7F,9,9,9,1); g('G',0x3E,0x41,0x49,0x49,0x7A); g('H',0x7F,8,8,8,0x7F);
            g('I',0,0x41,0x7F,0x41,0); g('J',0x20,0x40,0x41,0x3F,1); g('K',0x7F,8,0x14,0x22,0x41); g('L',0x7F,0x40,0x40,0x40,0x40);
            g('M',0x7F,2,0x0C,2,0x7F); g('N',0x7F,4,8,0x10,0x7F); g('O',0x3E,0x41,0x41,0x41,0x3E); g('P',0x7F,9,9,9,6);
            g('Q',0x3E,0x41,0x51,0x21,0x5E); g('R',0x7F,9,0x19,0x29,0x46); g('S',0x46,0x49,0x49,0x49,0x31); g('T',1,1,0x7F,1,1);
            g('U',0x3F,0x40,0x40,0x40,0x3F); g('V',0x1F,0x20,0x40,0x20,0x1F); g('W',0x3F,0x40,0x38,0x40,0x3F); g('X',0x63,0x14,8,0x14,0x63);
            g('Y',7,8,0x70,8,7); g('Z',0x61,0x51,0x49,0x45,0x43); g('[',0,0x7F,0x41,0x41,0); g('\\',2,4,8,0x10,0x20); g(']',0,0x41,0x41,0x7F,0);
            g('^',4,2,1,2,4); g('_',0x40,0x40,0x40,0x40,0x40); g('`',0,1,2,4,0);
            g('a',0x20,0x54,0x54,0x54,0x78); g('b',0x7F,0x48,0x44,0x44,0x38); g('c',0x38,0x44,0x44,0x44,0x20); g('d',0x38,0x44,0x44,0x48,0x7F);
            g('e',0x38,0x54,0x54,0x54,0x18); g('f',8,0x7E,9,1,2); g('g',0x0C,0x52,0x52,0x52,0x3E); g('h',0x7F,8,4,4,0x78);
            g('i',0,0x44,0x7D,0x40,0); g('j',0x20,0x40,0x44,0x3D,0); g('k',0x7F,0x10,0x28,0x44,0); g('l',0,0x41,0x7F,0x40,0);
            g('m',0x7C,4,0x18,4,0x78); g('n',0x7C,8,4,4,0x78); g('o',0x38,0x44,0x44,0x44,0x38); g('p',0x7C,0x14,0x14,0x14,8);
            g('q',8,0x14,0x14,0x18,0x7C); g('r',0x7C,8,4,4,8); g('s',0x48,0x54,0x54,0x54,0x20); g('t',4,0x3F,0x44,0x40,0x20);
            g('u',0x3C,0x40,0x40,0x20,0x7C); g('v',0x1C,0x20,0x40,0x20,0x1C); g('w',0x3C,0x40,0x30,0x40,0x3C); g('x',0x44,0x28,0x10,0x28,0x44);
            g('y',0x0C,0x50,0x50,0x50,0x3C); g('z',0x44,0x64,0x54,0x4C,0x44); g('{',0,8,0x36,0x41,0); g('|',0,0,0x7F,0,0);
            g('}',0,0x41,0x36,8,0); g('~',8,4,8,0x10,8);
        }
    }
}
