package net.kztmc.mc.common;

import com.google.protobuf.Any;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.kztmc.mc.teamviewer.ApolloPayload;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ApolloCommonReceiver {

    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);
    private static final List<Consumer<Any>> LISTENERS = new CopyOnWriteArrayList<>();

    public static void init() {
        if (REGISTERED.compareAndSet(false, true)) {

            ClientPlayNetworking.registerGlobalReceiver(
                    ApolloPayload.ID,
                    (payload, ctx) -> ctx.client().execute(() -> {
                        try {
                            Any any = Any.parseFrom(payload.data());

                            for (Consumer<Any> listener : LISTENERS) {
                                try {
                                    listener.accept(any);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
            );
        }
    }

    public static void addListener(Consumer<Any> listener) {
        init();
        LISTENERS.add(listener);
    }
}