package de.aventix.bot.message;

import com.google.inject.Singleton;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

/* This class was created by @Aventix on 06.06.2022 at 01:31. */
@Singleton
public class MessageController {
    public String getMessage(String message, Object... args) {
        if (message != null) {
            AtomicReference<String> value = new AtomicReference<>(message);

            if (args.length != 0) {
                int i = 0;

                for (Object argument : args) {
                    if (argument != null) {
                        value.set(value.get().replaceAll("\\{" + i + "}", argument.toString()));
                        i++;
                    }
                }
            }

            return value.get();
        }

        return null;
    }

    public MessageEmbed getMessage(EmbededMessageEntity messageEntity, Object... args) {
        EmbedBuilder builder = new EmbedBuilder();

        if (messageEntity.getTitle() != null) {
            builder.setTitle(getMessage(messageEntity.getTitle().getTitle(), args), getMessage(messageEntity.getTitle().getUrl(), args));
        }

        if (messageEntity.getColor() != null) {
            builder.setColor(new Color(messageEntity.getColor().getR(), messageEntity.getColor().getG(), messageEntity.getColor().getB(), messageEntity.getColor().getA()));
        }

        builder.setDescription(getMessage(messageEntity.getDescription(), args));

        messageEntity.getFields().forEach(field -> builder.addField(getMessage(field.getTitle(), args), getMessage(field.getDescription(), args), field.isInline()));
        messageEntity.getBlankedFields().forEach(builder::addBlankField);

        if (messageEntity.getAuthor() != null) {
            builder.setAuthor(getMessage(messageEntity.getAuthor().getName(), args), getMessage(messageEntity.getAuthor().getUrl(), args), getMessage(messageEntity.getAuthor().getIconUrl(), args));
        }

        if (messageEntity.getFooter() != null) {
            builder.setFooter(getMessage(messageEntity.getFooter().getText(), args), getMessage(messageEntity.getFooter().getIconUrl(), args));
        }

        builder
                .setThumbnail(getMessage(messageEntity.getThumbnail(), args))
                .setImage(getMessage(messageEntity.getImage(), args));

        return builder.build();
    }
}
