package de.aventix.bot.twitch;

import com.google.common.collect.Lists;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TwitchChannelConfigEntry {
    private String channelName = "Channelname";
    private String irc = "IRC";
    private List<SimpleTwitchCommandConfigEntry> simpleCommands = Lists.newArrayList();
    private List<AdvancedTwitchCommandEntry> advancedCommands = Lists.newArrayList();
    private List<Long> guildIds = Lists.newArrayList();
}
