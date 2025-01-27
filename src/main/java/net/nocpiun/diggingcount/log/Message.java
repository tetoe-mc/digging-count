package net.nocpiun.diggingcount.log;

import net.minecraft.text.Text;

public class Message {
    public static Text create(String message) {
        return colorize("[&6&lDiggin Count&r] "+ message);
    }

    public static Text colorize(String message) {
        return Text.of(message.replaceAll("&", "§"));
    }
}
