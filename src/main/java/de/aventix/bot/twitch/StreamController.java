package de.aventix.bot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.helix.domain.UserList;
import com.google.common.collect.Lists;
import de.aventix.bot.DiscordBotApplication;
import de.aventix.bot.message.MessageController;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Singleton
public class StreamController {
    private final MessageController messageController;
    private final TwitchConfig config;

    private TwitchClient twitchClient = null;

    @Inject
    public StreamController(MessageController messageController, TwitchConfig config) {
        this.messageController = messageController;
        this.config = config;
    }

    public void startStreamController() {
        TwitchClientBuilder clientBuilder = TwitchClientBuilder.builder();
        OAuth2Credential credential = new OAuth2Credential("twitch", config.getIrc());

        twitchClient = clientBuilder.withClientId(config.getClientId()).withClientSecret(config.getSecretKey()).withEnableHelix(true).withEnableChat(true).withChatAccount(credential).withDefaultEventHandler(SimpleEventHandler.class).build();

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
                            UserList userList = twitchClient.getHelix().getUsers(config.getIrc(), Lists.newArrayList(liveEvent.getChannel().getId()), Lists.newArrayList(liveEvent.getChannel().getName())).queue().get();
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

            if (twitchChannelConfigEntry != null) {
                SimpleTwitchCommandConfigEntry commandEntry = twitchChannelConfigEntry.getSimpleCommands().stream().filter(cmd -> cmd.getCommand().contains(chatEvent.getMessage())).findFirst().orElse(null);

            /*if (chatEvent.getMessage().startsWith("!settitle")) {
                if (hasChatPermissions(Arrays.asList("MODERATOR", "BROADCASTER"), chatEvent.getPermissions())) {
                    String[] arguments = chatEvent.getMessage().split(" ", 2);
                    if (arguments[1] != null && !arguments[1].isEmpty()) {
                        try {
                            ChannelInformation channelInformationList = twitchClient.getHelix().getChannelInformation(config.getIrc(), Collections.singletonList(chatEvent.getChannel().getId())).queue().get().getChannels().get(0).withTitle(arguments[1]);;
                            twitchClient.getHelix().updateChannelInformation(config.getIrc(), chatEvent.getChannel().getId(), channelInformationList).queue();
                            twitchClient.getChat().sendMessage(config.getChannelName(), "@" + chatEvent.getUser().getName() + " hat den Titel zu „" + arguments[1] + "“ geändert!");
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }*/

                if (commandEntry != null && (commandEntry.getPermissions() == null || commandEntry.getPermissions().isEmpty() || hasChatPermissions(commandEntry.getPermissions(), chatEvent.getPermissions()))) {
                    twitchClient.getChat().sendMessage(twitchChannelConfigEntry.getChannelName(), commandEntry.getMessage());
                }
            }
        });
    }

    public boolean hasChatPermissions(List<String> permissionRequired, Set<CommandPermission> userPermissions) {
        return userPermissions.stream().anyMatch(chatPerm -> permissionRequired.contains(chatPerm.name()));
    }
}
