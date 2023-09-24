package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.ProfilePicEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfilePicEventPublisherTest {

    @Mock
    private EventMapper eventMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    private ProfilePicEventPublisher profilePicEventPublisher;

    @BeforeEach
    void setUp() {
        profilePicEventPublisher = new ProfilePicEventPublisher(eventMapper, objectMapper, redisMessagePublisher);
        profilePicEventPublisher.setProfilePicEventChannel("pic_channel");
    }

    @Test
    @Description("Publish profile picture event: Positive scenario")
    void testPublishProfilePicEventIsOk() {
        User user = User.builder()
                .id(123L)
                .userProfilePic(UserProfilePic.builder()
                        .smallFileId("1L")
                        .build())
                .build();

        profilePicEventPublisher.publish(user);
        verify(redisMessagePublisher).publish(eq("pic_channel"), any(ProfilePicEvent.class));
    }
}