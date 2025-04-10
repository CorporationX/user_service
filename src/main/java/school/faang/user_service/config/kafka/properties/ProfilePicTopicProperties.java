package school.faang.user_service.config.kafka.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.topic.profile-pic")
public record ProfilePicTopicProperties(String name, int partitionCount) {}
