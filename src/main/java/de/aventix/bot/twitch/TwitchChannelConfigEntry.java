package de.aventix.bot.twitch;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TwitchChannelConfigEntry {
    private String channelName = "Channelname";
    private List<SimpleTwitchCommandConfigEntry> simpleCommands = Lists.newArrayList();
    private List<Long> guildIds = Lists.newArrayList();
}
