package school.faang.user_service.service.mentorship;

import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.EMPTY_MENTORSHIP_REQUEST_FILTER_DTO;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.INVALID_MENTORSHIP_REQUEST_REQUEST_DTO;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.MENTORSHIP_REQUEST_FILTERS;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.MENTORSHIP_REQUEST_REQUEST_DTO;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.USER_2;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_MENTORSHIP_REQUEST;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_MENTORSHIP_REQUESTS;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_MENTORSHIP_REQUEST_DESCRIPTION;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_MENTORSHIP_REQUEST_ID;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_MENTORSHIP_REQUEST_RESPONSE_DTOS;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_REJECTION_DTO;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_USER_ID_1;
import static school.faang.user_service.service.mentorship.MentorshipRequestServiceTestConstants.VALID_USER_ID_2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestResponseMapper;
import school.faang.user_service.repository.adapter.MentorshipRequestRepositoryAdapter;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

  private final MentorshipRequestRepository mentorshipRequestRepository =
      Mockito.mock(MentorshipRequestRepository.class);
  private final MentorshipRequestRepositoryAdapter mentorshipRequestRepositoryAdapter =
      Mockito.mock(MentorshipRequestRepositoryAdapter.class);
  private final UserRepositoryAdapter userRepositoryAdapter =
      Mockito.mock(UserRepositoryAdapter.class);
  private final List<MentorshipRequestFilter> mentorshipRequestFilters = MENTORSHIP_REQUEST_FILTERS;
  private final MentorshipRequestResponseMapper mentorshipRequestResponseMapper =
      Mockito.mock(MentorshipRequestResponseMapper.class);

  private final MentorshipRequestService mentorshipRequestService =
      new MentorshipRequestService(
          mentorshipRequestRepository,
          mentorshipRequestRepositoryAdapter,
          userRepositoryAdapter,
          mentorshipRequestFilters,
          mentorshipRequestResponseMapper);

  @Test
  void requestMentorship_shouldThrowDataValidationException_whenRequesterIdIsInvalid() {
    Mockito.when(userRepositoryAdapter.existsById(Mockito.anyLong())).thenReturn(false);

    Assertions.assertThrows(
        DataValidationException.class,
        () -> mentorshipRequestService.requestMentorship(INVALID_MENTORSHIP_REQUEST_REQUEST_DTO));
  }

  @Test
  void requestMentorship_shouldThrowDataValidationException_whenReceiverIdIsInvalid() {
    Mockito.when(userRepositoryAdapter.existsById(Mockito.anyLong())).thenReturn(false);

    Assertions.assertThrows(
        DataValidationException.class,
        () -> mentorshipRequestService.requestMentorship(INVALID_MENTORSHIP_REQUEST_REQUEST_DTO));
  }

  @Test
  void requestMentorship_shouldThrowDataValidationException_whenRequesterIdEqualsReceiverId() {
    Mockito.when(
            userRepositoryAdapter.existsById(INVALID_MENTORSHIP_REQUEST_REQUEST_DTO.requesterId()))
        .thenReturn(true);
    Mockito.when(
            userRepositoryAdapter.existsById(INVALID_MENTORSHIP_REQUEST_REQUEST_DTO.receiverId()))
        .thenReturn(true);

    Assertions.assertThrows(
        DataValidationException.class,
        () -> mentorshipRequestService.requestMentorship(INVALID_MENTORSHIP_REQUEST_REQUEST_DTO));
  }

  @Test
  void requestMentorship_shouldThrowDataValidationException_whenNotEnoughMonthsHavePassed() {
    Mockito.when(userRepositoryAdapter.existsById(VALID_USER_ID_1)).thenReturn(true);
    Mockito.when(userRepositoryAdapter.existsById(VALID_USER_ID_2)).thenReturn(true);

    Mockito.when(mentorshipRequestRepository.findLatestRequest(VALID_USER_ID_1, VALID_USER_ID_2))
        .thenReturn(Optional.of(VALID_MENTORSHIP_REQUEST));

    Assertions.assertThrows(
        DataValidationException.class,
        () -> mentorshipRequestService.requestMentorship(MENTORSHIP_REQUEST_REQUEST_DTO));
  }

  @Test
  void requestMentorship_shouldCreateRequestMentorship_whenMentorshipRequestDtoIsValid() {
    Mockito.when(userRepositoryAdapter.existsById(VALID_USER_ID_1)).thenReturn(true);
    Mockito.when(userRepositoryAdapter.existsById(VALID_USER_ID_2)).thenReturn(true);

    Mockito.when(mentorshipRequestRepository.findLatestRequest(VALID_USER_ID_1, VALID_USER_ID_2))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(VALID_MENTORSHIP_REQUEST));

    Mockito.doNothing()
        .when(mentorshipRequestRepository)
        .create(VALID_USER_ID_1, VALID_USER_ID_2, VALID_MENTORSHIP_REQUEST_DESCRIPTION);

    mentorshipRequestService.requestMentorship(MENTORSHIP_REQUEST_REQUEST_DTO);

    Mockito.verify(mentorshipRequestRepository, Mockito.times(2))
        .findLatestRequest(VALID_USER_ID_1, VALID_USER_ID_2);

    Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
        .create(VALID_USER_ID_1, VALID_USER_ID_2, VALID_MENTORSHIP_REQUEST_DESCRIPTION);
  }

  @Test
  void getRequests_shouldReturnAllMentorshipRequests() {
    Mockito.when(mentorshipRequestRepository.findAll()).thenReturn(VALID_MENTORSHIP_REQUESTS);

    Mockito.when(mentorshipRequestResponseMapper.toDtoList(VALID_MENTORSHIP_REQUESTS))
        .thenReturn(VALID_MENTORSHIP_REQUEST_RESPONSE_DTOS);

    Assertions.assertEquals(
        VALID_MENTORSHIP_REQUEST_RESPONSE_DTOS,
        mentorshipRequestService.getRequests(EMPTY_MENTORSHIP_REQUEST_FILTER_DTO));
  }

  @Test
  void acceptRequest_shouldThrowDataValidationException_whenReceiverIsAMentorToRequester() {
    VALID_MENTORSHIP_REQUEST.getRequester().setMentors(List.of(USER_2));

    Mockito.when(mentorshipRequestRepositoryAdapter.findById(VALID_MENTORSHIP_REQUEST_ID))
        .thenReturn(VALID_MENTORSHIP_REQUEST);

    Assertions.assertThrows(
        DataValidationException.class,
        () -> mentorshipRequestService.acceptRequest(VALID_MENTORSHIP_REQUEST_ID));
  }

  @Test
  void acceptRequest_shouldAcceptMentorshipRequest_whenIdIsValid() {
    VALID_MENTORSHIP_REQUEST.getRequester().setMentors(new ArrayList<>());

    Mockito.when(mentorshipRequestRepositoryAdapter.findById(VALID_MENTORSHIP_REQUEST_ID))
        .thenReturn(VALID_MENTORSHIP_REQUEST);

    mentorshipRequestService.acceptRequest(VALID_MENTORSHIP_REQUEST_ID);

    Assertions.assertEquals(RequestStatus.ACCEPTED, VALID_MENTORSHIP_REQUEST.getStatus());
    Assertions.assertEquals(List.of(USER_2), VALID_MENTORSHIP_REQUEST.getRequester().getMentors());
  }

  @Test
  void rejectRequest_shouldRejectMentorshipRequest_whenIdAndRejectionDtoIsValid() {
    Mockito.when(mentorshipRequestRepositoryAdapter.findById(VALID_MENTORSHIP_REQUEST_ID))
        .thenReturn(VALID_MENTORSHIP_REQUEST);

    mentorshipRequestService.rejectRequest(VALID_MENTORSHIP_REQUEST_ID, VALID_REJECTION_DTO);

    Assertions.assertEquals(RequestStatus.REJECTED, VALID_MENTORSHIP_REQUEST.getStatus());
    Assertions.assertEquals(
        VALID_REJECTION_DTO.reason(), VALID_MENTORSHIP_REQUEST.getRejectionReason());
  }
}
