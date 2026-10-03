package net.kztmc.mc.teamviewer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.NameableEnum;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger("teamviewer");

    //config
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("teamviewer.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    //marker
    public static boolean marker_display = true;
    public enum MarkerShape implements NameableEnum {
        INVERTEDTRIANGLE,
        TRIANGLE,
        DIAMOND,
        SQUARE;

        @Override
        public Text getDisplayName() {
            return Text.translatable("teamviewer.enum.markershape." + name().toLowerCase());
        }
    }
    public static MarkerShape MARKER_SHAPE = MarkerShape.INVERTEDTRIANGLE;

    public static float marker_size = 0.3f;
    public static float marker_inv = 3.f;
    public static float marker_y = 2.5f;

    //text
    public static DisplayMode DIST_MODE = DisplayMode.ALWAYS;
    public static DisplayMode NAME_MODE = DisplayMode.TARGET;
    public enum DisplayMode implements NameableEnum {
        ALWAYS,      // 常時表示
        TARGET,      // 範囲
        HIDDEN;
        // 非表示
        @Override
        public Text getDisplayName() {
            return Text.translatable("teamviewer.enum.displaymode." + name().toLowerCase());
        }
    }

    public static boolean background = true;

    public static float text_size = 2.5f;
    public static float text_inv = 10f;
    public static float text_angle = 8f;

    //text.dist
    public static int dist_color = 0xFFFFFFFF;
    public static float dist_y = 2f;

    //text.name
    public static int name_color = 0xFFFFFFFF;
    public static float name_y = 3f;


    public static boolean all = true;

    //debug
    public static String debug = "Do not change the content";


    //config
    public static void save() {
        try {
            Files.writeString(PATH, GSON.toJson(new ConfigData()));
        } catch (IOException e) {
            LOGGER.error("[Teamviewer] Failed to save config!", e);
        }
    }
    public static void load() {
        if (!Files.exists(PATH)) {
            save();
            return;
        }
        try {
            ConfigData data = GSON.fromJson(Files.readString(PATH), ConfigData.class);
            marker_display = data.marker_display;
            MARKER_SHAPE = data.MARKER_SHAPE;
            marker_size = data.marker_size;
            marker_inv = data.marker_inv;
            marker_y = data.marker_y;
            DIST_MODE = data.DIST_MODE;
            NAME_MODE = data.NAME_MODE;
            text_size = data.text_size;
            text_inv = data.text_inv;
            text_angle = data.text_angle;
            dist_color = data.dist_color;
            dist_y = data.dist_y;
            name_color = data.name_color;
            name_y = data.name_y;
            all = data.all;
            debug = data.debug;
        } catch (IOException e) {
            LOGGER.error("[Teamviewer] Failed to load config!", e);
        }
    }

    // for json
    private static class ConfigData {
        boolean marker_display = Config.marker_display;
        MarkerShape MARKER_SHAPE = Config.MARKER_SHAPE;
        float marker_size = Config.marker_size;
        float marker_inv = Config.marker_inv;
        float marker_y = Config.marker_y;
        DisplayMode DIST_MODE = Config.DIST_MODE;
        DisplayMode NAME_MODE = Config.NAME_MODE;
        float text_size = Config.text_size;
        float text_inv = Config.text_inv;
        float text_angle = Config.text_angle;
        int dist_color = Config.dist_color;
        float dist_y = Config.dist_y;
        int name_color = Config.name_color;
        float name_y = Config.name_y;
        boolean all = Config.all;
        String debug = Config.debug;
    }
}
