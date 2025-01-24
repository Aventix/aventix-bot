package de.aventix.bot.twitch;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TwitchConfigEntry {
    private List<TwitchChannelConfigEntry> channels = Lists.newArrayList();
    private List<TwitchMessageConfigEntry> guildSettings = Lists.newArrayList();
}
