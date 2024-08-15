package school.faang.user_service.publishers.recommendationReceived;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.recommendationReceived.RecommendationReceivedEvent;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic channelTopic;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private RecommendationPublisher recommendationPublisher;
    private RecommendationReceivedEvent recommendationReceivedEvent;
    String jsonString;

    @BeforeEach
    void init() {
        recommendationReceivedEvent = RecommendationReceivedEvent.builder()
                .authorId(1L)
                .receivedId(2L)
                .recommendationId(3L)
                .build();

        jsonString = "{\"authorId\":1L,\"receivedId\":2L,\"recommendationId\":3L\"}";
    }

    @Test
    void testPublishWriteValueAsString() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(Mockito.any(RecommendationReceivedEvent.class)))
                .thenReturn(jsonString);

        recommendationPublisher.publish(recommendationReceivedEvent);
    }

    @Test
    void testPublishConvertAndSend() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(Mockito.any(RecommendationReceivedEvent.class)))
                .thenReturn(jsonString);
        when(channelTopic.getTopic())
                .thenReturn("testTopic");

        recommendationPublisher.publish(recommendationReceivedEvent);

        Mockito.verify(redisTemplate).convertAndSend("testTopic", jsonString);
    }
}