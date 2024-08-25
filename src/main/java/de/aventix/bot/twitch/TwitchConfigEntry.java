package de.aventix.bot.twitch;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TwitchConfigEntry {
    private String channelName = "channelname";
    private List<SimpleTwitchCommandConfigEntry> simpleCommands = Lists.newArrayList();
    private List<TwitchMessageConfigEntry> guildSettings = Lists.newArrayList();
}
