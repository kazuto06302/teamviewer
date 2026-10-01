package net.kztmc.mc.teamviewer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;

import java.awt.Color;

public class Marker {

    private static void draw(PoseStack matrices, SubmitNodeCollector collector, Color color, float[] verts) {
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;
        float s = Config.marker_size;

        collector.submitCustomGeometry(matrices, TeamRenderer.SEE_THROUGH, (pose, buffer) -> {
            for (int i = 0; i + 1 < verts.length; i += 2) {
                buffer.addVertex(pose, verts[i] * s, verts[i + 1] * s, 0.0f)
                        .setColor(r, g, b, a);
            }
        });
    }

    // =========================
    // Shapes
    // =========================

    public static void drawTriangle(PoseStack matrices, SubmitNodeCollector consumers, Color color) {
        draw(matrices, consumers, color, new float[]{
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.0f,  0.5f
        });
    }

    public static void drawInvertedTriangle(PoseStack matrices, SubmitNodeCollector consumers, Color color) {
        draw(matrices, consumers, color, new float[]{
                -0.5f,  0.5f,
                0.0f,  -0.5f,
                0.5f,   0.5f
        });
    }

    public static void drawDiamond(PoseStack matrices, SubmitNodeCollector consumers, Color color) {
        draw(matrices, consumers, color, new float[]{
                -0.35f, 0.0f,
                0.35f, 0.0f,
                0.0f,  0.5f,
                -0.35f, 0.0f,
                0.0f, -0.5f,
                0.35f, 0.0f
        });
    }

    public static void drawSquare(PoseStack matrices, SubmitNodeCollector consumers, Color color) {
        draw(matrices, consumers, color, new float[]{
                -0.5f, -0.5f,
                0.5f,  -0.5f,
                0.5f,   0.5f,

                -0.5f, -0.5f,
                0.5f,   0.5f,
                -0.5f,  0.5f
        });
    }
}