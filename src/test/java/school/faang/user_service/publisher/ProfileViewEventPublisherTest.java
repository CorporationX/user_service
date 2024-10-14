package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.event.ProfileViewEvent;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class ProfileViewEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private ProfileViewEventPublisher profileViewEventPublisher;

    private ProfileViewEvent profileViewEvent;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        profileViewEvent = new ProfileViewEvent(1L, 1L,
                LocalDateTime.of(1, 1, 1, 1, 1, 1));

        Field field = profileViewEventPublisher.getClass().getDeclaredField("topic");
        field.setAccessible(true);
        field.set(profileViewEventPublisher, "test_profile_view_channel");
    }

    @Test
    void publish() {
        //when
        profileViewEventPublisher.publish(profileViewEvent);

        //then
        Mockito.verify(redisTemplate, Mockito.times(1))
                .convertAndSend(Mockito.eq("test_profile_view_channel"), Mockito.eq(profileViewEvent));
    }
}