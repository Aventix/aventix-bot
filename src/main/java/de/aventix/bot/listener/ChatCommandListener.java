package de.aventix.bot.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.aventix.bot.config.entry.DiscordApplicationConfig;
import de.aventix.bot.message.EmbededMessageEntity;
import de.aventix.bot.message.MessageController;
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
    private final MessageController messageController;

    @Inject
    public ChatCommandListener(DiscordApplicationConfig config, MessageController messageController) {
        this.config = config;
        this.messageController = messageController;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String[] arguments = event.getMessage().getContentRaw().split(" ", 4);
        if (arguments[0].startsWith(config.getBotCommandPrefix())) {
            Role foundPermissionRole = event.getMember().getRoles().stream().filter(role -> config.getBotPermissions().contains(role.getIdLong())).findAny().orElse(null);
            if (foundPermissionRole != null) {
                if (arguments[1] != null && arguments[1].startsWith("sendembedmessage")) {
                    System.out.println("YESS");
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        EmbededMessageEntity messageEntity = objectMapper.readValue(arguments[3].toString(), EmbededMessageEntity.class);
                        event.getGuild().getChannelById(TextChannel.class, arguments[2]).sendMessageEmbeds(this.messageController.getMessage(messageEntity)).queue();
                        event.getChannel().sendMessage("Erfolgreich embeded message in channel " + arguments[2] + " gesendet!").queue();
                    } catch (JsonProcessingException ignored) {
                    }
                }
            }
        }
    }
}
