package de.aventix.bot.config.entry;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import de.aventix.bot.config.Config;
import de.aventix.bot.config.Configuration;
import de.aventix.bot.config.JsonConfigurationType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/* This class was created by @Aventix on 06.06.2022 at 00:17. */
@Getter
@Setter
@Singleton
@Configuration(filename = "config", type = JsonConfigurationType.class)
public class DiscordApplicationConfig extends Config {
    private String botToken = "";
    private List<DiscordGuildApplicationConfig> guilds = Lists.newArrayList(new DiscordGuildApplicationConfig());
}
