package de.aventix.bot.config.entry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscordGuildCacheConfig {
    private long guildId = 1L;
    private long channelUpdateRateLimit = 600000;
    private long maxRateLimitRequests = 2;
    private long lastChannelUpdate = -1;
    private long lastChannelUpdateCount = 0;
}
