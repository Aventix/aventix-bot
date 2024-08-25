package de.aventix.bot.reaction;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import de.aventix.bot.config.Config;
import de.aventix.bot.config.Configuration;
import de.aventix.bot.config.JsonConfigurationType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Singleton
@Configuration(filename = "reaction", type = JsonConfigurationType.class)
public class ReactionConfig extends Config {
    private List<ReactionConfigGuildEntry> guilds = Lists.newArrayList(new ReactionConfigGuildEntry());
}

