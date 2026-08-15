import java.awt.Color;
import java.awt.Graphics2D;

public class MidpointDrawing {

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
        for (int r = radius; r > 0; r--) {
            double t = 1.0 - ((double) r / radius);
            Color c = lerpColor(edgeColor, centerColor, t);
            fillCircle(g, cx, cy, r, c);
        }
    }
    
    public static void fillEllipseGlow(Graphics2D g, int cx, int cy, int rx, int ry, Color centerColor, Color edgeColor) {
        int maxR = Math.max(rx, ry);
        for (int r = maxR; r > 0; r--) {
            double t = 1.0 - ((double) r / maxR);
            Color c = lerpColor(edgeColor, centerColor, t);
            int curRx = (int)(rx * ((double)r/maxR));
            int curRy = (int)(ry * ((double)r/maxR));
            if (curRx > 0 && curRy > 0) {
                fillEllipse(g, cx, cy, curRx, curRy, c);
            }
        }
    }
    
    private static Color lerpColor(Color a, Color b, double t) {
        if (t < 0) t = 0;
        if (t > 1) t = 1;
        int red = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int green = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int blue = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        int alpha = (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
        return new Color(red, green, blue, alpha);
    }
}
