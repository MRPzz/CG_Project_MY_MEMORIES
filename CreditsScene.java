import java.awt.*;
import java.awt.geom.*;

/**
 * Scene 9: Credits and Poem.
 */
public class CreditsScene extends Scene {
    
    private String[] poem = {
        "I see the player you mean.",
        "PLAYERNAME?",
        "Yes. Take care. It has reached a higher level now.",
        "It can read our thoughts.",
        "That doesn't matter. It thinks we are part of the game.",
        "I like this player. It played well. It did not give up.",
        "It is reading our thoughts as though they were words on a screen.",
        "That is how it chooses to imagine many things, when it is deep in the dream of a game.",
        "Words make a wonderful interface. Very flexible. And less terrifying.",
        "They used to hear voices. Before players could read.",
        "Back in the days when those who did not play called the players witches, and warlocks.",
        "What did this player dream?",
        "This player dreamed of sunlight and trees. Of fire and water.",
        "It dreamed it created. And it dreamed it destroyed.",
        "It dreamed it hunted, and was hunted. It dreamed of shelter.",
        "Hah, the original interface. A million years old, and it still works.",
        "Does it know that we love it? That the universe is kind?",
        "Sometimes, through the noise of its thoughts, it hears the universe, yes.",
        "And the game was over and the player woke up from the dream.",
        "And the player began a new dream. And the player dreamed better.",
        "And the player was the universe. And the player was love.",
        "You are the player.",
        "Wake up."
    };

    public CreditsScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2d.setColor(new Color(5, 5, 20)); // Deep dark blue
        g2d.fillRect(0, 0, width, height);
        DrawUtils.drawStars(g2d, width, height, 150, 999, progress * 2);

        // Alpha fading at the end (0.85 - 1.0)
        float alpha = 1.0f;
        if (progress > 0.85) {
            alpha = (float) (1.0 - (progress - 0.85) / 0.15);
            alpha = Math.max(0, Math.min(1, alpha));
        }
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(ac);

        // Scrolling Math
        // progress 0 to 1 -> scroll Y from height to -1000 to fit all text
        double smoothP = DrawUtils.easeInOut(progress);
        int startY = height + 100;
        int endY = -700;
        int currentY = (int) (startY + smoothP * (endY - startY));

        // Title
        DrawUtils.drawMinecraftText(g2d, "MINECRAFT", width/2 - 70, currentY, 24, DrawUtils.GOLD_YELLOW);

        // Poem Lines
        int lineY = currentY + 60;
        for (String line : poem) {
            if (!line.isEmpty()) {
                // Approximate centering based on character width
                DrawUtils.drawMinecraftText(g2d, line, width/2 - line.length()*4, lineY, 14, Color.WHITE);
            }
            lineY += 30;
        }

        // Vignettes (fade in and out on the sides)
        drawVignette(g2d, progress, 0.1, 0.3, 100, 200, 1); // Tree
        drawVignette(g2d, progress, 0.3, 0.5, 450, 300, 2); // Diamond
        drawVignette(g2d, progress, 0.5, 0.7, 100, 400, 3); // House
        drawVignette(g2d, progress, 0.7, 0.9, 450, 500, 4); // Dragon

        // Restore composite
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private void drawVignette(Graphics2D g2d, double progress, double startP, double endP, int x, int y, int type) {
        if (progress >= startP && progress <= endP) {
            double p = (progress - startP) / (endP - startP);
            // fade in 0-0.2, fade out 0.8-1.0
            float alpha = 1.0f;
            if (p < 0.2) alpha = (float)(p / 0.2);
            else if (p > 0.8) alpha = (float)((1.0 - p) / 0.2);
            
            AlphaComposite currentComposite = (AlphaComposite) g2d.getComposite();
            float combinedAlpha = currentComposite.getAlpha() * alpha;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, combinedAlpha));

            int bs = 10;
            switch(type) {
                case 1: // Tree + Steve
                    DrawUtils.drawBlock(g2d, x, y, bs, DrawUtils.LOG_BROWN);
                    DrawUtils.drawBlock(g2d, x, y-bs, bs, DrawUtils.LEAVES_GREEN);
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(x+bs, y, 5, 10);
                    break;
                case 2: // Diamond
                    MidpointDrawing.fillCircleGlow(g2d, x, y, 15, DrawUtils.DIAMOND_BLUE, new Color(0,0,0,0));
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(x-2, y-2, 4, 4);
                    break;
                case 3: // House
                    g2d.setColor(DrawUtils.WOOD_BROWN);
                    g2d.fillRect(x-15, y-10, 30, 20);
                    g2d.setColor(DrawUtils.LOG_BROWN);
                    int[] rx = {x-20, x, x+20};
                    int[] ry = {y-10, y-25, y-10};
                    g2d.fillPolygon(rx, ry, 3);
                    break;
                case 4: // Dragon
                    g2d.setColor(DrawUtils.DRAGON_BLACK);
                    g2d.fillRect(x-10, y-5, 20, 10);
                    g2d.setColor(DrawUtils.ENDER_PURPLE);
                    g2d.fillRect(x+5, y-2, 2, 2);
                    break;
            }
            g2d.setComposite(currentComposite);
        }
    }
}
