package de.aventix.bot.serverstats;

import de.aventix.bot.config.entry.DiscordCacheConfig;
import de.aventix.bot.config.entry.DiscordGuildCacheConfig;
import de.aventix.bot.message.MessageController;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class ServerStatsController {
    private final ServerStatsConfig config;
    private final MessageController messageController;
    private final DiscordCacheConfig cacheConfig;

    @Inject
    public ServerStatsController(ServerStatsConfig config, MessageController messageController, DiscordCacheConfig cacheConfig) {
        this.config = config;
        this.messageController = messageController;
        this.cacheConfig = cacheConfig;
    }

    public void runServerStatsUpdateTask(JDA jda) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        cacheConfig.getGuilds().forEach(guild -> scheduledExecutorService.scheduleAtFixedRate(() -> {
            GuildServerStatsConfig statsEntry = config.getGuilds().stream().filter(entry -> entry.getGuildId() == guild.getGuildId()).findFirst().orElse(null);
            if (statsEntry != null) {
                updateServerStats(jda, statsEntry);
            }
        }, 0, guild.getChannelUpdateRateLimit() + 100, TimeUnit.MILLISECONDS));
    }

    public void updateServerStats(JDA jda, GuildServerStatsConfig guild) {
        int members = 0;

        guild.getEntries().forEach((key, value) -> {
            if (jda.getGuildById(guild.getGuildId()) != null) {

                GuildChannel guildChannel = jda.getGuildById(guild.getGuildId()).getGuildChannelById(key);
                long maxMembers = guildChannel.getGuild().getMemberCount() - 1;
                long onlineMembers = guildChannel.getGuild().getMembers().stream().filter(member -> !member.getOnlineStatus().equals(OnlineStatus.OFFLINE) && !member.getOnlineStatus().equals(OnlineStatus.INVISIBLE) && !member.getOnlineStatus().equals(OnlineStatus.UNKNOWN)).count() - 1;

                if (canSendChannelUpdate(guild.getGuildId())) {
                    DiscordGuildCacheConfig discordGuildCacheConfig = cacheConfig.getGuilds().stream().filter(entry -> entry.getGuildId() == guild.getGuildId()).findFirst().orElse(null);

                    if (discordGuildCacheConfig != null) {
                        discordGuildCacheConfig.setLastChannelUpdateCount(discordGuildCacheConfig.getLastChannelUpdateCount() + 1);
                        discordGuildCacheConfig.setLastChannelUpdate(System.currentTimeMillis());
                        cacheConfig.save();
                        guildChannel.getManager().setName(messageController.getMessage(value, maxMembers, onlineMembers)).queue();
                        System.out.println("Updatet Channel name at " + System.currentTimeMillis());
                    }
                } else {
                    System.out.println("Can't update more guild channel, because discord rate limits just 2 channel updates every 10 minutes!");
                }
            }
        });
    }

    private boolean canSendChannelUpdate(long guildId) {
        DiscordGuildCacheConfig discordGuildCacheConfig = cacheConfig.getGuilds().stream().filter(guild -> guild.getGuildId() == guildId).findFirst().orElse(null);

        if (discordGuildCacheConfig == null) return false;
        if (discordGuildCacheConfig.getLastChannelUpdate() == -1) return true;
        if (discordGuildCacheConfig.getLastChannelUpdateCount() < discordGuildCacheConfig.getMaxRateLimitRequests())
            return true;
        if ((System.currentTimeMillis() - discordGuildCacheConfig.getLastChannelUpdate()) > discordGuildCacheConfig.getChannelUpdateRateLimit()) {
            discordGuildCacheConfig.setLastChannelUpdateCount(0);
            cacheConfig.save();
            return true;
        }

        return false;
    }
}
