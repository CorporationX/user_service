package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.publisher.ProfileViewEvent;
import school.faang.user_service.exception.EventSerializationException;

@ExtendWith(MockitoExtension.class)
class ProfileViewEventPublisherTest {

    @Mock private ObjectMapper objectMapper;

    @Mock private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks private ProfileViewEventPublisher profileViewEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(profileViewEventPublisher, "channel", "test-channel");
    }

    @Test
    @DisplayName("The test must success sent message to Redis")
    void testPublishSuccess() throws Exception {
        ProfileViewEvent profileViewEvent =
                ProfileViewEvent.builder()
                        .profileId(2)
                        .viewId(5)
                        .timestamp(LocalDateTime.of(2025, Month.FEBRUARY, 23, 12, 0, 0))
                        .build();
        String jsonEvent = "{\"profileId\":2,\"viewId\":5,\"timestamp\":\"2025-02-23T12:00:00\"}";

        Mockito.when(objectMapper.writeValueAsString(profileViewEvent)).thenReturn(jsonEvent);

        profileViewEventPublisher.publish(profileViewEvent);

        Mockito.verify(redisTemplate, Mockito.times(1)).convertAndSend("test-channel", jsonEvent);
    }

    @Test
    @DisplayName("The test must return exception when serialization failed")
    void testPublishFailed() throws Exception {
        ProfileViewEvent profileViewEvent = ProfileViewEvent.builder().build();

        Mockito.doThrow(new JsonMappingException(null, "error serialization"))
                .when(objectMapper)
                .writeValueAsString(Mockito.any());

        Assertions.assertThrows(
                EventSerializationException.class,
                () -> profileViewEventPublisher.publish(profileViewEvent));
    }
}
