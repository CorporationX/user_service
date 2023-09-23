package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.ProfilePicEvent;

@ExtendWith(MockitoExtension.class)
public class ProfilePicEventPublisherTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @InjectMocks
    private ProfilePicEventPublisher profilePicEventPublisher;

    @BeforeEach
    void setUp() {
        profilePicEventPublisher.setProfilePicEventChannel("channel_name");
    }

    @Test
    public void testPublish() throws JsonProcessingException {
        User user = new User();
        user.setId(123L);
        user.setUserProfilePic(UserProfilePic.builder().smallFileId("profile_pic_link").build());

        ProfilePicEvent expectedEvent = ProfilePicEvent.builder()
                .userId(user.getId())
                .title("Handsome")
                .description("You've uploaded your profile picture!")
                .profilePicLink(user.getUserProfilePic().getSmallFileId())
                .build();

        Mockito.when(objectMapper.writeValueAsString(expectedEvent)).thenReturn("jsonString");

        profilePicEventPublisher.publish(user);

        Mockito.verify(objectMapper).writeValueAsString(expectedEvent);
        Mockito.verify(redisMessagePublisher).publish("channel_name", "jsonString");
    }
}
