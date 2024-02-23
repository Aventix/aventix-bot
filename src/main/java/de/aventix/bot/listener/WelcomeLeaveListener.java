package de.aventix.bot.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.aventix.bot.config.entry.DiscordApplicationConfig;
import de.aventix.bot.config.entry.MessageConfiguration;
import de.aventix.bot.message.MessageController;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/* This class was created by @Aventix on 06.06.2022 at 01:37. */

@Singleton
@DiscordListener
public class WelcomeLeaveListener extends ListenerAdapter {
    private final MessageController messageController;
    private final DiscordApplicationConfig config;
    private final MessageConfiguration messages;

    @Inject
    public WelcomeLeaveListener(
            MessageController messageController,
            DiscordApplicationConfig config,
            MessageConfiguration messages) {
        this.messageController = messageController;
        this.config = config;
        this.messages = messages;
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        User user = event.getUser();
        TextChannel channel;

        if ((channel = event.getGuild().getTextChannelById(this.config.getWelcomeLeaveMessageChannel()))
                != null) {
            String timeStamp = new SimpleDateFormat("dd.MM.yyyy - HH:mm").format(Calendar.getInstance().getTime());
            String currentTimestamp = timeStamp + " Uhr";
            channel
                    .sendMessageEmbeds(
                            this.messageController.getMessage(
                                    this.messages.getWelcomeMessage(),
                                    user.getAsMention(),
                                    currentTimestamp,
                                    (user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl())))
                    .queue();
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        User user = event.getUser();
        TextChannel channel;

        if ((channel = event.getGuild().getTextChannelById(this.config.getWelcomeLeaveMessageChannel()))
                != null) {
            String timeStamp = new SimpleDateFormat("dd.MM.yyyy - HH:mm").format(Calendar.getInstance().getTime());
            String currentTimestamp = timeStamp + " Uhr";
            channel
                    .sendMessageEmbeds(
                            this.messageController.getMessage(
                                    this.messages.getLeaveMessage(),
                                    user.getAsMention(),
                                    currentTimestamp,
                                    (user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl())))
                    .queue();
        }
    }
}
