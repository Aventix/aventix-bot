package de.aventix.bot.serverstats;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class GuildServerStatsConfig {
    private long guildId = 1L;
    //ChannelID - Message
    private Map<String, String> entries = Maps.newHashMap();
}
