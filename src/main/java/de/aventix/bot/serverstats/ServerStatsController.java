package de.aventix.bot.serverstats;

import de.aventix.bot.config.entry.DiscordCacheConfig;
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
        scheduledExecutorService.scheduleAtFixedRate(() -> updateServerStats(jda), 0, cacheConfig.getChannelUpdateRateLimit()+100, TimeUnit.MILLISECONDS);
    }

    public void updateServerStats(JDA jda) {
        int members = 0;

        config.getEntries().forEach((key, value) -> {
            GuildChannel guildChannel = jda.getGuildChannelById(key);
            long maxMembers = guildChannel.getGuild().getMemberCount() - 1;
            long onlineMembers = guildChannel.getGuild().getMembers().stream().filter(member -> !member.getOnlineStatus().equals(OnlineStatus.OFFLINE) && !member.getOnlineStatus().equals(OnlineStatus.INVISIBLE) && !member.getOnlineStatus().equals(OnlineStatus.UNKNOWN)).count() - 1;

            if (canSendChannelUpdate()) {
                cacheConfig.setLastChannelUpdateCount(cacheConfig.getLastChannelUpdateCount() + 1);
                cacheConfig.setLastChannelUpdate(System.currentTimeMillis());
                cacheConfig.save();
                guildChannel.getManager().setName(messageController.getMessage(value, maxMembers, onlineMembers)).queue();
                System.out.println("Updatet Channel name at " + System.currentTimeMillis());
            } else {
                System.out.println("Can't update more guild channel, because discord rate limits just 2 channel updates every 10 minutes!");
            }
        });
    }

    private boolean canSendChannelUpdate() {
        if (cacheConfig.getLastChannelUpdate() == -1) return true;
        if (cacheConfig.getLastChannelUpdateCount() < cacheConfig.getMaxRateLimitRequests()) return true;
        if ((System.currentTimeMillis() - cacheConfig.getLastChannelUpdate()) > cacheConfig.getChannelUpdateRateLimit()) {
            cacheConfig.setLastChannelUpdateCount(0);
            cacheConfig.save();
            return true;
        }

        return false;
    }
}
