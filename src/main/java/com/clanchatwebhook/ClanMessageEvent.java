package com.clanchatwebhook;

import lombok.Data;

@Data
public class ClanMessageEvent {
    private String content;
    private String author;
    private ClanChatWebhookPlugin.AccountType accountType;
    private ClanChatWebhookPlugin.SystemMessageType systemMessageType;
    private Integer timestamp;

    public ClanMessageEvent(String author, String content, ClanChatWebhookPlugin.AccountType accountType, ClanChatWebhookPlugin.SystemMessageType systemMessageType, int timestamp)
    {
        this.author = author;
        this.content = content;
        this.accountType = accountType;
        this.systemMessageType = systemMessageType;
        this.timestamp = timestamp;

    }
}
