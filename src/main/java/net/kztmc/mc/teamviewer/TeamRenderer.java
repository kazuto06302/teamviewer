package net.kztmc.mc.teamviewer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.vertex.PoseStack;
import lunarclient.apollo.common.v1.UuidOuterClass;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

import static net.kztmc.mc.teamviewer.Marker.*;

public class TeamRenderer {

    private static final Minecraft client = Minecraft.getInstance();

    public static void init(){
        LevelRenderEvents.AFTER_SOLID_FEATURES.register((ctx) -> {
            render(ctx.poseStack(), client.gameRenderer.getMainCamera(), client.getDeltaTracker().getGameTimeDeltaPartialTick(true));
        });
    }

    public static void render(PoseStack matrices, Camera camera, float t) {
        if (client.player == null) return;

        Vec3 camPos = camera.position();
        matrices.pushPose();
        if (Objects.equals(Config.debug, "debug render1")) System.out.println("render1");

        MultiBufferSource.BufferSource consumers = client.renderBuffers().bufferSource();

        TeamData.getMembers().values().forEach(m -> {
            // timeout
            TeamData.cleanupExpired();

            // world
            if (TeamData.selfApolloWorld == null || !TeamData.selfApolloWorld.equals(m.world)) return;

            if (Objects.equals(Config.debug, "debug render2")) System.out.println("render2");

            Vec3 pos = resolvePosition(m,t);
            Vec3 rel = pos.subtract(camPos);
            double dist = camPos.distanceTo(pos);

            UUID playeruuid = toMinecraftUuid(m.uuid);
            String rawname = m.name.getString();
            String name = extractName(rawname);

            if (Objects.equals(Config.debug, "debug render3"))System.out.println(name);

            matrices.pushPose();
            // render
            if (!playeruuid.equals(client.getUser().getProfileId())) {
                matrices.translate(rel.x, rel.y + Config.marker_y, rel.z);
                matrices.mulPose(camera.rotation());
                //1.21.8 matrices.multiply(client.getEntityRenderDispatcher().getRotation());
                if(Config.marker_display) {
                    matrices.pushPose();
                    drawMarker(matrices, consumers, m.color, dist);
                    matrices.popPose();
                }

                matrices.pushPose();
                drawText(matrices, name, dist, pos);
                matrices.popPose();
            }
            matrices.popPose();
        });
        consumers.endBatch();
        matrices.popPose();
    }

    //draw
    private static void drawMarker(PoseStack matrices, MultiBufferSource consumers, Color color, double dist) {
        if (dist < Config.marker_inv) return;

        float scale = Scale(dist);
        matrices.scale(scale, scale, scale);

        switch (Config.MARKER_SHAPE) {
            case INVERTEDTRIANGLE -> Marker.drawInvertedTriangle(matrices, consumers, color);
            case TRIANGLE         -> Marker.drawTriangle(matrices, consumers, color);
            case DIAMOND          -> Marker.drawDiamond(matrices, consumers, color);
            case SQUARE           -> Marker.drawSquare(matrices, consumers, color);
        }
    }


    private static void drawText(PoseStack matrices, String name, double dist, Vec3 pos) {
        if (dist < Config.text_inv) return;

        Minecraft client = Minecraft.getInstance();
        if (client.font == null) return;

        boolean inFov = isNearCrosshair(pos.add(0, 1, 0), Config.text_angle);

        boolean showName = shouldShow(Config.NAME_MODE, inFov);
        boolean showDist = shouldShow(Config.DIST_MODE, inFov);


        if (!showName && !showDist) return;

        matrices.pushPose();
        matrices.translate(0, 0, 0.05);

        float distscale = Scale(dist);
        float scale = (Config.text_size / 100) * distscale;

        matrices.scale(scale, -scale, scale);

        MultiBufferSource.BufferSource consumers = client.renderBuffers().bufferSource();

        int backgroundColor = 0;
        if (Config.background){
            backgroundColor = (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24 | 0x666666;
        }

        if (showDist) {
            String distText = "(" + (int) dist + "m)";
            float x = -client.font.width(distText) / 2f;
            client.font.drawInBatch(
                    distText,
                    x,
                    -Config.dist_y * 10,
                    Config.dist_color,
                    false,
                    matrices.last().pose(),
                    consumers,
                    Font.DisplayMode.SEE_THROUGH,
                    backgroundColor,
                    0xF000F0
            );
        }

        if (showName) {
            float x = -client.font.width(name) / 2f;
            client.font.drawInBatch(
                    name,
                    x,
                    -Config.name_y * 10,
                    Config.name_color,
                    false,
                    matrices.last().pose(),
                    consumers,
                    Font.DisplayMode.SEE_THROUGH,
                    backgroundColor,
                    0xF000F0
            );
        }

        matrices.popPose();
    }


    //interpolate
    private static Vec3 resolvePosition(TeamMemberData m, double tickDelta) {
        String apolloName = extractName(m.name.getString());

        // 1 short：Minecraft API からdisplaynameで検索
        // nickされているとUUIDが一致しないためdisplaynameで代替
        Player player = findPlayerByDisplayName(apolloName);

        if (player != null) {
            return new Vec3(
                    Mth.lerp(tickDelta, player.xOld, player.getX()),
                    Mth.lerp(tickDelta, player.yOld, player.getY()),
                    Mth.lerp(tickDelta, player.zOld, player.getZ())
            );
        }

        // 2 long：Apollo [time interpolate]
        long now = System.currentTimeMillis();
        double x = interpolate(m.lastX, m.x, now, m.lastUpdate, m.lastLastUpdate);
        double y = interpolate(m.lastY, m.y, now, m.lastUpdate, m.lastLastUpdate);
        double z = interpolate(m.lastZ, m.z, now, m.lastUpdate, m.lastLastUpdate);

        return new Vec3(x, y, z);
    }

    private static Player findPlayerByDisplayName(String apolloName) {
        if (client.level == null || apolloName.isEmpty()) return null;

        for (Player p : client.level.players()) {
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

        return Mth.lerp(t, last, current);
    }

    // until
    public static float Scale(double distance) {
        if (distance < 10.f) return 1.f;
        return (float) (distance / 10f);
    }



    private static boolean isNearCrosshair(Vec3 targetWorldPos, float maxAngleDegrees) {
        if (client.player == null) return false;
        Vec3 eyePos = client.player.getEyePosition();
        Vec3 lookVec = client.player.getViewVector(1.0f);
        Vec3 toTarget = targetWorldPos.subtract(eyePos).normalize();

        double dot = lookVec.dot(toTarget);
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
