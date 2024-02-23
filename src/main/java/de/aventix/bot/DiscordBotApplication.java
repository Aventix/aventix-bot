package de.aventix.bot;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.aventix.bot.config.ConfigRegistrationProcedure;
import de.aventix.bot.config.entry.DiscordApplicationConfig;
import de.aventix.bot.filter.TypeFilter;
import de.aventix.bot.listener.DiscordListener;
import de.aventix.bot.searcher.TypeSearcher;
import de.aventix.bot.twitch.StreamController;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* This class was created by @Aventix on 05.06.2022 at 23:34. */
public class DiscordBotApplication {
    public static JDA jda = null;

    public static void main(String[] args) {
        try {

            Injector injector = Guice.createInjector(new AbstractModule() {
                protected void configure() {
                    this.bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
                    this.bind(ClassLoader.class).toInstance(this.getClass().getClassLoader());
                }
            });

            injector.getInstance(ConfigRegistrationProcedure.class)
                    .load(DiscordBotApplication.class.getPackage().getName());

            DiscordApplicationConfig config = injector.getInstance(DiscordApplicationConfig.class);

            if (config.getBotToken() == null || config.getBotToken().isEmpty()) {
                System.out.println("Can't load Bot, there is no valid Bot Token!");
                return;
            }

            JDABuilder discordBuilder = JDABuilder
                    .create(config.getBotToken(), Arrays.asList(GatewayIntent.values()))
                    .setMemberCachePolicy(MemberCachePolicy.ALL);

            injector.getInstance(TypeSearcher.class).filter(new String[]{DiscordBotApplication.class.getPackage().getName()}, TypeFilter.annotatedWith(DiscordListener.class), TypeFilter.subClassOf(ListenerAdapter.class)).forEach(clazz -> {
                discordBuilder.addEventListeners(injector.getInstance(clazz));
                System.out.println("Successfully registered Listener: " + clazz.getSimpleName());
            });

            jda = discordBuilder.build().awaitReady();

            //injector.getInstance(ServerStatsController.class).runServerStatsUpdateTask(jda);
            injector.getInstance(StreamController.class).startStreamController();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
