package com.clanchatwebhook;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("clanchatwebhook")
public interface ClanChatWebhookConfig extends Config
{

	@ConfigItem(
			keyName = "secret",
			name = "Secret Key",
			description = "The secret key for your clan.",
			position = 0
	)
	default String secretKey()
	{
		return "";
	}

	@ConfigItem(
			keyName = "webhook_endpoint",
			name = "Webhook Endpoint",
			description = "The endpoint for your webhook",
			position = 1
	)
	default String webhookEndpoint()
	{
		return "https://clanchat.net";
	}
}
