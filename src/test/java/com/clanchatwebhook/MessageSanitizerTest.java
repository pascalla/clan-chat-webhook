package com.clanchatwebhook;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class MessageSanitizerTest {
    @Test
    public void testSanitize_quest() {
        String input = "<str_quest_name_0=Dragon Slayer 2>Username has completed a quest: <str_quest_name_0>";
        String expected = " Username has completed a quest: Dragon Slayer 2";

        String result = MessageSanitizer.sanitize(input);

        assertEquals(expected, result);
    }
}
