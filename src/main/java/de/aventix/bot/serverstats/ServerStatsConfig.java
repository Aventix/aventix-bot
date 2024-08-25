package de.aventix.bot.serverstats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.aventix.bot.config.Config;
import de.aventix.bot.config.Configuration;
import de.aventix.bot.config.JsonConfigurationType;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@Singleton
@Getter
@Setter
@Configuration(filename = "serverstats", type = JsonConfigurationType.class)
public class ServerStatsConfig extends Config {
    private int updateTimer = 310;
    private List<GuildServerStatsConfig> guilds = Lists.newArrayList(new GuildServerStatsConfig());
}
