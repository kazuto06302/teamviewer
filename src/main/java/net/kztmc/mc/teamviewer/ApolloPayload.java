package net.kztmc.mc.teamviewer;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ApolloPayload(byte[] data) implements CustomPacketPayload {

    public static final Type<ApolloPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("lunar", "apollo"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ApolloPayload> CODEC =
            StreamCodec.of(ApolloPayload::write, ApolloPayload::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void write(RegistryFriendlyByteBuf buf, ApolloPayload payload) {
        buf.writeBytes(payload.data);
    }

    private static ApolloPayload read(RegistryFriendlyByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new ApolloPayload(data);
    }
}