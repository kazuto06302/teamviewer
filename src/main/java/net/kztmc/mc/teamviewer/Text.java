package net.kztmc.mc.teamviewer;

import net.minecraft.network.chat.Component;

/**
 * Small compatibility facade for the former Yarn text factory names.
 * The mod's public-facing text values are Mojang-mapped {@link Component}s.
 */
final class Text {
    private Text() {
    }

    static Component literal(String value) {
        return Component.literal(value);
    }

    static Component translatable(String key) {
        return Component.translatable(key);
    }
}
