package school.faang.user_service.service.mentorship;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
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
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.exception.mentorship.MentorshipIsAlreadyAgreedException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.mentorship.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.filter.mentorship.MentorshipRequestStatusFilter;
import school.faang.user_service.validator.mentorship.SelfMentorshipValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MentorshipRequestServiceImplTest {
    @Mock
    private MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestMapper mapper;

    @Mock
    private MentorshipRequestStatusFilter statusFilter;

    @Mock
    private SelfMentorshipValidator selfMentorshipValidator;

    @Captor
    private ArgumentCaptor<MentorshipRequestDto> dtoCaptor;

    @InjectMocks
    private MentorshipRequestServiceImpl sut;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sut, "mentorshipValidators", List.of(selfMentorshipValidator));
    }

    @Test
    void testRequestMentorship_throws_exception_when_validation_fails() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(1L);

        doThrow(new IllegalArgumentException(ExceptionMessages.SELF_MENTORSHIP)).when(selfMentorshipValidator).validate(dto);

        assertThatThrownBy(() -> sut.requestMentorship(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ExceptionMessages.SELF_MENTORSHIP);
        verifyNoInteractions(mentorshipRequestRepository, mapper);
    }

    @Test
    void testRequestMentorship_throws_exception_when_database_write_fails() {
        MentorshipRequestDto dto = new MentorshipRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);

        doNothing().when(selfMentorshipValidator).validate(any(MentorshipRequestDto.class));
        when(mapper.toEntity(dto)).thenReturn(new MentorshipRequest());
        when(mentorshipRequestRepository.save(new MentorshipRequest())).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> sut.requestMentorship(dto))
                .isInstanceOf(PersistenceException.class)
                .hasMessage(ExceptionMessages.FAILED_PERSISTENCE);
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

        when(mapper.toEntity(dto)).thenReturn(request);
        when(mentorshipRequestRepository.save(request)).thenReturn(request);
        when(mapper.toDto(request)).thenReturn(dto);
        doNothing().when(mentorshipRequestedEventPublisher).toEventAndPublish(dto);

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

    @Test
    void acceptRequest_should_successfully_update_entity_state() {
        User requester = new User();
        requester.setId(1L);
        requester.setMentors(new ArrayList<>());
        User receiver = new User();
        receiver.setId(2L);
        receiver.setMentees(new ArrayList<>());
        MentorshipRequest request = new MentorshipRequest();
        request.setId(100L);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(mapper.toDto(request)).thenReturn(new MentorshipRequestDto());

        sut.acceptRequest(100L);

        verify(mentorshipRequestRepository, times(1)).save(request);
        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
    }

    @Test
    void acceptRequest_Request_not_found() {
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> sut.acceptRequest(100L));
    }

    @Test
    void acceptRequest_request_already_accepted() {
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        requester.setMentors(Collections.singletonList(receiver));
        receiver.setMentees(Collections.singletonList(requester));

        MentorshipRequest request = new MentorshipRequest();
        request.setId(100L);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.ACCEPTED);

        when(mentorshipRequestRepository.findById(100L)).thenReturn(Optional.of(request));

        assertThrows(MentorshipIsAlreadyAgreedException.class, () -> sut.acceptRequest(100L), ExceptionMessages.MENTORSHIP_ALREADY_ONGOING);
    }

    @Test
    void rejectRequest_successfully_updates_entity() {
        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        MentorshipRequest request = new MentorshipRequest();
        request.setId(100L);
        request.setStatus(RequestStatus.PENDING);

        RejectionDto rejectionDto = new RejectionDto("Not a good fit");
        when(mentorshipRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(mapper.toDto(request)).thenReturn(new MentorshipRequestDto());

        sut.rejectRequest(100L, rejectionDto);

        verify(mentorshipRequestRepository, times(1)).save(request);
        assertEquals(RequestStatus.REJECTED, request.getStatus());
        assertEquals("Not a good fit", request.getRejectionReason());
    }

    @Test
    void rejectRequest_request_not_found() {
        RejectionDto rejectionDto = new RejectionDto("Not a good fit");
        when(mentorshipRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> sut.rejectRequest(100L, rejectionDto));
    }
}