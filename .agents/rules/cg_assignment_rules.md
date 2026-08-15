---
name: CG Assignment Constraints
description: Strict constraints for Computer Graphics assignments, focusing on authentic UI, Java 2D API, Midpoint algorithms, and code readability.
trigger: always_on
---

# Computer Graphics Assignment Rules

When working on Computer Graphics assignments (specifically the Minecraft Memories animation or similar projects) in this workspace, you MUST adhere to the following rules:

1. **Theme Interpretation:**
   - Interpret themes conceptually (e.g. visual storytelling, atmosphere) rather than literally forcing text strings onto the UI.
   - Prioritize 100% authentic aesthetic replication of the original subject matter (e.g. replicating Minecraft UI exactly) over literal textual interpretation of rubrics.

2. **Strict Tooling:**
   - **USE ONLY** Java 2D API (`java.awt.*`, `Graphics2D`, `javax.swing.*`).
   - **DO NOT** use external game libraries or JavaFX.

3. **Custom Algorithms (CRITICAL):**
   - **NEVER** use built-in circle/ellipse drawing functions like `drawOval()` or `fillOval()` for major circular/elliptical elements.
   - **ALWAYS** use the custom-built `MidpointDrawing` algorithms (Midpoint Circle / Ellipse) implemented from scratch.

4. **Grading Priorities:**
   - Prioritize code readability (clean formatting, clear variable names).
   - Keep code comments short, concise, and in English, avoiding overly long explanations.
   - Ensure feature completeness to satisfy rubric requirements.
