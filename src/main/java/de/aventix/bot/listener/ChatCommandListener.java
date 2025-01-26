package de.aventix.bot.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import de.aventix.bot.config.entry.DiscordApplicationConfig;
import de.aventix.bot.config.entry.DiscordGuildApplicationConfig;
import de.aventix.bot.message.EmbededMessageEntity;
import de.aventix.bot.message.MessageController;
import de.aventix.bot.twitch.TwitchChannelConfigEntry;
import de.aventix.bot.twitch.TwitchConfig;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@DiscordListener
public class ChatCommandListener extends ListenerAdapter {
    private final DiscordApplicationConfig config;
    private final TwitchConfig twitchConfig;
    private final MessageController messageController;

    @Inject
    public ChatCommandListener(DiscordApplicationConfig config, TwitchConfig twitchConfig, MessageController messageController) {
        this.config = config;
        this.twitchConfig = twitchConfig;
        this.messageController = messageController;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().split(" ", 8);
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

                if (arguments[1] != null && arguments[1].equalsIgnoreCase("twitch")) {
                    if (arguments[2] != null && arguments[2].equalsIgnoreCase("add")) {
                        if (arguments[3] == null) {
                            event.getChannel().sendMessage("Adde einen Twitch Nutzer mit dem Befehl: " + configuration.getBotCommandPrefix() + " twitch add <channelname>!").queue();
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
                                twitchConfig.getConfig().getChannels().add(new TwitchChannelConfigEntry(channelName, Lists.newArrayList(), Lists.newArrayList(event.getGuild().getIdLong())));
                            }

                            twitchConfig.save();
                            event.getChannel().sendMessage(channelName + " wurde als Streamer registriert!").queue();
                        }
                    }

                    if (arguments[2] != null && arguments[2].equalsIgnoreCase("remove")) {
                        if (arguments[3] == null) {
                            event.getChannel().sendMessage("Entferne einen Twitch Nutzer mit dem Befehl: " + configuration.getBotCommandPrefix() + " twitch remove <channelname>!").queue();
                        } else {
                            String channelName = arguments[3].toLowerCase();
                            TwitchChannelConfigEntry twitchChannelConfigEntry = twitchConfig.getConfig().getChannels().stream().filter(channel -> channel.getChannelName().equalsIgnoreCase(channelName)).findFirst().orElse(null);

                            if (twitchChannelConfigEntry == null || twitchChannelConfigEntry.getGuildIds().contains(event.getGuild().getIdLong())) {
                                event.getChannel().sendMessage(channelName + " ist nicht als Streamer registriert!").queue();
                                return;
                            }

                            if (twitchChannelConfigEntry.getGuildIds().size() == 1) {
                                twitchConfig.getConfig().getChannels().remove(twitchChannelConfigEntry);
                            } else {
                                twitchChannelConfigEntry.getGuildIds().removeIf(value -> value == event.getGuild().getIdLong());
                            }
                            twitchConfig.save();
                            event.getChannel().sendMessage(channelName + " wurde als Streamer entfernt!").queue();
                        }
                    }
                }
            }
        }
    }
}
