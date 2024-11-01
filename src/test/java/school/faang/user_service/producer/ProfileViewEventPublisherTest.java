package school.faang.user_service.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.event.ProfileViewEventDto;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileViewEventPublisherTest {
    @Value("${spring.data.redis.channels.profile}")
    private String profileView;
    @Mock
    private KafkaTemplate<String, Object> redisTemplate;

    @Mock
    private NewTopic channelTopic;

    @InjectMocks
    private ProfileViewEventPublisher profileViewEventPublisher;


    @BeforeEach
    void setUp() {
        when(channelTopic.name()).thenReturn(profileView);
    }

    @Test
//    @DisplayName("Send Event Test")
    public void sendTestEvent() {
        ProfileViewEventDto evt = ProfileViewEventDto.builder().build();
        profileViewEventPublisher.publish(evt);
        verify(redisTemplate).send(channelTopic.name(), evt);
    }
}
