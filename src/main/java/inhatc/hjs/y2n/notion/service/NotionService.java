package inhatc.hjs.y2n.notion.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import inhatc.hjs.y2n.youtube.model.ChannelInfo;

@Service
public class NotionService {
    @Value("${notion.api.token}")
    private String notionApiToken;
    @Value("${notion.database.id}")
    private String databaseId;

    public void saveToNotion(List<ChannelInfo> channels) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.notion.com/v1/pages";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(notionApiToken);
        headers.set("Notion-Version", "2022-06-28");
        headers.setContentType(MediaType.APPLICATION_JSON);

        for (ChannelInfo channel : channels) {
            Map<String, Object> properties = new HashMap<>();
            properties.put("Name", Map.of("rich_text", 
                List.of(Map.of("text", Map.of("content", channel.getTitle() != null ? channel.getTitle() : "")))));
            // Category 필드 제거
            properties.put("Subscribers",
                    Map.of("number", channel.getSubscriberCount() != null ? channel.getSubscriberCount() : 0L));

            Map<String, Object> payload = Map.of(
                    "parent", Map.of("database_id", databaseId),
                    "properties", properties);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(url, entity, String.class);
        }
    }
}
