package com.clanchatwebhook;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageSanitizer {
    public static String sanitize(String message)
    {
        String newMessage = message;
        newMessage = newMessage.replace((char)160, ' ');
        newMessage = newMessage.replace("<lt>", "<");
        newMessage = newMessage.replace("<gt>", ">");
        newMessage = sanitizeKeyValue(newMessage);

        return additionalCustomizations(newMessage);
    }

    //Parses key/value tags and inserts value to end of message
    //e.g. Quest messages: <str_quest_name_0=The Lost Tribe>Username has completed a quest: <str_quest_name_0>
    //Turns into: Username has completed a quest: The Lost Tribe
    private static String sanitizeKeyValue(String message){
        Pattern STRING_TEMPLATE_PATTERN = Pattern.compile("<(?<name>str_\\w+)=(?<value>[^>]*)>");
        Matcher matcher = STRING_TEMPLATE_PATTERN.matcher(message);

        if (matcher.find()) {
            String tagName = matcher.group(1);     // e.g., "str_quest_name_0"
            String tagValue = matcher.group(2);    // e.g., "The Lost Tribe"

            //Remove first tag
            message = matcher.replaceFirst(" ");

            //Replace second tag with value
            message = message.replace("<" + tagName + ">", tagValue);
        }
        return message;
    }
    private static String additionalCustomizations(String message)
    {
        String newMessage = message;
        newMessage = newMessage.replaceAll("\\<img=\\d+\\>", "");
        return newMessage;
    }
}
