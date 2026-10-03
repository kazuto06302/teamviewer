package net.kztmc.mc.teamviewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.vertex.VertexFormat;
import lunarclient.apollo.common.v1.UuidOuterClass;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

import static net.kztmc.mc.teamviewer.Marker.*;

public class TeamRenderer {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void init(){
        WorldRenderEvents.AFTER_ENTITIES.register((ctx) -> {
            render(ctx.matrices(), ctx.gameRenderer().getCamera(), ctx.gameRenderer().getClient().getRenderTickCounter().getTickProgress(true));
        });
    }

    public static final RenderPipeline MARKER_PIPELINE =
            RenderPipelines.register(
                    RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                            .withLocation(Identifier.of("teamviewer", "marker"))
                            .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES)
                            .withBlend(new BlendFunction(
                                    SourceFactor.SRC_ALPHA,
                                    DestFactor.ONE_MINUS_SRC_ALPHA
                            ))
                            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                            .withCull(false)
                            .withDepthWrite(false)
                            .build()
            );


    public static void render(MatrixStack matrices, Camera camera, float t) {
        if (client.player == null) return;

        Vec3d camPos = camera.getCameraPos();
        matrices.push();
        if (Objects.equals(Config.debug, "debug render1")) System.out.println("render1");

        VertexConsumerProvider.Immediate consumers = client.getBufferBuilders().getEntityVertexConsumers();

        TeamData.getMembers().values().forEach(m -> {
            // timeout
            TeamData.cleanupExpired();

            // world
            if (TeamData.selfApolloWorld == null || !TeamData.selfApolloWorld.equals(m.world)) return;

            if (Objects.equals(Config.debug, "debug render2")) System.out.println("render2");

            Vec3d pos = resolvePosition(m,t);
            Vec3d rel = pos.subtract(camPos);
            double dist = client.player.getEntityPos().distanceTo(pos);

            UUID playeruuid = toMinecraftUuid(m.uuid);
            String rawname = m.name.getString();
            String name = extractName(rawname);

            if (Objects.equals(Config.debug, "debug render3"))System.out.println(name);

            matrices.push();
            // render
            if (!playeruuid.equals(client.player.getUuid())) {
                matrices.translate(rel.x, rel.y + Config.marker_y, rel.z);
                matrices.multiply(camera.getRotation());
                //1.21.8 matrices.multiply(client.getEntityRenderDispatcher().getRotation());
                if(Config.marker_display) {
                    matrices.push();
                    drawMarker(matrices, m.color, dist);
                    matrices.pop();
                }

                matrices.push();
                drawText(matrices, name, dist, pos);
                matrices.pop();
            }
            matrices.pop();
        });
        consumers.draw();
        matrices.pop();
    }

    //draw
    private static void drawMarker(MatrixStack matrices, Color color, double dist) {
        if (dist < Config.marker_inv) return;

        float scale = Scale(dist);
        matrices.scale(scale, scale, scale);

        if (Objects.equals(Config.debug, "debug render marker")) System.out.println("render marker");

        switch (Config.MARKER_SHAPE) {
            case INVERTEDTRIANGLE -> drawInvertedTriangle(matrices, color);
            case TRIANGLE -> drawTriangle(matrices, color);
            case DIAMOND  -> drawDiamond(matrices, color);
            case SQUARE   -> drawSquare(matrices, color);
        }
    }


    private static void drawText(MatrixStack matrices, String name, double dist, Vec3d pos) {
        if (dist < Config.text_inv) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.textRenderer == null) return;

        boolean inFov = isNearCrosshair(pos.add(0, 1, 0), Config.text_angle);

        boolean showName = shouldShow(Config.NAME_MODE, inFov);
        boolean showDist = shouldShow(Config.DIST_MODE, inFov);


        if (!showName && !showDist) return;

        matrices.push();
        matrices.translate(0, 0, 0.05);

        float distscale = Scale(dist);
        float scale = (Config.text_size / 100) * distscale;

        matrices.scale(scale, -scale, scale);

        VertexConsumerProvider.Immediate consumers = client.getBufferBuilders().getEntityVertexConsumers();

        int backgroundColor = 0;
        if (Config.background){
            backgroundColor = (int)(MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F) * 255.0F) << 24 | 0x666666;
        }

        if (showDist) {
            String distText = "(" + (int) dist + "m)";
            float x = -client.textRenderer.getWidth(distText) / 2f;
            client.textRenderer.draw(
                    distText,
                    x,
                    -Config.dist_y * 10,
                    Config.dist_color,
                    false,
                    matrices.peek().getPositionMatrix(),
                    consumers,
                    TextRenderer.TextLayerType.SEE_THROUGH,
                    backgroundColor,
                    LightmapTextureManager.MAX_LIGHT_COORDINATE
            );
        }

        if (showName) {
            float x = -client.textRenderer.getWidth(name) / 2f;
            client.textRenderer.draw(
                    name,
                    x,
                    -Config.name_y * 10,
                    Config.name_color,
                    false,
                    matrices.peek().getPositionMatrix(),
                    consumers,
                    TextRenderer.TextLayerType.SEE_THROUGH,
                    backgroundColor,
                    LightmapTextureManager.MAX_LIGHT_COORDINATE
            );
        }

        matrices.pop();
    }


    //interpolate
    private static Vec3d resolvePosition(TeamMemberData m, double tickDelta) {
        String apolloName = extractName(m.name.getString());

        // 1 short：Minecraft API からdisplaynameで検索
        // nickされているとUUIDが一致しないためdisplaynameで代替
        PlayerEntity player = findPlayerByDisplayName(apolloName);

        if (player != null) {
            return new Vec3d(
                    MathHelper.lerp(tickDelta, player.lastX, player.getX()),
                    MathHelper.lerp(tickDelta, player.lastY, player.getY()),
                    MathHelper.lerp(tickDelta, player.lastZ, player.getZ())
            );
        }

        // 2 long：Apollo [time interpolate]
        long now = System.currentTimeMillis();
        double x = interpolate(m.lastX, m.x, now, m.lastUpdate, m.lastLastUpdate);
        double y = interpolate(m.lastY, m.y, now, m.lastUpdate, m.lastLastUpdate);
        double z = interpolate(m.lastZ, m.z, now, m.lastUpdate, m.lastLastUpdate);

        return new Vec3d(x, y, z);
    }

    private static PlayerEntity findPlayerByDisplayName(String apolloName) {
        if (client.world == null || apolloName.isEmpty()) return null;

        for (PlayerEntity p : client.world.getPlayers()) {
            // getDisplayName() はnick後の表示名、getName() は元のゲームタグ名
            String displayName = extractName(p.getDisplayName().getString());
            String gameName    = p.getName().getString();

            if (displayName.equalsIgnoreCase(apolloName) || gameName.equalsIgnoreCase(apolloName)) {
                return p;
            }
        }
        return null;
    }

    private static double interpolate(double last, double current, long now, long lastUpdate, long lastLastUpdate) {
        if (lastLastUpdate == 0 || lastUpdate == lastLastUpdate) return current;

        long interval = lastUpdate - lastLastUpdate;
        long elapsed = now - lastUpdate;

        double t = (double) elapsed / interval;
        t = Math.max(0.0, Math.min(1.0, t));

        return MathHelper.lerp(t, last, current);
    }

    // until
    public static float Scale(double distance) {
        if (distance < 10.f) return 1.f;
        return (float) (distance / 10f);
    }



    private static boolean isNearCrosshair(Vec3d targetWorldPos, float maxAngleDegrees) {
        if (client.player == null) return false;
        Vec3d eyePos = client.player.getEyePos();
        Vec3d lookVec = client.player.getRotationVec(1.0f);
        Vec3d toTarget = targetWorldPos.subtract(eyePos).normalize();

        double dot = lookVec.dotProduct(toTarget);
        double cosThreshold = Math.cos(Math.toRadians(maxAngleDegrees));

        return dot >= cosThreshold;
    }
    private static boolean shouldShow(Config.DisplayMode mode, boolean inFov) {
        return switch (mode) {
            case ALWAYS -> true;
            case TARGET -> inFov;
            case HIDDEN -> false;
        };
    }

    public static UUID toMinecraftUuid(UuidOuterClass.Uuid uuid) {
        return new UUID(uuid.getHigh64(), uuid.getLow64());
    }
    public static String extractName(String json) {
        try {
            JsonElement element = JsonParser.parseString(json);

            // hoplite
            if (element.isJsonPrimitive()) {
                return element.getAsString();
            }

            // apollo
            if (element.isJsonObject()) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.has("text")) {
                    return obj.get("text").getAsString();
                }
            }
        } catch (Exception ignored) {
            if (Objects.equals(Config.debug, "debug name")) System.out.print(json);
        }
        return "";
    }

}