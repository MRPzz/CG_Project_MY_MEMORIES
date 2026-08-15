import java.awt.Graphics2D;

/**
 * Base class for all scenes in the animation.
 */
public abstract class Scene {
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
