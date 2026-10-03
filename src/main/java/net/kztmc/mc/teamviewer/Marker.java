package net.kztmc.mc.teamviewer;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class Marker {
    private static void draw(MatrixStack matrices, Color color, float[] verts) {

        Matrix4f mat = matrices.peek().getPositionMatrix();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        BufferBuilder buffer = new BufferBuilder(
                new BufferAllocator(2048),
                VertexFormat.DrawMode.TRIANGLES,
                VertexFormats.POSITION_COLOR
        );

        for (int i = 0; i < verts.length; i += 2) {
            buffer.vertex(mat,
                    verts[i] * Config.marker_size,
                    verts[i + 1] * Config.marker_size,
                    0
            ).color(r, g, b, a);
        }

        BuiltBuffer built = buffer.end();

        var vertexBuffer = TeamRenderer.MARKER_PIPELINE.getVertexFormat()
                .uploadImmediateVertexBuffer(built.getBuffer());

        var framebuffer = MinecraftClient.getInstance().getFramebuffer();

        try (RenderPass pass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(
                        () -> "marker",
                        framebuffer.getColorAttachmentView(),
                        OptionalInt.empty(),
                        framebuffer.getDepthAttachmentView(),
                        OptionalDouble.empty()
                )) {

            pass.setPipeline(TeamRenderer.MARKER_PIPELINE);
            pass.setVertexBuffer(0, vertexBuffer);

            RenderSystem.bindDefaultUniforms(pass);

            pass.draw(0, built.getDrawParameters().vertexCount());
        }
    }
    // =========================
    // Shapes
    // =========================

    public static void drawTriangle(MatrixStack matrices, Color color) {
        draw(matrices, color, new float[]{
                -0.5f, -0.5f,
                0.0f,  0.5f,
                0.5f, -0.5f
        });
    }

    public static void drawInvertedTriangle(MatrixStack matrices, Color color) {
        draw(matrices, color, new float[]{
                -0.5f,  0.5f,
                0.0f, -0.5f,
                0.5f,  0.5f
        });
    }

    public static void drawDiamond(MatrixStack matrices, Color color) {
        draw(matrices, color, new float[]{
                0,  0.5f,
                -0.5f, 0,
                0.5f, 0,

                0, -0.5f,
                -0.5f, 0,
                0.5f, 0
        });
    }

    public static void drawSquare(MatrixStack matrices, Color color) {
        draw(matrices, color, new float[]{
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f,  0.5f,

                -0.5f, -0.5f,
                0.5f,  0.5f,
                -0.5f,  0.5f
        });
    }
}