import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;

public class CreateWorldScene extends Scene {

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
                DrawUtils.drawBlock(g2d, x, y, DrawUtils.BLOCK_SIZE, DrawUtils.DIRT_BROWN);
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
            g2d.setColor(Color.BLACK); g2d.fillRect(btnX - 2, 140 - 2, btnW + 4, btnH + 4);
            g2d.setColor(Color.GRAY); g2d.fillRect(btnX, 140, btnW, btnH);
            DrawUtils.drawMinecraftText(g2d, "Game Mode: Survival", btnX + 50, 140 + 25, 16, Color.WHITE);

            // Difficulty button
            g2d.setColor(Color.BLACK); g2d.fillRect(btnX - 2, 190 - 2, btnW + 4, btnH + 4);
            g2d.setColor(Color.GRAY); g2d.fillRect(btnX, 190, btnW, btnH);
            DrawUtils.drawMinecraftText(g2d, "Difficulty: Normal", btnX + 60, 190 + 25, 16, Color.WHITE);

            // Allow Cheats button
            g2d.setColor(Color.BLACK); g2d.fillRect(btnX - 2, 240 - 2, btnW + 4, btnH + 4);
            g2d.setColor(Color.GRAY); g2d.fillRect(btnX, 240, btnW, btnH);
            DrawUtils.drawMinecraftText(g2d, "Allow Cheats: OFF", btnX + 60, 240 + 25, 16, Color.WHITE);

            // Create button
            int btnY = height - 100;
            
            Color btnColor = Color.GRAY;
            if (progress >= 0.5) {
                btnColor = Color.DARK_GRAY;
                btnY += 2;
            }

            g2d.setColor(Color.BLACK);
            g2d.fillRect(btnX - 2, btnY - 2, btnW + 4, btnH + 4);
            g2d.setColor(btnColor);
            g2d.fillRect(btnX, btnY, btnW, btnH);
            
            DrawUtils.drawMinecraftText(g2d, "Create New World", btnX + 70, btnY + 25, 16, Color.WHITE);

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

        // Flash effect at the very end
        if (progress >= 0.95) {
            double flashAlpha = (progress - 0.95) / 0.05;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) flashAlpha));
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
}
