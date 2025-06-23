package inhatc.hjs.y2n.youtube.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;

@Configuration
public class YoutubeApiConfig {
    @Value("${youtube.api.key}")
    private String apiKey;

    @Bean
    public YouTube youTube() {
        return new YouTube.Builder(
            new NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            request -> {}
        ).setApplicationName("your-app-name").build();
    }

    public String getApiKey() {
        return apiKey;
    }
}

