package school.faang.user_service.service.mentorship;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filter.mentorship.MentorshipRequestStatusFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @Mock
    private MentorshipRequestStatusFilter statusFilter;

    @Captor
    private ArgumentCaptor<MentorshipRequestDto> dtoCaptor;

    @InjectMocks
    private DefaultMentorshipRequestService sut;

    @Test
    void testRequestMentorship_throws_exception_when_database_write_fails() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);

        when(userRepository.existsById(dto.getReceiverId())).thenReturn(true);
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(true);
        when(mapper.toEntity(dto)).thenReturn(new MentorshipRequest());
        when(mentorshipRequestRepository.save(new MentorshipRequest())).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> sut.requestMentorship(dto))
                .isInstanceOf(PersistenceException.class)
                .hasMessage(ExceptionMessages.FAILED_PERSISTENCE);
    }

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

        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(requester.getId());
        dto.setReceiverId(receiver.getId());
        dto.setDescription("Sample description");

        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setDescription("Sample description");
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        when(userRepository.existsById(dto.getReceiverId())).thenReturn(true);
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId())).thenReturn(Optional.empty());
        when(mapper.toEntity(dto)).thenReturn(request);
        when(mentorshipRequestRepository.save(request)).thenReturn(request);
        when(mapper.toDto(request)).thenReturn(dto);

        var result = sut.requestMentorship(dto);

        verify(mapper, times(1)).toEntity(dtoCaptor.capture());
        assertNotNull(result);
        assertEquals(RequestStatus.PENDING, dtoCaptor.getValue().getRequestStatus());
        assertEquals(RequestStatus.PENDING, result.getRequestStatus());
        assertEquals(dto.getRequesterId(), result.getRequesterId());
        assertEquals(dto.getReceiverId(), result.getReceiverId());
    }

    @Test
    void testGetRequests_throws_exception_when_database_read_fails() {
        when(mentorshipRequestRepository.findAll()).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> sut.getRequests(null))
                .isInstanceOf(PersistenceException.class)
                .hasMessage(ExceptionMessages.FAILED_RETRIEVAL);
    }

    @Test
    void testGetRequests_should_return_all_requests_when_filter_is_null() {
        var dto = new MentorshipRequestDto();
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(new MentorshipRequest(),
                new MentorshipRequest(), new MentorshipRequest()));
        when(mapper.toDto(any(MentorshipRequest.class))).thenReturn(dto);

        var result = sut.getRequests(null);

        assertEquals(3, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testGetRequests_should_return_filtered_requests_when_filters_apply() {
        ReflectionTestUtils.setField(sut, "mentorshipRequestFilters", List.of(statusFilter));

        var req1 = new MentorshipRequest();
        req1.setStatus(RequestStatus.PENDING);
        var req2 = new MentorshipRequest();
        req2.setStatus(RequestStatus.ACCEPTED);
        var req3 = new MentorshipRequest();
        req3.setStatus(RequestStatus.PENDING);

        var dto = new MentorshipRequestDto();
        dto.setRequestStatus(RequestStatus.PENDING);

        var filters = new RequestFilterDto();
        filters.setRequestStatusPattern("PENDING");

        when(mentorshipRequestRepository.findAll()).thenReturn(List.of());
        when(statusFilter.isApplicable(filters)).thenReturn(true);
        when(mapper.toDto(any(MentorshipRequest.class))).thenReturn(dto);
        when(statusFilter.apply(any(Stream.class), eq(filters))).thenReturn(Stream.of(req1, req3));

        var result = sut.getRequests(filters);

        assertEquals(2, result.size());
        assertEquals(dto, result.get(0));
    }
}