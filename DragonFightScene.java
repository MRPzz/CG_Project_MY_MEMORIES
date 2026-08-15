import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

/**
 * Scene 8: The End dimension and Ender Dragon fight.
 */
public class DragonFightScene extends Scene {
    private final Random random = new Random(800);
    
    // XP Orbs state for the end of the scene
    private double[] xpX = new double[30];
    private double[] xpY = new double[30];
    private double[] xpVX = new double[30];
    private double[] xpVY = new double[30];
    private boolean xpInit = false;

    public DragonFightScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        DrawUtils.drawStars(g2d, width, height, 100, 888, progress);

        int groundY = 450;
        int bs = DrawUtils.BLOCK_SIZE;

        // End Stone Platform
        g2d.setColor(DrawUtils.END_STONE);
        g2d.fillRect(100, groundY, 400, height - groundY);
        // Texture
        g2d.setColor(new Color(200, 200, 150));
        for (int i=0; i<50; i++) {
            g2d.fillRect(100 + random.nextInt(400), groundY + random.nextInt(height - groundY), 5, 5);
        }

        // Obsidian Pillars
        int[][] pillars = {
            {150, groundY - 6*bs, 2*bs, 6*bs},
            {400, groundY - 8*bs, 2*bs, 8*bs},
            {280, groundY - 5*bs, 2*bs, 5*bs}
        };

        for (int i = 0; i < pillars.length; i++) {
            int px = pillars[i][0];
            int py = pillars[i][1];
            int pw = pillars[i][2];
            int ph = pillars[i][3];

            g2d.setColor(DrawUtils.OBSIDIAN);
            g2d.fillRect(px, py, pw, ph);

            // End Crystal (except destroyed)
            boolean destroyed = (i == 1 && progress > 0.45);
            if (!destroyed) {
                int cx = px + pw/2;
                int cy = py - bs;
                
                // Crystal glow
                MidpointDrawing.fillCircleGlow(g2d, cx, cy, bs, new Color(255, 150, 255, 100), new Color(255, 150, 255, 0));
                
                // Rotating crystal
                g2d.setColor(Color.WHITE);
                g2d.translate(cx, cy);
                g2d.rotate(progress * Math.PI * 4);
                g2d.drawRect(-5, -5, 10, 10);
                g2d.rotate(-progress * Math.PI * 4);
                g2d.translate(-cx, -cy);

                // Beam to dragon (0 - 0.2)
                if (progress < 0.2) {
                    g2d.setColor(new Color(255, 255, 255, 150));
                    int dx = (int)(300 + Math.sin(progress * Math.PI * 4) * 150);
                    int dy = (int)(150 + Math.cos(progress * Math.PI * 2) * 50);
                    g2d.drawLine(cx, cy, dx, dy);
                }
            } else if (progress > 0.35 && progress < 0.5) {
                // Explosion
                int cx = px + pw/2;
                int cy = py - bs;
                double expProg = (progress - 0.35) / 0.15;
                MidpointDrawing.fillCircle(g2d, cx, cy, (int)(expProg * 40), Color.ORANGE);
            }
        }

        // Steve
        int steveX = 250;
        int steveY = groundY;
        DrawUtils.drawSteve(g2d, steveX, steveY, 1, true);

        // Arrows
        if (progress > 0.2 && progress <= 0.45) { // Shoot crystal
            double arrP = (progress - 0.2) / 0.15; // 0 to 1
            int ax = (int)(steveX + arrP * (400 - steveX));
            int ay = (int)(steveY - bs - arrP * (steveY - bs - (groundY - 8*bs)));
            g2d.setColor(Color.WHITE);
            g2d.drawLine(ax, ay, ax + 5, ay - 5);
        } else if (progress > 0.6 && progress <= 0.75) { // Shoot dragon
            double arrP = (progress - 0.6) / 0.15;
            int dx = 300; 
            int dy = 150;
            int ax = (int)(steveX + arrP * (dx - steveX));
            int ay = (int)(steveY - bs - arrP * (steveY - bs - dy));
            g2d.setColor(Color.WHITE);
            g2d.drawLine(ax, ay, ax + 5, ay - 5);
        }

        // Dragon
        if (progress < 0.9) {
            int dx = (int)(300 + Math.sin(progress * Math.PI * 4) * 150);
            int dy = (int)(150 + Math.cos(progress * Math.PI * 2) * 50);

            if (progress > 0.45 && progress < 0.75) {
                // Erratic flight
                dx += (random.nextDouble() - 0.5) * 20;
                dy += (random.nextDouble() - 0.5) * 20;
            } else if (progress >= 0.75) {
                // Death sequence, center dragon
                dx = 300;
                dy = 150;
            }

            // Draw Dragon
            g2d.translate(dx, dy);

            if (progress >= 0.75) {
                // Death explosion
                double deathP = (progress - 0.75) / 0.15;
                MidpointDrawing.fillCircleGlow(g2d, 0, 0, (int)(deathP * 150), new Color(255, 0, 255, (int)((1-deathP)*255)), new Color(0,0,0,0));
                
                // Fragments
                g2d.setColor(DrawUtils.DRAGON_BLACK);
                for(int i=0; i<10; i++) {
                    int fx = (int)((random.nextDouble() - 0.5) * deathP * 200);
                    int fy = (int)((random.nextDouble() - 0.5) * deathP * 200);
                    g2d.fillRect(fx, fy, 15, 15);
                }
            } else {
                // Body
                g2d.setColor(DrawUtils.DRAGON_BLACK);
                g2d.fillRect(-30, -10, 60, 20); // main body
                g2d.fillRect(30, -5, 20, 10); // neck
                g2d.fillRect(50, -10, 15, 15); // head
                g2d.fillRect(-50, -5, 20, 10); // tail
                
                // Eyes
                g2d.setColor(DrawUtils.ENDER_PURPLE);
                g2d.fillRect(58, -8, 4, 4);

                // Wings using GeneralPath (Curves requirement)
                g2d.setColor(DrawUtils.DRAGON_BLACK);
                double flap = Math.sin(progress * Math.PI * 20) * 30;
                
                GeneralPath wing1 = new GeneralPath();
                wing1.moveTo(-15, -10);
                wing1.quadTo(0, -40 - flap, 40, -20 - flap/2);
                wing1.quadTo(10, -10, 15, -10);
                wing1.closePath();
                g2d.fill(wing1);

                GeneralPath wing2 = new GeneralPath();
                wing2.moveTo(-15, 10);
                wing2.quadTo(0, 40 + flap, 40, 20 + flap/2);
                wing2.quadTo(10, 10, 15, 10);
                wing2.closePath();
                g2d.fill(wing2);
                
                // Purple trail
                g2d.setColor(DrawUtils.DRAGON_PURPLE);
                for(int i=0; i<5; i++) {
                    g2d.fillRect(-60 - random.nextInt(30), -10 + random.nextInt(20), 4, 4);
                }
            }
            g2d.translate(-dx, -dy);
        }

        // Death effects - XP Orbs
        if (progress >= 0.75) {
            if (!xpInit) {
                for(int i=0; i<30; i++) {
                    xpX[i] = 300;
                    xpY[i] = 150;
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double speed = 2 + random.nextDouble() * 5;
                    xpVX[i] = Math.cos(angle) * speed;
                    xpVY[i] = Math.sin(angle) * speed;
                }
                xpInit = true;
            }

            for(int i=0; i<30; i++) {
                xpX[i] += xpVX[i];
                xpY[i] += xpVY[i];
                // attract to steve
                double t = (progress - 0.75) / 0.25;
                if (t > 0.5) {
                    double attr = (t - 0.5) * 2; // 0 to 1
                    xpVX[i] += (steveX - xpX[i]) * 0.05 * attr;
                    xpVY[i] += (steveY - xpY[i]) * 0.05 * attr;
                }
                MidpointDrawing.fillCircle(g2d, (int)xpX[i], (int)xpY[i], 3, DrawUtils.XP_GREEN);
            }
        } else {
            xpInit = false; // reset
        }

        // End Portal (0.9 - 1.0)
        if (progress > 0.9) {
            int px = 300;
            int py = groundY;
            g2d.setColor(DrawUtils.END_STONE);
            g2d.fillRect(px - 40, py - 10, 80, 20); // base
            
            // portal center
            MidpointDrawing.fillEllipse(g2d, px, py - 5, 30, 10, Color.BLACK);
            DrawUtils.drawStars(g2d, width, height, 10, 111, progress); // swirling stars illusion
            
            // Achievement
            double achP = (progress - 0.9) / 0.1;
            DrawUtils.drawAchievement(g2d, width, "Free the End", achP);
        }

        // HUD
        int xpPercent = 50;
        if (progress > 0.75) {
            xpPercent = 50 + (int)(((progress - 0.75)/0.25) * 50);
        }
        DrawUtils.drawHUD(g2d, width, 6, 10, 5, xpPercent);
    }
}
