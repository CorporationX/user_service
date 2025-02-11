package school.faang.user_service.service.mentorship;

import java.time.LocalDateTime;
import java.util.List;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestResponseDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship.DescriptionMentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.ReceiverMentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.RequesterMentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.StatusMentorshipRequestFilter;

public class MentorshipRequestServiceTestConstants {
  protected static final Long VALID_MENTORSHIP_REQUEST_ID = 1L;
  protected static final Long INVALID_MENTORSHIP_REQUEST_ID = 0L;

  protected static final String VALID_MENTORSHIP_REQUEST_DESCRIPTION = "Why not?";
  protected static final String INVALID_MENTORSHIP_REQUEST_DESCRIPTION = null;

  protected static final User USER_1 = new User();
  protected static final User USER_2 = new User();

  protected static final RequestStatus VALID_REQUEST_STATUS = RequestStatus.PENDING;

  protected static final String NULL_REJECTION_REASON = null;

  protected static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now();

  protected static final MentorshipRequestRequestDto INVALID_MENTORSHIP_REQUEST_REQUEST_DTO =
      new MentorshipRequestRequestDto(
          INVALID_MENTORSHIP_REQUEST_DESCRIPTION,
          INVALID_MENTORSHIP_REQUEST_ID,
          INVALID_MENTORSHIP_REQUEST_ID);

  protected static final Long VALID_USER_ID_1 = 1L;
  protected static final Long VALID_USER_ID_2 = 2L;

  protected static final MentorshipRequestRequestDto MENTORSHIP_REQUEST_REQUEST_DTO =
      new MentorshipRequestRequestDto(
          VALID_MENTORSHIP_REQUEST_DESCRIPTION, VALID_USER_ID_1, VALID_USER_ID_2);

  protected static final MentorshipRequest VALID_MENTORSHIP_REQUEST =
      new MentorshipRequest(
          VALID_MENTORSHIP_REQUEST_ID,
          VALID_MENTORSHIP_REQUEST_DESCRIPTION,
          USER_1,
          USER_2,
          VALID_REQUEST_STATUS,
          NULL_REJECTION_REASON,
          LOCAL_DATE_TIME_NOW,
          LOCAL_DATE_TIME_NOW);

  protected static final MentorshipRequestResponseDto VALID_MENTORSHIP_REQUEST_RESPONSE_DTO =
      new MentorshipRequestResponseDto(
          VALID_MENTORSHIP_REQUEST_ID,
          VALID_MENTORSHIP_REQUEST_DESCRIPTION,
          VALID_USER_ID_1,
          VALID_USER_ID_2,
          VALID_REQUEST_STATUS,
          NULL_REJECTION_REASON,
          LOCAL_DATE_TIME_NOW,
          LOCAL_DATE_TIME_NOW);

  protected static final RejectionDto VALID_REJECTION_DTO = new RejectionDto("I don't want");

  protected static final MentorshipRequestFilterDto EMPTY_MENTORSHIP_REQUEST_FILTER_DTO =
      new MentorshipRequestFilterDto(null, null, null, null);

  protected static final List<MentorshipRequest> VALID_MENTORSHIP_REQUESTS =
      List.of(VALID_MENTORSHIP_REQUEST);

  protected static final List<MentorshipRequestResponseDto> VALID_MENTORSHIP_REQUEST_RESPONSE_DTOS =
      List.of(VALID_MENTORSHIP_REQUEST_RESPONSE_DTO);

  protected static final MentorshipRequestFilter DESCRIPTION_MENTORSHIP_REQUEST_FILTER =
      new DescriptionMentorshipRequestFilter();

  protected static final MentorshipRequestFilter RECEIVER_MENTORSHIP_REQUEST_FILTER =
      new ReceiverMentorshipRequestFilter();

  protected static final MentorshipRequestFilter REQUESTER_MENTORSHIP_REQUEST_FILTER =
      new RequesterMentorshipRequestFilter();

  protected static final MentorshipRequestFilter STATUS_MENTORSHIP_REQUEST_FILTER =
      new StatusMentorshipRequestFilter();

  protected static final List<MentorshipRequestFilter> MENTORSHIP_REQUEST_FILTERS =
      List.of(
          DESCRIPTION_MENTORSHIP_REQUEST_FILTER,
          RECEIVER_MENTORSHIP_REQUEST_FILTER,
          REQUESTER_MENTORSHIP_REQUEST_FILTER,
          STATUS_MENTORSHIP_REQUEST_FILTER);
}
