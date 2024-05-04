package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.handler.exception.DataValidationException;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filtres.DescriptionRequestFilter;
import school.faang.user_service.service.mentorship.filtres.RequestFilter;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {
    @Mock
    private MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    private final DescriptionRequestFilter descriptionRequestFilter = new DescriptionRequestFilter();
    private final List<RequestFilter> requestFilters = List.of(descriptionRequestFilter);
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @BeforeEach
    public void init() {
        mentorshipRequestService = new MentorshipRequestService(mentorshipAcceptedEventPublisher, mentorshipRequestRepository, mentorshipRequestValidator
                , mentorshipRequestMapper, requestFilters);
    }

    @Test
    void requestMentorshipTest() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .idRequester(1)
                .idReceiver(2)
                .description("Запрос на менторство").build();
        when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(MentorshipRequest.builder()
                        .createdAt(LocalDateTime.of(2022, Month.FEBRUARY, 10, 10, 10, 0))
                        .build()));
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestRepository).create(1, 2, mentorshipRequestDto.getDescription());
    }

    @Test
    void requestMentorshipTestNoRequest() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .idRequester(1)
                .idReceiver(2)
                .description("Запрос на менторство").build();

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        verify(mentorshipRequestRepository).create(1, 2, mentorshipRequestDto.getDescription());
    }

    @Test
    void requestMentorshipTestThereWasRecentRequest() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .idRequester(1)
                .idReceiver(2)
                .description("Запрос на менторство").build();
        when(mentorshipRequestRepository.findLatestRequest(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(MentorshipRequest.builder()
                        .createdAt(LocalDateTime.of(2024, Month.MARCH, 10, 10, 10, 0))
                        .build()));
        assertThrows(DataValidationException.class, () -> mentorshipRequestService.requestMentorship(mentorshipRequestDto));
    }

    @Test
    void acceptRequestTest() {
        long id = 1;
        User requester = User.builder()
                .id(1).build();

        User receiver = User.builder()
                .id(2).build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(id)
                .requester(requester)
                .receiver(receiver)
                .description("test").build();
        MentorshipRequestDto mentorshipRequestDtoReturn = MentorshipRequestDto.builder()
                .status(RequestStatus.ACCEPTED).build();

        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));

        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDtoReturn);
        Assertions.assertEquals(mentorshipRequestDtoReturn, mentorshipRequestService.acceptRequest(id));
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        verify(mentorshipRequestRepository).save(mentorshipRequest);
    }

    @Test
    void acceptRequestTestNotFoundMentorshipRequest() {
        assertThrows(EntityNotFoundException.class, () -> mentorshipRequestService.acceptRequest(1));
    }

    @Test
    void acceptRequestTestMenteesForThisMentor() {
        long id = 1;
        User requester = User.builder()
                .id(1).build();

        User receiver = User.builder()
                .id(2)
                .mentees(List.of(requester)).build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(id)
                .requester(requester)
                .receiver(receiver)
                .description("test").build();
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));

        assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(id));
    }

    @Test
    void rejectRequestTest() {
        long id = 1;
        RejectionDto rejectionDto = RejectionDto.builder()
                .reason("так надо").build();
        MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                .id(id)
                .description("test").build();
        MentorshipRequestDto mentorshipRequestDtoReturn = MentorshipRequestDto.builder()
                .description("test")
                .status(RequestStatus.REJECTED)
                .rejectionReason("так надо")
                .build();

        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDtoReturn);
        Assertions.assertEquals(mentorshipRequestDtoReturn, mentorshipRequestService.rejectRequest(id, rejectionDto));
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        verify(mentorshipRequestRepository).save(mentorshipRequest);

    }

    @Test
    void rejectRequestTestNotFoundMentorshipRequest() {
        long id = 1;
        RejectionDto rejectionDto = RejectionDto.builder()
                .reason("так надо").build();
        assertThrows(EntityNotFoundException.class, () -> mentorshipRequestService.rejectRequest(id, rejectionDto));
    }
}