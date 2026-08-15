import java.awt.Color;
import java.awt.Graphics2D;

public class FirstDayScene extends Scene {

    public FirstDayScene(String name, int durationMs) {
        super(name, durationMs);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height, double progress) {
        // Day/Night cycle
        double timeOfDay = 0.0 + (progress * 0.1); 
        DrawUtils.drawSky(g2d, width, height, timeOfDay);

        // Sun
        MidpointDrawing.fillCircleGlow(g2d, 500, 100, 40, Color.WHITE, DrawUtils.GOLD_YELLOW);
        MidpointDrawing.fillCircle(g2d, 500, 100, 30, Color.YELLOW);

        // Clouds
        DrawUtils.drawCloud(g2d, (int) (150 + progress * 20), 80, 100);
        DrawUtils.drawCloud(g2d, (int) (350 + progress * 30), 120, 80);

        // Ground
        int groundY = 450;
        DrawUtils.drawGround(g2d, width, height, groundY);

        // Background trees
        DrawUtils.drawTree(g2d, 400, groundY - 50, 1);
        DrawUtils.drawTree(g2d, 100, groundY - 50, 1);

        // Main Tree to be punched
        int treeX = 250;
        int treeY = groundY - 50;
        if (progress < 0.6) {
            DrawUtils.drawTree(g2d, treeX, treeY, 1);
            
            // Cracks if punching
            if (progress >= 0.3) {
                g2d.setColor(Color.BLACK);
                double punchProg = (progress - 0.3) / 0.3;
                int trunkX = treeX + 10;
                int trunkY = treeY + 30; // hit lower part
                if (punchProg > 0.2) g2d.drawLine(trunkX, trunkY, trunkX + 8, trunkY + 8);
                if (punchProg > 0.5) g2d.drawLine(trunkX + 8, trunkY, trunkX, trunkY + 8);
                if (punchProg > 0.8) g2d.drawLine(trunkX + 4, trunkY, trunkX + 4, trunkY + 10);
            }
        } else {
            // Tree broken, wood drops
            if (progress < 0.75) {
                int dropY = treeY + 30 + (int)((progress - 0.6) * 100);
                DrawUtils.drawBlock(g2d, treeX + 10, dropY, 10, DrawUtils.LOG_BROWN);
            }
        }

        // Steve
        int steveX;
        double swingAngle = 0;
        
        if (progress < 0.3) {
            // Walking in
            steveX = (int) (-50 + (progress / 0.3) * (treeX - 10));
        } else if (progress < 0.6) {
            // Punching
            steveX = treeX - 10;
            swingAngle = Math.sin((progress - 0.3) * 30) * Math.PI / 4;
        } else if (progress < 0.75) {
            // Walking to grab wood
            steveX = treeX - 10 + (int) ((progress - 0.6) / 0.15 * 10);
        } else {
            // Moving away
            steveX = treeX + (int) ((progress - 0.75) / 0.25 * 50);
        }
        
        DrawUtils.drawSteveWithTool(g2d, steveX, groundY - 40, 1, true, "hand", swingAngle);

        // Crafting table appears
        if (progress >= 0.85) {
            DrawUtils.drawBlock(g2d, treeX + 60, groundY - DrawUtils.BLOCK_SIZE, DrawUtils.BLOCK_SIZE, DrawUtils.WOOD_BROWN);
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(treeX + 60, groundY - DrawUtils.BLOCK_SIZE, DrawUtils.BLOCK_SIZE, 4); // top texture
        }

        // HUD
        DrawUtils.drawHUD(g2d, width, 10, 10, 10, (int)(progress * 10));
    }
}
