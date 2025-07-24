package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.mentorship.MentorshipFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipResponseDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.mentorship.MentorshipResponseMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Spy
    private MentorshipResponseMapperImpl mentorshipResponseMapper;

    @Captor
    private ArgumentCaptor<MentorshipRequest> requestCaptor;

    @Mock
    private Filter<MentorshipFilterDto, MentorshipRequest> filter;

    @BeforeEach
    void setup() {
        mentorshipRequestService = new MentorshipRequestService(mentorshipRequestRepository, mentorshipResponseMapper,
                List.of(), List.of());
    }

    @Test
    @DisplayName("Checking request persistence in the database")
    public void testRequestIsSaved() {

        MentorshipRequestDto dto = new MentorshipRequestDto(1L, 2L, "Test description");
        User requester = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        LocalDateTime fixedTime = LocalDateTime.of(2025, 5, 25, 10, 0);
        MentorshipRequest saved = MentorshipRequest.builder().id(100L).requester(requester).receiver(receiver)
                        .description("Test description").status(RequestStatus.PENDING).createdAt(fixedTime).build();
        Mockito.when(mentorshipRequestRepository.create(1L, 2L, "Test description"))
                .thenReturn(saved);
        MentorshipResponseDto actualResponse = mentorshipRequestService.requestMentorship(dto);
        MentorshipResponseDto expectedResponse = new MentorshipResponseDto(100, 1L, 2L,
                "PENDING", "Test description", fixedTime);
        Assertions.assertEquals(expectedResponse, actualResponse);
        verify(mentorshipRequestRepository, times(1)).create(1L, 2L,
                "Test description");
        verifyNoMoreInteractions(mentorshipRequestRepository);
    }

    @Test
    @DisplayName("getRequests applies only applicable filters and maps to response")
    void testGetRequests_appliesFilters_andMapsToDto() {
        // given
        MentorshipRequest r1 = MentorshipRequest.builder().description("desc1").build();
        MentorshipRequest r2 = MentorshipRequest.builder().description("desc2").build();
        List<MentorshipRequest> mockRequests = List.of(r1, r2);

        when(mentorshipRequestRepository.findAll()).thenReturn(mockRequests);
        when(filter.isApplicable(any())).thenReturn(true);
        when(filter.apply(any(), any())).thenAnswer(invocation -> {
            Stream<MentorshipRequest> input = invocation.getArgument(0);
            return input.filter(r -> "desc1".equals(r.getDescription())); // отфильтровать вручную
        });

        MentorshipResponseDto dto = new MentorshipResponseDto(1, 1L, 2L, "PENDING", "desc1", LocalDateTime.now());
        when(mentorshipResponseMapper.toResponseDto(any())).thenReturn(dto);

        MentorshipRequestService service = new MentorshipRequestService(
                mentorshipRequestRepository,
                mentorshipResponseMapper,
                List.of(),                // validators
                List.of(filter)           // filters
        );

        MentorshipFilterDto filterDto = new MentorshipFilterDto("desc1", null, null, null);

        // when
        List<MentorshipResponseDto> result = service.getRequests(filterDto);

        // then
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("desc1", result.get(0).description());
    }

    @Test
    @DisplayName("Request was accepted")
    void shouldAcceptRequest() {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        mentorshipRequestService.acceptRequest(1L);

        verify(mentorshipRequestRepository).save(requestCaptor.capture());
        MentorshipRequest savedRequest = requestCaptor.getValue();

        Assertions.assertEquals(RequestStatus.ACCEPTED, savedRequest.getStatus(),
                "Request status should be ACCEPTED");

    }

    @Test
    @DisplayName("Request is already accepted")
    void shouldThrowExceptionWhenRequestAlreadyAccepted() {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.ACCEPTED);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> mentorshipRequestService.acceptRequest(1L));

        Assertions.assertEquals("400 BAD_REQUEST \"The user is already a mentor of the requester.\"",
                exception.getMessage());
        verify(mentorshipRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Request for accepting not found")
    void shouldThrowExceptionWhenAcceptRequestNotFound() {
        when(mentorshipRequestRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> mentorshipRequestService.acceptRequest(999L));

        Assertions.assertEquals("404 NOT_FOUND \"Id not found\"", exception.getMessage());
        verify(mentorshipRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Request was rejected")
    void shouldRejectRequest() {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);

        RejectionDto dto = new RejectionDto("Reason");

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        mentorshipRequestService.rejectRequest(1L, dto);

        verify(mentorshipRequestRepository).save(requestCaptor.capture());
        MentorshipRequest savedRequest = requestCaptor.getValue();

        Assertions.assertEquals(RequestStatus.REJECTED, savedRequest.getStatus(),
                "Request status should be REJECTED");

    }

    @Test
    @DisplayName("Request is already rejected")
    void shouldThrowExceptionWhenRequestAlreadyRejected() {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.REJECTED);

        RejectionDto dto = new RejectionDto("Reason");

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
                () -> mentorshipRequestService.rejectRequest(1L, dto));

        Assertions.assertEquals("400 BAD_REQUEST \"The request is already rejected.\"", exception.getMessage());
        verify(mentorshipRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Request for rejecting not found")
    void shouldThrowExceptionWhenRejectRequestNotFound() {
        when(mentorshipRequestRepository.findById(999L)).thenReturn(Optional.empty());

        RejectionDto dto = new RejectionDto("Reason");

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, (

        ) -> mentorshipRequestService.rejectRequest(999L, dto));

        Assertions.assertEquals("404 NOT_FOUND \"Id not found\"", exception.getMessage());
        verify(mentorshipRequestRepository, never()).save(any());
    }
}

