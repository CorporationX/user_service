package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.junit.jupiter.api.extension.ExtendWith;
import school.faang.user_service.dto.projectfollower.ProjectFollowerEvent;
import school.faang.user_service.publisher.follower.ProjectFollowerEventPublisher;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectFollowerEventPublisherTest {

    private static final String PROJECT_FOLLOWER_EVENT_TOPIC_NAME = "projectFollowerTopic";

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic projectFollowerTopic;

    @InjectMocks
    private ProjectFollowerEventPublisher projectFollowerEventPublisher;

    private ProjectFollowerEvent projectFollowerEvent;

    @BeforeEach
    void setUp() {
        projectFollowerEvent = new ProjectFollowerEvent(1L, 2L, 3L);

        when(projectFollowerTopic.getTopic()).thenReturn(PROJECT_FOLLOWER_EVENT_TOPIC_NAME);
    }

    @Nested
    @DisplayName("When publishing a valid ProjectFollowerEvent")
    class WhenPublishingValidProjectFollowerEvent {

        @Test
        @DisplayName("Then the event should be sent to the project follower topic")
        void whenEventIsValidThenEventShouldBeSentToProjectFollowerTopic() {
            projectFollowerEventPublisher.publish(projectFollowerEvent);

            verify(redisTemplate).convertAndSend(eq(PROJECT_FOLLOWER_EVENT_TOPIC_NAME), eq(projectFollowerEvent));
        }
    }
}
