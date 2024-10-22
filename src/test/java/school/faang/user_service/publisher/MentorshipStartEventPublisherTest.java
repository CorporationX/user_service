package school.faang.user_service.publisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.event.MentorshipStartEvent;
import school.faang.user_service.publisher.mentorshipStart.MentorshipStartEventPublisher;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipStartEventPublisherTest {

    @InjectMocks
    private MentorshipStartEventPublisher mentorshipStartEventPublisher;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private ChannelTopic mentorshipStartEventTopic;

    private static final String MENTORSHIP_STAR_EVENT_TOPIC = "MentorshipStartEventTopic";

    @Test
    @DisplayName("Успешная отправка message")
    public void whenPublishEventShouldSuccess() {
        MentorshipStartEvent event = MentorshipStartEvent.builder().build();
        when(mentorshipStartEventTopic.getTopic()).thenReturn(MENTORSHIP_STAR_EVENT_TOPIC);

        mentorshipStartEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(MENTORSHIP_STAR_EVENT_TOPIC, event);
    }
}