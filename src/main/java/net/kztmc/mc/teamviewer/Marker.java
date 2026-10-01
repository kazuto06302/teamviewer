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

        for (int i = 0; i + 5 < verts.length; i += 6) {
            float ax = verts[i]     * s, ay = verts[i + 1] * s;
            float bx = verts[i + 2] * s, by = verts[i + 3] * s;
            float cx = verts[i + 4] * s, cy = verts[i + 5] * s;

            // 表面と裏面（巻き方向を逆にしたもの）の両方を出す
            emitIsolatedTriangle(buffer, mat, ax, ay, bx, by, cx, cy, r, g, b, a);
            emitIsolatedTriangle(buffer, mat, ax, ay, cx, cy, bx, by, r, g, b, a);
        }
    }

    private static void emitIsolatedTriangle(VertexConsumer buf, Matrix4f mat,
                                             float ax, float ay,
                                             float bx, float by,
                                             float cx, float cy,
                                             float r, float g, float b, float a) {
        vertex(buf, mat, ax, ay, r, g, b, a);
        vertex(buf, mat, ax, ay, r, g, b, a);
        vertex(buf, mat, bx, by, r, g, b, a);
        vertex(buf, mat, cx, cy, r, g, b, a);
        vertex(buf, mat, cx, cy, r, g, b, a);
        vertex(buf, mat, cx, cy, r, g, b, a);
    }

    private static void vertex(VertexConsumer buf, Matrix4f mat, float x, float y, float r, float g, float b, float a) {
        buf.addVertex(mat, x, y, 0.0f).setColor(r, g, b, a);
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