package school.faang.user_service.kafka;

public enum KafkaTopic {
    USER_CREATED("user.created"),
    USER_UPDATED("user.updated");

    private final String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }

    public String getName() {
        return topicName;
    }
}