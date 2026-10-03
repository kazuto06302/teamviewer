package net.kztmc.mc.teamviewer;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static net.kztmc.mc.teamviewer.TeamRenderer.extractName;

public class TvCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("tv")

                            // /tv → メニューを開く
                            .executes(ctx -> {
                                MinecraftClient client = MinecraftClient.getInstance();
                                client.execute(() -> {
                                    // ModMenuIntegration 経由で YACL 設定画面を開く
                                    client.setScreen(
                                            new ModMenuIntegration().getModConfigScreenFactory().create(client.currentScreen)
                                    );
                                });
                                return 1;
                            })

                            // /tv pmc <プレイヤー名> → 位置をチャットに出力
                            .then(ClientCommandManager.literal("pmc")
                                    .then(ClientCommandManager.argument("player", StringArgumentType.word())
                                            .suggests((ctx, builder) -> {
                                                // オートコンプリート：チームメンバー名を補完
                                                TeamData.getMembers().values().forEach(m -> {
                                                    String name = extractName(m.name.getString());
                                                    if (!name.isEmpty()) builder.suggest(name);
                                                });
                                                return builder.buildFuture();
                                            })
                                            .executes(ctx -> {
                                                String target = StringArgumentType.getString(ctx, "player");
                                                MinecraftClient client = MinecraftClient.getInstance();

                                                // チームメンバーから名前検索
                                                TeamMemberData found = TeamData.getMembers().values().stream()
                                                        .filter(m -> extractName(m.name.getString()).equalsIgnoreCase(target))
                                                        .findFirst()
                                                        .orElse(null);

                                                if (found == null) {
                                                    client.player.sendMessage(
                                                            Text.literal("§cPlayer §f" + target +  "§c is not on the team"),
                                                            false
                                                    );
                                                } else {
                                                    String displayName = extractName(found.name.getString());
                                                    String msg = String.format(
                                                            "§f%s§7's position: §eX=%.1f Y=%.1f Z=%.1f W=%s",
                                                            displayName, found.x, found.y, found.z, found.world
                                                    );
                                                    client.player.sendMessage(Text.literal(msg), false);
                                                }
                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }
}