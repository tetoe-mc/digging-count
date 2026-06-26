package net.nocpiun.diggingcount.log;

import net.minecraft.network.chat.Component;

public class Message {
    public static Component create(String message) {
        return colorize("[&6&lDiggin Count&r] "+ message);
    }

    public static Component colorize(String message) {
        return Component.literal(message.replaceAll("&", "§"));
    }
}
