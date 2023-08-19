package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.redis.FollowerEventDto;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FollowerEventPublisherTest {

    @Mock
    private ObjectMapper mapper;

    @Mock
    private MessagePublisher publisher;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    @Test
    void sendEventTest() throws JsonProcessingException {
        FollowerEventDto followerEventDto = FollowerEventDto.builder()
                .followerId(1)
                .followerId(2)
                .subscriptionTime(LocalDateTime.now())
                .build();

        String json = "{\"followerId\":\"123\",\"followeeId\":\"456\",\"subscriptionTime\":\"2023-08-18T10:30:00\"}";

        when(mapper.writeValueAsString(followerEventDto)).thenReturn(json);

        followerEventPublisher.sendEvent(followerEventDto);

        verify(mapper).writeValueAsString(followerEventDto);
        verify(publisher).publish(json);
    }
}