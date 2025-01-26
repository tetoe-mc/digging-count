package net.nocpiun.diggingcount;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.nocpiun.diggingcount.board.Board;
import net.nocpiun.diggingcount.log.Log;

import java.io.*;
import java.util.HashMap;

public class DiggingCountPlugin {
    private MinecraftServer server;

    private File configFile;
    private HashMap<String, Integer> counterMap;
    private Board board;

    public DiggingCountPlugin() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStop);
        PlayerBlockBreakEvents.AFTER.register(this::onPlayerBreakBlock);
    }

    private void onServerStart(MinecraftServer server) {
        this.server = server;

        // Initialize the config
        configFile = new File(FabricLoader.getInstance().getConfigDir().toString(), "digging-count.dat");
        if(configFile.exists()) {
            loadConfig();
        }
        saveConfig();

        // Initialize the scoreboard
        board = new Board(server);
    }

    private void onServerStop(MinecraftServer server) {
        this.server = null;
    }

    private void onPlayerBreakBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity entity) {
        board.setCount(player, board.getCount(player) + 1);
    }

    @SuppressWarnings("unchecked")
    public void loadConfig() {
        try {
            FileInputStream fis = new FileInputStream(configFile);
            ObjectInputStream ois = new ObjectInputStream(fis);

            counterMap = (HashMap<String, Integer>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Log.info("Config is loaded.");
    }

    public void saveConfig() {
        try {
            configFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(configFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(counterMap);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.info("Config is saved.");
    }
}
