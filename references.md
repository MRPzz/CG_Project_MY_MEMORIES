# References - MY MEMORIES (Minecraft Animation)
# Assignment 1 - Computer Graphics
# Student IDs: 67050476, 67050613

## Algorithms Implemented from Scratch
- **Bresenham's Line Algorithm**: Full integer decision variable arithmetic handling all 8 octants, steep/shallow slopes (|m| <= 1, |m| > 1), positive and negative directions. Used for all line segments, block borders, cracks, arrows, and connecting Bézier curve points.
  - *Reference*: Hearn & Baker, "Computer Graphics with OpenGL", Chapter 3.
- **Midpoint Circle Algorithm**: 8-way symmetry integer midpoint decision variable (p = 1 - r). Implemented for both circular outlines and horizontal scanline filled circles/glows (Sun/Moon halos, torches, stars, XP orbs).
  - *Reference*: Hearn & Baker, "Computer Graphics with OpenGL", Chapter 3.
- **Midpoint Ellipse Algorithm**: Two-region integer midpoint decision variables (p1, p2) with 4-way symmetry. Implemented for both ellipse outlines and scanline filled ellipses (torch ambient lighting, End Crystal glow).
  - *Reference*: Hearn & Baker, "Computer Graphics with OpenGL", Chapter 3.
- **Bézier Curve Algorithm**: Quadratic and Cubic parametric curves evaluated at discrete intervals and rasterized via Bresenham lines. Used for sword flurry swoosh arcs, Eye of Ender parabolic trajectories, and Ender Dragon flapping wings.
  - *Reference*: Course lecture materials on Parametric & Polynomial Curves.
- **Scanline Fill & Scanline Polygon Fill Algorithm**: Scanline intersection sorting and horizontal interval filling. Used for filling all rectangular blocks, UI buttons, panels, rotated entities, mouse cursor polygon, and closed Bézier wings.
  - *Reference*: Foley, van Dam, Feiner, Hughes, "Computer Graphics: Principles and Practice", Chapter 3.
- **Flood Fill Algorithm**: Queue-based 4-way connected region filling on the direct pixel buffer. Used for procedural terrain filling and cavern water/portal basins.
  - *Reference*: Course lecture materials on Raster Fill Algorithms.
- **Custom 5x7 Minecraft Bitmap Font Engine**: 100% custom bitmap font dictionary with scaling and drop shadows, plotting pixels directly into the buffer without using built-in drawString.

## Java 2D API
- Double-buffered raster pipeline using an empty 600x600 BufferedImage (BufferedImage.TYPE_INT_ARGB) and direct integer raster access (DataBufferInt).
- Blitting 100% custom-drawn buffer via Graphics.drawImage(buffer, 0, 0, null).
- Oracle Java 2D Graphics Tutorial: https://docs.oracle.com/javase/tutorial/2d/index.html
- Java AWT Graphics2D API Reference: https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/java/awt/Graphics2D.html
- Java AffineTransform API: https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/java/awt/geom/AffineTransform.html
- Java AlphaComposite API: https://docs.oracle.com/en/java/javase/17/docs/api/java.desktop/java/awt/AlphaComposite.html

## Main Models & Entities from Minecraft (Procedural Replications)
- **Player (Steve)**: https://minecraft.wiki/w/Player
  - Procedural humanoid model with walking animation, head, torso, articulated arms/legs, and tool wielding.
- **Creeper**: https://minecraft.wiki/w/Creeper
  - Procedural quad-legged green hostile mob with authentic pixel facial expression.
- **Zombie**: https://minecraft.wiki/w/Zombie
  - Procedural mob with outstretched arms, green skin, and walking cycle.
- **Blaze**: https://minecraft.wiki/w/Blaze
  - Procedural floating head with 6 trigonometric-orbiting blaze rods and fireball attacks.
- **Ghast**: https://minecraft.wiki/w/Ghast
  - Procedural floating white cube entity with tentacles, facial features, and fireballs.
- **Enderman**: https://minecraft.wiki/w/Enderman
  - Tall slender silhouette with glowing purple pixel eyes.
- **Ender Dragon**: https://minecraft.wiki/w/Ender_Dragon
  - Multi-part boss model with dynamic Bézier flapping wings and death particle beams.
- **End Crystal**: https://minecraft.wiki/w/End_Crystal
  - Rotating crystalline structure with bedrock/obsidian pedestal and magenta glow.

## Environment, Blocks & Items References
- **Minecraft Block Textures** (Grass, Dirt, Stone, Cobble, Log, Leaves, Planks, Glass, Ores, Obsidian, Netherrack, Glowstone, End Stone, Bedrock): https://minecraft.wiki/w/Block
- **Minecraft Nether Portal**: https://minecraft.wiki/w/Nether_portal
- **Minecraft End Portal & Frame**: https://minecraft.wiki/w/End_portal
- **Minecraft Eye of Ender**: https://minecraft.wiki/w/Eye_of_Ender
- **Minecraft Weapons & Tools** (Diamond Sword, Iron/Diamond Pickaxe, Bow): https://minecraft.wiki/w/Weapon

## UI, HUD & Game Systems References
- **Minecraft Title Screen & Menus**: https://minecraft.wiki/w/Menu_screen
- **Minecraft HUD** (Health Hearts, Hunger Bar, XP Bar): https://minecraft.wiki/w/Heads-up_display
- **Minecraft Advancement / Achievement System**: https://minecraft.wiki/w/Advancement

## Animation Techniques
- Smooth easing functions (Hermite interpolation): https://en.wikipedia.org/wiki/Smoothstep
- Color linear interpolation (lerp): Standard computer graphics technique
- Double-buffering & Alpha Compositing: Standard frame rasterization
