package net.nocpiun.diggingcount.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.nocpiun.diggingcount.DiggingCountPlugin;
import net.nocpiun.diggingcount.board.Board;
import net.nocpiun.diggingcount.log.Log;
import net.nocpiun.diggingcount.log.Message;

import java.util.concurrent.CompletableFuture;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DiggingCommand implements Command<ServerCommandSource> {
    public final static String cmd = "digging";

    private final DiggingCountPlugin plugin;

    public DiggingCommand(CommandDispatcher<ServerCommandSource> dispatcher, DiggingCountPlugin plugin) {
        final RootCommandNode<ServerCommandSource> root = dispatcher.getRoot();

        final LiteralCommandNode<ServerCommandSource> command = literal(cmd)
                .executes(this)
                .build();

        final ArgumentCommandNode<ServerCommandSource, String> args = argument("operation", string())
                .suggests((context, builder) -> {
                    builder = builder.createOffset(builder.getInput().lastIndexOf(" ") + 1);
                    String[] inputs = context.getInput().split(" ");

                    if(inputs.length == 1 || inputs.length == 2) {
                        builder.suggest("enable");
                        builder.suggest("disable");
                        builder.suggest("title");

                        return builder.buildFuture();
                    }

                    return new CompletableFuture<>();
                })
                .then(
                        argument("args", greedyString())
                                .suggests((context, builder) -> {
                                    builder = builder.createOffset(builder.getInput().lastIndexOf(" ") + 1);
                                    String[] inputs = context.getInput().split(" ");

                                    if(inputs[1].equals("enable") || inputs[1].equals("disable")) {
                                        builder.suggest("list");
                                        builder.suggest("sidebar");
                                        builder.suggest("below_name");
                                    }

                                    return builder.buildFuture();
                                })
                                .executes(this)
                                .build()
                )
                .executes(this)
                .build();

        command.addChild(args);
        root.addChild(command);

        this.plugin = plugin;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        String[] inputs = ctx.getInput().split(" ");

        if(!source.hasPermissionLevel(4)) {
            return 0;
        }

        if(inputs.length < 3) {
            return 0;
        }

        switch(inputs[1].toLowerCase()) {
            case "enable":
                plugin.setEnabled(Board.slotToEnum(inputs[2]), true);
                source.sendMessage(Message.create("§aSuccessfully enabled the scoreboard."));
                break;
            case "disable":
                plugin.setEnabled(Board.slotToEnum(inputs[2]), false);
                source.sendMessage(Message.create("§aSuccessfully disabled the scoreboard."));
                break;
            case "title":
                String text = "";

                for(int i = 2; i < inputs.length; i++) {
                    text += inputs[i];
                    if(i != inputs.length - 1) {
                        text += " ";
                    }
                }

                plugin.setTitle(text);
                Log.info("The scoreboard title is set to \""+ text +"\".");
                source.sendMessage(Message.create("§aSuccessfully set the scoreboard title."));
                break;
        }

        return 1;
    }
}
