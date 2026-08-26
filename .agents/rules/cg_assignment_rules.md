---
name: CG Assignment Constraints
description: Strict constraints for Computer Graphics assignments, focusing on authentic UI, Java 2D API, complete elimination of built-in drawing primitives, mandatory custom CG algorithms, and procedural double-buffering.
trigger: always_on
---

# Computer Graphics Assignment Rules

When working on Computer Graphics assignments (specifically the Minecraft Memories animation or similar projects) in this workspace, you MUST adhere to the following rules:

1. **Theme Interpretation:**
   - Interpret themes conceptually (e.g. visual storytelling, atmosphere) rather than literally forcing text strings onto the UI.
   - Prioritize 100% authentic aesthetic replication of the original subject matter (e.g. replicating Minecraft UI exactly) over literal textual interpretation of rubrics.

2. **Strict Tooling & Buffer Management:**
   - **USE ONLY** Java 2D API (`java.awt.*`, `Graphics2D`, `javax.swing.*`).
   - **DO NOT** use external game libraries or JavaFX.
   - **100% PROCEDURAL DRAWING**: Create an empty `BufferedImage` (`BufferedImage.TYPE_INT_ARGB`) and draw all pixels/shapes procedurally.
   - **ZERO EXTERNAL ASSETS**: NEVER load external image files (`ImageIO.read` from files or URLs is strictly forbidden).
   - Only `Graphics.drawImage(buffer, 0, 0, null)` is allowed to blit the custom-rendered buffer onto the panel.

3. **Strict Ban on Built-in Drawing Primitives (CRITICAL):**
   - **NEVER** use Java 2D built-in drawing methods:
     - Lines & Rectangles: `drawLine()`, `drawRect()`, `fillRect()`, `drawRoundRect()`, `fillRoundRect()`, `draw3DRect()`, `fill3DRect()`
     - Circles & Arcs: `drawOval()`, `fillOval()`, `drawArc()`, `fillArc()`
     - Polygons & Shapes: `drawPolygon()`, `fillPolygon()`, `draw()`, `fill()`, `GeneralPath`, `Path2D`
     - Text: `drawString()`
   - **ALWAYS** use custom algorithm implementations:
     - **Lines**: General Bresenham's Line Algorithm (all octants, integer arithmetic).
     - **Curves/Splines**: Bézier Curve Algorithm (Quadratic & Cubic parametric curves).
     - **Circles**: Midpoint Circle Algorithm (8-way symmetry integer decision parameter for outlines & scanline fills).
     - **Ellipses**: Midpoint Ellipse Algorithm (Region 1 & 2 midpoint decision parameters for outlines & scanline fills).
     - **Color Filling**: Scanline Fill Algorithm & Queue-based 4-Way Flood Fill Algorithm.
     - **Polygons**: Scanline Polygon Fill Algorithm & Bresenham Polygon Outlines.
     - **Typography / Text**: Custom 5x7 Bitmap/Pixel Font Engine rasterized via custom pixel/scanline plotting.

4. **Grading Priorities:**
   - Prioritize code readability (clean formatting, clear variable names).
   - Keep code comments short, concise, and in English, avoiding overly long explanations.
   - Ensure feature completeness to satisfy rubric requirements.
