# Clan Chat Webhooks
Sends Clan Chat Messages and Broadcasts to a Webhook.

If the "Discord Webhook" option is enabled it will send in the Discord Webhook format.

Otherwise the form data will look like:

For a Message in the Clan:
```key: "data"
"data:{"content":"This is the message","author":"Bao Gua","broadcast":false}"
```

For a Broadcast in the Clan
```key: "data"
"data:{"content":"CostCutters is now Level 99 Mining","broadcast":true}"
```