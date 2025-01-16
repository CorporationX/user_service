package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.EMPTY_MENTORSHIP_REQUEST_FILTER_DTO;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.INVALID_MENTORSHIP_REQUEST_DTO;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.MENTORSHIP_REQUESTS;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.MENTORSHIP_REQUEST_FILTER_ITERATOR;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.USER_2;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_MENTORSHIP_REQUEST;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_MENTORSHIP_REQUEST_DESCRIPTION;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_MENTORSHIP_REQUEST_DTO;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_MENTORSHIP_REQUEST_ID;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_REJECTION_DTO;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_USER_ID_1;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_USER_ID_2;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserService userService;
    @Spy
    private List<MentorshipRequestFilter> mentorshipRequestFilters;
    @Spy
    private MentorshipRequestMapperImpl mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Test
    void requestMentorship_shouldThrowDataValidationException_whenRequesterIdIsInvalid() {
        Mockito.when(userService.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.requestMentorship(INVALID_MENTORSHIP_REQUEST_DTO));
    }

    @Test
    void requestMentorship_shouldThrowDataValidationException_whenReceiverIdIsInvalid() {
        Mockito.when(userService.existsById(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.requestMentorship(INVALID_MENTORSHIP_REQUEST_DTO));
    }

    @Test
    void requestMentorship_shouldThrowDataValidationException_whenRequesterIdEqualsReceiverId() {
        Mockito.when(userService.existsById(INVALID_MENTORSHIP_REQUEST_DTO.getRequesterId())).thenReturn(true);
        Mockito.when(userService.existsById(INVALID_MENTORSHIP_REQUEST_DTO.getReceiverId())).thenReturn(true);

        Assertions.assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.requestMentorship(INVALID_MENTORSHIP_REQUEST_DTO));
    }

    @Test
    void requestMentorship_shouldThrowDataValidationException_whenNotEnoughMonthsHavePassed() {
        Mockito.when(userService.existsById(VALID_USER_ID_1)).thenReturn(true);
        Mockito.when(userService.existsById(VALID_USER_ID_2)).thenReturn(true);

        Mockito.when(mentorshipRequestRepository.findLatestRequest(VALID_USER_ID_1, VALID_USER_ID_2))
                .thenReturn(Optional.of(VALID_MENTORSHIP_REQUEST));

        Assertions.assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.requestMentorship(VALID_MENTORSHIP_REQUEST_DTO));
    }

    @Test
    void requestMentorship_shouldCreateRequestMentorship_whenMentorshipRequestDtoIsValid() {
        Mockito.when(userService.existsById(VALID_USER_ID_1)).thenReturn(true);
        Mockito.when(userService.existsById(VALID_USER_ID_2)).thenReturn(true);

        Mockito.when(mentorshipRequestRepository.findLatestRequest(VALID_USER_ID_1, VALID_USER_ID_2))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(VALID_MENTORSHIP_REQUEST));

        Mockito.doNothing().when(mentorshipRequestRepository)
                .create(VALID_USER_ID_1, VALID_USER_ID_2, VALID_MENTORSHIP_REQUEST_DESCRIPTION);

        mentorshipRequestService.requestMentorship(VALID_MENTORSHIP_REQUEST_DTO);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(2))
                .findLatestRequest(VALID_USER_ID_1, VALID_USER_ID_2);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .create(VALID_USER_ID_1, VALID_USER_ID_2, VALID_MENTORSHIP_REQUEST_DESCRIPTION);
    }

    @Test
    void getRequests_shouldReturnAllMentorshipRequests() {
        Mockito.when(mentorshipRequestRepository.findAll()).thenReturn(MENTORSHIP_REQUESTS);

        Mockito.when(mentorshipRequestFilters.iterator()).thenReturn(MENTORSHIP_REQUEST_FILTER_ITERATOR);

        List<MentorshipRequestDto> mentorshipRequestsDto
                = mentorshipRequestMapper.toDtoList(MENTORSHIP_REQUESTS);

        Assertions.assertEquals(mentorshipRequestsDto,
                mentorshipRequestService.getRequests(EMPTY_MENTORSHIP_REQUEST_FILTER_DTO));
    }

    @Test
    void acceptRequest_shouldThrowDataValidationException_whenReceiverIsAMentorToRequester() {
        VALID_MENTORSHIP_REQUEST.getRequester().setMentors(List.of(USER_2));

        Mockito.when(mentorshipRequestRepository.findById(VALID_MENTORSHIP_REQUEST_ID))
                .thenReturn(Optional.of(VALID_MENTORSHIP_REQUEST));

        Assertions.assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.acceptRequest(VALID_MENTORSHIP_REQUEST_ID));
    }

    @Test
    void acceptRequest_shouldAcceptMentorshipRequest_whenIdIsValid() {
        VALID_MENTORSHIP_REQUEST.getRequester().setMentors(new ArrayList<>());

        Mockito.when(mentorshipRequestRepository.findById(VALID_MENTORSHIP_REQUEST_ID))
                .thenReturn(Optional.of(VALID_MENTORSHIP_REQUEST));

        mentorshipRequestService.acceptRequest(VALID_MENTORSHIP_REQUEST_ID);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .save(VALID_MENTORSHIP_REQUEST);

        Assertions.assertEquals(RequestStatus.ACCEPTED, VALID_MENTORSHIP_REQUEST.getStatus());
        Assertions.assertEquals(List.of(USER_2), VALID_MENTORSHIP_REQUEST.getRequester().getMentors());
    }

    @Test
    void rejectRequest_shouldRejectMentorshipRequest_whenIdAndRejectionDtoIsValid() {
        Mockito.when(mentorshipRequestRepository.findById(VALID_MENTORSHIP_REQUEST_ID))
                .thenReturn(Optional.of(VALID_MENTORSHIP_REQUEST));

        mentorshipRequestService.rejectRequest(VALID_MENTORSHIP_REQUEST_ID, VALID_REJECTION_DTO);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .save(VALID_MENTORSHIP_REQUEST);

        Assertions.assertEquals(RequestStatus.REJECTED, VALID_MENTORSHIP_REQUEST.getStatus());
        Assertions.assertEquals(VALID_REJECTION_DTO.getReason(), VALID_MENTORSHIP_REQUEST.getRejectionReason());
    }
}