package school.faang.user_service.publisher;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.event.recomendationRerquested.RecommendationRequestedEvent;
import school.faang.user_service.exception.event.EventPublishingException;
import school.faang.user_service.messaging.publisher.recommendationRequested.RecommendationRequestedEventPublisher;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendationRequestedEventPublisherTest {

  private static final String SERIALIZED_STRING = "serialized string";

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @Mock
  private ChannelTopic channelTopic;

  @InjectMocks
  private RecommendationRequestedEventPublisher eventPublisher;

  private RecommendationRequestedEvent recommendationRequestedEvent;

  @BeforeEach
  void setUp() {
    recommendationRequestedEvent = RecommendationRequestedEvent.builder()
        .message(UUID.randomUUID().toString())
        .requesterId(1L)
        .receiverId(2L)
        .localDateTime(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("Проверка публикации топика в Redis")
  void testPublicationTopicInRedis() throws JsonProcessingException {
    when(objectMapper.writeValueAsString(recommendationRequestedEvent)).thenReturn(SERIALIZED_STRING);
    when(channelTopic.getTopic()).thenReturn("recommendation_requested_channel");

    eventPublisher.publish(recommendationRequestedEvent);

    verify(objectMapper).writeValueAsString(recommendationRequestedEvent);
    verify(redisTemplate).convertAndSend(channelTopic.getTopic(), SERIALIZED_STRING);

  }

  @Test
  @DisplayName("Проверка выброса исключения при сериализации event-а")
  void publishThrowsEventPublishingException() throws Exception {
    when(objectMapper.writeValueAsString(recommendationRequestedEvent))
        .thenThrow(new JsonProcessingException("Serialization error") {});

    assertThrows(EventPublishingException.class, () -> eventPublisher.publish(recommendationRequestedEvent));

    verify(objectMapper).writeValueAsString(recommendationRequestedEvent);
    verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
  }

}