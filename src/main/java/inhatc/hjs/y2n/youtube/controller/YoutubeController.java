package inhatc.hjs.y2n.youtube.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import inhatc.hjs.y2n.notion.service.NotionService;
import inhatc.hjs.y2n.youtube.model.ChannelInfo;
import inhatc.hjs.y2n.youtube.service.YoutubeService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/youtube")
@RequiredArgsConstructor
public class YoutubeController {

    private final YoutubeService youtubeService;
    private final NotionService notionService;

    @GetMapping("/form")
    public String showForm(Model model) {
        return "youtube_form";
    }

    @GetMapping("/save-subs")
    public String saveSubscriptions(
            @RequestParam String accessToken,
            @RequestParam(defaultValue = "subscribers") String sortBy,
            Model model) throws IOException, InterruptedException {
        List<ChannelInfo> channels = youtubeService.getMySubscriptionsWithQuotaGuard(accessToken);
        channels = youtubeService.sortChannels(channels, sortBy);
        notionService.saveToNotion(channels);
        model.addAttribute("message", "Notion에 저장 완료!");
        return "youtube_form";
    }

}
