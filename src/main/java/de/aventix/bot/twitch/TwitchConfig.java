package de.aventix.bot.twitch;

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
@Configuration(filename = "twitch", type = JsonConfigurationType.class)
public class TwitchConfig extends Config {
    private String clientId = "clientId";
    private String secretKey = "secretKey";
    private String redirectUrl = "redirectUrl";
    private String irc = "IRC";
    private List<TwitchConfigEntry> channels = Lists.newArrayList();
}
