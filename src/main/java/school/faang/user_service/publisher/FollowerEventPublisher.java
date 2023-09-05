package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEventDto;
import school.faang.user_service.dto.redis.EventRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.time.LocalDateTime;

@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEventDto>{
    private final ChannelTopic topicFollower;
    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                  ChannelTopic topicFollower) {
        super(redisTemplate, objectMapper);
        this.topicFollower = topicFollower;
    }

    public void publish(FollowerEventDto followerEventDto) {
        followerEventDto.setSubscriptionTime(LocalDateTime.now());
        publishInTopic(topicFollower, followerEventDto);
    }
}
