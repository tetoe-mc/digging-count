package net.nocpiun.diggingcount.board;

import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.nocpiun.diggingcount.log.Message;

public class Board {
    public final static String BOARD_ID = "digging-count";

    private final Scoreboard scoreboard;
    private Objective objective;

    public Board(MinecraftServer server) {
        scoreboard = server.getScoreboard();

        objective = scoreboard.getObjective(BOARD_ID);
        if(objective == null) {
            objective = scoreboard.addObjective(
                    BOARD_ID,
                    ObjectiveCriteria.DUMMY,
                    Component.literal(""),
                    ObjectiveCriteria.RenderType.INTEGER,
                    true,
                    null
            );
        }
    }

    public int getCount(Player player) {
        ScoreAccess access = scoreboard.getOrCreatePlayerScore(player, objective);
        return access.get();
    }

    public void setCount(Player player, int count) {
        ScoreAccess access = scoreboard.getOrCreatePlayerScore(player, objective);
        access.set(count);
    }

    public void setVisible(DisplaySlot slot, boolean visible) {
        if(visible) {
            scoreboard.setDisplayObjective(slot, objective);
        } else {
            scoreboard.setDisplayObjective(slot, null);
        }
    }

    public void setTitle(String title) {
        objective.setDisplayName(Message.colorize(title));
    }

    public void removePlayer(String player) {
        for(ScoreHolder holder : scoreboard.getTrackedPlayers()) {
            if(holder.getScoreboardName().equals(player)) {
                scoreboard.resetSinglePlayerScore(holder, null);
            }
        }
    }

    public static DisplaySlot slotToEnum(String slot) {
        switch(slot) {
            case "list": return DisplaySlot.LIST;
            case "sidebar": return DisplaySlot.SIDEBAR;
            case "below_name": return DisplaySlot.BELOW_NAME;
        }

        return DisplaySlot.valueOf(slot);
    }
}
