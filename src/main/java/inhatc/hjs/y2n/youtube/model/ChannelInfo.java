package inhatc.hjs.y2n.youtube.model;

public class ChannelInfo {
    private String id;
    private String title;
    private String category;
    private Long subscriberCount;

    // getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubscriberCount(Long subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public Long getSubscriberCount() {
        return subscriberCount;
    }
}
