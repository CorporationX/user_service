package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.*;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.exception.message.ExceptionMessage.NO_REQUEST_IN_DB;
import static school.faang.user_service.exception.message.ExceptionMessage.USER_ALREADY_HAS_SUCH_MENTOR;
import static school.faang.user_service.exception.message.MessageForGoalInvitationService.NO_USER_IN_DB;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;

    @Spy
    private final MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<RequestFilter> requestFilters;

    @Captor
    private ArgumentCaptor<MentorshipRequest> captor;

    private RejectionDto rejectionDto = getRejectionDto();

    private final MentorshipRequest request = getRequest();

    private static final long REQUEST_ID = 5L;

    @BeforeEach
    void init() {
        DescriptionFilter descriptionFilter = Mockito.spy(DescriptionFilter.class);
        ReceiverIdFilter receiverIdFilter = Mockito.spy(ReceiverIdFilter.class);
        RequesterIdFilter requesterIdFilter = Mockito.spy(RequesterIdFilter.class);
        StatusFilter statusFilter = Mockito.spy(StatusFilter.class);
        List<RequestFilter> filters = List.of(descriptionFilter, receiverIdFilter, requesterIdFilter, statusFilter);
        mentorshipRequestService.setRequestFilters(filters);
    }

    @Test
    void testForRequestMentorshipTrue() {
        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .status(RequestStatus.PENDING)
                .build();

        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        verify(mentorshipRequestRepository).save(captor.capture());
        MentorshipRequest mentorshipRequest = captor.getValue();

        assertEquals(mentorshipRequestDto.getStatus(), mentorshipRequest.getStatus());
    }

    @Test
    void testForFindAllWithFilters() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .description("Something")
                .requesterId(3L)
                .receiverId(4L)
                .status(RequestStatus.PENDING)
                .build();
        List<MentorshipRequest> requests = List.of(getRequest());

        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(getRequest()));

        assertEquals(requests.size(), 1);
        assertEquals(mentorshipRequestService.findAll(requestFilterDto).size(), 1);
    }

    @Test
    void testForAcceptRequestWithoutRequest() {
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.acceptRequest(REQUEST_ID));
        assertEquals(NO_REQUEST_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForAcceptRequestWithoutRequester() {
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        when(userRepository.findById(request.getRequester().getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.acceptRequest(REQUEST_ID));

        assertEquals(NO_USER_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForAcceptRequestWhenUserHasSuchMentor() {
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        when(userRepository.findById(request.getRequester().getId())).thenReturn(Optional.of(request.getRequester()));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.acceptRequest(REQUEST_ID));

        assertEquals(USER_ALREADY_HAS_SUCH_MENTOR.getMessage(), exception.getMessage());
    }

    @Test
    void testForAcceptRequestWithoutReceiver() {
        request.getRequester().setMentors(getMentors());

        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        when(userRepository.findById(request.getRequester().getId())).thenReturn(Optional.of(request.getRequester()));
        when(userRepository.findById(request.getReceiver().getId())).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.acceptRequest(REQUEST_ID));

        assertEquals(NO_USER_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForAcceptRequestWithReceiver() {
        User mentor = new User();
        mentor.setId(1L);
        request.getRequester().setMentors(getMentors());

        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
        when(userRepository.findById(request.getRequester().getId())).thenReturn(Optional.of(request.getRequester()));
        when(userRepository.findById(request.getReceiver().getId())).thenReturn(Optional.of(mentor));

        mentorshipRequestService.acceptRequest(REQUEST_ID);
        verify(mentorshipRequestRepository).save(captor.capture());
        assertEquals(request.getRequester().getMentors().get(0), captor.getValue().getRequester().getMentors().get(0));
    }

    @Test
    void testForRejectWithoutUser() {
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.rejectRequest(REQUEST_ID, rejectionDto));
        assertEquals(NO_REQUEST_IN_DB.getMessage(), exception.getMessage());
    }

    @Test
    void testForRejectWithUser() {
        request.setStatus(RequestStatus.REJECTED);
        when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));

        mentorshipRequestService.rejectRequest(REQUEST_ID, rejectionDto);
        verify(mentorshipRequestRepository).save(captor.capture());
        assertEquals(request.getStatus(), captor.getValue().getStatus());
    }

    private MentorshipRequest getRequest() {
        MentorshipRequest request = new MentorshipRequest();
        request.setId(5L);
        request.setDescription("Something");

        User userFirst = new User();
        userFirst.setId(3L);

        User userSecond = new User();
        userSecond.setId(4L);

        List<User> mentors = new ArrayList<>();
        mentors.add(userSecond);
        userFirst.setMentors(mentors);

        request.setRequester(userFirst);
        request.setReceiver(userSecond);
        request.setStatus(RequestStatus.PENDING);

        return request;
    }

    private List<User> getMentors() {
        User mentor = new User();
        mentor.setId(1L);
        List<User> mentors = new ArrayList<>();
        mentors.add(mentor);
        request.getRequester().setMentors(mentors);
        return mentors;
    }

    private RejectionDto getRejectionDto() {
        rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("Some reason");
        return rejectionDto;
    }
}
