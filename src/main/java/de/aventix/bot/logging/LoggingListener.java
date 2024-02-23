package de.aventix.bot.logging;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.aventix.bot.DiscordBotApplication;
import de.aventix.bot.listener.DiscordListener;
import de.aventix.bot.message.MessageController;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/* This class was created by @Aventix on 06.06.2022 at 13:33. */

@Singleton
@DiscordListener
public class LoggingListener extends ListenerAdapter {
    private final MessageController messageController;
    private final LoggingConfig config;

    @Inject
    public LoggingListener(MessageController messageController, LoggingConfig config) {
        this.messageController = messageController;
        this.config = config;
    }

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        //BAN EVENT
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        //CAN CHECK KICK EVENT
    }

    @Override
    public void onGuildTimeout(@NotNull GuildTimeoutEvent event) {
        //TIMEOUT EVENT
    }

    @Override
    public void onGuildMemberUpdateTimeOut(@NotNull GuildMemberUpdateTimeOutEvent event) {
        //TIMEOUT UPDATE EVENT
    }

    //TIMEOUT REMOVE IS MISSING

    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        //UNBAN EVENT
    }

    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        //CHANNEL CREATE EVENT
    }

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        //CHANNEL DELETE EVENT
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        //BOT JOIN
    }

    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        //BOT LEAVE
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        //ROLE ADD
    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        //ROLE REMOVE
    }

    @Override
    public void onMessageDelete(@NotNull MessageDeleteEvent event) {
        //MESSAGE DELETE
    }

    @Override
    public void onGuildMemberUpdateNickname(@NotNull GuildMemberUpdateNicknameEvent event) {
        sendLoggingMessage(LoggingAction.CHANGE_NICKNAME, event.getOldNickname(), event.getNewNickname());
    }

    private void sendLoggingMessage(LoggingAction action, Object... args) {
        this.config.getLoggingTypes().stream().filter(loggingEntry -> loggingEntry.getActionType().equals(action)).forEach(loggingEntry ->
                DiscordBotApplication.jda.getTextChannelById(loggingEntry.getChannelId()).sendMessageEmbeds(messageController.getMessage(loggingEntry.getMessage(), args)).queue());
    }
}
