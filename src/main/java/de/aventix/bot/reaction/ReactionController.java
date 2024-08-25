package de.aventix.bot.reaction;

import de.aventix.bot.listener.DiscordListener;
import de.aventix.bot.message.MessageController;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@DiscordListener
public class ReactionController extends ListenerAdapter {
    private final MessageController messageController;
    private final ReactionConfig config;

    @Inject
    public ReactionController(
            MessageController messageController,
            ReactionConfig config) {
        this.messageController = messageController;
        this.config = config;
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        ReactionConfigEntry configEntryMatch = this.config.getGuilds().stream()
                .filter(guildId -> guildId.getGuildId() == event.getGuild().getIdLong())
                .flatMap(guild -> guild.getEntries().stream())
                .filter(guildEntry -> guildEntry.getChannelId().equalsIgnoreCase(event.getChannel().getId()) &&
                        guildEntry.getMessageId().equalsIgnoreCase(event.getMessageId())).findFirst().orElse(null);

        TextChannel channel;
        if (configEntryMatch != null) {
            List<Map.Entry<String, List<String>>> emojiRoles = configEntryMatch.getEmojiRoles().entrySet().stream().filter(emojiEntry ->
                    {
                        if (event.getReaction().getEmoji().getType().equals(Emoji.Type.CUSTOM)) {
                            return event.getReaction().getEmoji().asCustom().getId().equalsIgnoreCase(emojiEntry.getKey());
                        } else {
                            return event.getReaction().getEmoji().asUnicode().getAsCodepoints().equalsIgnoreCase(emojiEntry.getKey());
                        }
                    }
            ).collect(Collectors.toList());

            if (emojiRoles.isEmpty()) return;
            emojiRoles.forEach(emojiRole -> {
                emojiRole.getValue().forEach(matchRole -> {
                    Role role = event.getGuild().getRoleById(matchRole);
                    assert role != null;

                    event.getGuild().addRoleToMember(UserSnowflake.fromId(event.getUserIdLong()), role).queue();
                });
            });
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        ReactionConfigEntry configEntryMatch = this.config.getGuilds().stream()
                .filter(guildId -> guildId.getGuildId() == event.getGuild().getIdLong())
                .flatMap(guild -> guild.getEntries().stream())
                .filter(guildEntry -> guildEntry.getChannelId().equalsIgnoreCase(event.getChannel().getId()) &&
                        guildEntry.getMessageId().equalsIgnoreCase(event.getMessageId())).findFirst().orElse(null);
        TextChannel channel;
        if (configEntryMatch != null) {
            List<Map.Entry<String, List<String>>> emojiRoles = configEntryMatch.getEmojiRoles().entrySet().stream().filter(emojiEntry ->
                    {
                        if (event.getReaction().getEmoji().getType().equals(Emoji.Type.CUSTOM)) {
                            return event.getReaction().getEmoji().asCustom().getId().equalsIgnoreCase(emojiEntry.getKey());
                        } else {
                            return event.getReaction().getEmoji().asUnicode().getAsCodepoints().equalsIgnoreCase(emojiEntry.getKey());
                        }
                    }
            ).toList();

            if (emojiRoles.isEmpty()) return;
            emojiRoles.forEach(emojiRole -> {
                emojiRole.getValue().forEach(matchRole -> {
                    Role role = event.getGuild().getRoleById(matchRole);
                    assert role != null;

                    event.getGuild().removeRoleFromMember(UserSnowflake.fromId(event.getUserIdLong()), role).queue();
                });
            });
        }
    }
}

