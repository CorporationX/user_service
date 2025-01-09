package school.faang.user_service.publisher;


import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
@Component
@Slf4j

public class RecommendationEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationTopic;
    private final ChannelTopic mentorshipChannel;

    public RecommendationEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("recommendationTopic") ChannelTopic recommendationTopic,
            @Qualifier("mentorshipChannel") ChannelTopic mentorshipChannel
    ) {
        this.redisTemplate = redisTemplate;
        this.recommendationTopic = recommendationTopic;
        this.mentorshipChannel = mentorshipChannel;
    }

    public void publishToRecommendation(Object message) {
        redisTemplate.convertAndSend(recommendationTopic.getTopic(), message);
    }

    public void publishToMentorship(Object message) {
        redisTemplate.convertAndSend(mentorshipChannel.getTopic(), message);
    }

}
