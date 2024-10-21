package school.faang.user_service.publisher;

import io.lettuce.core.RedisBusyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.event.MentorshipAcceptedEventDto;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipAcceptedEventPublisherTest {

    @Mock
    private RedisTemplate<String, MentorshipAcceptedEventDto> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;

    private MentorshipAcceptedEventDto mentorshipAcceptedEventDto;

    @BeforeEach
    void setUp() {
        mentorshipAcceptedEventDto = MentorshipAcceptedEventDto.builder().build();
    }

    @Test
    @DisplayName("Publish message")
    void mentorshipAcceptedEventPublisherTest_publishMessage() {
        when(channelTopic.getTopic()).thenReturn("test");

        mentorshipAcceptedEventPublisher.publish(mentorshipAcceptedEventDto);

        verify(channelTopic).getTopic();
        verify(redisTemplate).convertAndSend("test", mentorshipAcceptedEventDto);
    }

    @Test
    @DisplayName("Publish message with error")
    void mentorshipAcceptedEventPublisherTest_publishMessageWithError() {
        when(channelTopic.getTopic()).thenReturn("test");
        when(redisTemplate.convertAndSend("test", mentorshipAcceptedEventDto)).thenThrow(
                new RedisBusyException("message"));

        assertThrows(RuntimeException.class,
                () -> mentorshipAcceptedEventPublisher.publish(mentorshipAcceptedEventDto));

        verify(channelTopic).getTopic();
        verify(redisTemplate).convertAndSend("test", mentorshipAcceptedEventDto);
    }
}
