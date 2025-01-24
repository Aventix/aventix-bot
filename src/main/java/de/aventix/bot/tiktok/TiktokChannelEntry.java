package de.aventix.bot.tiktok;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TiktokChannelEntry {
    private String channelName = "tiktok";
    private boolean watchStream = true;
    private boolean watchVideos = true;
    private String lastVideoId = null;
    private boolean isLive = false;
}
