package net.kztmc.mc.teamviewer;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;

import java.awt.*;


public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("teamviewer.config.main.title"))
                .save(Config::save)
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("teamviewer.config.marker.name"))
                        .tooltip(Text.translatable("teamviewer.config.marker.tooltip"))

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("teamviewer.config.marker.enable.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.marker.enable.d")))
                                .binding(true, () -> Config.marker_display, newVal -> Config.marker_display = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Config.MarkerShape>createBuilder()
                                .name(Text.translatable("teamviewer.config.marker.shape.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.marker.shape.d")))
                                .binding(Config.MarkerShape.INVERTEDTRIANGLE, () -> Config.MARKER_SHAPE, newVal -> Config.MARKER_SHAPE = newVal)
                                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(Config.MarkerShape.class))
                                .build())


                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("teamviewer.config.marker.height.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.marker.height.d")))
                                .binding(2.5f, () -> Config.marker_y, newVal ->Config.marker_y = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .range(0f, 5.f)
                                        .step(0.1f))
                                .build())

                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("teamviewer.config.marker.marker_size.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.marker.marker_size.d")))
                                .binding(0.30f, () -> Config.marker_size, newVal ->Config.marker_size = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .range(0.10f, 1.00f)
                                        .step(0.05f)
                                        .formatValue(val -> Text.literal(String.format("%.2f", val)))
                                )
                                .build())

                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("teamviewer.config.marker.marker_inv.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.marker.marker_inv.d")))
                                .binding(3f, () -> Config.marker_inv, newVal ->Config.marker_inv = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .range(0f, 16f)
                                        .step(1f))
                                .build())
                        .build())




                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("teamviewer.config.text.name"))
                        .tooltip(Text.translatable("teamviewer.config.text.tooltip"))

                        .option(Option.<Config.DisplayMode>createBuilder() //dist
                                .name(Text.translatable("teamviewer.config.text.mode.dist.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.text.mode.d")))
                                .binding(Config.DisplayMode.ALWAYS, () -> Config.DIST_MODE, newVal -> Config.DIST_MODE = newVal)
                                .controller(opt -> EnumControllerBuilder.create(opt)
                                        .enumClass(Config.DisplayMode.class)
                                )
                                .build())

                        .option(Option.<Config.DisplayMode>createBuilder() //name
                                .name(Text.translatable("teamviewer.config.text.mode.name.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.text.mode.d")))
                                .binding(Config.DisplayMode.TARGET, () -> Config.NAME_MODE, newVal -> Config.NAME_MODE = newVal)
                                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(Config.DisplayMode.class))
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("teamviewer.config.text.background.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.text.background.d")))
                                .binding(true, () -> Config.background, newVal -> Config.background = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())


                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("teamviewer.config.text.text_size.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.text.text_size.d")))
                                .binding(2.5f, () -> Config.text_size, newVal ->Config.text_size = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .range(0.1f, 5.0f)
                                        .step(0.1f))
                                .build())

                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("teamviewer.config.text.text_inv.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.text.text_inv.d")))
                                .binding(10f, () -> Config.text_inv, newVal ->Config.text_inv = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .range(0f, 32f)
                                        .step(1f))
                                .build())

                        .option(Option.<Float>createBuilder()
                                .name(Text.translatable("teamviewer.config.text.text_angle.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.text.text_angle.d")))
                                .binding(8f, () -> Config.text_angle, newVal ->Config.text_angle = newVal)
                                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                        .range(0f, 16f)
                                        .step(1f))
                                .build())


                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("teamviewer.config.text.g.dist.name"))
                                .option(Option.<Color>createBuilder()
                                        .name(Text.translatable("teamviewer.config.text.g.dist.color.name"))
                                        .description(OptionDescription.of(Text.translatable("teamviewer.config.text.g.dist.color.d")))
                                        .binding(new Color(0xFFFFFFFF), () -> new Color(Config.dist_color), newVal -> Config.dist_color = newVal.getRGB())
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.translatable("teamviewer.config.text.g.dist.height.name"))
                                        .description(OptionDescription.of(Text.translatable("teamviewer.config.text.g.dist.height.d")))
                                        .binding(2f, () -> Config.dist_y, newVal ->Config.dist_y = newVal)
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .range(-10f, 10f)
                                                .step(0.1f))
                                        .build())
                                .build())



                        .group(OptionGroup.createBuilder()
                                .name(Text.translatable("teamviewer.config.text.g.name.name"))
                                .option(Option.<Color>createBuilder()
                                        .name(Text.translatable("teamviewer.config.text.g.name.color.name"))
                                        .description(OptionDescription.of(Text.translatable("teamviewer.config.text.g.name.color.d")))
                                        .binding(new Color(0xFFFFFFFF), () -> new Color(Config.name_color), newVal -> Config.name_color = newVal.getRGB())
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.translatable("teamviewer.config.text.g.name.height.name"))
                                        .description(OptionDescription.of(Text.translatable("teamviewer.config.text.g.name.height.d")))
                                        .binding(3f, () -> Config.name_y, newVal ->Config.name_y = newVal)
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .range(-10f, 10f)
                                                .step(0.1f))
                                        .build())
                                .build())
                        .build())



                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("teamviewer.config.option.name"))
                        .tooltip(Text.translatable("teamviewer.config.option.tooltip"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("teamviewer.config.option.all.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.option.all.d")))
                                .binding(true, () -> Config.all, newVal -> Config.all = newVal)
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<String>createBuilder()
                                .name(Text.translatable("teamviewer.config.option.debug.name"))
                                .description(OptionDescription.of(Text.translatable("teamviewer.config.option.debug.d")))
                                .binding("Do not change the content", () -> Config.debug, newVal -> Config.debug = newVal)
                                .controller(StringControllerBuilder::create)
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }
}
