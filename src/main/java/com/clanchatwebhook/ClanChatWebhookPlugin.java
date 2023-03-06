package com.clanchatwebhook;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
@PluginDescriptor(
	name = "Clan Chat Webhook"
)
public class ClanChatWebhookPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClanChatWebhookConfig config;

	@Inject
	private OkHttpClient okHttpClient;

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if ((event.getType() == ChatMessageType.CLAN_CHAT) && config.sendChat())
		{
			boolean personalMessage = Objects.equals(client.getLocalPlayer().getName(), event.getName());

			if(!personalMessage && !config.sendAll())
			{
				return;
			}

			sendMessage(event);
		}

		if ((event.getType() == ChatMessageType.CLAN_MESSAGE) && config.sendBroadcasts())
		{
			if(!(Objects.equals(client.getLocalPlayer().getName(), event.getName())) && !config.sendAll())
			{
				return;
			}

			if (event.getSender() == null)
			{
				return;
			}

			sendMessage(event);
		}
	}

	private void sendMessage(ChatMessage message)
	{
		boolean discordWebhook = config.discordWebhook();
		ClanMessageEvent messageEvent = new ClanMessageEvent(message.getName(), message.getMessage(), message.getTimestamp());

		if (discordWebhook)
		{
			generateDiscordWebhookPayload(messageEvent);
		}
		else
		{
			sendWebhook(messageEvent);
		}
	}

	private void generateDiscordWebhookPayload(ClanMessageEvent event)
	{
		DiscordWebhookBody discordWebhookBody = new DiscordWebhookBody();
		discordWebhookBody.setContent(event.toDiscordContentString());
		sendDiscordWebhook(discordWebhookBody);
	}

	private void sendWebhook(ClanMessageEvent messageEvent)
	{
		String configUrl = config.webhook();

		if (Strings.isNullOrEmpty(configUrl)) { return; }

		HttpUrl url = HttpUrl.parse(configUrl);
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("data", GSON.toJson(messageEvent));

		buildRequestAndSend(url, requestBodyBuilder);
	}

	private void sendDiscordWebhook(DiscordWebhookBody discordWebhookBody)
	{
		String configUrl = config.webhook();

		if (Strings.isNullOrEmpty(configUrl)) { return; }

		HttpUrl url = HttpUrl.parse(configUrl);
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("payload_json", GSON.toJson(discordWebhookBody));

		buildRequestAndSend(url, requestBodyBuilder);
	}

	private void buildRequestAndSend(HttpUrl url, MultipartBody.Builder requestBodyBuilder)
	{
		RequestBody requestBody = requestBodyBuilder.build();
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();
		sendRequest(request);
	}

	private void sendRequest(Request request)
	{
		okHttpClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.debug("Error submitting webhook", e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				response.close();
			}
		});
	}

	@Provides
	ClanChatWebhookConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ClanChatWebhookConfig.class);
	}
}
