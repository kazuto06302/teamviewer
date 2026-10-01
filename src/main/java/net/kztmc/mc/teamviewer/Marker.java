package net.kztmc.mc.teamviewer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;

import java.awt.Color;

public class Marker {

    private static void draw(PoseStack matrices, MultiBufferSource consumers, Color color, float[] verts) {
        Matrix4f mat = matrices.last().pose();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        VertexConsumer buffer = consumers.getBuffer(TeamRenderer.SEE_THROUGH);
        float s = Config.marker_size;

        for (int i = 0; i + 1 < verts.length; i += 2) {
            buffer.addVertex(mat, verts[i] * s, verts[i + 1] * s, 0.0f)
                    .setColor(r, g, b, a);
        }
    }

    // =========================
    // Shapes
    // =========================

    public static void drawTriangle(PoseStack matrices, MultiBufferSource consumers, Color color) {
        draw(matrices, consumers, color, new float[]{
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.0f,  0.5f
        });
    }

    public static void drawInvertedTriangle(PoseStack matrices, MultiBufferSource consumers, Color color) {
        draw(matrices, consumers, color, new float[]{
                -0.5f,  0.5f,
                0.0f,  -0.5f,
                0.5f,   0.5f
        });
    }

    public static void drawDiamond(PoseStack matrices, MultiBufferSource consumers, Color color) {
        draw(matrices, consumers, color, new float[]{
                -0.35f, 0.0f,
                0.35f, 0.0f,
                0.0f,  0.5f,
                -0.35f, 0.0f,
                0.0f, -0.5f,
                0.35f, 0.0f
        });
    }

    public static void drawSquare(PoseStack matrices, MultiBufferSource consumers, Color color) {
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