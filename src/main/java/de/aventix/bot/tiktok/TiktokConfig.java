package de.aventix.bot.tiktok;

import com.google.common.collect.Lists;
import de.aventix.bot.config.Config;
import de.aventix.bot.config.Configuration;
import de.aventix.bot.config.JsonConfigurationType;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Singleton;
import java.util.List;

@Getter
@Setter
@Singleton
@Configuration(filename = "tiktok", type = JsonConfigurationType.class)
public class TiktokConfig extends Config {
    public List<TiktokChannelEntry> channelWatchers = Lists.newArrayList();
}
