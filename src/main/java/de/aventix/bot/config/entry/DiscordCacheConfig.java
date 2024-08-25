package de.aventix.bot.config.entry;

import com.google.common.collect.Lists;
import de.aventix.bot.config.Config;
import de.aventix.bot.config.Configuration;
import de.aventix.bot.config.JsonConfigurationType;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Singleton;
import java.util.List;

@Configuration(
        filename = "cache",
        type = JsonConfigurationType.class
)
@Getter
@Setter
@Singleton
public class DiscordCacheConfig extends Config {
    private List<DiscordGuildCacheConfig> guilds = Lists.newArrayList(new DiscordGuildCacheConfig());
}
