package de.aventix.bot.config.entry;

import de.aventix.bot.message.EmbededMessageEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuildMessageConfiguration {
    private long guildId = 1L;
    private EmbededMessageEntity welcomeMessage;
    private EmbededMessageEntity leaveMessage;
}
