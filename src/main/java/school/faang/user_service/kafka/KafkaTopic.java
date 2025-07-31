package school.faang.user_service.kafka;

public enum KafkaTopic {
    USER_CREATE("user.create"),
    USER_UPDATED("user.update");

    private final String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }

    public String getName() {
        return topicName;
    }
}