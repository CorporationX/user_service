package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipStartEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.RequestFilter;
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

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<RequestFilter> requestFilters;

    @Mock
    private MentorshipStartEventPublisher mentorshipStartEventPublisher;

    @Captor
    private ArgumentCaptor<MentorshipRequest> captor;

    private RejectionDto rejectionDto = getRejectionDto();

    private final MentorshipRequest request = getRequest();

    private final MentorshipRequestDto mentorshipRequestDto = getMentorshipRequestDto();

    private static final long REQUEST_ID = 5L;

    @Nested
    class PositiveTests {

        @DisplayName("should return MentorshipRequestDto with PENDING status when validation passed")
        @Test
        void requestMentorshipTest() {
            MentorshipRequest mentorshipRequest = new MentorshipRequest();
            mentorshipRequest.setStatus(RequestStatus.PENDING);
            when(mentorshipRequestMapper.toEntity(mentorshipRequestDto)).thenReturn(mentorshipRequest);
            when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

            mentorshipRequestService.requestMentorship(new MentorshipRequestDto());

            verify(mentorshipRequestRepository).save(captor.capture());
            assertEquals(RequestStatus.PENDING, captor.getValue().getStatus());
        }

        @DisplayName("should return 1 when mentorshipRequestRepository.findAll()")
        @Test
        void findAllTest() {
            RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                    .description("Something")
                    .requesterId(3L)
                    .receiverId(4L)
                    .status(RequestStatus.PENDING)
                    .build();

            List<MentorshipRequest> requests = List.of(getRequest());
            when(mentorshipRequestRepository.findAll()).thenReturn(List.of(request));
            when(mentorshipRequestMapper.toDto(request)).thenReturn(mentorshipRequestDto);

            assertEquals(1, requests.size());
            assertEquals(1, mentorshipRequestService.findAll(requestFilterDto).size());
        }

        @DisplayName("should set ACCEPTED status when mentorshipRequest in DB")
        @Test
        void acceptRequestTest() {
            User mentor = new User();
            mentor.setId(1L);
            request.getRequester().setMentors(getMentors());
            when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
            when(userRepository.findById(request.getRequester().getId())).thenReturn(Optional.of(request.getRequester()));
            when(userRepository.findById(request.getReceiver().getId())).thenReturn(Optional.of(mentor));

            mentorshipRequestService.acceptRequest(REQUEST_ID);

            verify(mentorshipRequestRepository).save(captor.capture());
            assertEquals(request.getRequester().getMentors().get(0), captor.getValue().getRequester().getMentors().get(0));
            assertEquals(RequestStatus.ACCEPTED, captor.getValue().getStatus());
        }

        @DisplayName("should set REJECTED status & rejectionReason when mentorshipRequest in DB")
        @Test
        void rejectRequestTest() {
            request.setStatus(RequestStatus.REJECTED);
            when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));

            mentorshipRequestService.rejectRequest(REQUEST_ID, rejectionDto);

            verify(mentorshipRequestRepository).save(captor.capture());
            assertEquals(RequestStatus.REJECTED, captor.getValue().getStatus());
            assertEquals(rejectionDto.getRejectionReason(), captor.getValue().getRejectionReason());
        }
    }

    @Nested
    class NegativeTests {

        @DisplayName("should throw exception when mentorshipRequestRepository.findById(id)")
        @Test
        void acceptRequestWhenNoRequestInDBTest() {
            when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> mentorshipRequestService.acceptRequest(REQUEST_ID));

            assertEquals(NO_REQUEST_IN_DB.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when userRepository.findById(mentorshipRequest.getRequester().getId())")
        @Test
        void acceptRequestWhenNoRequesterInDBTest() {
            when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
            when(userRepository.findById(request.getRequester().getId())).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> mentorshipRequestService.acceptRequest(REQUEST_ID));

            assertEquals(NO_USER_IN_DB.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when mentor.getId() == receiverId")
        @Test
        void acceptRequestWhenUserHasSuchMentorTest() {
            when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
            when(userRepository.findById(request.getRequester().getId())).thenReturn(Optional.of(request.getRequester()));

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> mentorshipRequestService.acceptRequest(REQUEST_ID));

            assertEquals(USER_ALREADY_HAS_SUCH_MENTOR.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when userRepository.findById(receiverId)")
        @Test
        void acceptRequestWhenNoReceiverInDBTest() {
            request.getRequester().setMentors(getMentors());

            when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(request));
            when(userRepository.findById(request.getRequester().getId())).thenReturn(Optional.of(request.getRequester()));
            when(userRepository.findById(request.getReceiver().getId())).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> mentorshipRequestService.acceptRequest(REQUEST_ID));

            assertEquals(NO_USER_IN_DB.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when mentorshipRequestRepository.findById(id)")
        @Test
        void rejectRequestWhenNoRequestInDBTest() {
            when(mentorshipRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> mentorshipRequestService.rejectRequest(REQUEST_ID, rejectionDto));
            assertEquals(NO_REQUEST_IN_DB.getMessage(), exception.getMessage());
        }
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

    private MentorshipRequestDto getMentorshipRequestDto() {
        return MentorshipRequestDto.builder()
                .status(RequestStatus.PENDING)
                .build();
    }
}
