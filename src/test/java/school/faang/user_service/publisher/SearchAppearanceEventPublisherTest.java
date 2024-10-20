package school.faang.user_service.publisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.model.event.SearchAppearanceEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SearchAppearanceEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic searchAppearanceTopic;

    @InjectMocks
    private SearchAppearanceEventPublisher searchAppearanceEventPublisher;

    @Test
    @DisplayName("Search Appearance Event Test")
    void testSendEvent() {
        // given
        SearchAppearanceEvent event = SearchAppearanceEvent.builder().build();
        // when
        searchAppearanceEventPublisher.publish(event);
        // then
        verify(redisTemplate).convertAndSend(searchAppearanceTopic.getTopic(), event);
    }
}
