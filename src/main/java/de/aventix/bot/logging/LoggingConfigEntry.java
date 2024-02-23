package de.aventix.bot.logging;

import de.aventix.bot.message.EmbededMessageEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoggingConfigEntry {
    private LoggingAction actionType;
    private long channelId;
    private EmbededMessageEntity message;
}
