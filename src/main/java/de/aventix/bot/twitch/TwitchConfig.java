package de.aventix.bot.twitch;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.aventix.bot.config.Config;
import de.aventix.bot.config.Configuration;
import de.aventix.bot.config.JsonConfigurationType;
import de.aventix.bot.message.EmbededMessageEntity;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Singleton
@Configuration(filename = "twitch", type = JsonConfigurationType.class)
public class TwitchConfig extends Config {
    private String clientId;
    private String secretKey;
    private String redirectUrl;
    private String irc;
    private String channelName;
    private long broadcastChannelId;
    private EmbededMessageEntity broadcastLiveMessage;
    private Map<String, Long> specialGamePings = Maps.newHashMap();//GameId - Ping Role Id
    private List<SimpleTwitchCommandConfigEntry> simpleCommands = Lists.newArrayList();
}
