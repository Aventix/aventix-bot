package de.aventix.bot.logging;

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
@Configuration(
        filename = "logging",
        type = JsonConfigurationType.class
)
public class LoggingConfig extends Config {
    private List<LoggingConfigEntry> loggingTypes = Lists.newArrayList();
}
