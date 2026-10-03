package net.kztmc.mc.teamviewer;

import com.google.protobuf.Any;
import lunarclient.apollo.team.v1.Schema.ResetTeamMembersMessage;
import lunarclient.apollo.team.v1.Schema.UpdateTeamMembersMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApolloMessageDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger("teamviewer");

    public static void handle(Any any) {
        if (!Config.all) return;

        try {

            if (any.is(UpdateTeamMembersMessage.class)) {
                handleUpdate(any.unpack(UpdateTeamMembersMessage.class));
                return;
            }

            if (any.is(ResetTeamMembersMessage.class)) {
                handleReset();
            }

        } catch (Exception e) {
            LOGGER.debug("[Teamviewer] Ignored packet");
        }
    }

    private static void handleReset() {
        TeamData.clear();
        LOGGER.info("§6[Teamviewer] Team reset");
    }

    private static void handleUpdate(UpdateTeamMembersMessage msg) {
        TeamData.update(msg);
    }
}