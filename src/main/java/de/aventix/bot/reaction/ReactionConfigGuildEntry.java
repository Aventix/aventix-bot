package de.aventix.bot.reaction;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReactionConfigGuildEntry {
    private long guildId = 1L;
    private List<ReactionConfigEntry> entries = Lists.newArrayList(new ReactionConfigEntry());
}
