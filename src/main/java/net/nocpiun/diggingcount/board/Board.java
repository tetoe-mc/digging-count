package net.nocpiun.diggingcount.board;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public class Board {
    public final static String BOARD_ID = "digging-count";

    private Scoreboard scoreboard;
    private ScoreboardObjective objective;

    public Board(MinecraftServer server) {
        scoreboard = server.getScoreboard();

        objective = scoreboard.getNullableObjective(BOARD_ID);
        if(objective == null) {
            objective = scoreboard.addObjective(
                    BOARD_ID,
                    ScoreboardCriterion.DUMMY,
                    Text.of("§7§l挖掘榜"),
                    ScoreboardCriterion.RenderType.INTEGER,
                    true,
                    null
            );
        }

        scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective);
    }

    public int getCount(PlayerEntity player) {
        ScoreAccess access = scoreboard.getOrCreateScore(player, objective);
        return access.getScore();
    }

    public void setCount(PlayerEntity player, int count) {
        ScoreAccess access = scoreboard.getOrCreateScore(player, objective);
        access.setScore(count);
    }
}
