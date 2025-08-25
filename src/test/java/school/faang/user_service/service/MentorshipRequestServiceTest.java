package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;

    @Test
    @DisplayName("Testing when requester is mentor")
    public void createDtoWhenRequesterIsMentor() {
        Long requesterAndMentorId = 1L;
        User requester = new User();
        requester.setId(requesterAndMentorId);
        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);

        when(userContext.getUserId()).thenReturn(requesterAndMentorId);
        when(mentorshipRequestRepository.findLatestRequest(requesterAndMentorId, requesterAndMentorId))
                .thenReturn(Optional.of(request));

        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("", requesterAndMentorId);

        assertThrows(
                ForbiddenException.class,
                () -> mentorshipRequestService.create(createDto)
        );
    }

    @Test
    @DisplayName("Testing when not enough time has passed between requests")
    public void createDtoWhenInvalidMouthsBetweenRequests() {
        Long requesterId = 2L;
        User requester = new User();
        requester.setId(requesterId);
        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setCreatedAt(LocalDateTime.now());

        long mentorId = 1L;
        when(userContext.getUserId()).thenReturn(requesterId);
        when(mentorshipRequestRepository.findLatestRequest(requesterId, mentorId))
                .thenReturn(Optional.of(request));

        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("", mentorId);

        assertThrows(DataValidationException.class, () -> mentorshipRequestService.create(createDto));
    }

    @Test
    @DisplayName("Testing create mentorshipRequest")
    public void createMentorshipRequest() {
        long requesterId = 2L;
        User requester = new User();
        requester.setId(requesterId);
        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setId(1L);
        request.setCreatedAt(LocalDateTime.of(2000, 1, 1, 1, 1));

        long mentorId = 1L;
        when(userContext.getUserId()).thenReturn(requesterId);
        when(mentorshipRequestRepository.findLatestRequest(requesterId, mentorId))
                .thenReturn(Optional.of(request));
        when(mentorshipRequestRepository.create(requesterId, mentorId, "")).thenReturn(request);

        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("", mentorId);

        mentorshipRequestService.create(createDto);

        verify(mentorshipRequestRepository, times(1))
                .create(requesterId, mentorId, createDto.description());
    }

    @Test
    @DisplayName("Testing create mentorshipRequestDto")
    public void createToMentorshipRequestDto() {
        long requesterId = 2L;
        User requester = new User();
        requester.setId(requesterId);
        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setId(1L);
        request.setCreatedAt(LocalDateTime.of(2000, 1, 1, 1, 1));

        long mentorId = 1L;
        when(userContext.getUserId()).thenReturn(requesterId);
        when(mentorshipRequestRepository.findLatestRequest(requesterId, mentorId))
                .thenReturn(Optional.of(request));
        when(mentorshipRequestRepository.create(requesterId, mentorId, "")).thenReturn(request);

        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("", mentorId);

        mentorshipRequestService.create(createDto);

        verify(mentorshipRequestMapper, times(1)).toMentorshipRequestDto(request);
    }

    @Test
    @DisplayName("Testing trows EntityNotFound in accept method")
    public void throwsEntityNotFoundExceptionAccept() {
        long requestId = 1L;

        when(mentorshipRequestRepository
                .findById(requestId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> mentorshipRequestService.accept(requestId));
    }

    @Test
    @DisplayName("Testing creating relation mentor-mentee in accept method")
    public void createRelationMentorMentee() {
        Long menteeId = 1L;
        User mentee = new User();
        mentee.setMentors(new ArrayList<>());
        mentee.setMentees(new ArrayList<>());
        mentee.setId(menteeId);

        Long mentorId = 2L;
        User mentor = new User();
        mentor.setMentees(new ArrayList<>());
        mentor.setMentors(new ArrayList<>());
        mentor.setId(mentorId);

        long requestId = 1L;
        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(mentee);
        request.setReceiver(mentor);

        when(mentorshipRequestRepository
                .findById(requestId))
                .thenReturn(Optional.of(request));

        mentorshipRequestService.accept(requestId);

        verify(userRepository).save(mentee);
        verify(userRepository).save(mentor);
    }

    @Test
    @DisplayName("Testing set status ACCEPTED in accept method")
    public void setStatusAccepted() {
        User mentee = new User();
        mentee.setMentors(new ArrayList<>());
        mentee.setMentees(new ArrayList<>());
        Long menteeId = 1L;
        mentee.setId(menteeId);


        User mentor = new User();
        mentor.setMentees(new ArrayList<>());
        mentor.setMentors(new ArrayList<>());
        Long mentorId = 2L;
        mentor.setId(mentorId);

        long requestId = 1L;
        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(mentee);
        request.setReceiver(mentor);

        when(mentorshipRequestRepository
                .findById(requestId))
                .thenReturn(Optional.of(request));

        mentorshipRequestService.accept(requestId);

        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
    }

    @Test
    @DisplayName("Testing save request in accept method")
    public void saveRequest() {
        User mentee = new User();
        mentee.setMentors(new ArrayList<>());
        mentee.setMentees(new ArrayList<>());
        Long menteeId = 1L;
        mentee.setId(menteeId);

        User mentor = new User();
        mentor.setMentees(new ArrayList<>());
        mentor.setMentors(new ArrayList<>());
        Long mentorId = 2L;
        mentor.setId(mentorId);

        long requestId = 1L;
        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(mentee);
        request.setReceiver(mentor);

        when(mentorshipRequestRepository
                .findById(requestId))
                .thenReturn(Optional.of(request));

        mentorshipRequestService.accept(requestId);

        verify(mentorshipRequestRepository, times(1)).save(request);
    }

    @Test
    @DisplayName("Testing throws EntityNotFound in reject method")
    public void throwsEntityNotFoundExceptionReject() {
        long requestId = 1L;
        RejectionDto dto = new RejectionDto("");
        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);

        when(mentorshipRequestRepository
                .findById(requestId))
                .thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> mentorshipRequestService.reject(requestId, dto));
    }

    @Test
    @DisplayName("Testing set status Rejected in reject method")
    public void setRejectedStatus() {
        long requestId = 1L;
        RejectionDto dto = new RejectionDto("");
        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);

        when(mentorshipRequestRepository
                .findById(requestId))
                .thenReturn(Optional.of(request));

        mentorshipRequestService.reject(requestId, dto);

        assertEquals(RequestStatus.REJECTED, request.getStatus());
    }

    @Test
    @DisplayName("Testing save to repository in reject method")
    public void saveRejectedRequest() {
        long requestId = 1L;
        RejectionDto dto = new RejectionDto("");
        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);

        when(mentorshipRequestRepository
                        .findById(requestId))
                .thenReturn(Optional.of(request));

        mentorshipRequestService.reject(requestId, dto);

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
                1L, null, null);

        List<MentorshipRequestDto> attackedFilteredRequests = new ArrayList<>();
        attackedFilteredRequests.add(mentorshipRequestMapper.toMentorshipRequestDto(firstRequest));

        when(mentorshipRequestRepository.findAll()).thenReturn(requests);
        List<MentorshipRequestDto> defencedFilteredRequests = mentorshipRequestService.getByFilters(filterDto);

        assertEquals(attackedFilteredRequests, defencedFilteredRequests);
    }

    @Test
    @DisplayName("Should delete mentor from his mentees")
    public void deleteMentorFromMentees() {
        User mentor = User.builder()
                .id(1L)
                .build();
        User mentee = User.builder()
                .id(2L)
                .mentors(new ArrayList<>() {
                    {
                        add(mentor);
                    }
                })
                .build();
        mentor.setMentees(List.of(mentee));
        when(userRepository.getByIdOrThrow(1L)).thenReturn(mentor);

        mentorshipRequestService.deactivateMentor(mentor.getId());
        assertEquals(new ArrayList<>(), mentee.getMentors());
    }
}