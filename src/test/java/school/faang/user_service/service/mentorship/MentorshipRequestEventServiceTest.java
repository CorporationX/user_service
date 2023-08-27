package school.faang.user_service.service.mentorship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.mentorship.MentorshipRequestEventDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.publisher.MentorshipOfferEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestEventServiceTest {
    private static final String TEST_TOPIC_NAME = "test-topic";
    @Mock
    private MentorshipOfferEventPublisher offerEventPublisher;
    @Mock
    private ChannelTopic mentorshipOfferTopic;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private MentorshipRequestEventService service;

    @Test
    void testGetMentorshipRequestEventDto() {
        MentorshipRequest request = MentorshipRequest.builder()
                .id(1L)
                .description("description")
                .requester(User.builder().id(2L).build())
                .receiver(User.builder().id(3L).build())
                .build();

        MentorshipRequestEventDto eventDto = service.getMentorshipRequestEventDto(request);

        assertEquals(1L, eventDto.getId());
        assertEquals(2L, eventDto.getRequesterId());
        assertEquals(3L, eventDto.getReceiverId());
    }

    @Test
    void testPublishEventToChannel() throws JsonProcessingException {
        MentorshipRequestEventDto eventDto = MentorshipRequestEventDto.builder()
                .id(1L)
                .requesterId(2L)
                .receiverId(3L)
                .build();
        String message = "{\"id\":1,\"requesterId\":2,\"receiverId\":3}";

        when(mentorshipOfferTopic.getTopic()).thenReturn(TEST_TOPIC_NAME);
        when(objectMapper.writeValueAsString(eventDto)).thenReturn(message);

        service.publishEventToChannel(eventDto);

        verify(offerEventPublisher).publish(mentorshipOfferTopic.getTopic(), message);
    }

    @Test
    void testPublishEventToChannelWithNullDto() {
        assertThrows(NullPointerException.class, () -> service.publishEventToChannel(null));
    }
}