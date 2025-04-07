package school.faang.user_service.service.mentorship;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestEventDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.publisher.EventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @InjectMocks
    MentorshipRequestService requestService;

    @Mock
    private MentorshipRequestRepository requestRepository;

    @Mock
    private MentorshipRequestValidator requestValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventPublisher publisher;

    private MentorshipRequest requestEntity;
    private MentorshipRequestDto requestDto;
    private User requester;
    private User receiver;

    @BeforeEach
    public void init() {
        long requesterId = 5L;
        long receiverId = 7L;

        requester = new User();
        requester.setId(requesterId);
        requester.setSentMentorshipRequests(new ArrayList<>());

        receiver = new User();
        receiver.setId(receiverId);

        requestEntity = new MentorshipRequest();
        requestEntity.setId(1L);
        requestEntity.setDescription("Mentorship Request");
        requestEntity.setRequester(requester);
        requestEntity.setReceiver(receiver);
    }

    @Test
    public void testRequestMentorship() {
        long requesterId = requestEntity.getRequester().getId();
        long receiverId = requestEntity.getReceiver().getId();

        Mockito.when(requestValidator.validateLastRequestData(requesterId, receiverId)).thenReturn(true);
        Mockito.when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        Mockito.when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        Mockito.when(requestRepository.save(any(MentorshipRequest.class))).thenReturn(requestEntity);

        MentorshipRequest result = requestService.requestMentorship(requestEntity);

        assertNotNull(result);
        assertEquals(requestEntity, result);
        Mockito.verify(userRepository, times(1)).save(requester);
        Mockito.verify(requestRepository, times(1)).save(requestEntity);

        ArgumentCaptor<MentorshipRequestEventDto> eventCaptor = ArgumentCaptor.forClass(MentorshipRequestEventDto.class);
        Mockito.verify(publisher, times(1))
                .publishEvent(eq("mentorship_request"), eventCaptor.capture());

        MentorshipRequestEventDto capturedEvent = eventCaptor.getValue();
        assertEquals(requesterId, capturedEvent.getRequesterId());
        assertEquals(receiverId, capturedEvent.getMentorId());
        assertEquals(requestEntity.getId(), capturedEvent.getRequestId());
    }

    @Test
    public void testGetRequestMentorship() {
    }

    @Test
    public void testAcceptRequestMentorship() {}

    @Test
    public void testRejectRequestMentorship() {}
}
