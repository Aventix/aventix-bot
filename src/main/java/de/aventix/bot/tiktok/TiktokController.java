package de.aventix.bot.tiktok;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class TiktokController {
    private final TiktokConfig config;
    private final String redirectUrl = "http://localhost:8080/callback";

    @Inject
    public TiktokController(TiktokConfig config) {
        this.config = config;
    }

    public void startController() {
        System.out.println("Checking now for new videos...");
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::runCheckTask, 0, 10, TimeUnit.MINUTES);
    }

    private void runCheckTask() {

    }

}
