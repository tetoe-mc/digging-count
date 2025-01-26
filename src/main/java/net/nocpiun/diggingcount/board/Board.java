package net.nocpiun.diggingcount.board;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.nocpiun.diggingcount.log.Message;

public class Board {
    public final static String BOARD_ID = "digging-count";

    private final Scoreboard scoreboard;
    private ScoreboardObjective objective;

    public Board(MinecraftServer server) {
        scoreboard = server.getScoreboard();

        objective = scoreboard.getNullableObjective(BOARD_ID);
        if(objective == null) {
            objective = scoreboard.addObjective(
                    BOARD_ID,
                    ScoreboardCriterion.DUMMY,
                    Text.of(""),
                    ScoreboardCriterion.RenderType.INTEGER,
                    true,
                    null
            );
        }
    }

    public int getCount(PlayerEntity player) {
        ScoreAccess access = scoreboard.getOrCreateScore(player, objective);
        return access.getScore();
    }

    public void setCount(PlayerEntity player, int count) {
        ScoreAccess access = scoreboard.getOrCreateScore(player, objective);
        access.setScore(count);
    }

    public void setVisible(boolean visible) {
        if(visible) {
            scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective);
        } else {
            scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, null);
        }
    }

    public void setTitle(String title) {
        objective.setDisplayName(Message.colorize(title));
    }
}
