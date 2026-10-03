package net.kztmc.mc.teamviewer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.kztmc.mc.common.ApolloCommonReceiver;

public class ApolloTeamTrackerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TeamRenderer.init();
        System.out.println("[Teamviewer] Enabled mod!");
        Config.load();

        TvCommand.register();

        // パケットタイプ登録
        try {
            PayloadTypeRegistry.playS2C().register(
                    ApolloPayload.ID,
                    ApolloPayload.CODEC
            );
        } catch (IllegalArgumentException ignored) {}

        ApolloCommonReceiver.addListener(ApolloMessageDispatcher::handle);
    }
}