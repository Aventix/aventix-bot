package de.aventix.bot.config.entry;

import de.aventix.bot.config.Config;
import de.aventix.bot.config.Configuration;
import de.aventix.bot.config.JsonConfigurationType;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Singleton;

@Configuration(
        filename = "cache",
        type = JsonConfigurationType.class
)
@Getter
@Setter
@Singleton
public class DiscordCacheConfig extends Config {
    private long channelUpdateRateLimit = 600000;
    private long maxRateLimitRequests = 2;
    private long lastChannelUpdate = -1;
    private long lastChannelUpdateCount = 0;
}
