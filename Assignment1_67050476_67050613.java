/*
 * Assignment 1 - Computer Graphics (MY MEMORIES)
 * Minecraft Journey Animation: From Title Screen to Defeating the Ender Dragon
 * 
 * Students: 67050476, 67050613
 * 
 * Tools: Java 2D API (java.awt, javax.swing)
 * Algorithms: Custom Midpoint Circle, Midpoint Ellipse, Quadratic Bezier Curves
 * Canvas: 600x600, Duration: ~58 seconds, 10 animated scenes
 * 
 * See references.md for full list of inspired images and external resources.
 */

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;
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
            
            AnimationPanel panel = new AnimationPanel();
            frame.add(panel);
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // =========================================================================
    // ANIMATIONPANEL.JAVA
    // =========================================================================
    
    static class AnimationPanel extends JPanel implements ActionListener {
    
        private final List<Scene> scenes;
        private final Timer timer;
        private long startTime = -1;
        private final int TRANSITION_DURATION = 800;
    
        public AnimationPanel() {
            scenes = new ArrayList<>();
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
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
            if (startTime == -1) {
                startTime = System.currentTimeMillis();
            }
    
            long elapsed = System.currentTimeMillis() - startTime;
            
            int currentSceneIndex = 0;
            long timeAccumulator = 0;
            
            while (currentSceneIndex < scenes.size()) {
                int duration = scenes.get(currentSceneIndex).getDurationMs();
                if (elapsed < timeAccumulator + duration) {
                    break;
                }
                timeAccumulator += duration;
                currentSceneIndex++;
            }
    
            if (currentSceneIndex >= scenes.size()) {
                // Animation finished, just render the last frame of the last scene
                if (!scenes.isEmpty()) {
                    scenes.get(scenes.size() - 1).render(g2d, getWidth(), getHeight(), 1.0);
                }
                return;
            }
    
            Scene currentScene = scenes.get(currentSceneIndex);
            long sceneElapsed = elapsed - timeAccumulator;
            double progress = (double) sceneElapsed / currentScene.getDurationMs();
            
            // Check for transition
            if (sceneElapsed > currentScene.getDurationMs() - TRANSITION_DURATION && currentSceneIndex < scenes.size() - 1) {
                // Draw current scene
                currentScene.render(g2d, getWidth(), getHeight(), progress);
                
                // Draw fade overlay
                double transitionProgress = (double) (sceneElapsed - (currentScene.getDurationMs() - TRANSITION_DURATION)) / TRANSITION_DURATION;
                transitionProgress = transitionProgress * transitionProgress * (3 - 2 * transitionProgress); // Smooth easing
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.min(1.0, transitionProgress)));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            } else {
                currentScene.render(g2d, getWidth(), getHeight(), progress);
            }
        }
    
        @Override
        public void actionPerformed(ActionEvent e) {
            repaint();
        }
    }

    // =========================================================================
    // SCENE.JAVA
    // =========================================================================
    
    /**
     * Base class for all scenes in the animation.
     */
    static abstract class Scene {
        protected final String name;
        protected final int durationMs;
        
        public Scene(String name, int durationMs) {
            this.name = name;
            this.durationMs = durationMs;
        }
        
        public String getName() { 
            return name; 
        }
        
        public int getDurationMs() { 
            return durationMs; 
        }
        
        /**
         * Renders the scene.
         * @param g2d The Graphics2D context.
         * @param width Width of the canvas.
         * @param height Height of the canvas.
         * @param progress Progress of the scene from 0.0 to 1.0.
         */
        public abstract void render(Graphics2D g2d, int width, int height, double progress);
    }

    // =========================================================================
    // TITLESCENE.JAVA
    // =========================================================================
    
    static class TitleScene extends Scene {
    
        public TitleScene(String name, int durationMs) {
            super(name, durationMs);
        }
    
        @Override
        public void render(Graphics2D g2d, int width, int height, double progress) {
            // Gradient background
            for (int i = 0; i < height; i += 5) {
                double t = (double) i / height;
                g2d.setColor(DrawUtils.lerpColor(new Color(20, 20, 60), Color.BLACK, t));
                g2d.fillRect(0, i, width, 5);
            }
    
            // Drifting clouds
            int cloudX = (int) (progress * 100);
            DrawUtils.drawCloud(g2d, 50 + cloudX, 100, 80);
            DrawUtils.drawCloud(g2d, 300 + cloudX, 150, 120);
            DrawUtils.drawCloud(g2d, 500 + cloudX, 80, 100);
    
            // Animated grass & dirt blocks scrolling
            int scrollOffset = (int) (progress * DrawUtils.BLOCK_SIZE * 5) % DrawUtils.BLOCK_SIZE;
            for (int x = -DrawUtils.BLOCK_SIZE; x < width + DrawUtils.BLOCK_SIZE; x += DrawUtils.BLOCK_SIZE) {
                DrawUtils.drawGrassBlock(g2d, x - scrollOffset, height - 60, DrawUtils.BLOCK_SIZE);
                for (int y = height - 60 + DrawUtils.BLOCK_SIZE; y < height; y += DrawUtils.BLOCK_SIZE) {
                    DrawUtils.drawDirtBlock(g2d, x - scrollOffset, y, DrawUtils.BLOCK_SIZE);
                }
            }
    
            // Creeper walking across the dirt blocks from right to left
            int creeperX = (int) (width + 40 - progress * (width + 100));
            int creeperY = height - 60 - 52;
            DrawUtils.drawCreeper(g2d, creeperX, creeperY, 1);
    
            // Title text floating
            int titleY = 150 + (int) (Math.sin(progress * Math.PI * 4) * 10);
            DrawUtils.drawMinecraftText(g2d, "MINECRAFT", 150, titleY, 56, DrawUtils.GOLD_YELLOW);
    
            // Splash text rotating
            java.awt.geom.AffineTransform old = g2d.getTransform();
            g2d.translate(450, titleY - 20);
            g2d.rotate(-Math.PI / 8 + Math.sin(progress * Math.PI * 8) * 0.1);
            DrawUtils.drawMinecraftText(g2d, "Now with Java 2D!", 0, 0, 16, Color.YELLOW);
            g2d.setTransform(old);
    
            // Buttons
            int btnW = 300;
            int btnH = 40;
            int btnX = width / 2 - btnW / 2;
            int btnY = height - 200;
    
            // Singleplayer Button State: 0=Normal, 1=Hover, 2=Pressed
            int spState = 0;
            if (progress >= 0.7 && progress < 0.85) {
                spState = 1; // hover
            } else if (progress >= 0.85) {
                spState = 2; // pressed
            }
            int spY = btnY;
            DrawUtils.drawMinecraftButton(g2d, btnX, spY, btnW, btnH, "Singleplayer", spState);
    
            // Multiplayer Button
            DrawUtils.drawMinecraftButton(g2d, btnX, btnY + 50, btnW, btnH, "Multiplayer", 0);
    
            // Options Button
            DrawUtils.drawMinecraftButton(g2d, btnX, btnY + 100, btnW, btnH, "Options...", 0);
    
            // Minecraft Cursor moving to Singleplayer button (arrives around progress=0.70)
            double cursorT = Math.min(1.0, progress / 0.70);
            double easedCursor = DrawUtils.easeInOut(cursorT);
            int targetCursorX = btnX + btnW / 2;
            int targetCursorY = spY + btnH / 2;
            int curX = (int) (400 + (targetCursorX - 400) * easedCursor);
            int curY = (int) (100 + (targetCursorY - 100) * easedCursor);
            DrawUtils.drawMinecraftCursor(g2d, curX, curY);
        }
    }

    // =========================================================================
    // CREATEWORLDSCENE.JAVA
    // =========================================================================
    
    static class CreateWorldScene extends Scene {
    
        public CreateWorldScene(String name, int durationMs) {
            super(name, durationMs);
        }
    
        @Override
        public void render(Graphics2D g2d, int width, int height, double progress) {
            // Dark gray background
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, width, height);
    
            // Dirt texture background effect (repeating dirt blocks)
            for (int x = 0; x < width; x += DrawUtils.BLOCK_SIZE) {
                for (int y = 0; y < height; y += DrawUtils.BLOCK_SIZE) {
                    DrawUtils.drawDirtBlock(g2d, x, y, DrawUtils.BLOCK_SIZE);
                }
            }
            
            // A dark translucent overlay
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, width, height);
    
            if (progress < 0.55) {
                // UI Phase
                DrawUtils.drawMinecraftText(g2d, "Create New World", 180, 50, 24, Color.WHITE);
                
                // World name text field
                g2d.setColor(Color.BLACK);
                g2d.fillRect(150, 80, 300, 40);
                g2d.setColor(Color.GRAY);
                g2d.drawRect(150, 80, 300, 40);
                
                String fullText = "New World";
                int charsToShow = (int) Math.min(fullText.length(), (progress / 0.4) * fullText.length());
                String currentText = fullText.substring(0, charsToShow);
                
                DrawUtils.drawMinecraftText(g2d, currentText + (progress % 0.1 < 0.05 && progress < 0.4 ? "_" : ""), 160, 105, 16, Color.WHITE);
                
                int btnW = 300;
                int btnH = 40;
                int btnX = 150;
    
                // Game mode button
                DrawUtils.drawMinecraftButton(g2d, btnX, 140, btnW, btnH, "Game Mode: Survival", 0);
    
                // Difficulty button
                DrawUtils.drawMinecraftButton(g2d, btnX, 190, btnW, btnH, "Difficulty: Normal", 0);
    
                // Allow Cheats button
                DrawUtils.drawMinecraftButton(g2d, btnX, 240, btnW, btnH, "Allow Cheats: OFF", 0);
    
                // Create New World button
                int createBtnY = height - 100;
                int createState = (progress >= 0.5) ? 2 : (progress >= 0.45 ? 1 : 0);
                DrawUtils.drawMinecraftButton(g2d, btnX, createBtnY, btnW, btnH, "Create New World", createState);
    
            } else {
                // Loading Phase
                double loadProgress = (progress - 0.55) / 0.4; // 0.55 to 0.95
                if (loadProgress > 1.0) loadProgress = 1.0;
    
                DrawUtils.drawMinecraftText(g2d, "Building terrain...", 220, 250, 18, Color.WHITE);
    
                // Progress bar
                int barW = 400;
                int barH = 20;
                int barX = 100;
                int barY = 300;
    
                g2d.setColor(Color.BLACK);
                g2d.fillRect(barX, barY, barW, barH);
                g2d.setColor(DrawUtils.XP_GREEN);
                g2d.fillRect(barX, barY, (int) (barW * loadProgress), barH);
    
                DrawUtils.drawMinecraftText(g2d, (int) (loadProgress * 100) + "%", 280, 350, 18, Color.WHITE);
            }
    
            // Fade out at the very end
            if (progress >= 0.95) {
                double fadeAlpha = (progress - 0.95) / 0.05;
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.min(1.0, fadeAlpha)));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, width, height);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }

    // =========================================================================
    // FIRSTDAYSCENE.JAVA
    // =========================================================================
    
    /**
     * Scene 3: First Day - Steve spawns into the new world, looks around,
     * turns towards the oak tree, walks over to chop down wood, and crafts a workbench.
     */
    static class FirstDayScene extends Scene {
    
        public FirstDayScene(String name, int durationMs) {
            super(name, durationMs);
        }
    
        @Override
        public void render(Graphics2D g2d, int width, int height, double progress) {

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
    
            // Distant Mountains & Hills Backdrop
            int bs = 32;
            int groundY = 450;
            DrawUtils.drawDistantMountains(g2d, width, groundY, timeOfDay, progress * 60);
    
            // Ground (Grass + Dirt layers with 32px block scale)
            DrawUtils.drawGround(g2d, width, height, groundY, bs);
    
            // Background decorative trees (planted firmly on ground)
            int treeScale = 3;
            int treeBlockSize = 8 * treeScale; // 24px
            int treeH = 6 * treeBlockSize;      // 144px
            int treeGroundY = groundY - treeH;  // 306px
    
            DrawUtils.drawTree(g2d, 430, treeGroundY, treeScale);
            DrawUtils.drawTree(g2d, 50, treeGroundY, treeScale);
    
            // Background wandering pig
            int pigX = (int) (470 - progress * 80);
            int pigY = groundY - 24;
            DrawUtils.drawPig(g2d, pigX, pigY, 1, false);
    
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
            if (progress >= 0.62) {
                DrawUtils.drawAchievement(g2d, width, "Getting Wood", (progress - 0.62) / 0.25);
            }
        }
    }

    // =========================================================================
    // FIRSTNIGHTSCENE.JAVA
    // =========================================================================
    
    /**
     * Scene 4: First Night - 2D Cross-Section Dirt Shelter.
     * Steve builds a 2D dirt shelter with background walls, grass roof, and 2-block high room.
     * He places the wooden door, enters the warm torch-lit interior, and the Creeper comes to look outside.
     */
    static class FirstNightScene extends Scene {
    
        public FirstNightScene(String name, int durationMs) {
            super(name, durationMs);
        }
    
        @Override
        public void render(Graphics2D g2d, int width, int height, double progress) {

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
    
            // Distant Mountains & Hills Backdrop
            DrawUtils.drawDistantMountains(g2d, width, groundY, timeOfDay, progress * 40);
    
            // Ground (Grass & Dirt layers with identical 32px block textures)
            DrawUtils.drawGround(g2d, width, height, groundY, bs);
    
            // Distant background zombie walking from left to right behind the house (0.50 - 0.90)
            if (progress >= 0.50) {
                double zProg = Math.min(1.0, (progress - 0.50) / 0.40);
                int zombieX = (int) (-20 + zProg * 220);
                DrawUtils.drawZombie(g2d, zombieX, groundY - 64, 1, progress * 10);
            }
    
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
    
                // Shaking / vibration when near house (progress >= 0.75)
                int shakeOffset = 0;
                if (progress >= 0.75) {
                    shakeOffset = (int) (Math.sin(progress * 80) * 2);
                }
    
                // Draw Creeper walking/standing beside the house
                DrawUtils.drawCreeper(g2d, creeperX + shakeOffset, groundY - 52, 1);
    
                // Creeper looking curiously at the house (subtle head tilt/curious gaze)
                if (cProg >= 1.0) {
                    int gazeTick = (int) (Math.sin(progress * 8) * 2);
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(creeperX + shakeOffset + 9, groundY - 50 + gazeTick, 2, 2);
                }
            }
    
            // HUD
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, 20);
        }
    }

    // =========================================================================
    // MININGSCENE.JAVA
    // =========================================================================
    
    /**
     * Scene 5: Mining - Deep Underground Cave.
     * The entire top is solid stone layers from row 0 down.
     * Steve digs down through the solid rock into a natural block cavern,
     * and discovers diamond ore.
     */
    static class MiningScene extends Scene {
    
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
                // Digging down the vertical shaft through solid rock with iron pickaxe
                steveX = shaftCol1 * BS + 4;
                steveY = currentDugRow * BS - 64;
                swing = Math.sin(progress * 50) * Math.PI / 4;
                DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "iron_pickaxe", swing, false, 0);
            } else if (progress < 0.46) {
                // Gravity drop from shaft ceiling onto cave ledge
                double dropT = (progress - 0.38) / 0.08;
                double grav = dropT * dropT;
                int startY = maxDigRow * BS - 64;
                steveX = shaftCol1 * BS + 4;
                steveY = (int) (startY + grav * (ledgeLandingY - startY));
                DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "iron_pickaxe", 0.2, false, 0);
            } else if (progress < 0.58) {
                // Walking down the solid block staircase toward diamond wall holding iron pickaxe
                double walkT = DrawUtils.easeInOut((progress - 0.46) / 0.12);
                int targetX = diamondPX - 45;
                steveX = shaftCol1 * BS + 4 + (int) (walkT * (targetX - (shaftCol1 * BS + 4)));
                // Steps down from row 22 to row 25 (3 blocks down)
                steveY = ledgeLandingY + (int) (walkT * (3 * BS));
                DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "iron_pickaxe", 0, true, steveX * 0.35);
            } else if (progress < 0.66) {
                // Spots Diamond Ore ("!")
                steveX = diamondPX - 45;
                steveY = ledgeLandingY + 3 * BS;
                DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "iron_pickaxe", 0, false, 0);
                DrawUtils.drawMinecraftText(g2d, "!", steveX + 12, steveY - 14, 22, Color.WHITE);
            } else if (progress < 0.78) {
                // Mining Diamond Ore Block with iron pickaxe
                steveX = diamondPX - 45;
                steveY = ledgeLandingY + 3 * BS;
                swing = Math.sin((progress - 0.66) * 35) * Math.PI / 4;
                DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "iron_pickaxe", swing, false, 0);
    
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
                DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "iron_pickaxe", -0.3, false, 0);
    
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
    
            // === Cavern Bats & Spider Eyes ===
            if (progress > 0.45) {
                DrawUtils.drawBat(g2d, 180 + (int) (Math.sin(progress * 6) * 30), 240 + (int) (Math.cos(progress * 4) * 20), progress * 25);
                DrawUtils.drawBat(g2d, 350 + (int) (Math.sin(progress * 5) * 25), 210 + (int) (Math.cos(progress * 7) * 15), progress * 30);
            }
            if (progress > 0.50) {
                g2d.setColor(new Color(255, 0, 0, 180));
                // Pair 1: Upper-left dark cave alcove
                g2d.fillRect(90, 330, 3, 2);
                g2d.fillRect(95, 330, 3, 2);
                // Pair 2: Lower-left dark corner
                g2d.fillRect(105, 450, 3, 2);
                g2d.fillRect(110, 450, 3, 2);
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

    // =========================================================================
    // BUILDHOMESCENE.JAVA
    // =========================================================================
    
    /**
     * Scene 6: Building a Wooden Home.
     * Steve stands outside holding a wood block, watching his wooden house being built during sunset,
     * then walks over, enters through the wooden door into his new home.
     */
    static class BuildHomeScene extends Scene {
    
        public BuildHomeScene(String name, int durationMs) {
            super(name, durationMs);
        }
    
        @Override
        public void render(Graphics2D g2d, int width, int height, double progress) {

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
    
            // Distant Mountains & Hills Backdrop
            DrawUtils.drawDistantMountains(g2d, width, groundY, timeOfDay, progress * 50);
    
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
                                // Authentic Glass window pane
                                DrawUtils.drawGlassBlock(g2d, bx, by, bs);
                            } else {
                                // Authentic Oak wood planks
                                DrawUtils.drawOakPlankBlock(g2d, bx, by, bs);
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
    
            // === Chimney Smoke Puffs (0.60 - 1.00) ===
            if (progress >= 0.60) {
                int chimneyX = hx + 4 * bs + 4;
                int chimneyY = hy - 4 * bs;
                for (int i = 0; i < 5; i++) {
                    double smokeProgress = ((progress * 3.5) + (i * 0.2)) % 1.0;
                    int sx = chimneyX + (int) (Math.sin((progress * 8) + i * 1.5) * 6);
                    int sy = chimneyY - (int) (smokeProgress * 45);
                    int alpha = (int) ((1.0 - smokeProgress) * 160);
                    if (alpha > 0) {
                        g2d.setColor(new Color(120, 120, 120, Math.min(255, Math.max(0, alpha))));
                        int sz = 4 + (int) (smokeProgress * 4);
                        g2d.fillRect(sx, sy, sz, sz);
                    }
                }
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
    
            // === Tamed Wolf (0.65 - 1.00) ===
            if (progress >= 0.65) {
                DrawUtils.drawTamedWolf(g2d, 220, groundY - 30, 1, progress * 12);
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
    
            // === Achievement ===
            if (progress >= 0.90) {
                DrawUtils.drawAchievement(g2d, width, "Home Sweet Home", (progress - 0.90) / 0.10);
            }
    
            // HUD
            DrawUtils.drawHUD(g2d, width, 10, 10, 10, 30);
        }
    }

    // =========================================================================
    // NETHERPORTALSCENE.JAVA
    // =========================================================================
    
    /**
     * Scene 7: Nether Portal Activation & Journey into the Nether Dimension.
     * Overworld: Steve lights the Obsidian frame, activating the rectangular Minecraft Nether Portal.
     * Nether Dimension: Authentic Netherrack terrain, Glowstone clusters, lava falls,
     * Nether fortress bridge in distance, Ghast floating, and rising flame embers.
     */
    static class NetherPortalScene extends Scene {
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
    
                // Ghast Fireball
                if (netherProgress > 0.5) {
                    int fbX = ghastX - (int) ((netherProgress - 0.5) / 0.5 * 300);
                    int fbY = ghastY + 20;
                    g2d.setColor(Color.YELLOW);
                    g2d.fillRect(fbX, fbY, 8, 8);
                    g2d.setColor(new Color(255, 120, 0));
                    g2d.drawRect(fbX, fbY, 8, 8);
                }
    
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
    
                // Achievement
                if (progress >= 0.60) {
                    DrawUtils.drawAchievement(g2d, width, "We Need to Go Deeper", (progress - 0.60) / 0.25);
                }
    
                // HUD
                DrawUtils.drawHUD(g2d, width, 10, 10, 8, 40);
            }
        }
    }

    // =========================================================================
    // BLAZEFIGHTSCENE.JAVA
    // =========================================================================
    
    /**
     * Scene 8: Blaze Fight in the Nether Fortress.
     * Blaze shoots fireballs, and after 1 second, Steve dashes at hyper-speed
     * and unleashes a rapid diamond sword slash flurry to defeat the Blaze.
     */
    static class BlazeFightScene extends Scene {
        private final Random random = new Random(850);
    
        public BlazeFightScene(String name, int durationMs) {
            super(name, durationMs);
        }
    
        @Override
        public void render(Graphics2D g2d, int width, int height, double progress) {
            random.setSeed(850 + (long) (progress * 100));

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
                // Stage 3: Rapid 360-degree diamond sword flurry slash
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

    // =========================================================================
    // EYEOFENDERSCENE.JAVA
    // =========================================================================
    
    /**
     * Scene 9: Eye of Ender & Stronghold End Portal.
     * Part 1: Steve throws the Eye of Ender in the Overworld to locate the Stronghold.
     * Part 2: Stronghold End Portal Room with Stone Bricks, Lava pool, End Portal Frame,
     * inserting the final Eye of Ender, activating the cosmic void portal, and jumping in.
     */
    static class EyeOfEnderScene extends Scene {
        private final Random random = new Random(999);
    
        public EyeOfEnderScene(String name, int durationMs) {
            super(name, durationMs);
        }
    
        @Override
        public void render(Graphics2D g2d, int width, int height, double progress) {
            random.setSeed(999 + (long) (progress * 100));

            int bs = 24; // 24px block scale
    
            if (progress < 0.50) {
                // ==========================================
                // PART 1: OVERWORLD - THROWING EYE OF ENDER
                // ==========================================
                double overworldP = progress * 2.0;
    
                // Afternoon Sky & Sun
                DrawUtils.drawSky(g2d, width, height, 0.25);
                DrawUtils.drawMinecraftSun(g2d, 520, 70, 36);
                DrawUtils.drawCloud(g2d, (int) (60 + overworldP * 40), 70, 110);
                DrawUtils.drawCloud(g2d, (int) (340 + overworldP * 50), 100, 90);
    
                // Distant Snowy Mountains & Peaks Backdrop
                int groundY = 440;
                DrawUtils.drawSnowyMountains(g2d, width, groundY, overworldP * 50);
    
                // Ground & Village Foundation
                DrawUtils.drawGround(g2d, width, height, groundY, bs);
    
                // ==========================================
                // AUTHENTIC MINECRAFT NPC VILLAGE
                // ==========================================
    
                // 1. The Iconic Minecraft Village Well (Left: x = 16..88, 3 blocks wide)
                int wellX = 16;
                int wellY = groundY - 2 * bs; // y = 392
                // Well rim base
                DrawUtils.drawCobblestoneBlock(g2d, wellX, groundY - bs, bs);
                DrawUtils.drawWaterBlock(g2d, wellX + bs, groundY - bs, bs, overworldP * 8);
                DrawUtils.drawCobblestoneBlock(g2d, wellX + 2 * bs, groundY - bs, bs);
                // 4 Corner Fence Pillars
                g2d.setColor(new Color(90, 60, 30));
                g2d.fillRect(wellX + 2, wellY - bs, 4, 2 * bs);
                g2d.fillRect(wellX + 3 * bs - 6, wellY - bs, 4, 2 * bs);
                // Well Cobblestone & Wood Canopy Roof
                for (int c = 0; c < 3; c++) {
                    DrawUtils.drawCobblestoneBlock(g2d, wellX + c * bs, wellY - 2 * bs, bs);
                }
                DrawUtils.drawCobblestoneBlock(g2d, wellX + bs, wellY - 3 * bs, bs);
    
                // 2. Iron Golem Standing Guard Holding a Red Poppy (x = 96, y = groundY - 72)
                DrawUtils.drawIronGolem(g2d, 96, groundY - 72, 1, true);
    
                // 3. Village Street Lamp Post (x = 160)
                int lampX = 160;
                g2d.setColor(new Color(60, 45, 30));
                g2d.fillRect(lampX, groundY - 4 * bs, 4, 4 * bs);
                g2d.fillRect(lampX - 4, groundY - 4 * bs, 12, 4);
                DrawUtils.drawTorch(g2d, lampX - 2, groundY - 4 * bs - 10);
    
                // 4. Village Farmland & Crops (x = 192..312, 5 blocks wide)
                int farmX = 192;
                // Oak log borders
                DrawUtils.drawOakLog(g2d, farmX, groundY - bs, bs);
                DrawUtils.drawOakLog(g2d, farmX + 4 * bs, groundY - bs, bs);
                // Moist tilled soil + Crops
                for (int c = 1; c < 4; c++) {
                    DrawUtils.drawFarmlandBlock(g2d, farmX + c * bs, groundY - bs, bs, true);
                    if (c == 2) {
                        // Central Water Canal
                        DrawUtils.drawWaterBlock(g2d, farmX + c * bs, groundY - bs, bs, overworldP * 8);
                    } else if (c == 1) {
                        // Growing Wheat Stalks
                        g2d.setColor(new Color(95, 175, 45));
                        g2d.fillRect(farmX + c * bs + 4, groundY - bs - 8, 3, 8);
                        g2d.fillRect(farmX + c * bs + 12, groundY - bs - 12, 3, 12);
                        g2d.setColor(DrawUtils.GOLD_YELLOW);
                        g2d.fillRect(farmX + c * bs + 11, groundY - bs - 16, 5, 5);
                        g2d.fillRect(farmX + c * bs + 3, groundY - bs - 12, 5, 4);
                    } else {
                        // Growing Carrots (Orange roots with green leaves)
                        g2d.setColor(new Color(60, 160, 40));
                        g2d.fillRect(farmX + c * bs + 4, groundY - bs - 10, 3, 10);
                        g2d.fillRect(farmX + c * bs + 14, groundY - bs - 8, 3, 8);
                        g2d.setColor(new Color(235, 120, 25));
                        g2d.fillRect(farmX + c * bs + 3, groundY - bs - 4, 5, 4);
                        g2d.fillRect(farmX + c * bs + 13, groundY - bs - 3, 5, 3);
                    }
                }
    
                // 5. Stacked Hay Bales beside Farm
                DrawUtils.drawHayBaleBlock(g2d, farmX + 5 * bs + 2, groundY - bs, bs);
                DrawUtils.drawHayBaleBlock(g2d, farmX + 5 * bs + 2, groundY - 2 * bs, bs);
    
                // 6. Farmer Villager with Straw Hat tending crops (x = 205, y = groundY - 64)
                DrawUtils.drawVillager(g2d, 205, groundY - 64, 1, true, "farmer");
    
                // 7. Village Small House / Blacksmith (Right: x = 360..480, 5 blocks wide, 4 blocks tall)
                int houseX = 360;
                int houseY = groundY - 3 * bs;
                // Cobblestone foundation layer
                for (int c = 0; c < 5; c++) {
                    DrawUtils.drawCobblestoneBlock(g2d, houseX + c * bs, groundY - bs, bs);
                }
                // Oak log corner pillars
                for (int r = 1; r < 3; r++) {
                    DrawUtils.drawOakLog(g2d, houseX, groundY - (r + 1) * bs, bs);
                    DrawUtils.drawOakLog(g2d, houseX + 4 * bs, groundY - (r + 1) * bs, bs);
                }
                // Authentic Oak Plank walls
                for (int c = 1; c < 4; c++) {
                    for (int r = 1; r < 3; r++) {
                        DrawUtils.drawOakPlankBlock(g2d, houseX + c * bs, groundY - (r + 1) * bs, bs);
                    }
                }
                // Glass window with white glint
                DrawUtils.drawGlassBlock(g2d, houseX + bs, groundY - 3 * bs, bs);
                // Wooden door
                DrawUtils.drawWoodenDoor(g2d, houseX + 3 * bs, groundY - 3 * bs, bs, 2 * bs, false);
                // Stepped wooden roof with cobblestone rim
                for (int c = 0; c < 5; c++) {
                    DrawUtils.drawCobblestoneBlock(g2d, houseX + c * bs, groundY - 4 * bs, bs);
                }
                for (int c = 1; c < 4; c++) {
                    DrawUtils.drawOakPlankBlock(g2d, houseX + c * bs, groundY - 5 * bs, bs);
                }
                DrawUtils.drawOakPlankBlock(g2d, houseX + 2 * bs, groundY - 6 * bs, bs);
                // House wall torch
                DrawUtils.drawTorch(g2d, houseX + 2 * bs + 4, groundY - 3 * bs);
    
                // 8. Plains Villager (Standing beside house)
                DrawUtils.drawVillager(g2d, houseX + 5 * bs + 6, groundY - 64, 1, false, "plains");
    
                // ==========================================
                // STEVE & EYE OF ENDER THROW
                // ==========================================
                int steveX = 130 + (int) (overworldP * 40);
                int steveY = groundY - 64;
    
                // Eye of Ender Thrown in the Sky
                if (overworldP > 0.15 && overworldP < 0.90) {
                    double eyeT = (overworldP - 0.15) / 0.75;
                    int eyeX = steveX + 24 + (int) (eyeT * 290);
                    // Parabolic flight arc high over the village rooftops
                    int eyeY = (groundY - 50) - (int) (Math.sin(eyeT * Math.PI) * 190) - (int) (eyeT * 30);
    
                    // Draw Eye of Ender Pearl (Teal pearl with black slit pupil)
                    g2d.setColor(new Color(20, 150, 120));
                    g2d.fillRect(eyeX - 4, eyeY - 4, 8, 8);
                    g2d.setColor(new Color(85, 235, 205));
                    g2d.fillRect(eyeX - 3, eyeY - 3, 3, 3);
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(eyeX - 1, eyeY - 3, 2, 6);
    
                    // Trailing Ender particle sparks
                    g2d.setColor(DrawUtils.PORTAL_PURPLE);
                    for (int i = 0; i < 5; i++) {
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
    
                    // Shockwave Ring effect when End Portal activates (portalP >= 0.35 and portalP < 0.55)
                    if (portalP < 0.55) {
                        double shockT = (portalP - 0.35) / 0.20;
                        int ringSize = (int) (shockT * 120);
                        int alpha = Math.max(0, Math.min(255, (int) ((1.0 - shockT) * 200)));
                        g2d.setColor(new Color(25, 200, 180, alpha));
                        g2d.setStroke(new BasicStroke(3.0f));
                        int portalCX = portalBaseX + 3 * bs;
                        int portalCY = portalBaseY;
                        g2d.drawRect(portalCX - ringSize, portalCY - ringSize / 2, ringSize * 2, ringSize);
                        g2d.setStroke(new BasicStroke(1.0f));
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
    
                // 6. Achievement Popup
                if (portalP >= 0.40) {
                    DrawUtils.drawAchievement(g2d, width, "Eye Spy", (portalP - 0.40) / 0.25);
                }
    
                // HUD
                DrawUtils.drawHUD(g2d, width, 10, 10, 8, 50);
            }
        }
    }

    // =========================================================================
    // DRAGONFIGHTSCENE.JAVA
    // =========================================================================
    
    /**
     * Scene 10: The End Dimension - Comedic Boss Fight.
     * Steve shoots 4 arrows with the bow and misses all of them, gets annoyed,
     * pulls out an AK-47 assault rifle, shoots the Ender Dragon directly with full-auto fire,
     * destroys the boss, and celebrates an epic victory.
     */
    static class DragonFightScene extends Scene {
        private final Random random = new Random(800);
    
        public DragonFightScene(String name, int durationMs) {
            super(name, durationMs);
        }
    
        @Override
        public void render(Graphics2D g2d, int width, int height, double progress) {
            random.setSeed(800 + (long) (progress * 100));

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
    
            // 5. Endermen standing on End Island
            DrawUtils.drawEnderman(g2d, 200, groundY - 68, 1, true);
            DrawUtils.drawEnderman(g2d, 500, groundY - 68, 1, false);
    
            // Subtle teleport purple particles flickering near Endermen
            g2d.setColor(DrawUtils.PORTAL_PURPLE);
            for (int i = 0; i < 4; i++) {
                int px1 = 200 + random.nextInt(28) - 4;
                int py1 = groundY - 68 + random.nextInt(68);
                g2d.fillRect(px1, py1, 2, 2);
    
                int px2 = 500 + random.nextInt(28) - 4;
                int py2 = groundY - 68 + random.nextInt(68);
                g2d.fillRect(px2, py2, 2, 2);
            }
    
            // 6. Steve Weapon Progression & Comedic Narrative
            int steveX = (int) (110 + Math.min(progress, 0.70) * 40);
            // Steve dodges Dragon Breath by stepping back (0.10 - 0.18)
            if (progress >= 0.10 && progress <= 0.18) {
                double dodgeT = (progress - 0.10) / 0.08;
                steveX -= (int) (Math.sin(dodgeT * Math.PI) * 18);
            }
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
    
            } else if (progress < 0.30) {
                // Beat 2: Comedic reaction - Steve puts down bow, gets frustrated, pulls out AK-47!
                if (progress < 0.26) {
                    DrawUtils.drawSteveStanding(g2d, steveX, steveY, 1, true);
                } else {
                    // Steve reaches behind and whips out AK-47!
                    DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "ak", 0, false, 0);
                }
    
            } else if (progress < 0.85) {
                // Beat 3: Full-Auto AK-47 spraying directly at the Ender Dragon!
                DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "ak", akRecoil, false, 0);
    
            } else {
                // Beat 4: Victory pose - Steve raising AK-47 proudly in the air!
                DrawUtils.drawSteveWithTool(g2d, steveX, steveY, 1, true, "ak", -0.4, false, 0);
            }
    
            // 7. Dragon Breath Attack (0.10 to 0.18)
            if (progress >= 0.10 && progress <= 0.18) {
                double breathT = (progress - 0.10) / 0.08;
                int startX = (int) (300 + Math.sin(0.10 * Math.PI * 3) * 120);
                int startY = (int) (130 + Math.cos(0.10 * Math.PI * 2) * 30);
                int targetX = 110 + 16;
                int targetY = groundY - 12;
    
                int fX = (int) (startX + breathT * (targetX - startX));
                int fY = (int) (startY + breathT * (targetY - startY) - Math.sin(breathT * Math.PI) * 45);
    
                // Purple smoke trail particles behind fireball
                for (int i = 0; i < 6; i++) {
                    int px = fX + (int) ((random.nextDouble() - 0.5) * 16) + (int) ((1.0 - breathT) * (i * 3));
                    int py = fY + (int) ((random.nextDouble() - 0.5) * 16);
                    g2d.setColor(new Color(135, 55, 195, 160));
                    g2d.fillRect(px, py, 4, 4);
                    g2d.setColor(new Color(60, 15, 85, 120));
                    g2d.fillRect(px + 1, py + 1, 3, 3);
                }
    
                // Dragon Breath Fireball (12x12 purple/black)
                g2d.setColor(new Color(30, 8, 45));
                g2d.fillRect(fX - 6, fY - 6, 12, 12);
                g2d.setColor(DrawUtils.ENDER_PURPLE);
                g2d.fillRect(fX - 4, fY - 4, 8, 8);
                g2d.setColor(new Color(245, 130, 255));
                g2d.fillRect(fX - 2, fY - 2, 4, 4);
            }
    
            // 8. AK-47 Muzzle Flash, Ejected Shell Casings & High-Velocity Bullet Tracers
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
    
            // 9. Ender Dragon Boss
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
    
            // 10. Massive XP Orbs Fountain from Dragon Death (0.68 - 1.00)
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
    
            // 11. Grand Victory Banners & Achievements
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
    
            // 12. HUD (XP level increases rapidly to 100 upon dragon defeat)
            int currentXP = (progress < 0.68) ? 50 : (int) (50 + (progress - 0.68) / 0.32 * 50);
            DrawUtils.drawHUD(g2d, width, 10, 10, 8, currentXP);
        }
    }

    // =========================================================================
    // DRAWUTILS.JAVA
    // =========================================================================
    
    static class DrawUtils {
    
        // Minecraft Core Colors
        public static final Color SKY_BLUE = new Color(135, 206, 235);
        public static final Color GRASS_TOP = new Color(105, 178, 55);
        public static final Color GRASS_SIDE_GREEN = new Color(92, 160, 48);
        public static final Color DIRT_BROWN = new Color(134, 96, 67);
        public static final Color DIRT_DARK = new Color(107, 74, 49);
        public static final Color DIRT_LIGHT = new Color(156, 114, 82);
        public static final Color STONE_GRAY = new Color(128, 128, 128);
        public static final Color STONE_DARK = new Color(95, 95, 95);
        public static final Color STONE_LIGHT = new Color(155, 155, 155);
        public static final Color COBBLE_DARK = new Color(75, 75, 75);
        public static final Color WOOD_BROWN = new Color(162, 130, 78);
        public static final Color WOOD_DARK = new Color(125, 98, 55);
        public static final Color LOG_BROWN = new Color(100, 75, 45);
        public static final Color LOG_SIDE = new Color(108, 85, 52);
        public static final Color LOG_RING = new Color(175, 145, 95);
        public static final Color LEAVES_GREEN = new Color(46, 115, 25);
        public static final Color LEAVES_DARK = new Color(32, 85, 18);
        public static final Color LEAVES_LIGHT = new Color(65, 145, 38);
        public static final Color DIAMOND_BLUE = new Color(80, 225, 235);
        public static final Color DIAMOND_DARK = new Color(40, 165, 185);
        public static final Color NIGHT_SKY = new Color(10, 10, 32);
        public static final Color SUNSET_ORANGE = new Color(255, 135, 45);
        public static final Color SUNSET_PINK = new Color(245, 90, 120);
        public static final Color STEVE_SKIN = new Color(188, 142, 105);
        public static final Color STEVE_SKIN_DARK = new Color(155, 112, 80);
        public static final Color STEVE_HAIR = new Color(60, 35, 15);
        public static final Color STEVE_SHIRT = new Color(45, 165, 180);
        public static final Color STEVE_SHIRT_DARK = new Color(30, 125, 140);
        public static final Color STEVE_PANTS = new Color(55, 55, 165);
        public static final Color STEVE_PANTS_DARK = new Color(40, 40, 125);
        public static final Color STEVE_SHOES = new Color(75, 75, 75);
        public static final Color CREEPER_GREEN = new Color(75, 145, 45);
        public static final Color CREEPER_DARK = new Color(45, 95, 28);
        public static final Color CREEPER_LIGHT = new Color(110, 185, 65);
        public static final Color OBSIDIAN = new Color(22, 18, 35);
        public static final Color OBSIDIAN_PURPLE = new Color(55, 35, 80);
        public static final Color END_STONE = new Color(222, 222, 165);
        public static final Color END_STONE_DARK = new Color(185, 185, 130);
        public static final Color NETHER_RED = new Color(110, 22, 22);
        public static final Color NETHER_DARK = new Color(75, 12, 12);
        public static final Color NETHER_BRICK = new Color(62, 22, 25);
        public static final Color PORTAL_PURPLE = new Color(135, 55, 195);
        public static final Color GOLD_YELLOW = new Color(255, 205, 45);
        public static final Color GOLD_DARK = new Color(205, 155, 25);
        public static final Color IRON_GRAY = new Color(195, 185, 175);
        public static final Color IRON_DARK = new Color(145, 135, 125);
        public static final Color COAL_BLACK = new Color(38, 38, 38);
        public static final Color REDSTONE_RED = new Color(225, 30, 30);
        public static final Color LAVA_ORANGE = new Color(215, 95, 18);
        public static final Color LAVA_YELLOW = new Color(255, 175, 25);
        public static final Color WATER_BLUE = new Color(45, 95, 195, 210);
        public static final Color WATER_SURFACE = new Color(75, 135, 235, 180);
        public static final Color HEART_RED = new Color(195, 25, 25);
        public static final Color XP_GREEN = new Color(115, 225, 45);
        public static final Color ENDER_PURPLE = new Color(85, 0, 130);
        public static final Color DRAGON_BLACK = new Color(28, 28, 28);
        public static final Color DRAGON_GRAY = new Color(55, 55, 55);
        public static final Color DRAGON_PURPLE = new Color(115, 45, 160);
    
        public static final int BLOCK_SIZE = 20;
    
        // ==========================================
        // MINECRAFT TEXTURED BLOCKS
        // ==========================================
    
        /**
         * Generic block with 3D bevel.
         */
        public static void drawBlock(Graphics2D g, int x, int y, int size, Color color) {
            g.setColor(color);
            g.fillRect(x, y, size, size);
    
            // Minecraft style block edge shading
            g.setColor(color.brighter());
            g.drawLine(x, y, x + size - 1, y);
            g.drawLine(x, y, x, y + size - 1);
    
            g.setColor(color.darker());
            g.drawLine(x + size - 1, y, x + size - 1, y + size - 1);
            g.drawLine(x, y + size - 1, x + size - 1, y + size - 1);
    
            g.setColor(new Color(0, 0, 0, 50));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Grass Block with dirt body and jagged grass top.
         */
        public static void drawGrassBlock(Graphics2D g, int x, int y, int size) {
            // Dirt base
            drawDirtBlock(g, x, y, size);
    
            double u = size / 16.0;
            int topH = Math.max(2, (int) (4 * u));
    
            // Green grass top layer
            g.setColor(GRASS_TOP);
            g.fillRect(x, y, size, topH);
    
            // Hanging grass blades fringe (authentic Minecraft pixel fringe)
            g.setColor(GRASS_SIDE_GREEN);
            int[] fringePattern = {2, 4, 3, 5, 2, 4, 3, 2};
            double subW = size / 8.0;
            for (int i = 0; i < 8; i++) {
                int fx = (int) (x + i * subW);
                int fw = Math.max(1, (int) ((i + 1) * subW) - (int) (i * subW));
                int fh = Math.max(1, (int) (fringePattern[i] * u));
                g.fillRect(fx, y + topH, fw, Math.min(size - topH, fh));
            }
    
            // Top highlight
            g.setColor(new Color(255, 255, 255, 40));
            g.drawLine(x, y, x + size - 1, y);
        }
    
        /**
         * Authentic Minecraft Dirt Block with pixel noise texture (Proportionally scaled).
         */
        public static void drawDirtBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(DIRT_BROWN);
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Normalized 16x16 Minecraft dirt pixel clusters
            g.setColor(DIRT_DARK);
            g.fillRect((int) (x + 2 * u), (int) (y + 3 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 10 * u), (int) (y + 2 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (4 * u)));
            g.fillRect((int) (x + 5 * u), (int) (y + 9 * u), Math.max(1, (int) (5 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 12 * u), (int) (y + 11 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (4 * u)));
            g.fillRect((int) (x + 1 * u), (int) (y + 13 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 8 * u), (int) (y + 14 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
    
            g.setColor(DIRT_LIGHT);
            g.fillRect((int) (x + 7 * u), (int) (y + 4 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 13 * u), (int) (y + 7 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 2 * u), (int) (y + 7 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 10 * u), (int) (y + 13 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 4 * u), (int) (y + 1 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (2 * u)));
    
            // Outer edge bevel
            g.setColor(new Color(0, 0, 0, 45));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Stone Block with speckled pixel noise.
         */
        public static void drawStoneBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(STONE_GRAY);
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Stone flecks
            g.setColor(STONE_DARK);
            g.fillRect((int) (x + 3 * u), (int) (y + 2 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 11 * u), (int) (y + 4 * u), Math.max(1, (int) (5 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 2 * u), (int) (y + 9 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (4 * u)));
            g.fillRect((int) (x + 9 * u), (int) (y + 11 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 14 * u), (int) (y + 13 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (3 * u)));
    
            g.setColor(STONE_LIGHT);
            g.fillRect((int) (x + 1 * u), (int) (y + 1 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 8 * u), (int) (y + 3 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 6 * u), (int) (y + 8 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 3 * u), (int) (y + 15 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 14 * u), (int) (y + 8 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (3 * u)));
    
            g.setColor(new Color(0, 0, 0, 40));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Cobblestone Block.
         */
        public static void drawCobblestoneBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(STONE_GRAY);
            g.fillRect(x, y, size, size);
    
            // Cobble mortar & cracks
            g.setColor(COBBLE_DARK);
            g.fillRect(x + 1, y + 6, 8, 2);
            g.fillRect(x + 10, y + 5, 9, 2);
            g.fillRect(x + 8, y + 1, 2, 6);
            g.fillRect(x + 5, y + 12, 10, 2);
            g.fillRect(x + 5, y + 8, 2, 5);
            g.fillRect(x + 14, y + 7, 2, 6);
    
            // Stone highlights
            g.setColor(STONE_LIGHT);
            g.fillRect(x + 2, y + 2, 5, 3);
            g.fillRect(x + 11, y + 1, 6, 3);
            g.fillRect(x + 8, y + 8, 5, 3);
            g.fillRect(x + 2, y + 14, 5, 3);
            g.fillRect(x + 12, y + 14, 6, 3);
    
            g.setColor(new Color(0, 0, 0, 60));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Ore Block (Diamond, Iron, Gold, Coal, Redstone).
         */
        public static void drawOreBlock(Graphics2D g, int x, int y, int size, String oreType) {
            drawStoneBlock(g, x, y, size);
    
            Color mainColor, darkColor, lightColor;
    
            switch (oreType.toLowerCase()) {
                case "iron":
                    mainColor = IRON_GRAY; darkColor = IRON_DARK; lightColor = Color.WHITE; break;
                case "gold":
                    mainColor = GOLD_YELLOW; darkColor = GOLD_DARK; lightColor = Color.WHITE; break;
                case "coal":
                    mainColor = COAL_BLACK; darkColor = Color.BLACK; lightColor = Color.GRAY; break;
                case "redstone":
                    mainColor = REDSTONE_RED; darkColor = new Color(140, 15, 15); lightColor = new Color(255, 130, 130); break;
                default: // diamond
                    mainColor = DIAMOND_BLUE; darkColor = DIAMOND_DARK; lightColor = Color.WHITE; break;
            }
    
            // Ore gem clusters (Minecraft pixel ore shape)
            g.setColor(mainColor);
            g.fillRect(x + 4, y + 3, 5, 4);
            g.fillRect(x + 11, y + 8, 5, 4);
            g.fillRect(x + 4, y + 12, 4, 4);
            g.fillRect(x + 12, y + 3, 3, 3);
    
            g.setColor(darkColor);
            g.fillRect(x + 4, y + 7, 5, 1);
            g.fillRect(x + 11, y + 12, 5, 1);
            g.fillRect(x + 4, y + 16, 4, 1);
    
            g.setColor(lightColor);
            g.fillRect(x + 5, y + 4, 2, 2);
            g.fillRect(x + 12, y + 9, 2, 2);
            g.fillRect(x + 5, y + 13, 2, 2);
        }
    
        /**
         * Authentic Minecraft Oak Log.
         */
        public static void drawOakLog(Graphics2D g, int x, int y, int size) {
            g.setColor(LOG_SIDE);
            g.fillRect(x, y, size, size);
    
            // Vertical bark grooves
            g.setColor(new Color(65, 48, 28));
            g.fillRect(x + 3, y, 2, size);
            g.fillRect(x + 9, y, 3, size);
            g.fillRect(x + 15, y, 2, size);
    
            g.setColor(new Color(135, 105, 68));
            g.fillRect(x + 5, y, 2, size);
            g.fillRect(x + 12, y, 2, size);
    
            g.setColor(new Color(0, 0, 0, 50));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Oak Leaves.
         */
        public static void drawLeavesBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(LEAVES_GREEN);
            g.fillRect(x, y, size, size);
    
            // Leaf clusters
            g.setColor(LEAVES_DARK);
            g.fillRect(x + 2, y + 2, 4, 4);
            g.fillRect(x + 10, y + 3, 5, 3);
            g.fillRect(x + 3, y + 11, 4, 5);
            g.fillRect(x + 11, y + 11, 5, 4);
    
            g.setColor(LEAVES_LIGHT);
            g.fillRect(x + 6, y + 4, 3, 3);
            g.fillRect(x + 2, y + 8, 3, 2);
            g.fillRect(x + 13, y + 7, 3, 3);
            g.fillRect(x + 8, y + 13, 3, 3);
    
            g.setColor(new Color(0, 0, 0, 40));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Oak Planks Block with horizontal boards, nail rivets, and woodgrain.
         */
        public static void drawOakPlankBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(new Color(160, 115, 68)); // Oak plank base
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // 4 Horizontal Plank Boards
            g.setColor(new Color(110, 75, 40)); // Dark horizontal board joints
            g.fillRect(x, (int) (y + 4 * u), size, Math.max(1, (int) (u)));
            g.fillRect(x, (int) (y + 8 * u), size, Math.max(1, (int) (u)));
            g.fillRect(x, (int) (y + 12 * u), size, Math.max(1, (int) (u)));
    
            // Vertical stagger joint lines
            g.fillRect((int) (x + 10 * u), y, Math.max(1, (int) (u)), (int) (4 * u));
            g.fillRect((int) (x + 5 * u), (int) (y + 4 * u), Math.max(1, (int) (u)), (int) (4 * u));
            g.fillRect((int) (x + 12 * u), (int) (y + 8 * u), Math.max(1, (int) (u)), (int) (4 * u));
            g.fillRect((int) (x + 4 * u), (int) (y + 12 * u), Math.max(1, (int) (u)), (int) (4 * u));
    
            // Light board highlights
            g.setColor(new Color(185, 140, 90));
            g.fillRect(x, y, size, Math.max(1, (int) (u)));
            g.fillRect(x, (int) (y + 5 * u), size, Math.max(1, (int) (u)));
            g.fillRect(x, (int) (y + 9 * u), size, Math.max(1, (int) (u)));
            g.fillRect(x, (int) (y + 13 * u), size, Math.max(1, (int) (u)));
    
            // Tiny dark nail rivets
            g.setColor(new Color(85, 55, 30));
            g.fillRect((int) (x + 9 * u), (int) (y + 2 * u), Math.max(1, (int) (u)), Math.max(1, (int) (u)));
            g.fillRect((int) (x + 6 * u), (int) (y + 6 * u), Math.max(1, (int) (u)), Math.max(1, (int) (u)));
            g.fillRect((int) (x + 11 * u), (int) (y + 10 * u), Math.max(1, (int) (u)), Math.max(1, (int) (u)));
            g.fillRect((int) (x + 5 * u), (int) (y + 14 * u), Math.max(1, (int) (u)), Math.max(1, (int) (u)));
    
            g.setColor(new Color(0, 0, 0, 45));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Glass Block with light-cyan border and diagonal glare streaks.
         */
        public static void drawGlassBlock(Graphics2D g, int x, int y, int size) {
            double u = size / 16.0;
    
            // Semi-transparent inner pane
            g.setColor(new Color(200, 235, 255, 60));
            g.fillRect(x, y, size, size);
    
            // Pixelated outer glass frame
            g.setColor(new Color(210, 240, 255, 190));
            g.fillRect(x, y, size, Math.max(1, (int) (u)));
            g.fillRect(x, (int) (y + size - u), size, Math.max(1, (int) (u)));
            g.fillRect(x, y, Math.max(1, (int) (u)), size);
            g.fillRect((int) (x + size - u), y, Math.max(1, (int) (u)), size);
    
            // Diagonal white glass glare glints (Iconic Minecraft Glass texture)
            g.setColor(Color.WHITE);
            // Top-left glint streak
            g.fillRect((int) (x + 2 * u), (int) (y + 2 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 4 * u), (int) (y + 4 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 6 * u), (int) (y + 6 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (2 * u)));
            // Bottom-right glint streak
            g.fillRect((int) (x + 10 * u), (int) (y + 11 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 12 * u), (int) (y + 13 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (2 * u)));
    
            // Light blue glass corner frame pixels
            g.setColor(new Color(150, 195, 225, 200));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Farmland Block (Tilled soil).
         */
        public static void drawFarmlandBlock(Graphics2D g, int x, int y, int size, boolean isMoist) {
            Color baseSoil = isMoist ? new Color(75, 48, 28) : new Color(115, 80, 48);
            g.setColor(baseSoil);
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Dark furrow ridges
            g.setColor(isMoist ? new Color(50, 30, 16) : new Color(85, 55, 30));
            g.fillRect(x, (int) (y + 3 * u), size, Math.max(1, (int) (2 * u)));
            g.fillRect(x, (int) (y + 8 * u), size, Math.max(1, (int) (2 * u)));
            g.fillRect(x, (int) (y + 13 * u), size, Math.max(1, (int) (2 * u)));
    
            // Light crumb highlights
            g.setColor(isMoist ? new Color(105, 68, 40) : new Color(140, 100, 65));
            g.fillRect((int) (x + 3 * u), (int) (y + 1 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (u)));
            g.fillRect((int) (x + 10 * u), (int) (y + 6 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (u)));
            g.fillRect((int) (x + 5 * u), (int) (y + 11 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (u)));
    
            g.setColor(new Color(0, 0, 0, 50));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Chest Block.
         */
        public static void drawChestBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(new Color(160, 110, 55)); // Warm oak chest base
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Black outer frame
            g.setColor(new Color(30, 20, 10));
            g.drawRect(x, y, size, size);
            g.fillRect(x, (int) (y + 7 * u), size, Math.max(1, (int) (2 * u))); // Lid seam
    
            // Silver / Iron lock latch in center
            g.setColor(new Color(220, 220, 220));
            g.fillRect((int) (x + 7 * u), (int) (y + 5 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (5 * u)));
            g.setColor(new Color(80, 80, 80));
            g.drawRect((int) (x + 7 * u), (int) (y + 5 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (5 * u)));
        }
    
        /**
         * Authentic Minecraft Furnace Block (Lit or Unlit).
         */
        public static void drawFurnaceBlock(Graphics2D g, int x, int y, int size, boolean isLit) {
            drawCobblestoneBlock(g, x, y, size);
    
            double u = size / 16.0;
    
            // Front opening / furnace mouth
            int fx = (int) (x + 3 * u);
            int fy = (int) (y + 6 * u);
            int fw = (int) (10 * u);
            int fh = (int) (8 * u);
    
            g.setColor(new Color(25, 25, 25));
            g.fillRect(fx, fy, fw, fh);
    
            if (isLit) {
                // Glowing fiery furnace interior
                g.setColor(new Color(255, 140, 20));
                g.fillRect((int) (fx + 2 * u), (int) (fy + 2 * u), (int) (fw - 4 * u), (int) (fh - 3 * u));
                g.setColor(Color.YELLOW);
                g.fillRect((int) (fx + 3 * u), (int) (fy + 3 * u), (int) (fw - 6 * u), (int) (fh - 5 * u));
            }
        }
    
        /**
         * Authentic Minecraft Stone Brick Block (0=Normal, 1=Mossy, 2=Cracked).
         */
        public static void drawStoneBrickBlock(Graphics2D g, int x, int y, int size, int variant) {
            g.setColor(new Color(125, 125, 125)); // Stone gray
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Mortar lines
            g.setColor(new Color(65, 65, 65));
            g.fillRect(x, (int) (y + 7 * u), size, Math.max(1, (int) (2 * u)));
            g.fillRect(x, (int) (y + 15 * u), size, Math.max(1, (int) (u)));
            g.fillRect((int) (x + 7 * u), y, Math.max(1, (int) (2 * u)), (int) (7 * u));
            g.fillRect((int) (x + 11 * u), (int) (y + 8 * u), Math.max(1, (int) (2 * u)), (int) (7 * u));
            g.fillRect((int) (x + 3 * u), (int) (y + 8 * u), Math.max(1, (int) (2 * u)), (int) (7 * u));
    
            // Brick highlights
            g.setColor(new Color(155, 155, 155));
            g.fillRect(x, y, (int) (7 * u), Math.max(1, (int) (u)));
            g.fillRect((int) (x + 8 * u), y, (int) (7 * u), Math.max(1, (int) (u)));
            g.fillRect((int) (x + 4 * u), (int) (y + 8 * u), (int) (6 * u), Math.max(1, (int) (u)));
    
            // Variant details
            if (variant == 1) {
                // Mossy Stone Brick (green vines)
                g.setColor(new Color(55, 115, 45));
                g.fillRect((int) (x + 4 * u), (int) (y + 2 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (4 * u)));
                g.fillRect((int) (x + 9 * u), (int) (y + 6 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (3 * u)));
                g.fillRect((int) (x + 2 * u), (int) (y + 10 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (3 * u)));
            } else if (variant == 2) {
                // Cracked Stone Brick
                g.setColor(new Color(40, 40, 40));
                g.drawLine((int) (x + 2 * u), (int) (y + 3 * u), (int) (x + 12 * u), (int) (y + 13 * u));
                g.drawLine((int) (x + 8 * u), (int) (y + 2 * u), (int) (x + 3 * u), (int) (y + 7 * u));
            }
    
            g.setColor(new Color(0, 0, 0, 50));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft End Portal Frame Block.
         */
        public static void drawEndPortalFrame(Graphics2D g, int x, int y, int size, boolean hasEye) {
            // End stone/green sandstone frame base
            g.setColor(new Color(75, 110, 92));
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Top decorative gold/teal rim
            g.setColor(new Color(135, 175, 140));
            g.fillRect(x, y, size, Math.max(1, (int) (3 * u)));
            g.setColor(new Color(50, 75, 62));
            g.fillRect(x, (int) (y + size - 3 * u), size, Math.max(1, (int) (3 * u)));
    
            // Central socket
            g.setColor(new Color(35, 52, 44));
            g.fillRect((int) (x + 3 * u), (int) (y + 3 * u), (int) (10 * u), (int) (10 * u));
    
            // Eye of Ender inserted into socket
            if (hasEye) {
                // Dark green/teal pearl
                g.setColor(new Color(25, 145, 115));
                g.fillRect((int) (x + 4 * u), (int) (y + 4 * u), (int) (8 * u), (int) (8 * u));
                // Cyan highlight
                g.setColor(new Color(85, 230, 200));
                g.fillRect((int) (x + 5 * u), (int) (y + 5 * u), (int) (3 * u), (int) (3 * u));
                // Black pupil
                g.setColor(Color.BLACK);
                g.fillRect((int) (x + 7 * u), (int) (y + 6 * u), Math.max(1, (int) (2 * u)), (int) (4 * u));
            }
    
            g.setColor(new Color(0, 0, 0, 60));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft End Stone Block.
         */
        public static void drawEndStoneBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(new Color(222, 222, 168)); // Pale yellow-gray base
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Dark speckles
            g.setColor(new Color(185, 185, 130));
            g.fillRect((int) (x + 2 * u), (int) (y + 3 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 9 * u), (int) (y + 2 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 4 * u), (int) (y + 8 * u), Math.max(1, (int) (5 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 11 * u), (int) (y + 11 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (4 * u)));
            g.fillRect((int) (x + 1 * u), (int) (y + 12 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
    
            // Light sandy flecks
            g.setColor(new Color(245, 245, 198));
            g.fillRect((int) (x + 6 * u), (int) (y + 4 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 12 * u), (int) (y + 7 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 2 * u), (int) (y + 7 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 7 * u), (int) (y + 13 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (2 * u)));
    
            g.setColor(new Color(0, 0, 0, 35));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Bedrock Block.
         */
        public static void drawBedrockBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(new Color(35, 35, 35)); // Charcoal base
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Dark black patches
            g.setColor(new Color(15, 15, 15));
            g.fillRect((int) (x + 2 * u), (int) (y + 2 * u), Math.max(1, (int) (5 * u)), Math.max(1, (int) (4 * u)));
            g.fillRect((int) (x + 9 * u), (int) (y + 8 * u), Math.max(1, (int) (6 * u)), Math.max(1, (int) (5 * u)));
            g.fillRect((int) (x + 1 * u), (int) (y + 10 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (4 * u)));
    
            // Lighter stone gray flecks
            g.setColor(new Color(75, 75, 75));
            g.fillRect((int) (x + 8 * u), (int) (y + 3 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 4 * u), (int) (y + 7 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 6 * u), (int) (y + 13 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (2 * u)));
    
            g.setColor(Color.BLACK);
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Cosmic End Portal Starry Void.
         */
        public static void drawEndPortalPlane(Graphics2D g, int x, int y, int width, int height, double time) {
            // Pure pitch-black void base
            g.setColor(new Color(8, 6, 14));
            g.fillRect(x, y, width, height);
    
            // Cosmic drifting starry parallax layers
            Random starR = new Random(333);
            int starCount = 30;
            for (int layer = 1; layer <= 3; layer++) {
                double speed = layer * 8.0;
                for (int i = 0; i < starCount / 3; i++) {
                    int sx = x + starR.nextInt(Math.max(1, width - 4));
                    int sy = y + (int) ((starR.nextInt(Math.max(1, height)) + time * speed) % height);
                    if (layer == 1) {
                        g.setColor(new Color(90, 180, 255, 180)); // Cyan star
                        g.fillRect(sx, sy, 2, 2);
                    } else if (layer == 2) {
                        g.setColor(new Color(210, 120, 255, 200)); // Magenta star
                        g.fillRect(sx, sy, 3, 3);
                    } else {
                        g.setColor(Color.WHITE); // Bright white star
                        g.fillRect(sx, sy, 2, 2);
                    }
                }
            }
    
            // Inner glowing border
            g.setColor(new Color(25, 145, 115, 200));
            g.drawRect(x, y, width - 1, height - 1);
        }
    
        /**
         * Authentic Minecraft Obsidian Block.
         */
        public static void drawObsidianBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(OBSIDIAN);
            g.fillRect(x, y, size, size);
    
            g.setColor(OBSIDIAN_PURPLE);
            g.fillRect(x + 3, y + 4, 4, 3);
            g.fillRect(x + 11, y + 2, 3, 4);
            g.fillRect(x + 6, y + 11, 5, 3);
            g.fillRect(x + 13, y + 12, 3, 4);
    
            g.setColor(new Color(110, 75, 150));
            g.fillRect(x + 4, y + 5, 2, 2);
            g.fillRect(x + 12, y + 3, 1, 2);
            g.fillRect(x + 7, y + 12, 2, 1);
    
            g.setColor(new Color(0, 0, 0, 80));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Netherrack Block.
         */
        public static void drawNetherrackBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(new Color(112, 34, 34)); // Blood-red base
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Dark crimson/black mottled texture
            g.setColor(new Color(65, 14, 14));
            g.fillRect((int) (x + 2 * u), (int) (y + 3 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 9 * u), (int) (y + 2 * u), Math.max(1, (int) (5 * u)), Math.max(1, (int) (4 * u)));
            g.fillRect((int) (x + 4 * u), (int) (y + 9 * u), Math.max(1, (int) (6 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 11 * u), (int) (y + 11 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (4 * u)));
            g.fillRect((int) (x + 1 * u), (int) (y + 12 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (3 * u)));
    
            // Bright blood-red highlights
            g.setColor(new Color(155, 52, 52));
            g.fillRect((int) (x + 6 * u), (int) (y + 5 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 13 * u), (int) (y + 6 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 2 * u), (int) (y + 7 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 8 * u), (int) (y + 13 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
    
            // Black speckles
            g.setColor(new Color(25, 6, 6));
            g.fillRect((int) (x + 3 * u), (int) (y + 4 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 10 * u), (int) (y + 10 * u), Math.max(1, (int) (2 * u)), Math.max(1, (int) (2 * u)));
    
            g.setColor(new Color(0, 0, 0, 50));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Nether Brick Block.
         */
        public static void drawNetherBrickBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(new Color(48, 22, 26)); // Dark maroon brick base
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Mortar lines
            g.setColor(new Color(22, 8, 12));
            g.fillRect(x, (int) (y + 7 * u), size, Math.max(1, (int) (2 * u)));
            g.fillRect(x, (int) (y + 15 * u), size, Math.max(1, (int) (u)));
            g.fillRect((int) (x + 7 * u), y, Math.max(1, (int) (2 * u)), (int) (7 * u));
            g.fillRect((int) (x + 11 * u), (int) (y + 8 * u), Math.max(1, (int) (2 * u)), (int) (7 * u));
            g.fillRect((int) (x + 3 * u), (int) (y + 8 * u), Math.max(1, (int) (2 * u)), (int) (7 * u));
    
            // Brick highlights
            g.setColor(new Color(75, 34, 40));
            g.fillRect(x, y, (int) (7 * u), Math.max(1, (int) (u)));
            g.fillRect((int) (x + 8 * u), y, (int) (7 * u), Math.max(1, (int) (u)));
            g.fillRect((int) (x + 4 * u), (int) (y + 8 * u), (int) (6 * u), Math.max(1, (int) (u)));
    
            g.setColor(new Color(0, 0, 0, 60));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Glowstone Block.
         */
        public static void drawGlowstoneBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(new Color(215, 155, 45)); // Gold crystal base
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Bright yellow-white crystal nodes
            g.setColor(new Color(255, 240, 120));
            g.fillRect((int) (x + 2 * u), (int) (y + 2 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (4 * u)));
            g.fillRect((int) (x + 9 * u), (int) (y + 3 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 3 * u), (int) (y + 9 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (4 * u)));
            g.fillRect((int) (x + 10 * u), (int) (y + 9 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (5 * u)));
    
            // Deep amber crystal borders
            g.setColor(new Color(155, 95, 20));
            g.fillRect((int) (x + 6 * u), (int) (y + 4 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 1 * u), (int) (y + 7 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 7 * u), (int) (y + 11 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (3 * u)));
    
            // Dark brown matrix lines
            g.setColor(new Color(90, 50, 10));
            g.fillRect((int) (x + 7 * u), y, Math.max(1, (int) (u)), size);
            g.fillRect(x, (int) (y + 7 * u), size, Math.max(1, (int) (u)));
    
            g.setColor(new Color(0, 0, 0, 40));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Molten Lava Block.
         */
        public static void drawLavaBlock(Graphics2D g, int x, int y, int size, double wave) {
            g.setColor(new Color(230, 95, 15)); // Molten orange base
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
            int shift = (int) (wave * 4) % 16;
    
            // Bright yellow-gold magma swirls
            g.setColor(new Color(255, 210, 35));
            g.fillRect(x, (int) (y + ((2 * u + shift * u) % size)), size, Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + ((4 * u + shift * u) % size)), (int) (y + 8 * u), Math.max(1, (int) (6 * u)), Math.max(1, (int) (3 * u)));
    
            // Deep crust red lines
            g.setColor(new Color(150, 35, 10));
            g.fillRect(x, (int) (y + ((7 * u + shift * u) % size)), size, Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + ((10 * u + shift * u) % size)), (int) (y + 12 * u), Math.max(1, (int) (5 * u)), Math.max(1, (int) (3 * u)));
        }
    
        /**
         * Authentic Minecraft Crafting Table with authentic 3x3 grid top, saw and hammer side motifs.
         */
        public static void drawCraftingTable(Graphics2D g, int x, int y, int size) {
            double u = size / 16.0;
    
            // Oak wood base
            g.setColor(new Color(160, 115, 68));
            g.fillRect(x, y, size, size);
    
            // Top 3x3 crafting grid table top
            g.setColor(new Color(195, 155, 95));
            g.fillRect(x, y, size, Math.max(1, (int) (3 * u)));
            g.setColor(new Color(90, 60, 30));
            g.fillRect(x, (int) (y + 3 * u), size, Math.max(1, (int) (u)));
            g.fillRect((int) (x + 5 * u), y, Math.max(1, (int) (u)), (int) (3 * u));
            g.fillRect((int) (x + 10 * u), y, Math.max(1, (int) (u)), (int) (3 * u));
    
            // Side tool board recessed background
            g.setColor(new Color(110, 75, 40));
            g.fillRect((int) (x + 2 * u), (int) (y + 5 * u), (int) (12 * u), (int) (9 * u));
    
            // Saw blade motif (left side)
            g.setColor(new Color(220, 220, 220)); // Iron saw blade
            g.fillRect((int) (x + 4 * u), (int) (y + 6 * u), (int) (2 * u), (int) (7 * u));
            g.setColor(new Color(130, 80, 40)); // Saw handle
            g.fillRect((int) (x + 4 * u), (int) (y + 11 * u), (int) (3 * u), (int) (2 * u));
    
            // Hammer / Pliers motif (right side)
            g.setColor(new Color(220, 220, 220)); // Hammer head
            g.fillRect((int) (x + 8 * u), (int) (y + 6 * u), (int) (5 * u), (int) (2 * u));
            g.setColor(new Color(130, 80, 40)); // Hammer handle
            g.fillRect((int) (x + 10 * u), (int) (y + 8 * u), (int) (2 * u), (int) (5 * u));
    
            // Corner iron nails
            g.setColor(new Color(50, 50, 50));
            g.fillRect((int) (x + u), (int) (y + 4 * u), Math.max(1, (int) (u)), Math.max(1, (int) (u)));
            g.fillRect((int) (x + 14 * u), (int) (y + 4 * u), Math.max(1, (int) (u)), Math.max(1, (int) (u)));
            g.fillRect((int) (x + u), (int) (y + 14 * u), Math.max(1, (int) (u)), Math.max(1, (int) (u)));
            g.fillRect((int) (x + 14 * u), (int) (y + 14 * u), Math.max(1, (int) (u)), Math.max(1, (int) (u)));
    
            g.setColor(new Color(0, 0, 0, 50));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Wall Torch.
         */
        public static void drawTorch(Graphics2D g, int x, int y) {
            // Wooden stick
            g.setColor(new Color(110, 85, 50));
            g.fillRect(x + 2, y + 4, 4, 12);
            g.setColor(new Color(75, 55, 30));
            g.fillRect(x + 4, y + 4, 2, 12);
    
            // Coal head
            g.setColor(COAL_BLACK);
            g.fillRect(x + 1, y + 2, 6, 4);
    
            // Flame (yellow core, orange outer)
            g.setColor(GOLD_YELLOW);
            g.fillRect(x + 2, y - 2, 4, 4);
            g.setColor(LAVA_ORANGE);
            g.drawRect(x + 1, y - 3, 5, 5);
            g.setColor(Color.WHITE);
            g.fillRect(x + 3, y - 1, 2, 2);
        }
    
        /**
         * Authentic Minecraft Wooden Door (Closed or Open).
         */
        public static void drawWoodenDoor(Graphics2D g, int x, int y, int width, int height, boolean isOpen) {
            if (!isOpen) {
                // Closed door: full front view
                g.setColor(new Color(135, 95, 52));
                g.fillRect(x, y, width, height);
    
                // Door border & panels
                g.setColor(new Color(95, 65, 35));
                g.drawRect(x, y, width, height);
    
                // Top window grids (2 rectangular cutouts)
                g.setColor(new Color(55, 38, 20));
                g.fillRect(x + 3, y + 4, width / 2 - 4, height / 3);
                g.fillRect(x + width / 2 + 1, y + 4, width / 2 - 4, height / 3);
    
                // Bottom recessed panels
                g.fillRect(x + 3, y + height / 2, width / 2 - 4, height / 3);
                g.fillRect(x + width / 2 + 1, y + height / 2, width / 2 - 4, height / 3);
    
                // Gold doorknob
                g.setColor(GOLD_YELLOW);
                g.fillRect(x + width - 5, y + height / 2 - 2, 3, 4);
            } else {
                // Open door: side perspective swung open against the doorframe
                g.setColor(new Color(110, 75, 40));
                g.fillRect(x, y, 4, height);
                g.setColor(new Color(80, 50, 25));
                g.drawRect(x, y, 4, height);
            }
        }
    
        /**
         * Authentic Minecraft Water Block.
         */
        public static void drawWaterBlock(Graphics2D g, int x, int y, int size, double wave) {
            g.setColor(WATER_BLUE);
            g.fillRect(x, y, size, size);
    
            // Flowing pixel lines
            g.setColor(WATER_SURFACE);
            int offset = (int) (wave * 4) % 4;
            g.fillRect(x + offset, y + 2, 6, 2);
            g.fillRect(x + (offset + 8) % size, y + 7, 7, 2);
            g.fillRect(x + (offset + 4) % size, y + 13, 8, 2);
        }
    
        /**
         * Authentic Minecraft Nether Portal swirling rectangular texture.
         */
        public static void drawNetherPortalTexture(Graphics2D g, int x, int y, int width, int height, double time) {
            // Base dark purple plane
            g.setColor(new Color(85, 20, 140, 230));
            g.fillRect(x, y, width, height);
    
            // Animated swirling pixel noise lines
            Random pRand = new Random(101);
            int pxSize = 4;
            for (int py = y; py < y + height; py += pxSize) {
                for (int px = x; px < x + width; px += pxSize) {
                    double wave1 = Math.sin((px - x) * 0.15 + (py - y) * 0.20 + time * 12);
                    double wave2 = Math.cos((px - x) * 0.25 - (py - y) * 0.15 - time * 8);
                    double val = (wave1 + wave2) / 2.0;
    
                    if (val > 0.45) {
                        g.setColor(new Color(210, 120, 255, 220)); // Light magenta/lavender
                        g.fillRect(px, py, pxSize, pxSize);
                    } else if (val > 0.0) {
                        g.setColor(new Color(145, 45, 210, 200)); // Vibrant purple
                        g.fillRect(px, py, pxSize, pxSize);
                    } else if (val > -0.45) {
                        g.setColor(new Color(95, 20, 155, 210)); // Deep purple
                        g.fillRect(px, py, pxSize, pxSize);
                    } else {
                        g.setColor(new Color(55, 10, 95, 230)); // Dark violet
                        g.fillRect(px, py, pxSize, pxSize);
                    }
                }
            }
    
            // Shimmering white portal spark lines
            for (int i = 0; i < 5; i++) {
                int sparkX = x + pRand.nextInt(Math.max(1, width - 6));
                int sparkY = y + (int) ((pRand.nextInt(Math.max(1, height)) + time * 40) % height);
                g.setColor(new Color(255, 220, 255, 190));
                g.fillRect(sparkX, sparkY, 4, 2);
            }
    
            // Exact inner frame border outline
            g.setColor(new Color(160, 60, 230, 180));
            g.drawRect(x, y, width - 1, height - 1);
        }
    
        // ==========================================
        // MINECRAFT ENTITY RENDERERS
        // ==========================================
    
        /**
         * Authentic Minecraft Steve with pixel-art detail (Brisk natural walk).
         */
        public static void drawSteve(Graphics2D g, int x, int y, int scale, boolean facingRight) {
            drawSteveWithTool(g, x, y, scale, facingRight, "hand", 0);
        }
    
        /**
         * Steve standing completely still with straight legs.
         */
        public static void drawSteveStanding(Graphics2D g, int x, int y, int scale, boolean facingRight) {
            drawSteveWithTool(g, x, y, scale, facingRight, "hand", 0, false, 0);
        }
    
        /**
         * Authentic Minecraft Steve holding a tool.
         */
        public static void drawSteveWithTool(Graphics2D g, int x, int y, int scale, boolean facingRight, String tool, double overrideSwing) {
            boolean isWalking = (overrideSwing == 0);
            drawSteveWithTool(g, x, y, scale, facingRight, tool, overrideSwing, isWalking, x * 0.28);
        }
    
        /**
         * Fully controlled Steve renderer.
         */
        public static void drawSteveWithTool(Graphics2D g, int x, int y, int scale, boolean facingRight, String tool, double overrideSwing, boolean isWalking, double walkPhase) {
            int px = 2 * scale; // 1 pixel = 2*scale screen pixels
            double legSwing = isWalking ? Math.sin(walkPhase) * 0.55 : 0;
            if (!facingRight) legSwing = -legSwing;
    
            AffineTransform old = g.getTransform();
    
            // 1. BACK ARM
            g.translate(x + 8 * px, y + 8 * px);
            g.rotate(-legSwing);
            g.setColor(STEVE_SHIRT);
            g.fillRect(-2 * px, 0, 4 * px, 4 * px);
            g.setColor(STEVE_SHIRT_DARK);
            g.fillRect(0, 0, 2 * px, 4 * px);
            g.setColor(STEVE_SKIN);
            g.fillRect(-2 * px, 4 * px, 4 * px, 8 * px);
            g.setColor(STEVE_SKIN_DARK);
            g.fillRect(0, 4 * px, 2 * px, 8 * px);
            g.setTransform(old);
    
            // 2. BACK LEG
            g.translate(x + 8 * px, y + 20 * px);
            g.rotate(legSwing);
            g.setColor(STEVE_PANTS);
            g.fillRect(-2 * px, 0, 4 * px, 10 * px);
            g.setColor(STEVE_PANTS_DARK);
            g.fillRect(0, 0, 2 * px, 10 * px);
            g.setColor(STEVE_SHOES);
            g.fillRect(-2 * px, 10 * px, 4 * px, 2 * px);
            g.setTransform(old);
    
            // 3. BODY (8x12 pixels)
            g.setColor(STEVE_SHIRT);
            g.fillRect(x + 4 * px, y + 8 * px, 8 * px, 12 * px);
            // V-Neck collar
            g.setColor(STEVE_SKIN);
            g.fillRect(x + 7 * px, y + 8 * px, 2 * px, 2 * px);
            // Shirt shading
            g.setColor(STEVE_SHIRT_DARK);
            g.fillRect(x + 4 * px, y + 18 * px, 8 * px, 2 * px);
    
            // 4. FRONT LEG
            g.translate(x + 8 * px, y + 20 * px);
            g.rotate(-legSwing);
            g.setColor(STEVE_PANTS);
            g.fillRect(-2 * px, 0, 4 * px, 10 * px);
            g.setColor(STEVE_PANTS_DARK);
            g.fillRect(0, 0, 2 * px, 10 * px);
            g.setColor(STEVE_SHOES);
            g.fillRect(-2 * px, 10 * px, 4 * px, 2 * px);
            g.setTransform(old);
    
            // 5. HEAD (8x8 pixels)
            g.setColor(STEVE_SKIN);
            g.fillRect(x + 4 * px, y, 8 * px, 8 * px);
    
            // Hair (Top and sides)
            g.setColor(STEVE_HAIR);
            g.fillRect(x + 4 * px, y, 8 * px, 2 * px);
            if (facingRight) {
                g.fillRect(x + 4 * px, y + 2 * px, 2 * px, 3 * px);
            } else {
                g.fillRect(x + 10 * px, y + 2 * px, 2 * px, 3 * px);
            }
    
            // Eyes (White + Indigo Pupils)
            g.setColor(Color.WHITE);
            if (facingRight) {
                g.fillRect(x + 8 * px, y + 4 * px, 2 * px, px);
                g.setColor(new Color(0, 50, 180));
                g.fillRect(x + 9 * px, y + 4 * px, px, px);
            } else {
                g.fillRect(x + 6 * px, y + 4 * px, 2 * px, px);
                g.setColor(new Color(0, 50, 180));
                g.fillRect(x + 6 * px, y + 4 * px, px, px);
            }
    
            // Nose & Mouth (Steve's iconic beard/mouth)
            g.setColor(new Color(135, 80, 50));
            if (facingRight) {
                g.fillRect(x + 8 * px, y + 6 * px, 2 * px, px);
            } else {
                g.fillRect(x + 6 * px, y + 6 * px, 2 * px, px);
            }
    
            // 6. FRONT ARM WITH TOOL
            g.translate(x + 8 * px, y + 8 * px);
            double armSwing = facingRight ? overrideSwing : -overrideSwing;
            if (tool == null || tool.equals("hand") || tool.isEmpty()) {
                g.rotate(armSwing + legSwing);
            } else {
                g.rotate(armSwing - (facingRight ? Math.PI / 4 : -Math.PI / 4));
            }
    
            g.setColor(STEVE_SHIRT);
            g.fillRect(-2 * px, 0, 4 * px, 4 * px);
            g.setColor(STEVE_SKIN);
            g.fillRect(-2 * px, 4 * px, 4 * px, 8 * px);
    
            // Tool rendering
            if ("pickaxe".equals(tool) || "iron_pickaxe".equals(tool)) {
                // Authentic Minecraft Iron Pickaxe (correctly mounted at tip of handle)
                // 1. Wooden Handle extending outward from hand (10*px to 24*px)
                g.setColor(WOOD_BROWN);
                g.fillRect(-px, 10 * px, 2 * px, 14 * px);
                g.setColor(new Color(115, 75, 35));
                g.fillRect(0, 10 * px, px, 14 * px);
    
                // 2. Iron Pickaxe Head mounted at the tip of the handle (22*px to 26*px)
                // Main metallic iron crossbar
                g.setColor(new Color(220, 220, 220)); // Bright Iron metal
                g.fillRect(-6 * px, 22 * px, 13 * px, 3 * px);
    
                // Forward striking pick point (curving forward towards target)
                g.fillRect(4 * px, 19 * px, 3 * px, 4 * px);
                g.fillRect(6 * px, 16 * px, 2 * px, 4 * px);
    
                // Rear curved pick counter-point
                g.fillRect(-7 * px, 19 * px, 2 * px, 4 * px);
                g.fillRect(-8 * px, 16 * px, 2 * px, 4 * px);
    
                // Shiny white metallic iron highlights along top edge
                g.setColor(Color.WHITE);
                g.fillRect(-5 * px, 22 * px, 11 * px, px);
                g.fillRect(5 * px, 17 * px, px, 3 * px);
    
                // Dark steel bottom outline
                g.setColor(new Color(110, 110, 110));
                g.fillRect(-6 * px, 24 * px, 12 * px, px);
                g.fillRect(7 * px, 16 * px, px, 4 * px);
            } else if ("diamond_pickaxe".equals(tool)) {
                // Diamond Pickaxe
                g.setColor(WOOD_BROWN);
                g.fillRect(-px, 10 * px, 2 * px, 14 * px);
                g.setColor(DIAMOND_BLUE);
                g.fillRect(-6 * px, 22 * px, 13 * px, 3 * px);
                g.fillRect(4 * px, 19 * px, 3 * px, 4 * px);
                g.fillRect(6 * px, 16 * px, 2 * px, 4 * px);
                g.fillRect(-7 * px, 19 * px, 2 * px, 4 * px);
                g.fillRect(-8 * px, 16 * px, 2 * px, 4 * px);
                g.setColor(Color.WHITE);
                g.fillRect(-5 * px, 22 * px, 11 * px, px);
                g.setColor(DIAMOND_DARK);
                g.fillRect(-6 * px, 24 * px, 12 * px, px);
            } else if ("sword".equals(tool)) {
                // Hilt
                g.setColor(WOOD_BROWN);
                g.fillRect(-px, 10 * px, 2 * px, 4 * px);
                // Guard
                g.setColor(new Color(110, 85, 45));
                g.fillRect(-3 * px, 14 * px, 6 * px, 2 * px);
                // Diamond Blade
                g.setColor(DIAMOND_BLUE);
                g.fillRect(-px, 16 * px, 2 * px, 16 * px);
                g.setColor(Color.WHITE);
                g.fillRect(0, 16 * px, px, 15 * px);
            } else if ("bow".equals(tool)) {
                // Curved wooden bow limbs
                g.setColor(WOOD_DARK);
                g.fillRect(-px, 8 * px, 2 * px, 12 * px);
                g.fillRect(-2 * px, 6 * px, 2 * px, 3 * px);
                g.fillRect(-2 * px, 19 * px, 2 * px, 3 * px);
                // Bow string
                g.setColor(Color.WHITE);
                g.drawLine(-2 * px, 6 * px, -2 * px, 22 * px);
            } else if ("ak".equals(tool) || "ak47".equals(tool) || "gun".equals(tool)) {
                // Authentic Minecraft Pixel-Art AK-47 Assault Rifle
                // 1. Wooden Stock
                g.setColor(new Color(130, 80, 40));
                g.fillRect(-6 * px, 9 * px, 5 * px, 3 * px);
                g.fillRect(-7 * px, 10 * px, 2 * px, 3 * px);
    
                // 2. Dark Iron Receiver & Grip
                g.setColor(new Color(45, 45, 45));
                g.fillRect(-2 * px, 9 * px, 8 * px, 3 * px);
                // Pistol grip
                g.setColor(new Color(130, 80, 40));
                g.fillRect(-px, 12 * px, 2 * px, 3 * px);
    
                // 3. Curved Banana Magazine
                g.setColor(new Color(30, 30, 30));
                g.fillRect(2 * px, 12 * px, 2 * px, 5 * px);
                g.fillRect(3 * px, 15 * px, 2 * px, 2 * px);
    
                // 4. Wooden Handguard
                g.setColor(new Color(145, 90, 45));
                g.fillRect(6 * px, 9 * px, 5 * px, 2 * px);
    
                // 5. Long Steel Barrel & Gas Tube
                g.setColor(new Color(60, 60, 60));
                g.fillRect(6 * px, 8 * px, 12 * px, 2 * px);
                // Front sight & Muzzle
                g.setColor(new Color(35, 35, 35));
                g.fillRect(16 * px, 6 * px, 2 * px, 3 * px);
                g.fillRect(17 * px, 8 * px, 2 * px, 2 * px);
            } else if ("dirt".equals(tool) || "block".equals(tool)) {
                // Holding a 3D mini dirt block in hand
                drawDirtBlock(g, -3 * px, 7 * px, 6 * px);
            } else if ("wood".equals(tool) || "plank".equals(tool)) {
                // Holding a mini wood plank block in hand
                drawBlock(g, -3 * px, 7 * px, 6 * px, WOOD_BROWN);
            } else if ("log".equals(tool)) {
                // Holding a mini oak log block in hand
                drawOakLog(g, -3 * px, 7 * px, 6 * px);
            } else if ("door".equals(tool)) {
                // Holding a wooden door item in hand
                g.setColor(new Color(135, 95, 52));
                g.fillRect(-2 * px, 5 * px, 4 * px, 10 * px);
                g.setColor(new Color(95, 65, 35));
                g.drawRect(-2 * px, 5 * px, 4 * px, 10 * px);
            }
            g.setTransform(old);
        }
    
        /**
         * Authentic Minecraft Zombie.
         */
        public static void drawZombie(Graphics2D g, int x, int y, int scale, double progress) {
            int px = 2 * scale;
            double swingAngle = Math.sin(progress * 10) * 0.25;
    
            AffineTransform old = g.getTransform();
    
            // Arms forward (iconic Minecraft zombie pose)
            g.translate(x + 8 * px, y + 8 * px);
            g.rotate(-Math.PI / 2 + swingAngle);
            g.setColor(new Color(0, 145, 95));
            g.fillRect(-2 * px, 0, 4 * px, 12 * px);
            g.setTransform(old);
    
            // Legs
            g.translate(x + 8 * px, y + 20 * px);
            g.rotate(swingAngle);
            g.setColor(new Color(45, 45, 145));
            g.fillRect(-2 * px, 0, 4 * px, 12 * px);
            g.setTransform(old);
    
            // Body
            g.setColor(new Color(0, 120, 145));
            g.fillRect(x + 4 * px, y + 8 * px, 8 * px, 12 * px);
    
            // Front leg
            g.translate(x + 8 * px, y + 20 * px);
            g.rotate(-swingAngle);
            g.setColor(new Color(45, 45, 145));
            g.fillRect(-2 * px, 0, 4 * px, 12 * px);
            g.setTransform(old);
    
            // Head
            g.setColor(new Color(0, 145, 95));
            g.fillRect(x + 4 * px, y, 8 * px, 8 * px);
            // Black hollow eyes
            g.setColor(Color.BLACK);
            g.fillRect(x + 5 * px, y + 4 * px, 2 * px, px);
            g.fillRect(x + 9 * px, y + 4 * px, 2 * px, px);
        }
    
        /**
         * Authentic Minecraft Creeper with camouflage pixel mosaic.
         */
        public static void drawCreeper(Graphics2D g, int x, int y, int scale) {
            int px = 2 * scale;
            double swing = Math.sin(x * 0.1) * 0.25;
    
            AffineTransform old = g.getTransform();
    
            // 4 Legs
            g.translate(x + 5 * px, y + 20 * px);
            g.rotate(-swing);
            g.setColor(CREEPER_DARK);
            g.fillRect(-2 * px, 0, 4 * px, 6 * px);
            g.setTransform(old);
    
            g.translate(x + 11 * px, y + 20 * px);
            g.rotate(swing);
            g.setColor(CREEPER_GREEN);
            g.fillRect(-2 * px, 0, 4 * px, 6 * px);
            g.setColor(Color.BLACK);
            g.fillRect(-2 * px, 5 * px, 4 * px, px);
            g.setTransform(old);
    
            // Body (8x12) with camo pixels
            g.setColor(CREEPER_GREEN);
            g.fillRect(x + 4 * px, y + 8 * px, 8 * px, 12 * px);
            g.setColor(CREEPER_DARK);
            g.fillRect(x + 5 * px, y + 10 * px, 2 * px, 3 * px);
            g.fillRect(x + 9 * px, y + 13 * px, 2 * px, 3 * px);
            g.setColor(CREEPER_LIGHT);
            g.fillRect(x + 7 * px, y + 9 * px, 2 * px, 2 * px);
            g.fillRect(x + 4 * px, y + 15 * px, 3 * px, 2 * px);
    
            // Head (8x8)
            g.setColor(CREEPER_GREEN);
            g.fillRect(x + 4 * px, y, 8 * px, 8 * px);
            g.setColor(CREEPER_DARK);
            g.fillRect(x + 4 * px, y, 2 * px, 2 * px);
            g.fillRect(x + 10 * px, y + 6 * px, 2 * px, 2 * px);
    
            // Creeper Face
            g.setColor(Color.BLACK);
            g.fillRect(x + 5 * px, y + 2 * px, 2 * px, 2 * px); // L Eye
            g.fillRect(x + 9 * px, y + 2 * px, 2 * px, 2 * px); // R Eye
            g.fillRect(x + 7 * px, y + 4 * px, 2 * px, 3 * px); // Nose
            g.fillRect(x + 6 * px, y + 5 * px, px, 3 * px);     // L Mouth
            g.fillRect(x + 9 * px, y + 5 * px, px, 3 * px);     // R Mouth
        }
    
        /**
         * Authentic Minecraft Ghast.
         */
        public static void drawGhast(Graphics2D g, int x, int y, int scale) {
            int px = 2 * scale;
    
            // Cubic Head / Body (16x16)
            g.setColor(new Color(245, 245, 245));
            g.fillRect(x, y, 16 * px, 16 * px);
    
            // Shading on right & bottom
            g.setColor(new Color(210, 210, 210));
            g.fillRect(x + 14 * px, y, 2 * px, 16 * px);
            g.fillRect(x, y + 14 * px, 16 * px, 2 * px);
    
            // Face
            g.setColor(Color.BLACK);
            g.fillRect(x + 3 * px, y + 6 * px, 3 * px, px);
            g.fillRect(x + 10 * px, y + 6 * px, 3 * px, px);
            g.fillRect(x + 6 * px, y + 9 * px, 4 * px, 3 * px);
    
            // Tears
            g.setColor(new Color(160, 160, 160));
            g.fillRect(x + 4 * px, y + 7 * px, px, 4 * px);
            g.fillRect(x + 11 * px, y + 7 * px, px, 4 * px);
    
            // 9 Cubic Tentacles
            for (int i = 0; i < 5; i++) {
                g.setColor(new Color(230, 230, 230));
                g.fillRect(x + (1 + i * 3) * px, y + 16 * px, 2 * px, (4 + (i % 2) * 2) * px);
            }
        }
    
        /**
         * Authentic Minecraft Blaze with spinning cubic rods.
         */
        public static void drawBlaze(Graphics2D g, int x, int y, int scale, double progress) {
            int px = 2 * scale;
    
            // Head (8x8)
            g.setColor(GOLD_YELLOW);
            g.fillRect(x + 4 * px, y, 8 * px, 8 * px);
            g.setColor(GOLD_DARK);
            g.fillRect(x + 4 * px, y + 6 * px, 8 * px, 2 * px);
    
            // Glowing yellow/orange eyes
            g.setColor(Color.BLACK);
            g.fillRect(x + 5 * px, y + 3 * px, 2 * px, 2 * px);
            g.fillRect(x + 9 * px, y + 3 * px, 2 * px, 2 * px);
            g.setColor(Color.YELLOW);
            g.fillRect(x + 5 * px, y + 3 * px, px, px);
            g.fillRect(x + 9 * px, y + 3 * px, px, px);
    
            // 3 Tiers of orbiting Blaze Rods (Cubic 2x8)
            double angleBase = progress * Math.PI * 4;
            for (int i = 0; i < 6; i++) {
                double angle = angleBase + (i * Math.PI / 3);
                int rodX = x + 8 * px + (int) (Math.cos(angle) * 11 * px);
                int rodY = y + 10 * px + (int) (Math.sin(angle) * 3 * px) + (i % 2 == 0 ? -2 * px : 4 * px);
    
                g.setColor(GOLD_YELLOW);
                g.fillRect(rodX - px, rodY - 4 * px, 2 * px, 8 * px);
                g.setColor(LAVA_ORANGE);
                g.fillRect(rodX - px, rodY, 2 * px, 2 * px);
                g.setColor(GOLD_DARK);
                g.drawRect(rodX - px, rodY - 4 * px, 2 * px, 8 * px);
            }
        }
    
        /**
         * Authentic Minecraft Pig (Pink, 4-legged).
         */
        public static void drawPig(Graphics2D g, int x, int y, int scale, boolean facingRight) {
            int px = 2 * scale;
            Color pigPink = new Color(230, 150, 150);
            Color pigDark = new Color(195, 115, 115);
            Color snoutPink = new Color(235, 175, 175);
    
            // Body
            g.setColor(pigPink);
            g.fillRect(x + 2 * px, y + 4 * px, 12 * px, 8 * px);
            g.setColor(pigDark);
            g.fillRect(x + 2 * px, y + 10 * px, 12 * px, 2 * px);
    
            // 4 Legs
            g.setColor(pigPink);
            g.fillRect(x + 3 * px, y + 12 * px, 2 * px, 4 * px);
            g.fillRect(x + 7 * px, y + 12 * px, 2 * px, 4 * px);
            g.fillRect(x + 11 * px, y + 12 * px, 2 * px, 4 * px);
            g.fillRect(x + 5 * px, y + 12 * px, 2 * px, 4 * px);
    
            // Head
            int hx = facingRight ? x + 12 * px : x - 4 * px;
            g.setColor(pigPink);
            g.fillRect(hx, y + 2 * px, 8 * px, 8 * px);
            // Snout
            g.setColor(snoutPink);
            int sx = facingRight ? hx + 5 * px : hx + px;
            g.fillRect(sx, y + 6 * px, 3 * px, 3 * px);
            g.setColor(pigDark);
            g.fillRect(sx + px, y + 7 * px, px, px);
            // Eyes
            g.setColor(Color.BLACK);
            int ex = facingRight ? hx + 5 * px : hx + 2 * px;
            g.fillRect(ex, y + 4 * px, px, px);
        }
    
        /**
         * Authentic Tamed Minecraft Wolf/Dog with red collar.
         */
        public static void drawTamedWolf(Graphics2D g, int x, int y, int scale, double tailWag) {
            int px = 2 * scale;
            Color fur = new Color(220, 220, 220);
            Color furDark = new Color(175, 175, 175);
    
            // Body
            g.setColor(fur);
            g.fillRect(x + 3 * px, y + 6 * px, 10 * px, 6 * px);
            g.setColor(furDark);
            g.fillRect(x + 3 * px, y + 10 * px, 10 * px, 2 * px);
    
            // Legs
            g.setColor(fur);
            g.fillRect(x + 4 * px, y + 12 * px, 2 * px, 3 * px);
            g.fillRect(x + 10 * px, y + 12 * px, 2 * px, 3 * px);
    
            // Head
            g.setColor(fur);
            g.fillRect(x + 12 * px, y + 3 * px, 6 * px, 6 * px);
            // Red Collar
            g.setColor(Color.RED);
            g.fillRect(x + 12 * px, y + 8 * px, 3 * px, 2 * px);
            // Ears
            g.setColor(furDark);
            g.fillRect(x + 12 * px, y + 2 * px, 2 * px, 2 * px);
            g.fillRect(x + 16 * px, y + 2 * px, 2 * px, 2 * px);
            // Eyes
            g.setColor(Color.BLACK);
            g.fillRect(x + 15 * px, y + 5 * px, px, px);
            // Nose
            g.setColor(Color.BLACK);
            g.fillRect(x + 17 * px, y + 7 * px, px, px);
    
            // Wagging Tail
            AffineTransform old = g.getTransform();
            g.translate(x + 3 * px, y + 6 * px);
            g.rotate(Math.sin(tailWag) * 0.6 - 0.8);
            g.setColor(fur);
            g.fillRect(-px, -5 * px, 2 * px, 5 * px);
            g.setTransform(old);
        }
    
        /**
         * Minecraft Bat with flapping wings.
         */
        public static void drawBat(Graphics2D g, int x, int y, double wingPhase) {
            // Body
            g.setColor(new Color(50, 35, 25));
            g.fillRect(x, y, 4, 6);
            // Head
            g.setColor(new Color(60, 45, 30));
            g.fillRect(x - 1, y - 4, 6, 5);
            // Ears
            g.fillRect(x - 1, y - 6, 2, 3);
            g.fillRect(x + 3, y - 6, 2, 3);
            // Eyes
            g.setColor(new Color(80, 50, 255));
            g.fillRect(x, y - 3, 2, 2);
            g.fillRect(x + 3, y - 3, 2, 2);
            // Wings
            double flapY = Math.sin(wingPhase) * 4;
            g.setColor(new Color(45, 30, 20));
            g.fillRect(x - 6, y - 2 + (int) flapY, 6, 3);
            g.fillRect(x + 4, y - 2 - (int) flapY, 6, 3);
            g.fillRect(x - 8, y - 1 + (int) (flapY * 0.7), 3, 2);
            g.fillRect(x + 9, y - 1 - (int) (flapY * 0.7), 3, 2);
        }
    
        /**
         * Authentic Minecraft Enderman (Tall, slim, purple eyes, optionally holding block).
         */
        public static void drawEnderman(Graphics2D g, int x, int y, int scale, boolean hasBlock) {
            int px = 2 * scale;
    
            // Long thin legs
            g.setColor(new Color(15, 15, 15));
            g.fillRect(x + 5 * px, y + 20 * px, 2 * px, 14 * px);
            g.fillRect(x + 9 * px, y + 20 * px, 2 * px, 14 * px);
    
            // Slim body
            g.setColor(new Color(20, 20, 20));
            g.fillRect(x + 4 * px, y + 10 * px, 8 * px, 10 * px);
    
            // Long thin arms
            g.setColor(new Color(15, 15, 15));
            g.fillRect(x + 2 * px, y + 10 * px, 2 * px, 14 * px);
            g.fillRect(x + 12 * px, y + 10 * px, 2 * px, 14 * px);
    
            // Head
            g.setColor(new Color(20, 20, 20));
            g.fillRect(x + 4 * px, y, 8 * px, 8 * px);
            // Glowing purple eyes
            g.setColor(ENDER_PURPLE);
            g.fillRect(x + 5 * px, y + 4 * px, 2 * px, px);
            g.fillRect(x + 9 * px, y + 4 * px, 2 * px, px);
    
            // Holding a grass block
            if (hasBlock) {
                drawGrassBlock(g, x + 12 * px, y + 20 * px, 5 * px);
            }
        }
    
        /**
         * Minecraft-style white mouse cursor (pointer arrow).
         */
        public static void drawMinecraftCursor(Graphics2D g, int x, int y) {
            // White filled arrow
            g.setColor(Color.WHITE);
            int[] xPts = {x, x, x + 4, x + 6, x + 8, x + 5, x + 10};
            int[] yPts = {y, y + 14, y + 11, y + 16, y + 14, y + 10, y + 10};
            g.fillPolygon(xPts, yPts, 7);
            // Black outline
            g.setColor(Color.BLACK);
            g.drawPolygon(xPts, yPts, 7);
        }
    
        /**
         * Authentic Minecraft Hay Bale Block (Dried yellow straw with red cinch bands).
         */
        public static void drawHayBaleBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(new Color(215, 175, 55)); // Dried golden straw base
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Straw texture fibers
            g.setColor(new Color(185, 145, 40));
            for (int i = 0; i < 16; i += 3) {
                g.fillRect((int) (x + i * u), y, Math.max(1, (int) (u)), size);
            }
            g.setColor(new Color(240, 205, 90));
            for (int i = 1; i < 16; i += 4) {
                g.fillRect((int) (x + i * u), (int) (y + 2 * u), Math.max(1, (int) (u)), (int) (12 * u));
            }
    
            // Dual red cinch tie bands (at row 4 and row 11)
            g.setColor(new Color(165, 30, 25));
            g.fillRect(x, (int) (y + 3 * u), size, Math.max(1, (int) (2 * u)));
            g.fillRect(x, (int) (y + 11 * u), size, Math.max(1, (int) (2 * u)));
    
            g.setColor(new Color(0, 0, 0, 45));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Villager (Brown robe, folded arms, hanging nose, profession hats).
         */
        public static void drawVillager(Graphics2D g, int x, int y, int scale, boolean facingRight) {
            drawVillager(g, x, y, scale, facingRight, "plains");
        }
    
        public static void drawVillager(Graphics2D g, int x, int y, int scale, boolean facingRight, String profession) {
            int px = 2 * scale;
            Color skinColor = new Color(198, 142, 110);
            Color robeBrown = new Color(140, 85, 45);
            Color robeDark = new Color(105, 60, 30);
            Color robeHighlight = new Color(165, 105, 60);
    
            // 1. Brown Robe (Body & Skirt)
            g.setColor(robeBrown);
            g.fillRect(x + 4 * px, y + 10 * px, 8 * px, 18 * px);
            g.setColor(robeDark);
            g.fillRect(x + 4 * px, y + 26 * px, 8 * px, 2 * px);
            g.setColor(robeHighlight);
            g.fillRect(x + 4 * px, y + 10 * px, 8 * px, px);
    
            // Emerald Green Collar / Trim (Plains Villager style)
            g.setColor(new Color(35, 125, 55));
            g.fillRect(x + 6 * px, y + 10 * px, 4 * px, 2 * px);
    
            // Boots (Dark gray/brown)
            g.setColor(new Color(45, 35, 30));
            g.fillRect(x + 4 * px, y + 28 * px, 3 * px, 4 * px);
            g.fillRect(x + 9 * px, y + 28 * px, 3 * px, 4 * px);
    
            // 2. Folded Arms in Robe (Iconic seamless villager sleeve crossed over chest)
            g.setColor(robeDark);
            g.fillRect(x + 3 * px, y + 13 * px, 10 * px, 7 * px);
            g.setColor(robeBrown);
            g.fillRect(x + 4 * px, y + 14 * px, 8 * px, 5 * px);
            g.setColor(new Color(85, 48, 25));
            g.fillRect(x + 4 * px, y + 18 * px, 8 * px, px);
            // Hands tucked inside sleeve
            g.setColor(skinColor);
            g.fillRect(x + 7 * px, y + 15 * px, 2 * px, 2 * px);
    
            // 3. Head (Long rectangular head, 8x10)
            g.setColor(skinColor);
            g.fillRect(x + 4 * px, y, 8 * px, 10 * px);
            g.setColor(new Color(175, 120, 90));
            g.fillRect(x + 4 * px, y + 9 * px, 8 * px, px); // Jaw shadow
    
            // Connected Unibrow
            g.setColor(new Color(55, 38, 22));
            g.fillRect(x + 5 * px, y + 3 * px, 6 * px, px);
    
            // Eyes (White corners with emerald-green iris)
            g.setColor(Color.WHITE);
            g.fillRect(x + 5 * px, y + 4 * px, 2 * px, 2 * px);
            g.fillRect(x + 9 * px, y + 4 * px, 2 * px, 2 * px);
            g.setColor(new Color(0, 135, 60)); // Emerald green pupil
            int eyeOff = facingRight ? 1 : 0;
            g.fillRect(x + (5 + eyeOff) * px, y + 4 * px, px, 2 * px);
            g.fillRect(x + (9 + eyeOff) * px, y + 4 * px, px, 2 * px);
    
            // Iconic Long Hanging Nose (Protrudes outward and downward)
            int noseX = facingRight ? x + 9 * px : x + 3 * px;
            g.setColor(new Color(185, 125, 95));
            g.fillRect(noseX, y + 5 * px, 3 * px, 6 * px);
            g.setColor(new Color(210, 155, 120));
            g.fillRect(noseX, y + 5 * px, 3 * px, px); // Nose bridge highlight
            g.setColor(new Color(145, 95, 70));
            g.fillRect(noseX, y + 10 * px, 3 * px, px); // Under-nose shadow
    
            // 4. Profession Hats
            if ("farmer".equalsIgnoreCase(profession)) {
                // Authentic Straw Hat
                Color strawYellow = new Color(225, 195, 105);
                Color strawDark = new Color(185, 155, 75);
                // Hat Crown
                g.setColor(strawYellow);
                g.fillRect(x + 3 * px, y - 4 * px, 10 * px, 4 * px);
                // Red Hatband
                g.setColor(new Color(175, 30, 25));
                g.fillRect(x + 3 * px, y - px, 10 * px, px);
                // Wide Straw Hat Brim
                g.setColor(strawYellow);
                g.fillRect(x + px, y, 14 * px, 2 * px);
                g.setColor(strawDark);
                g.fillRect(x + px, y + px, 14 * px, px);
            }
        }
    
        /**
         * Authentic Minecraft Iron Golem (Heavy iron plates, vines, red eyes, holding poppy).
         */
        public static void drawIronGolem(Graphics2D g, int x, int y, int scale) {
            drawIronGolem(g, x, y, scale, true);
        }
    
        public static void drawIronGolem(Graphics2D g, int x, int y, int scale, boolean holdingPoppy) {
            int px = 2 * scale;
            Color ironBody = new Color(225, 220, 215);
            Color ironShade = new Color(175, 170, 165);
            Color ironDark = new Color(135, 130, 125);
            Color vineGreen = new Color(55, 120, 45);
    
            // 1. Heavy Legs
            g.setColor(ironBody);
            g.fillRect(x + 4 * px, y + 26 * px, 4 * px, 10 * px);
            g.fillRect(x + 12 * px, y + 26 * px, 4 * px, 10 * px);
            g.setColor(ironShade);
            g.fillRect(x + 4 * px, y + 33 * px, 4 * px, 3 * px);
            g.fillRect(x + 12 * px, y + 33 * px, 4 * px, 3 * px);
            g.setColor(ironDark);
            g.drawRect(x + 4 * px, y + 26 * px, 4 * px, 10 * px);
            g.drawRect(x + 12 * px, y + 26 * px, 4 * px, 10 * px);
    
            // 2. Heavy Broad Torso (Wide 18x12 upper chest + 12x6 lower waist)
            g.setColor(ironBody);
            g.fillRect(x + 2 * px, y + 8 * px, 16 * px, 18 * px);
            g.setColor(ironShade);
            g.fillRect(x + 2 * px, y + 22 * px, 16 * px, 4 * px);
            g.setColor(ironDark);
            g.drawRect(x + 2 * px, y + 8 * px, 16 * px, 18 * px);
    
            // Vines crawling across torso
            g.setColor(vineGreen);
            g.fillRect(x + 4 * px, y + 10 * px, 2 * px, 8 * px);
            g.fillRect(x + 5 * px, y + 13 * px, 4 * px, 2 * px);
            g.fillRect(x + 12 * px, y + 15 * px, 3 * px, 7 * px);
            g.fillRect(x + 11 * px, y + 18 * px, 2 * px, 2 * px);
    
            // 3. Massive Hanging Iron Arms
            g.setColor(ironBody);
            g.fillRect(x - 2 * px, y + 10 * px, 4 * px, 22 * px);
            g.fillRect(x + 18 * px, y + 10 * px, 4 * px, 22 * px);
            g.setColor(ironShade);
            g.fillRect(x - 2 * px, y + 28 * px, 4 * px, 4 * px);
            g.fillRect(x + 18 * px, y + 28 * px, 4 * px, 4 * px);
            g.setColor(ironDark);
            g.drawRect(x - 2 * px, y + 10 * px, 4 * px, 22 * px);
            g.drawRect(x + 18 * px, y + 10 * px, 4 * px, 22 * px);
    
            // 4. Head with Brow, Hanging Nose & Glowing Red Eyes
            g.setColor(ironBody);
            g.fillRect(x + 6 * px, y, 8 * px, 8 * px);
            g.setColor(ironDark);
            g.drawRect(x + 6 * px, y, 8 * px, 8 * px);
            // Heavy Brow
            g.setColor(ironShade);
            g.fillRect(x + 6 * px, y + 2 * px, 8 * px, px);
            // Sunken Red Eyes
            g.setColor(new Color(220, 20, 20));
            g.fillRect(x + 7 * px, y + 3 * px, 2 * px, px);
            g.fillRect(x + 11 * px, y + 3 * px, 2 * px, px);
            // Protruding Iron Nose
            g.setColor(new Color(195, 150, 130));
            g.fillRect(x + 9 * px, y + 4 * px, 2 * px, 4 * px);
            g.setColor(new Color(155, 110, 95));
            g.fillRect(x + 9 * px, y + 7 * px, 2 * px, px);
    
            // 5. Holding a Red Poppy Flower (Minecraft Golem offering a poppy)
            if (holdingPoppy) {
                int fx = x + 21 * px;
                int fy = y + 22 * px;
                // Green stem
                g.setColor(new Color(45, 130, 40));
                g.fillRect(fx, fy + 3 * px, px, 4 * px);
                g.fillRect(fx + px, fy + 4 * px, px, 2 * px); // Leaf
                // Red Poppy petals
                g.setColor(new Color(225, 30, 30));
                g.fillRect(fx - px, fy, 3 * px, 3 * px);
                g.fillRect(fx, fy - px, px, px);
                // Black Poppy center
                g.setColor(Color.BLACK);
                g.fillRect(fx, fy + px, px, px);
            }
        }
    
        /**
         * Authentic Blocky Minecraft Ender Dragon.
         */
        public static void drawDragon(Graphics2D g, int x, int y, int scale, double progress) {
            int px = 2 * scale;
            AffineTransform old = g.getTransform();
            g.translate(x, y);
    
            double flap = Math.sin(progress * 18) * 0.45;
    
            // 1. Articulated Tail (3 cubic segments)
            for (int i = 0; i < 3; i++) {
                int tx = -14 * px - i * 6 * px;
                int ty = -2 * px;
                g.setColor(DRAGON_BLACK);
                g.fillRect(tx, ty, 5 * px, 4 * px);
                g.setColor(DRAGON_GRAY);
                g.fillRect(tx + 2 * px, ty - 2 * px, 2 * px, 2 * px); // Spine
            }
    
            // 2. Cubic Main Body (18x10)
            g.setColor(DRAGON_BLACK);
            g.fillRect(-10 * px, -5 * px, 18 * px, 10 * px);
            g.setColor(DRAGON_GRAY);
            for (int i = -8; i <= 6; i += 4) {
                g.fillRect(i * px, -7 * px, 2 * px, 2 * px); // Dorsal spines
            }
    
            // 3. Blocky Segmented Wings
            g.rotate(flap);
            // Wing bone (thick black block)
            g.setColor(DRAGON_BLACK);
            g.fillRect(-6 * px, -18 * px, 14 * px, 4 * px);
            g.fillRect(4 * px, -16 * px, 8 * px, 3 * px);
            // Wing membrane (purple pixel blocks)
            g.setColor(DRAGON_PURPLE);
            g.fillRect(-4 * px, -14 * px, 10 * px, 9 * px);
            g.fillRect(6 * px, -13 * px, 6 * px, 7 * px);
            g.setTransform(old);
    
            g.translate(x, y);
    
            // 4. Neck (2 blocks)
            g.setColor(DRAGON_BLACK);
            g.fillRect(8 * px, -4 * px, 5 * px, 7 * px);
            g.fillRect(13 * px, -5 * px, 4 * px, 6 * px);
    
            // 5. Head (8x7) & Snout
            g.setColor(DRAGON_BLACK);
            g.fillRect(17 * px, -6 * px, 7 * px, 7 * px);
            g.fillRect(24 * px, -4 * px, 4 * px, 5 * px); // Snout
    
            // Glowing Purple Dragon Eye
            g.setColor(new Color(220, 80, 255));
            g.fillRect(19 * px, -4 * px, 2 * px, 2 * px);
            g.setColor(Color.WHITE);
            g.fillRect(19 * px, -4 * px, px, px);
    
            // Horns
            g.setColor(DRAGON_GRAY);
            g.fillRect(16 * px, -9 * px, 2 * px, 3 * px);
    
            g.setTransform(old);
        }
    
        /**
         * Authentic Blocky Minecraft Oak Tree.
         */
        public static void drawTree(Graphics2D g, int x, int y, int scale) {
            int bs = 8 * scale;
    
            // Wood Trunk (3 blocks high)
            for (int r = 0; r < 4; r++) {
                drawOakLog(g, x + 2 * bs, y + (2 + r) * bs, bs);
            }
    
            // Leaf canopy (authentic 5x5 and 3x3 cubic layers)
            for (int row = 0; row < 2; row++) {
                for (int col = 0; col < 5; col++) {
                    drawLeavesBlock(g, x + col * bs, y + row * bs + bs, bs);
                }
            }
            for (int col = 1; col < 4; col++) {
                drawLeavesBlock(g, x + col * bs, y, bs);
            }
        }
    
        /**
         * Authentic Minecraft Blocky Pixel Clouds.
         */
        public static void drawCloud(Graphics2D g, int x, int y, int cloudWidth) {
            g.setColor(new Color(255, 255, 255, 220));
            // Stepped rectangular pixel cloud shape
            int h = 14;
            g.fillRect(x, y + 4, cloudWidth, h - 4);
            g.fillRect(x + 12, y, cloudWidth - 24, h);
            g.fillRect(x + 28, y - 4, cloudWidth - 56, h + 4);
    
            // Cloud shadow underneath (Minecraft gray rim)
            g.setColor(new Color(215, 220, 235, 180));
            g.fillRect(x, y + h, cloudWidth, 3);
        }
    
        /**
         * Authentic Minecraft Square Sun.
         */
        public static void drawMinecraftSun(Graphics2D g, int x, int y, int size) {
            // Radiant halo (using Midpoint Circle Glow per custom algorithm rule)
            MidpointDrawing.fillCircleGlow(g, x + size / 2, y + size / 2, size + 15,
                new Color(255, 240, 150, 60), new Color(255, 200, 50, 0));
    
            // Solid Square Sun
            g.setColor(Color.WHITE);
            g.fillRect(x, y, size, size);
            g.setColor(new Color(255, 245, 180));
            g.fillRect(x + 2, y + 2, size - 4, size - 4);
        }
    
        /**
         * Authentic Minecraft Square Moon.
         */
        public static void drawMinecraftMoon(Graphics2D g, int x, int y, int size) {
            // Radiant moonlight halo
            MidpointDrawing.fillCircleGlow(g, x + size / 2, y + size / 2, size + 12,
                new Color(220, 230, 255, 45), new Color(150, 180, 255, 0));
    
            // Solid Square Moon
            g.setColor(new Color(240, 240, 245));
            g.fillRect(x, y, size, size);
            // Moon craters (blocky gray patches)
            g.setColor(new Color(190, 195, 205));
            g.fillRect(x + 3, y + 3, 5, 5);
            g.fillRect(x + size - 8, y + 4, 4, 6);
            g.fillRect(x + 5, y + size - 9, 6, 5);
        }
    
        // ==========================================
        // UI & UTILITIES
        // ==========================================
    
        public static void drawHUD(Graphics2D g, int width, int hearts, int maxHearts, int hunger, int xpPercent) {
            for (int i = 0; i < maxHearts; i++) {
                int hx = 10 + i * 15;
                int hy = 10;
                g.setColor(Color.BLACK);
                g.fillRect(hx + 1, hy, 3, 1); g.fillRect(hx + 5, hy, 3, 1);
                g.fillRect(hx, hy + 1, 1, 3); g.fillRect(hx + 4, hy + 1, 1, 2); g.fillRect(hx + 8, hy + 1, 1, 3);
                g.fillRect(hx + 1, hy + 4, 1, 1); g.fillRect(hx + 7, hy + 4, 1, 1);
                g.fillRect(hx + 2, hy + 5, 1, 1); g.fillRect(hx + 6, hy + 5, 1, 1);
                g.fillRect(hx + 3, hy + 6, 1, 1); g.fillRect(hx + 5, hy + 6, 1, 1);
                g.fillRect(hx + 4, hy + 7, 1, 1);
    
                if (i < hearts) {
                    g.setColor(HEART_RED);
                    g.fillRect(hx + 1, hy + 1, 3, 3); g.fillRect(hx + 5, hy + 1, 3, 3);
                    g.fillRect(hx + 2, hy + 4, 5, 1); g.fillRect(hx + 3, hy + 5, 3, 1);
                    g.fillRect(hx + 4, hy + 6, 1, 1);
                    g.setColor(Color.WHITE);
                    g.fillRect(hx + 1, hy + 1, 1, 1);
                } else {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(hx + 1, hy + 1, 3, 3); g.fillRect(hx + 5, hy + 1, 3, 3);
                    g.fillRect(hx + 2, hy + 4, 5, 1); g.fillRect(hx + 3, hy + 5, 3, 1);
                    g.fillRect(hx + 4, hy + 6, 1, 1);
                }
            }
    
            for (int i = 0; i < 10; i++) {
                int hx = width - 150 + i * 12;
                int hy = 10;
                g.setColor(Color.BLACK);
                g.fillRect(hx + 3, hy, 4, 1);
                g.fillRect(hx + 2, hy + 1, 1, 3); g.fillRect(hx + 7, hy + 1, 1, 2);
                g.fillRect(hx + 1, hy + 4, 1, 2); g.fillRect(hx + 8, hy + 3, 1, 3);
                g.fillRect(hx + 2, hy + 6, 2, 1); g.fillRect(hx + 7, hy + 6, 1, 1);
                g.fillRect(hx + 4, hy + 7, 3, 1);
    
                if (i < hunger) {
                    g.setColor(new Color(160, 80, 20));
                    g.fillRect(hx + 3, hy + 1, 4, 2);
                    g.fillRect(hx + 3, hy + 3, 5, 1);
                    g.fillRect(hx + 2, hy + 4, 6, 1);
                    g.fillRect(hx + 2, hy + 5, 5, 1);
                    g.fillRect(hx + 4, hy + 6, 3, 1);
                    g.setColor(new Color(100, 40, 10));
                    g.fillRect(hx + 3, hy + 5, 4, 1);
                } else {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(hx + 3, hy + 1, 4, 2);
                    g.fillRect(hx + 3, hy + 3, 5, 1);
                    g.fillRect(hx + 2, hy + 4, 6, 1);
                    g.fillRect(hx + 2, hy + 5, 5, 1);
                    g.fillRect(hx + 4, hy + 6, 3, 1);
                }
            }
        }
    
        /**
         * Authentic Minecraft GUI Button (Normal, Hover, Pressed).
         */
        public static void drawMinecraftButton(Graphics2D g, int x, int y, int width, int height, String text, int state) {
            // state: 0 = Normal, 1 = Hover, 2 = Pressed
            Color baseColor = (state == 2) ? new Color(90, 90, 90) : (state == 1 ? new Color(175, 175, 175) : new Color(130, 130, 130));
            Color highlight = (state == 2) ? new Color(60, 60, 60) : (state == 1 ? new Color(220, 220, 220) : new Color(190, 190, 190));
            Color shadow = (state == 2) ? new Color(120, 120, 120) : new Color(55, 55, 55);
    
            // Black border
            g.setColor(Color.BLACK);
            g.fillRect(x - 2, y - 2, width + 4, height + 4);
    
            // Base fill
            g.setColor(baseColor);
            g.fillRect(x, y, width, height);
    
            // Top & Left 3D highlight bevel (2px)
            g.setColor(highlight);
            g.fillRect(x, y, width - 2, 2);
            g.fillRect(x, y, 2, height - 2);
    
            // Bottom & Right 3D shadow bevel (2px)
            g.setColor(shadow);
            g.fillRect(x + 2, y + height - 2, width - 2, 2);
            g.fillRect(x + width - 2, y + 2, 2, height - 2);
    
            // Centered Text with shadow
            int textY = y + height / 2 + 5;
            if (state == 2) textY += 1;
            Color textColor = (state == 1) ? new Color(255, 255, 160) : Color.WHITE;
            int textX = x + width / 2 - (text.length() * 4);
            drawMinecraftText(g, text, textX, textY, 16, textColor);
        }
    
        public static void drawAchievement(Graphics2D g, int width, String title, double popupProgress) {
            if (popupProgress <= 0 || popupProgress >= 1) return;
    
            int yOffset;
            if (popupProgress < 0.3) {
                double t = DrawUtils.easeInOut(popupProgress / 0.3);
                yOffset = (int) (-50 + 70 * t);
            } else if (popupProgress > 0.7) {
                double t = DrawUtils.easeInOut((popupProgress - 0.7) / 0.3);
                yOffset = (int) (20 - 70 * t);
            } else {
                yOffset = 20;
            }
    
            int w = 260;
            int h = 42;
            int x = width / 2 - w / 2;
    
            g.setColor(new Color(35, 35, 35));
            g.fillRect(x, yOffset, w, h);
            g.setColor(GOLD_YELLOW);
            g.drawRect(x, yOffset, w, h);
            g.drawRect(x + 1, yOffset + 1, w - 2, h - 2);
    
            drawMinecraftText(g, "Achievement Get!", x + 10, yOffset + 16, 12, GOLD_YELLOW);
            drawMinecraftText(g, title, x + 10, yOffset + 32, 14, Color.WHITE);
        }
    
        public static Color lerpColor(Color a, Color b, double t) {
            if (t < 0) t = 0;
            if (t > 1) t = 1;
            int red = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
            int green = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
            int blue = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
            int alpha = (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
            return new Color(red, green, blue, alpha);
        }
    
        public static void drawSky(Graphics2D g, int width, int height, double timeOfDay) {
            Color topColor, bottomColor;
            if (timeOfDay < 0.2) {
                topColor = SKY_BLUE;
                bottomColor = new Color(185, 230, 255);
            } else if (timeOfDay < 0.4) {
                double t = (timeOfDay - 0.2) / 0.2;
                topColor = lerpColor(SKY_BLUE, SUNSET_PINK, t);
                bottomColor = lerpColor(new Color(185, 230, 255), SUNSET_ORANGE, t);
            } else if (timeOfDay < 0.6) {
                double t = (timeOfDay - 0.4) / 0.2;
                topColor = lerpColor(SUNSET_PINK, NIGHT_SKY, t);
                bottomColor = lerpColor(SUNSET_ORANGE, Color.BLACK, t);
            } else if (timeOfDay < 0.8) {
                double t = (timeOfDay - 0.6) / 0.2;
                topColor = lerpColor(NIGHT_SKY, SUNSET_PINK, t);
                bottomColor = lerpColor(Color.BLACK, SUNSET_ORANGE, t);
            } else {
                double t = (timeOfDay - 0.8) / 0.2;
                topColor = lerpColor(SUNSET_PINK, SKY_BLUE, t);
                bottomColor = lerpColor(SUNSET_ORANGE, new Color(185, 230, 255), t);
            }
    
            for (int i = 0; i < height; i += 2) {
                double t = (double) i / height;
                g.setColor(lerpColor(topColor, bottomColor, t));
                g.fillRect(0, i, width, 2);
            }
        }
    
        public static void drawGround(Graphics2D g, int width, int height, int groundY) {
            drawGround(g, width, height, groundY, BLOCK_SIZE);
        }
    
        public static void drawGround(Graphics2D g, int width, int height, int groundY, int blockSize) {
            for (int x = 0; x < width; x += blockSize) {
                drawGrassBlock(g, x, groundY, blockSize);
                for (int y = groundY + blockSize; y < height; y += blockSize) {
                    drawDirtBlock(g, x, y, blockSize);
                }
            }
        }
    
        /**
         * Authentic Minecraft Snow Block (Pure white with subtle icy-blue/gray pixel noise).
         */
        public static void drawSnowBlock(Graphics2D g, int x, int y, int size) {
            g.setColor(new Color(245, 250, 255)); // Pure white snow base
            g.fillRect(x, y, size, size);
    
            double u = size / 16.0;
    
            // Subtle cold snow texture speckles
            g.setColor(new Color(225, 235, 250));
            g.fillRect((int) (x + 3 * u), (int) (y + 4 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 10 * u), (int) (y + 3 * u), Math.max(1, (int) (4 * u)), Math.max(1, (int) (2 * u)));
            g.fillRect((int) (x + 5 * u), (int) (y + 10 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (3 * u)));
            g.fillRect((int) (x + 12 * u), (int) (y + 12 * u), Math.max(1, (int) (3 * u)), Math.max(1, (int) (2 * u)));
    
            // Pure white top glint
            g.setColor(Color.WHITE);
            g.fillRect(x, y, size, Math.max(1, (int) (2 * u)));
    
            // Soft blue-gray shadow border
            g.setColor(new Color(195, 210, 230));
            g.drawRect(x, y, size, size);
        }
    
        /**
         * Authentic Minecraft Snow-Covered Grass Block (Snow top with icicle fringes hanging over dirt).
         */
        public static void drawSnowyGrassBlock(Graphics2D g, int x, int y, int size) {
            // Dirt base
            drawDirtBlock(g, x, y, size);
    
            double u = size / 16.0;
            int topH = Math.max(1, (int) (4 * u));
    
            // Pure white snow top cap
            g.setColor(new Color(250, 252, 255));
            g.fillRect(x, y, size, topH);
    
            // Snow icicle fringes hanging into dirt side
            int[] snowFringes = {3, 5, 2, 6, 3, 5, 4, 2};
            double subW = size / 8.0;
            for (int i = 0; i < 8; i++) {
                int fx = (int) (x + i * subW);
                int fw = Math.max(1, (int) ((i + 1) * subW) - (int) (i * subW));
                int fh = Math.max(1, (int) (snowFringes[i] * u));
                g.fillRect(fx, y + topH, fw, Math.min(size - topH, fh));
            }
    
            // Snow top highlight
            g.setColor(Color.WHITE);
            g.drawLine(x, y, x + size - 1, y);
        }
    
        /**
         * Authentic Minecraft Overworld Distant Mountains Backdrop (Vast Horizon with Atmospheric Depth Fog).
         */
        public static void drawDistantMountains(Graphics2D g, int width, int groundY, double timeOfDay, double scrollOffset) {
            // Atmospheric Sky Fog Color based on time of day
            Color skyFog;
            if (timeOfDay < 0.2) {
                skyFog = new Color(175, 215, 245); // Day cyan mist
            } else if (timeOfDay < 0.4) {
                double t = (timeOfDay - 0.2) / 0.2;
                skyFog = lerpColor(new Color(175, 215, 245), new Color(230, 155, 140), t); // Sunset
            } else if (timeOfDay < 0.6) {
                double t = (timeOfDay - 0.4) / 0.2;
                skyFog = lerpColor(new Color(230, 155, 140), new Color(20, 25, 45), t); // Night
            } else if (timeOfDay < 0.8) {
                double t = (timeOfDay - 0.6) / 0.2;
                skyFog = lerpColor(new Color(20, 25, 45), new Color(230, 155, 140), t); // Dawn
            } else {
                double t = (timeOfDay - 0.8) / 0.2;
                skyFog = lerpColor(new Color(230, 155, 140), new Color(175, 215, 245), t);
            }
    
            // =========================================================================
            // LAYER 1: VAST HORIZON PEAKS (Far Distance: 4px Micro-Voxel Block Grid)
            // =========================================================================
            int farBs = 4;
            int farCols = (width / farBs) + 4;
            int farScroll = (int) (scrollOffset * 0.05); // Very slow distant parallax
            int farOffsetCol = farScroll / farBs;
            int farShiftPx = farScroll % farBs;
    
            Color farGrass = lerpColor(new Color(75, 135, 55), skyFog, 0.70);
            Color farStone = lerpColor(new Color(115, 115, 120), skyFog, 0.72);
            Color farStoneDark = lerpColor(new Color(85, 85, 90), skyFog, 0.75);
            Color farDirt = lerpColor(new Color(125, 85, 50), skyFog, 0.72);
    
            for (int c = -2; c < farCols; c++) {
                int globalCol = c + farOffsetCol;
                int bx = c * farBs - farShiftPx;
    
                // Vast horizon mountain range formula (25..55 micro-blocks high = 100..220px)
                double n = globalCol * 0.045;
                int hBlocks = (int) (38 + Math.sin(n * 1.5) * 14.0 + Math.cos(n * 0.7) * 9.0 + Math.sin(n * 2.8) * 5.0);
                hBlocks = Math.max(18, Math.min(58, hBlocks));
    
                int peakY = groundY - hBlocks * farBs;
    
                // Render column from peak down to ground
                for (int r = 0; r < hBlocks; r++) {
                    int by = peakY + r * farBs;
    
                    if (r == 0) {
                        g.setColor(hBlocks >= 50 ? farStone : farGrass);
                    } else if (r == 1 && hBlocks < 50) {
                        g.setColor(farDirt);
                    } else {
                        int hash = (globalCol * 31 + r * 17) & 0x7FFFFFFF;
                        g.setColor((hash % 3 == 0) ? farStoneDark : farStone);
                    }
                    g.fillRect(bx, by, farBs, farBs);
                }
            }
    
            // Atmospheric Distance Haze between Layer 1 and Layer 2
            g.setColor(new Color(skyFog.getRed(), skyFog.getGreen(), skyFog.getBlue(), 110));
            g.fillRect(0, groundY - 240, width, 240);
    
            // =========================================================================
            // LAYER 2: MID-DISTANCE FOOTHILLS & RIDGES (6px Voxel Block Grid)
            // =========================================================================
            int midBs = 6;
            int midCols = (width / midBs) + 4;
            int midScroll = (int) (scrollOffset * 0.12); // Mid-distance parallax
            int midOffsetCol = midScroll / midBs;
            int midShiftPx = midScroll % midBs;
    
            Color midGrass = lerpColor(new Color(75, 140, 50), skyFog, 0.48);
            Color midGrassSide = lerpColor(new Color(60, 115, 40), skyFog, 0.50);
            Color midDirt = lerpColor(new Color(130, 90, 55), skyFog, 0.52);
            Color midStone = lerpColor(new Color(110, 110, 115), skyFog, 0.52);
            Color midStoneDark = lerpColor(new Color(80, 80, 85), skyFog, 0.56);
    
            for (int c = -2; c < midCols; c++) {
                int globalCol = c + midOffsetCol;
                int bx = c * midBs - midShiftPx;
    
                // Rolling mid-distance ridge profile (10..24 blocks high = 60..144px)
                double n = globalCol * 0.065;
                int hBlocks = (int) (15 + Math.sin(n * 1.8) * 7.0 + Math.cos(n * 1.1) * 4.5);
                hBlocks = Math.max(8, Math.min(26, hBlocks));
    
                int peakY = groundY - hBlocks * midBs;
    
                for (int r = 0; r < hBlocks; r++) {
                    int by = peakY + r * midBs;
    
                    if (r == 0) {
                        g.setColor(midGrass);
                    } else if (r == 1) {
                        g.setColor(midGrassSide);
                    } else if (r == 2) {
                        g.setColor(midDirt);
                    } else {
                        int hash = (globalCol * 23 + r * 13) & 0x7FFFFFFF;
                        g.setColor((hash % 4 == 0) ? midStoneDark : midStone);
                    }
                    g.fillRect(bx, by, midBs, midBs);
                }
    
                // Distant miniature Minecraft Trees on hill ridges (6px tall)
                if (globalCol % 11 == 0 && hBlocks >= 12) {
                    int treeY = peakY - 8;
                    Color treeFoliage = lerpColor(new Color(40, 95, 35), skyFog, 0.45);
                    Color treeTrunk = lerpColor(new Color(85, 60, 35), skyFog, 0.50);
                    g.setColor(treeTrunk);
                    g.fillRect(bx + 2, treeY + 4, 2, 4);
                    g.setColor(treeFoliage);
                    g.fillRect(bx, treeY, 6, 4);
                    g.fillRect(bx + 1, treeY - 2, 4, 2);
                }
            }
    
            // Soft Horizon Base Fog (Fades mountain foot softly into the horizon ground)
            for (int i = 0; i < 40; i += 2) {
                double t = (double) i / 40.0;
                int alpha = (int) (t * 140);
                g.setColor(new Color(skyFog.getRed(), skyFog.getGreen(), skyFog.getBlue(), alpha));
                g.fillRect(0, groundY - 40 + i, width, 2);
            }
        }
    
        /**
         * Authentic Minecraft Snowy Mountain Peaks & Slopes (Distant Horizon with Ice Mist).
         */
        public static void drawSnowyMountains(Graphics2D g, int width, int groundY, double scrollOffset) {
            Color skyFog = new Color(185, 220, 245); // Cold crisp alpine sky fog
    
            // =========================================================================
            // LAYER 1: EPIC TOWERING SNOW SUMMITS (Far Distance: 4px Micro-Voxel Grid)
            // =========================================================================
            int farBs = 4;
            int farCols = (width / farBs) + 4;
            int farScroll = (int) (scrollOffset * 0.05); // Slow parallax
            int farOffsetCol = farScroll / farBs;
            int farShiftPx = farScroll % farBs;
    
            Color snowPeakWhite = lerpColor(new Color(255, 255, 255), skyFog, 0.25); // Bright gleaming snow
            Color snowPeakShade = lerpColor(new Color(225, 235, 250), skyFog, 0.35);
            Color farRock = lerpColor(new Color(110, 115, 125), skyFog, 0.65);
            Color farRockDark = lerpColor(new Color(80, 85, 95), skyFog, 0.70);
    
            for (int c = -2; c < farCols; c++) {
                int globalCol = c + farOffsetCol;
                int bx = c * farBs - farShiftPx;
    
                // Towering majestic snow mountain peaks (30..65 micro-blocks = 120..260px high)
                double n = globalCol * 0.040;
                int hBlocks = (int) (44 + Math.sin(n * 1.4) * 16.0 + Math.cos(n * 0.7) * 10.0 + Math.sin(n * 2.6) * 6.0);
                hBlocks = Math.max(22, Math.min(68, hBlocks));
    
                int peakY = groundY - hBlocks * farBs;
    
                for (int r = 0; r < hBlocks; r++) {
                    int by = peakY + r * farBs;
    
                    // Snowline threshold: Top 16 micro-blocks are gleaming snow
                    if (r < 14) {
                        g.setColor(r < 8 ? snowPeakWhite : snowPeakShade);
                    } else if (r == 14) {
                        // Transition snow ledge
                        g.setColor(snowPeakShade);
                    } else {
                        int hash = (globalCol * 29 + r * 19) & 0x7FFFFFFF;
                        g.setColor((hash % 3 == 0) ? farRockDark : farRock);
                    }
                    g.fillRect(bx, by, farBs, farBs);
                }
            }
    
            // Cold Mountain Distance Mist
            g.setColor(new Color(skyFog.getRed(), skyFog.getGreen(), skyFog.getBlue(), 90));
            g.fillRect(0, groundY - 260, width, 260);
    
            // =========================================================================
            // LAYER 2: NEAR SNOWY SLOPES & SPRUCE FORESTS (6px Voxel Block Grid)
            // =========================================================================
            int midBs = 6;
            int midCols = (width / midBs) + 4;
            int midScroll = (int) (scrollOffset * 0.12);
            int midOffsetCol = midScroll / midBs;
            int midShiftPx = midScroll % midBs;
    
            Color midSnow = lerpColor(new Color(250, 252, 255), skyFog, 0.30);
            Color midSnowShade = lerpColor(new Color(215, 225, 240), skyFog, 0.40);
            Color midRock = lerpColor(new Color(115, 120, 130), skyFog, 0.50);
            Color midRockDark = lerpColor(new Color(85, 90, 100), skyFog, 0.55);
    
            for (int c = -2; c < midCols; c++) {
                int globalCol = c + midOffsetCol;
                int bx = c * midBs - midShiftPx;
    
                // Near snowy ridge profile (12..25 blocks high = 72..150px)
                double n = globalCol * 0.060;
                int hBlocks = (int) (16 + Math.sin(n * 1.6) * 7.5 + Math.cos(n * 0.9) * 4.0);
                hBlocks = Math.max(9, Math.min(27, hBlocks));
    
                int peakY = groundY - hBlocks * midBs;
    
                for (int r = 0; r < hBlocks; r++) {
                    int by = peakY + r * midBs;
    
                    if (r < 4) {
                        g.setColor(r == 0 ? midSnow : midSnowShade);
                    } else {
                        int hash = (globalCol * 23 + r * 11) & 0x7FFFFFFF;
                        g.setColor((hash % 4 == 0) ? midRockDark : midRock);
                    }
                    g.fillRect(bx, by, midBs, midBs);
                }
    
                // Distant Snow-Dusted Spruce Trees on Foothills (8px tall)
                if (globalCol % 9 == 0 && hBlocks >= 14) {
                    int treeY = peakY - 10;
                    Color spruceFoliage = lerpColor(new Color(25, 65, 40), skyFog, 0.35);
                    Color spruceTrunk = lerpColor(new Color(60, 45, 30), skyFog, 0.45);
                    g.setColor(spruceTrunk);
                    g.fillRect(bx + 2, treeY + 6, 2, 4);
                    g.setColor(spruceFoliage);
                    g.fillRect(bx, treeY + 2, 6, 4);
                    g.fillRect(bx + 1, treeY, 4, 2);
                    // Snow on spruce crown
                    g.setColor(midSnow);
                    g.fillRect(bx, treeY + 2, 6, 1);
                    g.fillRect(bx + 1, treeY, 4, 1);
                }
            }
    
            // Soft Alpine Base Mist
            for (int i = 0; i < 40; i += 2) {
                double t = (double) i / 40.0;
                int alpha = (int) (t * 130);
                g.setColor(new Color(skyFog.getRed(), skyFog.getGreen(), skyFog.getBlue(), alpha));
                g.fillRect(0, groundY - 40 + i, width, 2);
            }
        }
    
        private static Font cachedFont = null;
        private static int cachedFontSize = -1;

        public static void drawMinecraftText(Graphics2D g, String text, int x, int y, int fontSize, Color color) {
            if (cachedFont == null || cachedFontSize != fontSize) {
                cachedFont = new Font("Monospaced", Font.BOLD, fontSize);
                cachedFontSize = fontSize;
            }
            g.setFont(cachedFont);
            g.setColor(new Color(30, 30, 30));
            g.drawString(text, x + 2, y + 2);
            g.setColor(color);
            g.drawString(text, x, y);
        }
    
        public static void drawStars(Graphics2D g, int width, int height, int count, long seed, double twinkle) {
            Random rand = new Random(seed);
            for (int i = 0; i < count; i++) {
                int x = rand.nextInt(width);
                int y = rand.nextInt(height / 2);
                int r = rand.nextInt(2) + 1;
    
                int alpha = (int) (155 + 100 * Math.sin(twinkle * Math.PI * 2 + rand.nextDouble() * 10));
                alpha = Math.max(0, Math.min(255, alpha));
    
                MidpointDrawing.fillCircle(g, x, y, r, new Color(255, 255, 255, alpha));
            }
        }
    
        public static double easeInOut(double t) {
            return t * t * (3 - 2 * t);
        }
    
        public static void drawTorchLight(Graphics2D g, int x, int y, int radius) {
            MidpointDrawing.fillCircleGlow(g, x, y, radius, new Color(255, 200, 50, 60), new Color(0, 0, 0, 0));
            MidpointDrawing.fillCircleGlow(g, x, y, radius / 2, new Color(255, 220, 100, 40), new Color(0, 0, 0, 0));
        }
    
        public static void drawCaveDarkness(Graphics2D g, int width, int height, int lightX, int lightY, int lightRadius) {
            // Edge vignette darkness bands
            for (int i = 0; i < 4; i++) {
                int alpha = 30 - i * 5;
                if (alpha <= 0) break;
                g.setColor(new Color(0, 0, 0, alpha));
                g.fillRect(0, 0, width, i * 40);
                g.fillRect(0, height - i * 40, width, i * 40);
                g.fillRect(0, 0, i * 40, height);
                g.fillRect(width - i * 40, 0, i * 40, height);
            }
            // Radial light glow around torch/light source
            if (lightRadius > 0) {
                MidpointDrawing.fillCircleGlow(g, lightX, lightY, lightRadius,
                    new Color(255, 200, 80, 25), new Color(0, 0, 0, 0));
            }
        }
    
        public static void drawLavaAmbient(Graphics2D g, int width, int lavaY, double progress) {
            int glowAlpha = 30 + (int) (Math.sin(progress * Math.PI * 4) * 15);
            glowAlpha = Math.max(0, Math.min(60, glowAlpha));
            g.setColor(new Color(255, 80, 20, glowAlpha));
            g.fillRect(0, lavaY - 100, width, 100);
        }
    }

    // =========================================================================
    // MIDPOINTDRAWING.JAVA
    // =========================================================================
    
    static class MidpointDrawing {
    
        public static void drawCircle(Graphics2D g, int cx, int cy, int radius, Color color) {
            g.setColor(color);
            int x = radius;
            int y = 0;
            int p = 1 - radius;
    
            while (x >= y) {
                plotSymmetricPoints(g, cx, cy, x, y);
                y++;
                if (p <= 0) {
                    p = p + 2 * y + 1;
                } else {
                    x--;
                    p = p + 2 * y - 2 * x + 1;
                }
            }
        }
    
        private static void plotSymmetricPoints(Graphics2D g, int cx, int cy, int x, int y) {
            g.drawLine(cx + x, cy + y, cx + x, cy + y);
            g.drawLine(cx - x, cy + y, cx - x, cy + y);
            g.drawLine(cx + x, cy - y, cx + x, cy - y);
            g.drawLine(cx - x, cy - y, cx - x, cy - y);
            g.drawLine(cx + y, cy + x, cx + y, cy + x);
            g.drawLine(cx - y, cy + x, cx - y, cy + x);
            g.drawLine(cx + y, cy - x, cx + y, cy - x);
            g.drawLine(cx - y, cy - x, cx - y, cy - x);
        }
    
        public static void fillCircle(Graphics2D g, int cx, int cy, int radius, Color color) {
            g.setColor(color);
            int x = radius;
            int y = 0;
            int p = 1 - radius;
    
            while (x >= y) {
                g.drawLine(cx - x, cy + y, cx + x, cy + y);
                g.drawLine(cx - x, cy - y, cx + x, cy - y);
                g.drawLine(cx - y, cy + x, cx + y, cy + x);
                g.drawLine(cx - y, cy - x, cx + y, cy - x);
                
                y++;
                if (p <= 0) {
                    p = p + 2 * y + 1;
                } else {
                    x--;
                    p = p + 2 * y - 2 * x + 1;
                }
            }
        }
    
        public static void drawEllipse(Graphics2D g, int cx, int cy, int rx, int ry, Color color) {
            g.setColor(color);
            int rx2 = rx * rx;
            int ry2 = ry * ry;
            int twoRx2 = 2 * rx2;
            int twoRy2 = 2 * ry2;
            int p;
            int x = 0;
            int y = ry;
            int px = 0;
            int py = twoRx2 * y;
    
            // Region 1
            p = (int) (ry2 - (rx2 * ry) + (0.25 * rx2));
            while (px < py) {
                plotEllipseSymmetricPoints(g, cx, cy, x, y);
                x++;
                px += twoRy2;
                if (p < 0) {
                    p += ry2 + px;
                } else {
                    y--;
                    py -= twoRx2;
                    p += ry2 + px - py;
                }
            }
    
            // Region 2
            p = (int) (ry2 * (x + 0.5) * (x + 0.5) + rx2 * (y - 1) * (y - 1) - rx2 * ry2);
            while (y > 0) {
                plotEllipseSymmetricPoints(g, cx, cy, x, y);
                y--;
                py -= twoRx2;
                if (p > 0) {
                    p += rx2 - py;
                } else {
                    x++;
                    px += twoRy2;
                    p += rx2 - py + px;
                }
            }
        }
    
        private static void plotEllipseSymmetricPoints(Graphics2D g, int cx, int cy, int x, int y) {
            g.drawLine(cx + x, cy + y, cx + x, cy + y);
            g.drawLine(cx - x, cy + y, cx - x, cy + y);
            g.drawLine(cx + x, cy - y, cx + x, cy - y);
            g.drawLine(cx - x, cy - y, cx - x, cy - y);
        }
    
        public static void fillEllipse(Graphics2D g, int cx, int cy, int rx, int ry, Color color) {
            g.setColor(color);
            int rx2 = rx * rx;
            int ry2 = ry * ry;
            int twoRx2 = 2 * rx2;
            int twoRy2 = 2 * ry2;
            int p;
            int x = 0;
            int y = ry;
            int px = 0;
            int py = twoRx2 * y;
    
            p = (int) (ry2 - (rx2 * ry) + (0.25 * rx2));
            while (px < py) {
                g.drawLine(cx - x, cy + y, cx + x, cy + y);
                g.drawLine(cx - x, cy - y, cx + x, cy - y);
                x++;
                px += twoRy2;
                if (p < 0) {
                    p += ry2 + px;
                } else {
                    y--;
                    py -= twoRx2;
                    p += ry2 + px - py;
                }
            }
    
            p = (int) (ry2 * (x + 0.5) * (x + 0.5) + rx2 * (y - 1) * (y - 1) - rx2 * ry2);
            while (y > 0) {
                g.drawLine(cx - x, cy + y, cx + x, cy + y);
                g.drawLine(cx - x, cy - y, cx + x, cy - y);
                y--;
                py -= twoRx2;
                if (p > 0) {
                    p += rx2 - py;
                } else {
                    x++;
                    px += twoRy2;
                    p += rx2 - py + px;
                }
            }
        }
        
        public static void fillCircleGlow(Graphics2D g, int cx, int cy, int radius, Color centerColor, Color edgeColor) {
            int step = Math.max(1, radius / 20);
            for (int r = radius; r > 0; r -= step) {
                double t = 1.0 - ((double) r / radius);
                Color c = lerpColor(edgeColor, centerColor, t);
                fillCircle(g, cx, cy, r, c);
            }
            // Draw center
            fillCircle(g, cx, cy, Math.max(1, step), centerColor);
        }
        
        public static void fillEllipseGlow(Graphics2D g, int cx, int cy, int rx, int ry, Color centerColor, Color edgeColor) {
            int maxR = Math.max(rx, ry);
            int step = Math.max(1, maxR / 20);
            for (int r = maxR; r > 0; r -= step) {
                double t = 1.0 - ((double) r / maxR);
                Color c = lerpColor(edgeColor, centerColor, t);
                int curRx = (int)(rx * ((double)r/maxR));
                int curRy = (int)(ry * ((double)r/maxR));
                if (curRx > 0 && curRy > 0) {
                    fillEllipse(g, cx, cy, curRx, curRy, c);
                }
            }
            fillCircle(g, cx, cy, Math.max(1, step), centerColor);
        }
        
        private static Color lerpColor(Color a, Color b, double t) {
            return DrawUtils.lerpColor(a, b, t);
        }
    }

}
