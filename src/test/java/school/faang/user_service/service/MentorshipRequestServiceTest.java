package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    @Captor
    private ArgumentCaptor<MentorshipRequest> requestCaptor;

    @Test
    @DisplayName("Testing when requester is mentor")
    public void createDtoWhenMentorshipIsScheduled() {
        Long mentorAndResaverId = 1L;
        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("", mentorAndResaverId);

        Mockito.when(userContext.getUserId()).thenReturn(mentorAndResaverId);

        assertThrows(
                ForbiddenException.class,
                () -> mentorshipRequestService.create(createDto)
        );
    }

    @Test
    @DisplayName("Testing when not enough time has passed between requests")
    public void createDtoWhenInvalidMouthsBetweenRequests() {
        Long mentorId = 1L;
        Long requesterId = 2L;
        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("", mentorId);

        Mockito.when(userContext.getUserId()).thenReturn(requesterId);
        Mockito.when(mentorshipRequestRepository.findLatestRequest(requesterId, mentorId))
                .thenReturn(Optional.of(new MentorshipRequest()));

        assertThrows(DataValidationException.class, () -> mentorshipRequestService.create(createDto));
    }

    @Test
    @DisplayName("Testing create mentorshipRequest")
    public void createMentorshipRequest() {
        Long mentorId = 1L;
        Long requesterId = 2L;
        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("", mentorId);

        Mockito.when(userContext.getUserId()).thenReturn(requesterId);

        mentorshipRequestService.create(createDto);

        verify(mentorshipRequestRepository, times(1))
                .create(requesterId, mentorId, createDto.description());
    }

    @Test
    @DisplayName("Testing create mentorshipRequestDto")
    public void createToMentorshipRequestDto() {
        Long mentorId = 1L;
        Long requesterId = 2L;
        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("", mentorId);

        User requester = new User();
        requester.setId(requesterId);

        User mentor = new User();
        mentor.setId(mentorId);

        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setReceiver(mentor);
        request.setDescription("");

        Mockito.when(userContext.getUserId()).thenReturn(requesterId);
        Mockito.when(mentorshipRequestRepository
                        .create(requesterId, mentorId, createDto.description()))
                        .thenReturn(request);

        mentorshipRequestService.create(createDto);

        verify(mentorshipRequestMapper, times(1)).toMentorshipRequestDto(request);
    }

    @Test
    @DisplayName("Testing trows EntityNotFound in accept method")
    public void throwsEntityNotFoundEAccept() {
        Long requestId = 1L;

        Mockito.when(mentorshipRequestRepository
                .findById(requestId))
                .thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> mentorshipRequestService.accept(requestId));
    }

    @Test
    @DisplayName("Testing creating relation mentor-mentee in accept method")
    public void createRelationMentorMentee() {
        Long requestId = 1L;
        Long menteeId = 1L;
        Long mentorId = 2L;
        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("", mentorId);

        User mentee = new User();
        mentee.setId(menteeId);

        User mentor = new User();
        mentor.setId(mentorId);

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(mentee);
        request.setReceiver(mentor);

        Mockito.when(mentorshipRequestRepository
                .findById(requestId))
                .thenReturn(Optional.of(request));

        mentorshipRequestService.create(createDto);

        verify(userRepository).save(mentee);
        verify(userRepository).save(mentor);
    }

    @Test
    @DisplayName("Testing set status ACCEPTED in accept method")
    public void setStatusAccepted() {
        Long requestId = 1L;
        Long mentorId = 2L;
        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("", mentorId);

        Mockito.when(mentorshipRequestRepository
                .findById(requestId))
                .thenReturn(Optional.of(new MentorshipRequest()));

        mentorshipRequestService.create(createDto);

        MentorshipRequest request = requestCaptor.getValue();
        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
    }

    @Test
    @DisplayName("Testing save request in accept method")
    public void saveRequest() {
        Long requestId = 1L;
        Long mentorId = 2L;
        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("", mentorId);
        MentorshipRequest request = new MentorshipRequest();

        Mockito.when(mentorshipRequestRepository
                .findById(requestId))
                .thenReturn(Optional.of(request));

        mentorshipRequestService.create(createDto);

        verify(mentorshipRequestRepository, times(1)).save(request);
    }

    @Test
    @DisplayName("Testing throws EntityNotFound in reject method")
    public void throwsEntityNotFoundEReject() {
        Long requestId = 1L;

        Mockito.when(mentorshipRequestRepository
                .findById(requestId))
                .thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> mentorshipRequestRepository.findById(requestId));
    }

    @Test
    @DisplayName("Testing set status Rejected in reject method")
    public void setRejectedStatus() {
        Long requestId = 1L;
        RejectionDto dto = new RejectionDto("");

        Mockito.when(mentorshipRequestRepository
                .findById(requestId))
                .thenReturn(Optional.of(new MentorshipRequest()));

        mentorshipRequestService.reject(requestId, dto);
        MentorshipRequest request = requestCaptor.getValue();

        assertEquals(RequestStatus.REJECTED, request.getStatus());
    }

    @Test
    @DisplayName("Testing set Rejection reason in reject method")
    public void setRejectionReason() {
        Long requestId = 1L;
        RejectionDto dto = new RejectionDto("");

        Mockito.when(mentorshipRequestRepository
                .findById(requestId))
                .thenReturn(Optional.of(new MentorshipRequest()));

        mentorshipRequestService.reject(requestId, dto);

        MentorshipRequest request = requestCaptor.getValue();
        verify(request, times(1)).setRejectionReason(dto.reason());
    }

    @Test
    @DisplayName("Testing save to repository in reject method")
    public void saveRejectedRequest() {
        Long requestId = 1L;
        RejectionDto dto = new RejectionDto("");

        Mockito.when(mentorshipRequestRepository
                        .findById(requestId))
                .thenReturn(Optional.of(new MentorshipRequest()));

        mentorshipRequestService.reject(requestId, dto);

        MentorshipRequest request = requestCaptor.getValue();
        verify(mentorshipRequestRepository, times(1)).save(request);
    }

    @Test
    @DisplayName("Testing filter")
    public void getByFilter() {
        User firstUser = new User();
        firstUser.setId(1L);
        User secondUser = new User();
        secondUser.setId(2L);
        User thirstUser = new User();
        thirstUser.setId(3L);

        MentorshipRequest firstRequest = new MentorshipRequest();
        firstRequest.setId(1L);
        firstRequest.setRequester(firstUser);
        firstRequest.setReceiver(secondUser);
        firstRequest.setStatus(RequestStatus.ACCEPTED);

        MentorshipRequest secondRequest = new MentorshipRequest();
        secondRequest.setId(2L);
        secondRequest.setRequester(secondUser);
        secondRequest.setReceiver(thirstUser);
        secondRequest.setStatus(RequestStatus.REJECTED);

        List<MentorshipRequest> requests = List.of(firstRequest, secondRequest);

        MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto(
                null, null, RequestStatus.ACCEPTED);

        List<MentorshipRequestDto> attackedFilteredRequests = new ArrayList<>();
        attackedFilteredRequests.add(mentorshipRequestMapper.toMentorshipRequestDto(firstRequest));

        Mockito.when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        List<MentorshipRequestDto> defencedFilteredRequests = mentorshipRequestService.getByFilters(filterDto);

        assertEquals(attackedFilteredRequests, defencedFilteredRequests);
    }
}