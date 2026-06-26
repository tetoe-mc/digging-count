package net.nocpiun.diggingcount;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.scores.DisplaySlot;
import net.nocpiun.diggingcount.board.Board;
import net.nocpiun.diggingcount.command.DiggingCommand;
import net.nocpiun.diggingcount.log.Log;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DiggingCountPlugin {
    public final static HashMap<String, Object> defaultConfig = new HashMap<>();

    private MinecraftServer server;

    private File configFile;
    private File dataFile;
    private HashMap<String, Object> config;
    private HashMap<String, Integer> counterMap;
    private Board board;

    public DiggingCountPlugin() {
        defaultConfig.put(DisplaySlot.LIST.name(), false);
        defaultConfig.put(DisplaySlot.SIDEBAR.name(), true);
        defaultConfig.put(DisplaySlot.BELOW_NAME.name(), false);
        defaultConfig.put("title", "§7§lDigging Count");

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStop);
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoinServer);
        PlayerBlockBreakEvents.AFTER.register(this::onPlayerBreakBlock);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> new DiggingCommand(dispatcher, this));
    }

    private void onServerStart(MinecraftServer server) {
        this.server = server;

        // Initialize the config
        configFile = new File(FabricLoader.getInstance().getConfigDir().toString(), "digging-count-config.dat");
        if(configFile.exists()) {
            config = loadFile(configFile);

            defaultConfig.forEach((key, value) -> {
                if(!config.containsKey(key)) {
                    config.put(key, value);
                }
            });
        } else { // default config
            config = new HashMap<>(defaultConfig);
        }
        saveConfig();

        // Initialize the data
        dataFile = new File(FabricLoader.getInstance().getConfigDir().toString(), "digging-count.dat");
        if(dataFile.exists()) {
            counterMap = loadFile(dataFile);
        } else {
            counterMap = new HashMap<>();
        }
        saveData();

        // Initialize the scoreboard
        board = new Board(this.server);
        board.setVisible(DisplaySlot.LIST, getEnabled(DisplaySlot.LIST));
        board.setVisible(DisplaySlot.SIDEBAR, getEnabled(DisplaySlot.SIDEBAR));
        board.setVisible(DisplaySlot.BELOW_NAME, getEnabled(DisplaySlot.BELOW_NAME));
        board.setTitle(getTitle());
    }

    private void onServerStop(MinecraftServer server) {
        this.server = null;
    }

    private void onPlayerJoinServer(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        ServerPlayer player = handler.getPlayer();

        AtomicInteger sum = new AtomicInteger();
        Stats.BLOCK_MINED.forEach((stat) -> {
            sum.addAndGet(player.getStats().getValue(stat));
        });

        board.setCount(player, sum.get());
        counterMap.put(player.getName().getString(), sum.get());
        saveData();
    }

    private void onPlayerBreakBlock(Level world, Player player, BlockPos pos, BlockState state, BlockEntity entity) {
        if(player.isCreative()) return;

        int currentCount = board.getCount(player) + 1;
        board.setCount(player, currentCount);
        counterMap.put(player.getName().getString(), currentCount);
        saveData();
    }

    @SuppressWarnings("unchecked")
    public <T> T loadFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            T result = (T) ois.readObject();
            ois.close();

            return result;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T> void saveFile(File file, T obj) {
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(obj);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getEnabled(DisplaySlot slot) {
        if(
                slot == DisplaySlot.LIST
                || slot == DisplaySlot.SIDEBAR
                || slot == DisplaySlot.BELOW_NAME
        ) {
            return (boolean) config.get(slot.name());
        }

        return false;
    }

    public void setEnabled(DisplaySlot slot, boolean enabled) {
        if(
                slot == DisplaySlot.LIST
                || slot == DisplaySlot.SIDEBAR
                || slot == DisplaySlot.BELOW_NAME
        ) {
            config.put(slot.name(), enabled);
            board.setVisible(slot, enabled);
            saveConfig();
        }
    }

    public String getTitle() {
        return (String) config.get("title");
    }

    public void setTitle(String title) {
        config.put("title", title);
        board.setTitle(title);
        saveConfig();
    }


    public void removePlayer(String player) {
        board.removePlayer(player);
    }

    private void saveConfig() {
        saveFile(configFile, config);
    }

    private void saveData() {
        saveFile(dataFile, counterMap);
    }
}
