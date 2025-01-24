package de.aventix.bot.tiktok;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
public class TiktokController {
    private final TiktokConfig config;

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
        this.config.channelWatchers.forEach(channel -> {
            System.out.println("Run channel: " + channel.getChannelName());
            System.out.println("Last Known video: " + channel.getLastVideoId());
            String tiktokUrl = "https://www.tiktok.com/@" + channel.getChannelName();
            try {
                String pageContent = fetchPageContent(tiktokUrl);
                String videoId = extractFirstVideoId(pageContent);

                System.out.println("LATEST VIDEO ID: " + videoId);
                if (videoId != null && !videoId.equals(channel.getLastVideoId())) {
                    channel.setLastVideoId(videoId);
                    System.out.println("VIDEO HOCHGELADEN: " + tiktokUrl + "/video/" + videoId);
                    //sendDiscordMessage(client, "Neues Video hochgeladen: " + tiktokUrl + "/video/" + videoId);
                }

                if (pageContent.contains("\"isLive\":true") && !channel.isLive()) {
                    System.out.println("LIVE!");
                    channel.setLive(true);
                    //sendDiscordMessage(client, TIKTOK_USERNAME + " ist gerade live! Schau hier vorbei: " + tiktokUrl);
                } else {
                    channel.setLive(false);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        this.config.save();
    }

    private String fetchPageContent(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private static String extractFirstVideoId(String pageContent) {
        String videoPrefix = "/video/";
        int index = pageContent.indexOf(videoPrefix);
        if (index != -1) {
            int start = index + videoPrefix.length();
            int end = pageContent.indexOf('"', start);
            if (end != -1) {
                return pageContent.substring(start, end);
            }
        }
        return null;
    }
}
