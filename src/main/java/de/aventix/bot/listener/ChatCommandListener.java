package de.aventix.bot.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import de.aventix.bot.config.entry.DiscordApplicationConfig;
import de.aventix.bot.config.entry.DiscordGuildApplicationConfig;
import de.aventix.bot.message.EmbededMessageEntity;
import de.aventix.bot.message.MessageController;
import de.aventix.bot.twitch.StreamController;
import de.aventix.bot.twitch.TwitchChannelConfigEntry;
import de.aventix.bot.twitch.TwitchConfig;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Collectors;

@Singleton
@DiscordListener
public class ChatCommandListener extends ListenerAdapter {
    private final DiscordApplicationConfig config;
    private final TwitchConfig twitchConfig;
    private final MessageController messageController;
    private final StreamController streamController;

    @Inject
    public ChatCommandListener(DiscordApplicationConfig config, TwitchConfig twitchConfig, MessageController messageController, StreamController streamController) {
        this.config = config;
        this.twitchConfig = twitchConfig;
        this.messageController = messageController;
        this.streamController = streamController;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().split(" ");
        DiscordGuildApplicationConfig configuration = config.getGuilds().stream().filter(cfg -> cfg.getGuildId() == event.getGuild().getIdLong()).findFirst().orElse(null);
        if (configuration == null) return;

        if (arguments[0].startsWith(configuration.getBotCommandPrefix())) {
            Role foundPermissionRole = event.getMember().getRoles().stream().filter(role -> configuration.getBotPermissions().contains(role.getIdLong())).findAny().orElse(null);
            if (foundPermissionRole != null) {
                if (arguments[1] != null && arguments[1].startsWith("sendembedmessage")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        EmbededMessageEntity messageEntity = objectMapper.readValue(arguments[3].toString(), EmbededMessageEntity.class);
                        event.getGuild().getChannelById(TextChannel.class, arguments[2]).sendMessageEmbeds(this.messageController.getMessage(messageEntity)).queue();
                        event.getChannel().sendMessage("Erfolgreich embeded message in channel " + arguments[2] + " gesendet!").queue();
                    } catch (JsonProcessingException ignored) {
                    }
                }

                if (arguments[1].equalsIgnoreCase("twitch")) {
                    if (arguments.length >= 3 && arguments[2].equalsIgnoreCase("add")) {
                        if (arguments.length < 5) {
                            event.getChannel().sendMessage("Token Generator: https://twitchtokengenerator.com/quick/oYKn5SkceX").queue();
                            event.getChannel().sendMessage("Klicke auf den Link. Melde dich bei Twitch an. Genehmige alle Rechte. Kopiere \"Access Token\".").queue();
                            event.getChannel().sendMessage("Adde den Benutzer mit " + configuration.getBotCommandPrefix() + " twitch add <channelname> <access-token>").queue();
                        } else {
                            String channelName = arguments[3].toLowerCase();
                            TwitchChannelConfigEntry twitchChannelConfigEntry = twitchConfig.getConfig().getChannels().stream().filter(channel -> channel.getChannelName().equalsIgnoreCase(channelName)).findFirst().orElse(null);

                            if (twitchChannelConfigEntry != null && twitchChannelConfigEntry.getGuildIds().contains(event.getGuild().getIdLong())) {
                                event.getChannel().sendMessage(channelName + " ist bereits auf diesem Server als Streamer registriert!").queue();
                                return;
                            }

                            if (twitchChannelConfigEntry != null) {
                                twitchChannelConfigEntry.getGuildIds().add(event.getGuild().getIdLong());
                            } else {
                                twitchConfig.getConfig().getChannels().add(new TwitchChannelConfigEntry(channelName, arguments[4], Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(event.getGuild().getIdLong())));
                            }

                            twitchConfig.save();
                            streamController.getTwitchClient().getChat().joinChannel(channelName);
                            streamController.getTwitchClient().getClientHelper().enableStreamEventListener(channelName);
                            event.getChannel().sendMessage(channelName + " wurde als Streamer registriert!").queue();
                        }
                    }

                    if (arguments[2] != null && arguments[2].equalsIgnoreCase("remove")) {
                        if (arguments[3] == null || arguments[3].isEmpty() || arguments[3].isBlank()) {
                            event.getChannel().sendMessage("Entferne einen Twitch Nutzer mit dem Befehl: " + configuration.getBotCommandPrefix() + " twitch remove <channelname>!").queue();
                        } else {
                            String channelName = arguments[3].toLowerCase();
                            TwitchChannelConfigEntry twitchChannelConfigEntry = twitchConfig.getConfig().getChannels().stream().filter(channel -> channel.getChannelName().equalsIgnoreCase(channelName)).findFirst().orElse(null);

                            if (twitchChannelConfigEntry == null || !twitchChannelConfigEntry.getGuildIds().contains(event.getGuild().getIdLong())) {
                                event.getChannel().sendMessage(channelName + " ist nicht als Streamer registriert!").queue();
                                return;
                            }

                            if (twitchChannelConfigEntry.getGuildIds().size() == 1) {
                                twitchConfig.getConfig().getChannels().remove(twitchChannelConfigEntry);
                            } else {
                                twitchChannelConfigEntry.getGuildIds().removeIf(value -> value == event.getGuild().getIdLong());
                            }
                            twitchConfig.save();
                            streamController.getTwitchClient().getChat().leaveChannel(channelName);
                            streamController.getTwitchClient().getClientHelper().disableStreamEventListener(channelName);
                            event.getChannel().sendMessage(channelName + " wurde als Streamer entfernt!").queue();
                        }
                    }
                }
            }
        }
    }
}
