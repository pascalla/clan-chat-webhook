package com.clanchatwebhook;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.clan.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClanChannelChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
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

	public boolean activeClan = false;

	@Subscribe
	private void onClanChannelChanged(ClanChannelChanged event)
	{
		if (event.getClanId() == ClanID.CLAN)
		{
			ClanChannel clan = client.getClanChannel();
			String clanName = client.getClanChannel().getName();

			if (clan != null && clanName != null) {
				activeClan = true;
			} else {
				activeClan = false;
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (StringUtils.isEmpty(config.secretKey()) || StringUtils.isEmpty(config.webhookEndpoint()))
		{
			return;
		}

		String content;

		if (chatMessage.getType() == ChatMessageType.CLAN_CHAT || chatMessage.getType() == ChatMessageType.CLAN_MESSAGE)
		{

			String clanName = client.getClanChannel().getName();
			clanName = clanName.replace((char)160, ' ');
			String configClanName = config.clanName();

			if( clanName == null || (!StringUtils.isEmpty(configClanName)  && !configClanName.equalsIgnoreCase(clanName))) {
		 		return;
			}

			content = sanitizeMessage(chatMessage.getMessage(), chatMessage.getType());

			if (!content.contains("</col>")) {
				sendMessage(chatMessage);
			}

		}
	}

	public enum SystemMessageType {
		NORMAL(1),
		DROP(2),
		RAID_DROP(3),
		PET_DROP(4),
		PERSONAL_BEST(5),
		COLLECTION_LOG(6),
		QUESTS(7),
		PVP(8),
		ATTENDANCE(9),
		LEVEL_UP(10),
		COMBAT_ACHIEVEMENTS(11),
		CLUE_DROP(12),
		DIARY(13),
		UNKNOWN(100),
		LOGIN(-1);

		public final int code;

		private SystemMessageType(int code) {
			this.code = code;
		}
	}

	public enum AccountType {
		NORMAL(1),
		IRON(2),
		HARDCORE_IRON(3),
		ULTIMATE_IRON(4),
		UNRANKED_IRON(5),
		GROUP_IRON(6),
		HARDCORE_GROUP_IRON(7),
		PLAYER_MODERATOR(8),
		JAGEX_MODERATOR(9);

		public final int code;

		private AccountType(int code) {
			this.code = code;
		}
	}

	private SystemMessageType getSystemMessageType(String message, ChatMessageType messageType)
	{
		if(messageType == ChatMessageType.CLAN_MESSAGE) {
			if (message.contains("received a drop:")) {
				return SystemMessageType.DROP;
			} else if (message.contains("received special loot from a raid:")) {
				return SystemMessageType.RAID_DROP;
			} else if (message.contains("has completed a quest:")) {
				return SystemMessageType.QUESTS;
			} else if (message.contains("received a new collection log item:")) {
				return SystemMessageType.COLLECTION_LOG;
			} else if (message.contains("personal best:")) {
				return SystemMessageType.PERSONAL_BEST;
			} else if (message.contains("To talk in your clan's channel, start each line of chat with")) {
				return SystemMessageType.LOGIN;
			} else if (message.contains("has defeated") || message.contains("has been defeated by")) {
				return SystemMessageType.PVP;
			} else if (message.contains("has a funny feeling like") || message.contains("backpack:") || message.contains("something special:")) {
				return SystemMessageType.PET_DROP;
			} else if ((message.contains("has reached") && (message.contains("level") || message.contains("XP"))) || message.contains("has reached a total level of")) {
				return SystemMessageType.LEVEL_UP;
			} else if (message.contains("tier of rewards from Combat Achievements!")) {
				return SystemMessageType.COMBAT_ACHIEVEMENTS;
			} else if (message.contains("received a clue item:")) {
				return SystemMessageType.CLUE_DROP;
			} else if (message.contains("has left.") || message.contains("has been invited into the clan by") || message.contains("has joined.")) {
				return SystemMessageType.ATTENDANCE;
			} else if(message.contains("has completed the") && message.contains("diary.")) {
				return SystemMessageType.DIARY;
			}

			return SystemMessageType.UNKNOWN;
		}

		return SystemMessageType.NORMAL;
	}

	public AccountType getAccountType(String message)
	{
		if (message.contains("<img=0>")) {
			return AccountType.PLAYER_MODERATOR;
		} else if (message.contains("<img=2>")) {
			return AccountType.IRON;
		} else if (message.contains("<img=10>")) {
			return AccountType.HARDCORE_IRON;
		} else if (message.contains("<img=3>")) {
			return AccountType.ULTIMATE_IRON;
		} else if (message.contains("<img=41>")) {
			return AccountType.GROUP_IRON;
		} else if (message.contains("<img=43>")) {
			return AccountType.UNRANKED_IRON;
		} else if (message.contains("<img=42>")) {
			return AccountType.HARDCORE_GROUP_IRON;
		} else {
			return AccountType.NORMAL;
		}
	}

	private String sanitizeMessage(String message, ChatMessageType messageType)
	{
		String newMessage = message;
		newMessage = newMessage.replace((char)160, ' ');
		newMessage = newMessage.replace("<lt>", "<");
		newMessage = newMessage.replace("<gt>", ">");
		return additionalCustomizations(newMessage);
	}

	private String additionalCustomizations(String message)
	{
		String newMessage = message;
		newMessage = newMessage.replaceAll("\\<img=\\d+\\>", "");
		return newMessage;
	}

	private void sendMessage(ChatMessage chatMessage)
	{
		String author = chatMessage.getName().replace((char)160, ' ').replaceAll("<img=\\d+>", "");
		String content = sanitizeMessage(chatMessage.getMessage(), chatMessage.getType());
		AccountType accountType = getAccountType(chatMessage.getName());
		SystemMessageType systemMessageType = getSystemMessageType(chatMessage.getMessage(), chatMessage.getType());
		String clanTitle = null;

		if (systemMessageType == SystemMessageType.NORMAL) {
			ClanRank clanRank = client.getClanChannel().findMember(author).getRank();
			clanTitle = client.getClanSettings().titleForRank(clanRank).getName();
		}

		ClanMessageEvent messageEvent = new ClanMessageEvent(author, content, accountType, systemMessageType, clanTitle, chatMessage.getTimestamp());

		sendWebhook(messageEvent);
	}


	private void sendWebhook(ClanMessageEvent messageEvent)
	{
		String configUrl = config.webhookEndpoint() + "/webhook/" + config.secretKey();

		if (Strings.isNullOrEmpty(configUrl)) { return; }

		HttpUrl url = HttpUrl.parse(configUrl);
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("data", GSON.toJson(messageEvent));

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
