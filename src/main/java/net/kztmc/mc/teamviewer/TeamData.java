package net.kztmc.mc.teamviewer;

import lunarclient.apollo.common.v1.UuidOuterClass;
import lunarclient.apollo.team.v1.Schema.UpdateTeamMembersMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.awt.Color;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.kztmc.mc.teamviewer.TeamRenderer.toMinecraftUuid;

public class TeamData {
private static final Map<UuidOuterClass.Uuid, TeamMemberData> MEMBERS = new ConcurrentHashMap<>();

    public static final long TIMEOUT_MS = 5000;
    public static volatile String selfApolloWorld;

    public static Map<UuidOuterClass.Uuid, TeamMemberData> getMembers() {
        return MEMBERS;
    }

    public static void clear() {
        MEMBERS.clear();
    }

    public static void update(UpdateTeamMembersMessage msg) {
        long now = System.currentTimeMillis();

        for (var m : msg.getMembersList()) {
            var loc = m.getLocation();
            Color color = new Color(m.getMarkerColor().getColor());
            String world = loc.getWorld();
            Component name = Component.literal(m.getAdventureJsonPlayerName());

            if (toMinecraftUuid(m.getPlayerUuid()).equals(Minecraft.getInstance().player.getGameProfile().id())) {
                selfApolloWorld = world;
            }

            MEMBERS.compute(m.getPlayerUuid(), (uuid, old) -> {

                if (old == null) {
                    return new TeamMemberData(
                            uuid,
                            name,
                            new Vec3(loc.getX(), loc.getY(), loc.getZ()),
                            color,
                            world,
                            now
                    );
                }

                old.lastX = old.x;
                old.lastY = old.y;
                old.lastZ = old.z;

                old.x = loc.getX();
                old.y = loc.getY();
                old.z = loc.getZ();

                old.world = world;
                old.name = name;
                old.color = color;

                old.lastLastUpdate = old.lastUpdate;
                old.lastUpdate = now;

                return old;
            });
        }
    }

    // timeout
    public static void cleanupExpired() {
        long now = System.currentTimeMillis();
        MEMBERS.entrySet().removeIf(e ->
                now - e.getValue().lastUpdate > TIMEOUT_MS
        );
    }


}
