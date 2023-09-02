# Clan Chat Webhooks
Clan Chat Webhooks is a utility to send Clan Chat messages to a webhook.

It can be combined with https://clanchat.net/ (or self-hosted https://github.com/pascalla/clanchat.net) to easily show the Clan Messages in your Discord.

### Instructions of Use

1. Install Runelite Plugin
2. Login to https://clanchat.net (or you're self-hosted version) and create a clan.
3. Create a Discord Webhook and update it in clan settings.
4. Generate a Secret Key and put it into the settings of the Runelite Plugin

### Common Questions and Issues

> What should "Endpoint URL" be in the plugin settings?

This points to where your messaged are being processed, if you are using my hosted "https://clanchat.net" then the setting value should be  "https://clanchat.net". If you are hosting your own version at "https://myownversion.com" the setting value should be ""https://myownversion.com".

> What is the Clan Name setting?

This is an optional setting. This allows you to only send messages from a clan with the specified name. It is useful if you frequently switch between account that are part of different clans, and you only want the plugin to apply to one.

> Do I need to generate multiple Secret Keys?

Secret keys have no limit on how many people can use them simultaneously. The main reason for being able to generate multiple, is so you can control access to plugin easier, If you gave everyone a single key and someone started using it in another clan and spamming your channel, everyone would need to update there key.

> What is the Guest Features?

The main use case of this is to allow frequent guests in the clan to also have there broadcasts shown in the Discord. This will only show broadcasts related to them from there primary clan in the Discord. They need to be using a Secret Guest Key and be on the approved username list.

![](https://i.imgur.com/ZbCScxc.png)
![](https://i.imgur.com/XOPMxNt.png)
![](https://i.imgur.com/u6UQjIY.png)

