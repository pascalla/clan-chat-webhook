package com.clanchatwebhook;

import net.runelite.api.ChatMessageType;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class MessageSanitizerTest {
    @Test
    public void testSanitize_quest() {
        String input = "<str_quest_name_0=Dragon Slayer 2>Username has completed a quest: <str_quest_name_0>";
        String expected = "Username has completed a quest: Dragon Slayer 2";

        String result = MessageSanitizer.sanitize(input, ChatMessageType.CLAN_MESSAGE);

        assertEquals(expected, result);
    }

    @Test
    public void testSanitize_xpMilestone() {
        String input = "<int_0=115000000>PlatinaGuy has reached <int_0:spacer> XP in Ranged.";
        String expected = "PlatinaGuy has reached 115,000,000 XP in Ranged.";

        String result = MessageSanitizer.sanitize(input, ChatMessageType.CLAN_MESSAGE);

        assertEquals(expected, result);
    }

    @Test
    public void testSanitize_reusedVariable() {
        String input = "Relder Maul received a drop: <int_0=225><int_0:spacer> x Dragon bolts (unf)<int_0=560250> (<int_0:spacer> coins) from The Leviathan.";
        String expected = "Relder Maul received a drop: 225 x Dragon bolts (unf) (560,250 coins) from The Leviathan.";

        String result = MessageSanitizer.sanitize(input, ChatMessageType.CLAN_MESSAGE);

        assertEquals(expected, result);
    }

    @Test
    public void testSanitize_playerChatKeepsTags() {
        String input = "<lt>int_0=5<gt>hello";
        String expected = "<int_0=5>hello";

        String result = MessageSanitizer.sanitize(input, ChatMessageType.CLAN_CHAT);

        assertEquals(expected, result);
    }
}
