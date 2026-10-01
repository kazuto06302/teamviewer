package net.kztmc.mc.teamviewer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.joml.Matrix4f;

import java.awt.Color;

public class Marker {

    private static void draw(PoseStack matrices, MultiBufferSource consumers, Color color, float[] verts) {
        Matrix4f mat = matrices.last().pose();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        VertexConsumer buffer = consumers.getBuffer(RenderTypes.debugFilledBox());

        for (int i = 0; i < verts.length; i += 2) {
            float x = verts[i] * Config.marker_size;
            float y = verts[i + 1] * Config.marker_size;

            buffer.addVertex(mat, x, y, 0.0f)
                    .setColor(r, g, b, a);
        }
    }

    // =========================
    // Shapes
    // =========================

    public static void drawTriangle(PoseStack matrices, MultiBufferSource consumers, Color color) {
        draw(matrices, consumers, color, new float[]{
                -0.5f, -0.5f,
                0.0f,   0.5f,
                0.5f,  -0.5f
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
                0.0f,   0.5f,
                -0.5f,  0.0f,
                0.5f,   0.0f,

                0.0f,  -0.5f,
                -0.5f,  0.0f,
                0.5f,   0.0f
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