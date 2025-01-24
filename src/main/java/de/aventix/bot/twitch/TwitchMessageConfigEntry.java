package de.aventix.bot.twitch;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.aventix.bot.message.EmbededMessageEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TwitchMessageConfigEntry {
    private long guildId = 1L;
    private long channelId = 1L;
    private EmbededMessageEntity broadcastLiveMessage;
    private Map<String, Long> specialGamePings = Maps.newHashMap();
}
