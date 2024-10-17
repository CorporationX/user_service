package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.GoalCompletedEventDto;

@Component
public class GoalCompletedEventPublisher implements MessagePublisher<GoalCompletedEventDto> {

    private final RedisTemplate<String, GoalCompletedEventDto> redisTemplate;
    private final ChannelTopic topic;

    public GoalCompletedEventPublisher(RedisTemplate<String, GoalCompletedEventDto> redisTemplate,
                                       @Qualifier("goalCompletedTopic") ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(GoalCompletedEventDto goalCompletedEventDto) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), goalCompletedEventDto);
        } catch (Exception e) {
            throw new RuntimeException("Not able to publish message to topic %s".formatted(topic.getTopic()), e);
        }
    }
}