# Project Assignment 1 - Computer Graphics (MY MEMORIES)

## Team Members (Work in Pairs)
- **Student ID 1**: 67050476
- **Student ID 2**: 67050613

---

## Project Specifications & Constraints

1. **Resolution & Canvas**: 600 x 600 Animation Canvas.
2. **Theme**: "MY MEMORIES" (Conceptual theme portraying nostalgic Minecraft journey from starting a new world to defeating the Ender Dragon).
3. **Tooling & API**: Java 2D API only (`java.awt.*`, `javax.swing.*`). No external libraries or JavaFX.
4. **Animation Duration**: AT LEAST 5 SECONDS (Current implementation: ~57 seconds across 10 animated scenes with smooth transitions).
5. **Empty Buffer & Procedural Drawing**:
   - Double-buffered rendering via an empty 600 x 600 `BufferedImage` (`BufferedImage.TYPE_INT_ARGB`).
   - 100% procedural pixel drawing and filling.
   - **STRICTLY PROHIBITED**: Loading external images (`ImageIO.read` from files/URLs).
   - Only `Graphics.drawImage(buffer, 0, 0, null)` is used to present the custom buffer to the panel.
6. **Strict Ban on Built-in Drawing Primitives**:
   - **DO NOT USE**: `drawLine`, `drawRect`, `fillRect`, `drawOval`, `fillOval`, `drawString`, `drawArc`, `fillArc`, `drawPolygon`, `fillPolygon`.

---

## Required Algorithms Implemented from Scratch

| Feature | Algorithm | Implementation Details |
|---|---|---|
| **Lines** | **Bresenham's Line Algorithm** | Integer decision parameter arithmetic handling all 8 octants, steep/shallow slopes ($|m| \le 1, |m| > 1$), positive/negative directions. |
| **Curves & Splines** | **Bézier Curve Algorithm** | Quadratic & Cubic Bézier parametric curves ($B(t)$) evaluated and rasterized via Bresenham line segments. |
| **Polygons** | **Scanline Polygon Fill & Bresenham Outline** | Scanline edge intersection sorting with horizontal interval filling; Bresenham segment outlines. |
| **Color Filling** | **Scanline Fill & Flood Fill Algorithms** | Row-by-row horizontal scanline rasterization; Queue-based 4-way connected Flood Fill algorithm. |
| **Circles** | **Midpoint Circle Algorithm** | 8-way symmetry integer midpoint decision variable ($p = 1 - r$) for outlines and scanline filled circles/glows. |
| **Ellipses** | **Midpoint Ellipse Algorithm** | Region 1 & Region 2 midpoint decision variables with 4-way symmetry for outlines and scanline filled ellipses/glows. |
| **Text / Typography** | **Custom 5x7 Minecraft Pixel Font Engine** | 100% custom-built bitmap font dictionary with scaling and drop-shadows (zero `drawString`). |

---

### Rubric ###

## Complete Given Tasks — /40
**Implements all required tasks and features as specified, and submits by the due date.**

| Level | Points | Description |
|---|---:|---|
| **Excellent** | 40 pts | All tasks completed correctly and submitted on time. |
| **Good** | 30 pts | Most tasks completed correctly with minor errors; submitted on time. |
| **Fair** | 20 pts | Over half of tasks completed; some major errors; submitted on time. |
| **Poor** | 10 pts | Less than half of tasks completed; major errors; submitted late. |
| **Very Poor** | 0 pts | Tasks mostly incomplete or incorrect; submitted late or not submitted. |

## Completeness of Code — /20
**Implements all required functions, algorithms, and features thoroughly.**

| Level | Points | Description |
|---|---:|---|
| **Excellent** | 20 pts | All required features implemented thoroughly with optimal approaches. |
| **Good** | 15 pts | Most features implemented with reasonable approaches. |
| **Fair** | 10 pts | Some features missing or poorly implemented. |
| **Poor** | 5 pts | Many features missing or incorrect. |
| **Very Poor** | 0 pts | Very minimal implementation. |

## Readability — /10
**Uses clear naming, comments, and formatting for easy understanding.**

| Level | Points | Description |
|---|---:|---|
| **Excellent** | 10 pts | Excellent code style; clear naming, proper indentation, and comments. |
| **Good** | 7 pts | Good code style with minor readability issues. |
| **Fair** | 5 pts | Some readability issues, inconsistent style. |
| **Poor** | 3 pts | Poor readability, difficult to follow. |
| **Very Poor** | 0 pts | Unreadable code structure. |

## Dedication — /15
**Shows effort beyond minimum requirements, such as creativity or enhancements.**

| Level | Points | Description |
|---|---:|---|
| **Outstanding Effort** | 15 pts | Evident effort beyond minimum requirements. |
| **Strong Effort** | 12 pts | Strong effort shown; meets all requirements clearly. |
| **Acceptable Effort** | 9 pts | Acceptable effort with basic implementation only. |
| **Minimal Effort** | 5 pts | Minimal effort beyond partial implementation. |
| **No Effort** | 0 pts | Very little effort observed. |

## References — /5
**Cites sources of code, algorithms, and external resources properly.**

| Level | Points | Description |
|---|---:|---|
| **Fully Referenced** | 5 pts | All resources and references properly cited (libraries, tutorials, images). |
| **Mostly Referenced** | 4 pts | Minor missing citations. |
| **Some Referenced** | 3 pts | Some references missing. |
| **Few Referenced** | 1 pt | Few references cited. |
| **No References** | 0 pts | No references provided. |

## Popular Vote — /10
**Receives peer recognition based on creativity and presentation.**

| Level | Points | Description |
|---|---:|---|
| **First Place** | 10 pts | First Place. |
| **Second Place** | 8 pts | Second Place. |
| **Third Place** | 6 pts | Third Place. |
| **Fourth Place** | 4 pts | Fourth Place. |
| **Semi-Finalist** | 2 pts | Semi-Finalist. |
| **First Rounder** | 0 pts | First Rounder. |

---

**Total: 100 points**
