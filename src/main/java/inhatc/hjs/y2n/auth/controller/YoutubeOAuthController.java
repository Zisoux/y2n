package inhatc.hjs.y2n.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class YoutubeOAuthController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/")
    public String home() {
        return "token";
    }

    @GetMapping("/youtube/token")
    public String getYoutubeToken(
            OAuth2AuthenticationToken authentication,
            Model model) {

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

        String accessToken = client.getAccessToken().getTokenValue();
        model.addAttribute("accessToken", accessToken);
        return "token";
    }
}
