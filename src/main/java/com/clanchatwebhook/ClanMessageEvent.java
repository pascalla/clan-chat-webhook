package com.clanchatwebhook;

import lombok.Data;
import net.runelite.api.clan.ClanTitle;

@Data
public class ClanMessageEvent {
    private String content;
    private String author;
    private ClanChatWebhookPlugin.AccountType accountType;
    private ClanChatWebhookPlugin.SystemMessageType systemMessageType;
    private Integer timestamp;
    private String clanTitle;

    public ClanMessageEvent(String author, String content, ClanChatWebhookPlugin.AccountType accountType, ClanChatWebhookPlugin.SystemMessageType systemMessageType, String clanTitle, int timestamp)
    {
        this.author = author;
        this.content = content;
        this.accountType = accountType;
        this.systemMessageType = systemMessageType;
        this.clanTitle = clanTitle;
        this.timestamp = timestamp;
    }
}
