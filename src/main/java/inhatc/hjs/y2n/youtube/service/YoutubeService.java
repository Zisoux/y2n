package inhatc.hjs.y2n.youtube.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

import inhatc.hjs.y2n.youtube.config.YoutubeApiConfig;
import inhatc.hjs.y2n.youtube.model.ChannelInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class YoutubeService {
    @Autowired
    private YouTube youTube;
    @Autowired
    private YoutubeApiConfig config;

    private int quotaUsed = 0;

    private void logQuotaUsage(int used) {
    quotaUsed += used;
    System.out.println("현재까지 사용한 쿼터: " + quotaUsed);
}

    public List<ChannelInfo> getMySubscriptionsWithQuotaGuard(String accessToken)
            throws IOException, InterruptedException {
        List<ChannelInfo> channels = new ArrayList<>();
        String nextPageToken = null;
        
        do {
            try {
                YouTube.Subscriptions.List request = youTube.subscriptions()
                        .list("snippet,contentDetails")
                        .setMine(true)
                        .setMaxResults(50L)
                        .setOauthToken(accessToken)
                        .setPageToken(nextPageToken)
                        .setFields("items(snippet/resourceId/channelId),nextPageToken");
                SubscriptionListResponse response = request.execute();
                quotaUsed += 1;
                logQuotaUsage(quotaUsed);

                List<String> channelIds = response.getItems().stream()
                        .map(s -> s.getSnippet().getResourceId().getChannelId())
                        .collect(Collectors.toList());

                if (!channelIds.isEmpty()) {
                    YouTube.Channels.List channelRequest = youTube.channels()
                            .list("snippet,statistics")
                            .setId(String.join(",", channelIds))
                            .setKey(config.getApiKey())
                            .setFields("items(id,snippet/title,statistics/subscriberCount)");
                    ChannelListResponse channelResponse = channelRequest.execute();
                    quotaUsed += 1;
                    logQuotaUsage(quotaUsed);

                    for (Channel channel : channelResponse.getItems()) {
                        ChannelInfo info = new ChannelInfo();
                        info.setId(channel.getId());
                        info.setTitle(channel.getSnippet().getTitle());
                        info.setCategory(null);
                        info.setSubscriberCount(channel.getStatistics().getSubscriberCount() != null
                                ? channel.getStatistics().getSubscriberCount().longValue()
                                : null);
                        channels.add(info);
                    }
                }
                nextPageToken = response.getNextPageToken();

                Thread.sleep(200); // 딜레이로 쿼터 소진 속도 완화

            } catch (GoogleJsonResponseException e) {
                if (e.getStatusCode() == 403 || e.getStatusCode() == 429) {
                    throw new RuntimeException("Youtube API 쿼터 초과. 내일 다시 시도해주세요.");
                } else {
                    throw e;
                }
            }
        } while (nextPageToken != null);

        return channels;
    }

    public List<ChannelInfo> sortChannels(List<ChannelInfo> channels, String sortBy) {
        if ("category".equalsIgnoreCase(sortBy)) {
            channels.sort(Comparator.comparing(ChannelInfo::getCategory, Comparator.nullsLast(String::compareTo)));
        } else if ("subscribers".equalsIgnoreCase(sortBy)) {
            channels.sort(Comparator.comparing(ChannelInfo::getSubscriberCount, Comparator.nullsLast(Long::compareTo))
                    .reversed());
        }
        return channels;
    }
}
