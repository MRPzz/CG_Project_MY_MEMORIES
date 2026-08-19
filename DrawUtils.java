import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class DrawUtils {

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

        Color mainColor = DIAMOND_BLUE;
        Color darkColor = DIAMOND_DARK;
        Color lightColor = Color.WHITE;

        if ("iron".equalsIgnoreCase(oreType)) {
            mainColor = IRON_GRAY;
            darkColor = IRON_DARK;
            lightColor = Color.WHITE;
        } else if ("gold".equalsIgnoreCase(oreType)) {
            mainColor = GOLD_YELLOW;
            darkColor = GOLD_DARK;
            lightColor = Color.WHITE;
        } else if ("coal".equalsIgnoreCase(oreType)) {
            mainColor = COAL_BLACK;
            darkColor = Color.BLACK;
            lightColor = Color.GRAY;
        } else if ("redstone".equalsIgnoreCase(oreType)) {
            mainColor = REDSTONE_RED;
            darkColor = new Color(140, 15, 15);
            lightColor = new Color(255, 130, 130);
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
     * Authentic Minecraft Crafting Table.
     */
    public static void drawCraftingTable(Graphics2D g, int x, int y, int size) {
        // Wood base
        g.setColor(WOOD_BROWN);
        g.fillRect(x, y, size, size);

        // Top crafting grid surface (top 4px)
        g.setColor(new Color(195, 155, 95));
        g.fillRect(x, y, size, 4);
        g.setColor(Color.BLACK);
        g.drawLine(x + size / 3, y, x + size / 3, y + 4);
        g.drawLine(x + 2 * size / 3, y, x + 2 * size / 3, y + 4);

        // Side tools pattern (saw & hammer motif)
        g.setColor(new Color(85, 60, 35));
        g.fillRect(x + 3, y + 6, size - 6, size - 8);
        g.setColor(IRON_GRAY);
        g.fillRect(x + 5, y + 8, 4, 6);
        g.fillRect(x + 11, y + 8, 4, 4);

        g.setColor(new Color(0, 0, 0, 60));
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
        if ("pickaxe".equals(tool)) {
            // Wood Handle
            g.setColor(WOOD_BROWN);
            g.fillRect(-px, 10 * px, 2 * px, 14 * px);
            // Iron / Diamond Pickaxe Head
            g.setColor(IRON_GRAY);
            g.fillRect(-6 * px, 10 * px, 12 * px, 3 * px);
            g.fillRect(-7 * px, 12 * px, 3 * px, 2 * px);
            g.fillRect(4 * px, 12 * px, 3 * px, 2 * px);
            g.setColor(IRON_DARK);
            g.fillRect(-5 * px, 13 * px, 10 * px, px);
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

    public static void drawMinecraftText(Graphics2D g, String text, int x, int y, int fontSize, Color color) {
        Font font = new Font("Monospaced", Font.BOLD, fontSize);
        g.setFont(font);
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
        for (int i = 0; i < 4; i++) {
            int alpha = 30 - i * 5;
            if (alpha <= 0) break;
            g.setColor(new Color(0, 0, 0, alpha));
            g.fillRect(0, 0, width, i * 40);
            g.fillRect(0, height - i * 40, width, i * 40);
            g.fillRect(0, 0, i * 40, height);
            g.fillRect(width - i * 40, 0, i * 40, height);
        }
    }

    public static void drawLavaAmbient(Graphics2D g, int width, int lavaY, double progress) {
        int glowAlpha = 30 + (int) (Math.sin(progress * Math.PI * 4) * 15);
        glowAlpha = Math.max(0, Math.min(60, glowAlpha));
        g.setColor(new Color(255, 80, 20, glowAlpha));
        g.fillRect(0, lavaY - 100, width, 100);
    }
}
