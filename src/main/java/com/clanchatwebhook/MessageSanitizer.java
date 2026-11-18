package com.clanchatwebhook;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.ChatMessageType;

public class MessageSanitizer {
    private static final Pattern VALUE_TAG = Pattern.compile("<(?<name>[a-z_]+_\\d+)(=(?<value>[^>]*)|:(?<format>\\w+))?>");

    public static String sanitize(String message, ChatMessageType messageType)
    {
        String newMessage = message;
        newMessage = newMessage.replace((char)160, ' ');
        newMessage = newMessage.replace("<lt>", "<");
        newMessage = newMessage.replace("<gt>", ">");
        if (messageType == ChatMessageType.CLAN_MESSAGE) {
            newMessage = resolveVariables(newMessage);
        }

        return additionalCustomizations(newMessage);
    }

    private static String resolveVariables(String message)
    {
        Map<String, String> values = new HashMap<>();
        Matcher tag = VALUE_TAG.matcher(message);
        StringBuffer result = new StringBuffer();
        while (tag.find()) {
            String name = tag.group("name");
            String newValue = tag.group("value");
            String text;
            if (newValue != null) {
                values.put(name, newValue);
                text = "";
            } else if (values.containsKey(name)) {
                text = formatValue(values.get(name), tag.group("format"));
            } else {
                text = tag.group();
            }
            tag.appendReplacement(result, Matcher.quoteReplacement(text));
        }
        tag.appendTail(result);
        return result.toString();
    }

    private static String formatValue(String value, String format)
    {
        if ("spacer".equals(format) && value.matches("-?\\d+")) {
            return String.format(Locale.US, "%,d", new BigInteger(value));
        }
        return value;
    }

    private static String additionalCustomizations(String message)
    {
        String newMessage = message;
        newMessage = newMessage.replaceAll("\\<img=\\d+\\>", "");
        return newMessage;
    }
}
