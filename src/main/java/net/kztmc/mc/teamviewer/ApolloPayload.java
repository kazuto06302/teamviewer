package net.kztmc.mc.teamviewer;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ApolloPayload(byte[] data) implements CustomPayload {

    public static final Id<ApolloPayload> ID = new Id<>(Identifier.of("lunar", "apollo"));

    public static final PacketCodec<RegistryByteBuf, ApolloPayload> CODEC =
            PacketCodec.of(ApolloPayload::write, ApolloPayload::read);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    private static void write(ApolloPayload payload, RegistryByteBuf buf) {
        buf.writeByteArray(payload.data);
    }

    private static ApolloPayload read(RegistryByteBuf buf) {
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return new ApolloPayload(data);
    }
}