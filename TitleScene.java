import java.awt.Color;
import java.awt.Graphics2D;

public class TitleScene extends Scene {

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
        int cloudX = (int) (width * progress * 0.5);
        DrawUtils.drawCloud(g2d, 100 - cloudX, 100, 80);
        DrawUtils.drawCloud(g2d, 400 - cloudX, 150, 120);
        DrawUtils.drawCloud(g2d, 600 - cloudX, 80, 100);

        // Animated dirt blocks scrolling
        int scrollOffset = (int) (progress * DrawUtils.BLOCK_SIZE * 5) % DrawUtils.BLOCK_SIZE;
        for (int x = -DrawUtils.BLOCK_SIZE; x < width + DrawUtils.BLOCK_SIZE; x += DrawUtils.BLOCK_SIZE) {
            for (int y = height - 60; y < height; y += DrawUtils.BLOCK_SIZE) {
                DrawUtils.drawBlock(g2d, x - scrollOffset, y, DrawUtils.BLOCK_SIZE, DrawUtils.DIRT_BROWN);
            }
        }

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
        
        // Singleplayer Button
        Color btnColor = Color.GRAY;
        if (progress >= 0.7 && progress < 0.85) {
            btnColor = Color.LIGHT_GRAY; // hover
        } else if (progress >= 0.85) {
            btnColor = Color.DARK_GRAY; // click
        }
        
        int spY = btnY;
        if (progress >= 0.85) spY += 2;
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(btnX - 2, spY - 2, btnW + 4, btnH + 4);
        g2d.setColor(btnColor);
        g2d.fillRect(btnX, spY, btnW, btnH);
        DrawUtils.drawMinecraftText(g2d, "Singleplayer", btnX + 75, spY + 25, 18, Color.WHITE);
        
        // Multiplayer Button
        int mpY = btnY + 50;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(btnX - 2, mpY - 2, btnW + 4, btnH + 4);
        g2d.setColor(Color.GRAY);
        g2d.fillRect(btnX, mpY, btnW, btnH);
        DrawUtils.drawMinecraftText(g2d, "Multiplayer", btnX + 85, mpY + 25, 18, Color.WHITE);
        
        // Options Button
        int opY = btnY + 100;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(btnX - 2, opY - 2, btnW + 4, btnH + 4);
        g2d.setColor(Color.GRAY);
        g2d.fillRect(btnX, opY, btnW, btnH);
        DrawUtils.drawMinecraftText(g2d, "Options...", btnX + 95, opY + 25, 18, Color.WHITE);
    }
}
