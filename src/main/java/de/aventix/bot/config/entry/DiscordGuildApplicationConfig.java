package de.aventix.bot.config.entry;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DiscordGuildApplicationConfig {
    private long guildId = 1L;
    private long welcomeLeaveMessageChannel = 1L;
    private String botCommandPrefix = "!aventixbot";
    private List<Long> botPermissions = Lists.newArrayList();
}
