package net.kztmc.mc.teamviewer;

import lunarclient.apollo.common.v1.UuidOuterClass;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import java.awt.Color;

public class TeamMemberData {
    public final UuidOuterClass.Uuid uuid;
    public Text name;
    public Color color;
    public String world;

    // current pos
    public double x, y, z;

    // last pos
    public double lastX, lastY, lastZ;

    // update time
    public long lastUpdate;
    public long lastLastUpdate;

    public TeamMemberData(UuidOuterClass.Uuid uuid, Text name, Vec3d pos, Color color, String world, long now) {
        this.uuid = uuid;
        this.name = name;
        this.color = color;
        this.world = world;

        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;

        this.lastX = pos.x;
        this.lastY = pos.y;
        this.lastZ = pos.z;

        this.lastUpdate = now;
        this.lastLastUpdate = 0;
    }
}