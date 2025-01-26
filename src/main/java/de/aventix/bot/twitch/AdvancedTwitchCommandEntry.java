package de.aventix.bot.twitch;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AdvancedTwitchCommandEntry {
    private List<String> command = Lists.newArrayList();
    private List<String> permissions = Lists.newArrayList();
    private String actionType = COMMAND_ACTION.SETTITLE.name();
    private String message = "";
}
