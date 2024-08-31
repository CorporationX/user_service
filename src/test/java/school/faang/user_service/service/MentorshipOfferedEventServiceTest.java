package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.events.MentorshipOfferedEvent;
import school.faang.user_service.publishers.EventJsonConverter;
import school.faang.user_service.publishers.MentorshipOfferedPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipOfferedEventServiceTest {
    @Mock
    private MentorshipOfferedPublisher publisher;
    @Mock
    private EventJsonConverter<MentorshipOfferedEvent> converter;
    @InjectMocks
    private MentorshipOfferedEventService service;

    @Test
    public void testPublishMentorshipOfferedEventSuccess() {
        MentorshipRequest request = new MentorshipRequest();
        User requester = new User();
        User receiver = new User();
        requester.setId(1L);
        receiver.setId(2L);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setId(1L);
        when(converter.toJson(any())).thenReturn("{\"id\":1,\"requester\":{\"id\":2}}");
        service.publishEvent(request);
        verify(publisher).publish(any());
    }
}
