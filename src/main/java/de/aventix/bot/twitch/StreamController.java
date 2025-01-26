package de.aventix.bot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.helix.domain.ChannelInformation;
import com.github.twitch4j.helix.domain.Game;
import com.github.twitch4j.helix.domain.UserList;
import com.google.common.collect.Lists;
import de.aventix.bot.DiscordBotApplication;
import de.aventix.bot.message.MessageController;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Singleton
public class StreamController {
    private final MessageController messageController;
    private final TwitchConfig config;

    @Getter
    private TwitchClient twitchClient = null;

    @Inject
    public StreamController(MessageController messageController, TwitchConfig config) {
        this.messageController = messageController;
        this.config = config;
    }

    public void startStreamController() {
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();
        OAuth2Credential credential = new OAuth2Credential("twitch", config.getChatToken());

        twitchClient = clientBuilder
                .withClientId(config.getClientId())
                .withClientSecret(config.getSecretKey())
                .withEnableHelix(true)
                .withEnableChat(true)
                .withChatAccount(credential)
                .withDefaultEventHandler(SimpleEventHandler.class).build();

        config.getConfig().getChannels().forEach(channel -> {
            twitchClient.getChat().joinChannel(channel.getChannelName());
            twitchClient.getClientHelper().enableStreamEventListener(channel.getChannelName());
        });

        twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, liveEvent -> {
            System.out.println(liveEvent.getChannel().getName() + " ist nun Live!");

            TwitchChannelConfigEntry twitchChannelConfigEntry = config.getConfig().getChannels().stream().filter(channel -> channel.getChannelName().equalsIgnoreCase(liveEvent.getChannel().getName())).findFirst().orElse(null);

            if (twitchChannelConfigEntry != null) {
                twitchChannelConfigEntry.getGuildIds().forEach(guildId -> {
                    TwitchMessageConfigEntry twitchMessageConfigEntry = this.config.getConfig().getGuildSettings().stream().filter(guildSettings -> guildSettings.getGuildId() == guildId).findFirst().orElse(null);

                    if (twitchMessageConfigEntry != null) {
                        TextChannel guildChannelById = (TextChannel) Objects.requireNonNull(DiscordBotApplication.jda.getGuildById(twitchMessageConfigEntry.getGuildId())).getGuildChannelById(ChannelType.TEXT, twitchMessageConfigEntry.getChannelId());
                        String timeStamp = new SimpleDateFormat("dd.MM.yyyy - HH:mm").format(Calendar.getInstance().getTime());
                        String currentTimestamp = timeStamp + " Uhr";

                        try {
                            UserList userList = twitchClient.getHelix().getUsers(getIrc(liveEvent.getChannel().getName()), Lists.newArrayList(liveEvent.getChannel().getId()), Lists.newArrayList(liveEvent.getChannel().getName())).queue().get();
                            String profileImage = userList.getUsers().get(0).getProfileImageUrl();
                            Long match = twitchMessageConfigEntry.getSpecialGamePings().get(liveEvent.getStream().getGameId());
                            String roleMention = "";

                            if (match != null && match != -1 && match != 0) {
                                Role role = guildChannelById.getGuild().getRoleById(match);
                                if (role != null) roleMention = role.getAsMention();
                            }

                            guildChannelById.sendMessageEmbeds(messageController.getMessage(twitchMessageConfigEntry.getBroadcastLiveMessage(), userList.getUsers().get(0).getDisplayName(), (liveEvent.getStream().getGameName().isEmpty() ? "Just Chatting" : liveEvent.getStream().getGameName()), liveEvent.getStream().getTitle(), liveEvent.getStream().getThumbnailUrl(1920, 1080), profileImage, "https://twitch.tv/" + liveEvent.getChannel().getName(), currentTimestamp, liveEvent.getStream().getViewerCount(), roleMention)).queue();
                        } catch (InterruptedException | ExecutionException ignored) {
                        }
                    }
                });
            }
        });

        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, chatEvent -> {
            TwitchChannelConfigEntry twitchChannelConfigEntry = config.getConfig().getChannels().stream().filter(channel -> channel.getChannelName().equalsIgnoreCase(chatEvent.getChannel().getName())).findFirst().orElse(null);

            String[] arguments = chatEvent.getMessage().split(" ");

            if (twitchChannelConfigEntry != null && arguments.length > 0) {
                SimpleTwitchCommandConfigEntry commandEntry = twitchChannelConfigEntry.getSimpleCommands().stream().filter(cmd -> cmd.getCommand().contains(arguments[0])).findFirst().orElse(null);

                if (commandEntry != null && (commandEntry.getPermissions() == null || commandEntry.getPermissions().isEmpty() || hasChatPermissions(commandEntry.getPermissions(), chatEvent.getPermissions()))) {
                    twitchClient.getChat().sendMessage(twitchChannelConfigEntry.getChannelName(), commandEntry.getMessage());
                    return;
                }

                AdvancedTwitchCommandEntry advancedCommandEntry = twitchChannelConfigEntry.getAdvancedCommands().stream().filter(cmd -> cmd.getCommand().contains(arguments[0])).findFirst().orElse(null);

                if (advancedCommandEntry != null && (advancedCommandEntry.getPermissions() == null || advancedCommandEntry.getPermissions().isEmpty() || hasChatPermissions(advancedCommandEntry.getPermissions(), chatEvent.getPermissions()))) {
                    List<String> messageArguments = Lists.newArrayList();
                    if (advancedCommandEntry.getActionType().equalsIgnoreCase(COMMAND_ACTION.SETTITLE.name())) {
                        String title = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));
                        twitchClient.getHelix().updateChannelInformation(
                                getIrc(chatEvent.getChannel().getName()),
                                chatEvent.getChannel().getId(),
                                ChannelInformation.builder().title(title).build()
                        ).queue();
                        twitchClient.getChat().sendMessage(chatEvent.getChannel().getName(), "@" + chatEvent.getUser().getName() + " hat den Titel zu „" + title + "“ geändert!");
                    } else if (advancedCommandEntry.getActionType().equalsIgnoreCase(COMMAND_ACTION.SETGAME.name())) {
                        String gameName = String.join(" ", Arrays.copyOfRange(arguments, 1, arguments.length));

                        Game foundGame = twitchClient.getHelix().getGames(null, null, Collections.singletonList(gameName), null)
                                .execute()
                                .getGames()
                                .stream()
                                .findFirst()
                                .orElse(null);

                        if (foundGame == null) {
                            twitchClient.getChat().sendMessage(chatEvent.getChannel().getName(),
                                    "@" + chatEvent.getUser().getName() + " Spiel „" + gameName + "“ nicht gefunden!");
                            return;
                        }

                        twitchClient.getHelix().updateChannelInformation(
                                getIrc(chatEvent.getChannel().getName()),
                                chatEvent.getChannel().getId(),
                                ChannelInformation.builder().gameId(foundGame.getId()).build()
                        ).queue();
                        twitchClient.getChat().sendMessage(chatEvent.getChannel().getName(), "@" + chatEvent.getUser().getName() + " hat das Spiel zu „" + foundGame.getName() + "“ geändert!");
                    }
                }
            }
        });
    }

    public boolean hasChatPermissions(List<String> permissionRequired, Set<CommandPermission> userPermissions) {
        return userPermissions.stream().anyMatch(chatPerm -> permissionRequired.contains(chatPerm.name()));
    }

    public String getIrc(String channelName) {
        TwitchChannelConfigEntry twitchChannelConfigEntry = this.config.getConfig().getChannels().stream().filter(channel -> channel.getChannelName().equalsIgnoreCase(channelName)).findFirst().orElse(null);
        return twitchChannelConfigEntry == null ? null : twitchChannelConfigEntry.getIrc();
    }
}
