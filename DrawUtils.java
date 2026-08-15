import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
import java.util.Random;

public class DrawUtils {

    public static final Color SKY_BLUE = new Color(135, 206, 235);
    public static final Color GRASS_GREEN = new Color(95, 159, 53);
    public static final Color GRASS_TOP = new Color(118, 179, 67);
    public static final Color DIRT_BROWN = new Color(134, 96, 67);
    public static final Color STONE_GRAY = new Color(128, 128, 128);
    public static final Color WOOD_BROWN = new Color(156, 127, 78);
    public static final Color LOG_BROWN = new Color(100, 75, 45);
    public static final Color LEAVES_GREEN = new Color(56, 118, 29);
    public static final Color DIAMOND_BLUE = new Color(80, 220, 235);
    public static final Color NIGHT_SKY = new Color(10, 10, 40);
    public static final Color SUNSET_ORANGE = new Color(255, 140, 50);
    public static final Color SUNSET_PINK = new Color(255, 100, 120);
    public static final Color STEVE_SKIN = new Color(180, 140, 100);
    public static final Color STEVE_HAIR = new Color(60, 35, 15);
    public static final Color STEVE_SHIRT = new Color(50, 155, 175);
    public static final Color STEVE_PANTS = new Color(55, 55, 175);
    public static final Color STEVE_SHOES = new Color(80, 80, 80);
    public static final Color CREEPER_GREEN = new Color(73, 138, 43);
    public static final Color CREEPER_DARK = new Color(50, 100, 30);
    public static final Color OBSIDIAN = new Color(20, 18, 30);
    public static final Color END_STONE = new Color(219, 219, 163);
    public static final Color NETHER_RED = new Color(110, 20, 20);
    public static final Color PORTAL_PURPLE = new Color(120, 50, 180);
    public static final Color GOLD_YELLOW = new Color(255, 200, 50);
    public static final Color IRON_GRAY = new Color(190, 180, 170);
    public static final Color COAL_BLACK = new Color(40, 40, 40);
    public static final Color LAVA_ORANGE = new Color(207, 92, 16);
    public static final Color HEART_RED = new Color(190, 30, 30);
    public static final Color XP_GREEN = new Color(100, 220, 50);
    public static final Color ENDER_PURPLE = new Color(80, 0, 120);
    public static final Color DRAGON_BLACK = new Color(30, 30, 30);
    public static final Color DRAGON_PURPLE = new Color(100, 50, 150);

    public static final int BLOCK_SIZE = 20;

    public static void drawBlock(Graphics2D g, int x, int y, int size, Color color) {
        g.setColor(color);
        g.fillRect(x, y, size, size);
        g.setColor(color.darker());
        g.drawRect(x, y, size, size);
    }

    public static void drawSteve(Graphics2D g, int x, int y, int scale, boolean facingRight) {
        int w = 20 * scale;
        int h = 40 * scale;
        
        // head
        g.setColor(STEVE_SKIN);
        g.fillRect(x + w/4, y, w/2, w/2);
        
        // hair
        g.setColor(STEVE_HAIR);
        g.fillRect(x + w/4, y, w/2, w/8);
        
        // eyes
        g.setColor(Color.WHITE);
        int eyeOffset = facingRight ? 2 * scale : 0;
        g.fillRect(x + w/4 + 2 * scale + eyeOffset, y + w/4, 2 * scale, 2 * scale);
        g.setColor(Color.BLUE);
        g.fillRect(x + w/4 + 3 * scale + eyeOffset, y + w/4, 1 * scale, 2 * scale);
        
        // body
        g.setColor(STEVE_SHIRT);
        g.fillRect(x + w/4, y + w/2, w/2, h/2 - w/2);
        
        // arms
        g.setColor(STEVE_SKIN);
        g.fillRect(x, y + w/2, w/4, h/2 - w/2);
        g.fillRect(x + 3*w/4, y + w/2, w/4, h/2 - w/2);
        
        // legs
        g.setColor(STEVE_PANTS);
        g.fillRect(x + w/4, y + h/2, w/2, h/2 - w/8);
        
        // shoes
        g.setColor(STEVE_SHOES);
        g.fillRect(x + w/4, y + h - w/8, w/2, w/8);
    }
    
    public static void drawSteveWithTool(Graphics2D g, int x, int y, int scale, boolean facingRight, String tool, double swingAngle) {
        drawSteve(g, x, y, scale, facingRight);
        
        AffineTransform old = g.getTransform();
        int armPivotX = facingRight ? x + 15 * scale : x + 5 * scale;
        int armPivotY = y + 15 * scale;
        
        g.translate(armPivotX, armPivotY);
        if (facingRight) {
            g.rotate(swingAngle);
        } else {
            g.rotate(-swingAngle);
        }
        
        // draw tool
        if (tool.equals("pickaxe")) {
            g.setColor(WOOD_BROWN);
            g.fillRect(0, -10 * scale, 4 * scale, 20 * scale);
            g.setColor(IRON_GRAY);
            g.fillRect(-5 * scale, -10 * scale, 14 * scale, 4 * scale);
        }
        
        g.setTransform(old);
    }

    public static void drawTree(Graphics2D g, int x, int y, int scale) {
        // trunk
        g.setColor(LOG_BROWN);
        g.fillRect(x + 10 * scale, y + 20 * scale, 10 * scale, 30 * scale);
        
        // leaves
        g.setColor(LEAVES_GREEN);
        g.fillRect(x, y, 30 * scale, 20 * scale);
        g.fillRect(x + 5 * scale, y - 10 * scale, 20 * scale, 10 * scale);
    }

    public static void drawHUD(Graphics2D g, int width, int hearts, int maxHearts, int hunger, int xpPercent) {
        // hearts
        for (int i = 0; i < maxHearts; i++) {
            int hx = 10 + i * 15;
            int hy = 10;
            // Black outline
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
                
                // white glint
                g.setColor(Color.WHITE);
                g.fillRect(hx + 1, hy + 1, 1, 1);
            } else {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(hx + 1, hy + 1, 3, 3); g.fillRect(hx + 5, hy + 1, 3, 3);
                g.fillRect(hx + 2, hy + 4, 5, 1); g.fillRect(hx + 3, hy + 5, 3, 1);
                g.fillRect(hx + 4, hy + 6, 1, 1);
            }
        }
        
        // hunger
        for (int i = 0; i < 10; i++) {
            int hx = width - 150 + i * 12;
            int hy = 10;
            // Black outline
            g.setColor(Color.BLACK);
            g.fillRect(hx + 3, hy, 4, 1);
            g.fillRect(hx + 2, hy + 1, 1, 3); g.fillRect(hx + 7, hy + 1, 1, 2);
            g.fillRect(hx + 1, hy + 4, 1, 2); g.fillRect(hx + 8, hy + 3, 1, 3);
            g.fillRect(hx + 2, hy + 6, 2, 1); g.fillRect(hx + 7, hy + 6, 1, 1);
            g.fillRect(hx + 4, hy + 7, 3, 1);
            
            if (i < hunger) {
                g.setColor(new Color(160, 80, 20)); // drumstick color
                g.fillRect(hx + 3, hy + 1, 4, 2);
                g.fillRect(hx + 3, hy + 3, 5, 1);
                g.fillRect(hx + 2, hy + 4, 6, 1);
                g.fillRect(hx + 2, hy + 5, 5, 1);
                g.fillRect(hx + 4, hy + 6, 3, 1);
                
                // dark outline/shading inside
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
        
        // XP bar
        int barW = 182;
        int barH = 5;
        int barX = width/2 - barW/2;
        int barY = 30;
        
        g.setColor(Color.BLACK);
        g.fillRect(barX - 1, barY - 1, barW + 2, barH + 2);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barW, barH);
        
        // Notches
        g.setColor(Color.BLACK);
        for (int i = 1; i <= 9; i++) {
            g.fillRect(barX + (barW / 10) * i, barY, 1, barH);
        }
        
        // Fill XP
        int fillW = (int) ((xpPercent / 100.0) * barW);
        g.setColor(XP_GREEN);
        g.fillRect(barX, barY, fillW, barH);
    }

    public static void drawAchievement(Graphics2D g, int width, String title, double popupProgress) {
        if (popupProgress <= 0 || popupProgress >= 1) return;
        
        int yOffset;
        if (popupProgress < 0.3) {
            yOffset = (int) (-50 + 70 * (popupProgress / 0.3));
        } else if (popupProgress > 0.7) {
            yOffset = (int) (20 - 70 * ((popupProgress - 0.7) / 0.3));
        } else {
            yOffset = 20;
        }
        
        int w = 250;
        int h = 40;
        int x = width/2 - w/2;
        
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, yOffset, w, h);
        g.setColor(GOLD_YELLOW);
        g.drawRect(x, yOffset, w, h);
        g.drawRect(x+1, yOffset+1, w-2, h-2);
        
        drawMinecraftText(g, "Achievement Get!", x + 10, yOffset + 15, 12, GOLD_YELLOW);
        drawMinecraftText(g, title, x + 10, yOffset + 30, 14, Color.WHITE);
    }

    public static void drawCloud(Graphics2D g, int x, int y, int cloudWidth) {
        g.setColor(Color.WHITE);
        QuadCurve2D q1 = new QuadCurve2D.Float(x, y, x + cloudWidth/2, y - 20, x + cloudWidth, y);
        g.fill(q1);
        g.fillRect(x, y, cloudWidth, 15);
        QuadCurve2D q2 = new QuadCurve2D.Float(x, y + 15, x + cloudWidth/2, y + 30, x + cloudWidth, y + 15);
        g.fill(q2);
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
            bottomColor = Color.CYAN;
        } else if (timeOfDay < 0.4) {
            double t = (timeOfDay - 0.2) / 0.2;
            topColor = lerpColor(SKY_BLUE, SUNSET_PINK, t);
            bottomColor = lerpColor(Color.CYAN, SUNSET_ORANGE, t);
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
            bottomColor = lerpColor(SUNSET_ORANGE, Color.CYAN, t);
        }
        
        // Simple linear gradient replacement using basic rects for Java 2D fallback compatibility
        for (int i = 0; i < height; i += 10) {
            double t = (double) i / height;
            g.setColor(lerpColor(topColor, bottomColor, t));
            g.fillRect(0, i, width, 10);
        }
    }

    public static void drawGround(Graphics2D g, int width, int height, int groundY) {
        // grass top
        g.setColor(GRASS_TOP);
        g.fillRect(0, groundY, width, 10);
        
        // dirt blocks
        for (int x = 0; x < width; x += BLOCK_SIZE) {
            for (int y = groundY + 10; y < height; y += BLOCK_SIZE) {
                drawBlock(g, x, y, BLOCK_SIZE, DIRT_BROWN);
            }
        }
    }

    public static void drawMinecraftText(Graphics2D g, String text, int x, int y, int fontSize, Color color) {
        Font font = new Font("Monospaced", Font.BOLD, fontSize);
        g.setFont(font);
        
        // shadow
        g.setColor(Color.DARK_GRAY);
        g.drawString(text, x + 2, y + 2);
        
        // text
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
}
