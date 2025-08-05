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
}