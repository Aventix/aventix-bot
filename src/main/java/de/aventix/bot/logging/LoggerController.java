package de.aventix.bot.logging;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.aventix.bot.config.entry.DiscordApplicationConfig;

/* This class was created by @Aventix on 06.06.2022 at 13:29. */
@Singleton
public class LoggerController {
  private final DiscordApplicationConfig config;

  @Inject
  public LoggerController(DiscordApplicationConfig config) {
    this.config = config;
  }
}
