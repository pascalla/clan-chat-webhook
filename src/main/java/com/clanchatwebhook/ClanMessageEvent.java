package com.clanchatwebhook;

import com.google.common.base.Strings;
import lombok.Data;

import java.text.MessageFormat;

@Data
public class ClanMessageEvent {
    private String content;
    private String author;
    private Boolean broadcast;
    private Integer timestamp;

    public ClanMessageEvent(String author, String content, int timestamp)
    {
        this.content = content;
        this.timestamp = timestamp;
        if (Strings.isNullOrEmpty(author))
        {
            this.broadcast = true;
        }
        else
        {
            this.broadcast = false;
            this.author = author;
        }
    }


    public String toDiscordContentString()
    {
        if (broadcast)
        {
            return this.content;
        }

        return MessageFormat.format("**{0}**: {1}", this.author, this.content);
    }
}
