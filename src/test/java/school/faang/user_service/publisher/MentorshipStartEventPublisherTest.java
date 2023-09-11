package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.redis.RedisMessagePublisher;

@ExtendWith(MockitoExtension.class)
public class MentorshipStartEventPublisherTest {
    @Mock
    private RedisMessagePublisher redisMessagePublisher;
    @InjectMocks
    private MentorshipStartEventPublisher mentorshipStartEventPublisher;

    @Test
    public void shouldPublishMentorshipEvent() {
        long mentorId = 1L;
        long menteeId = 2L;

        mentorshipStartEventPublisher.publishMentorshipEvent(mentorId, menteeId);
        Mockito.verify(redisMessagePublisher)
                .publish(Mockito.any(), Mockito.any());
    }
}
