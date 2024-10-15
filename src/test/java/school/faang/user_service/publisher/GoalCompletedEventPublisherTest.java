package school.faang.user_service.publisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.goal.event.GoalCompletedEventDto;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GoalCompletedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Test
    @DisplayName("Send Event Test")
    void publish_isOk() {
        // given
        var goalCompletedEvent = GoalCompletedEventDto.builder().build();
        // when
        goalCompletedEventPublisher.publish(goalCompletedEvent);
        // then
        verify(redisTemplate).convertAndSend(channelTopic.getTopic(), goalCompletedEvent);
    }
}