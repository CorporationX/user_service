package school.faang.user_service.publisher;

import school.faang.user_service.config.redis.RedisProperties;

public class TestRedisPropertiesFactory {

    public static RedisProperties createDefaultRedisProperties() {
        return new RedisProperties(
                "localhost",
                6379,
                new RedisProperties.Channel(
                        "mentorshipChannel",
                        "subscription_event_channel",
                        "recommendationChannel",
                        "userBanChannel",
                        "mentorshipRequest",
                        "followerChannel",
                        "eventStartEventChannel"
                )
        );
    }
}