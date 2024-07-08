package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultMentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorshipRequestMapper mapper;

    @InjectMocks
    private DefaultMentorshipRequestService sut;

    @Test
    void testRequestMentorship_throws_exception_when_receiver_and_requester_ids_are_same() {
        MentorshipRequestDto dto = new MentorshipRequestDto();

        assertThatThrownBy(() -> sut.requestMentorship(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessages.SELF_MENTORSHIP);
    }

    @Test
    void testRequestMentorship_throws_exception_when_receiver_not_found() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        when(userRepository.existsById(dto.getReceiverId())).thenReturn(false);
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(true);

        assertThatThrownBy(() -> sut.requestMentorship(dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(ExceptionMessages.RECEIVER_NOT_FOUND);
    }

    @Test
    void testRequestMentorship_throws_exception_when_requester_not_found() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        when(userRepository.existsById(dto.getReceiverId())).thenReturn(true);
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(false);

        assertThatThrownBy(() -> sut.requestMentorship(dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(ExceptionMessages.REQUESTER_NOT_FOUND);
    }

    @Test
    void testRequestMentorship_throws_exception_when_mentorship_request_recently_happened() {
        MentorshipRequest request = new MentorshipRequest();
        request.setCreatedAt(LocalDateTime.now().minusMonths(1));
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        when(userRepository.existsById(dto.getReceiverId())).thenReturn(true);
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> sut.requestMentorship(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ExceptionMessages.MENTORSHIP_FREQUENCY);
    }

    @Test
    void testRequestMentorship_creates_entity() {
        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        MentorshipRequestDto incomingDto = new MentorshipRequestDto();
        incomingDto.setRequesterId(requester.getId());
        incomingDto.setReceiverId(receiver.getId());
        incomingDto.setDescription("Sample description");

        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        when(userRepository.existsById(incomingDto.getReceiverId())).thenReturn(true);
        when(userRepository.existsById(incomingDto.getRequesterId())).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(incomingDto.getRequesterId(), incomingDto.getReceiverId())).thenReturn(Optional.empty());
        when(mentorshipRequestRepository.create(requester.getId(), receiver.getId(), incomingDto.getDescription())).thenReturn(request);
        when(mapper.toDto(request)).thenReturn(incomingDto);

        var resultingDto = sut.requestMentorship(incomingDto);

        assertNotNull(resultingDto);
        assertEquals(incomingDto.getReceiverId(), resultingDto.getReceiverId());
        assertEquals(incomingDto.getRequesterId(), resultingDto.getRequesterId());
        assertEquals(incomingDto.getDescription(), resultingDto.getDescription());
    }
}