package de.aventix.bot.reaction;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ReactionConfigEntry {
    private String channelId;
    private String messageId;
    private Map<String, List<String>> emojiRoles = Maps.newHashMap();//EmojiId -- RoleId

    public ReactionConfigEntry create(String channelId, String messageId, Map roles) {
        this.channelId = channelId;
        this.messageId = messageId;
        this.emojiRoles = roles;

        return this;
    }
}
