package school.faang.user_service.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.model.event.SearchAppearanceEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SearchAppearanceEventPublisherTest {
    @Mock
    private KafkaTemplate<String, Object> redisTemplate;

    @Mock
    private NewTopic searchAppearanceTopic;

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
        verify(redisTemplate).send(searchAppearanceTopic.name(), event);
    }
}
