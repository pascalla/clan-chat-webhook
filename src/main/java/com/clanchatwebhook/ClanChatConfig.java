package com.clanchatwebhook;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("clanchat")
public interface ClanChatConfig extends Config
{
	@ConfigItem(
			keyName = "webhook",
			name = "Webhook URL",
			description = "The URL to send the Webhook data too",
			position = 0
	)
	default String webhook()
	{
		return "";
	}

	@ConfigItem(
			keyName = "discord",
			name = "Discord Webhook",
			description = "Is the Webhook a Discord Channel Webhook?",
			position = 1
	)
	default boolean discordWebhook()
	{
		return false;
	}

	@ConfigItem(
			keyName = "broadcasts",
			name = "Send My Broadcasts",
			description = "Send my Broadcasts, eg Level up and Collection Log notifications ",
			position = 2
	)
	default boolean sendBroadcasts()
	{
		return false;
	}

	@ConfigItem(
			keyName = "chat",
			name = "Send My Chat Messages",
			description = "Send My Chat Messages",
			position = 3
	)
	default boolean sendChat()
	{
		return true;
	}

	@ConfigItem(
			keyName = "all",
			name = "Send Others",
			description = "Send Chats of Others",
			position = 3
	)
	default boolean sendAll()
	{
		return false;
	}
}
