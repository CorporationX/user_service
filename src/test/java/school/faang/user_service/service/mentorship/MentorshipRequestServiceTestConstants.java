package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorship.DescriptionMentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.ReceiverMentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.RequesterMentorshipRequestFilter;
import school.faang.user_service.filter.mentorship.StatusMentorshipRequestFilter;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

public class MentorshipRequestServiceTestConstants {
    protected static final String VALID_MENTORSHIP_REQUEST_DESCRIPTION = "Why not?";
    protected static final String INVALID_MENTORSHIP_REQUEST_DESCRIPTION = null;

    protected static final Long VALID_MENTORSHIP_REQUEST_ID = 1L;
    protected static final Long INVALID_MENTORSHIP_REQUEST_ID = 0L;

    protected static final MentorshipRequestDto INVALID_MENTORSHIP_REQUEST_DTO
            = new MentorshipRequestDto(INVALID_MENTORSHIP_REQUEST_DESCRIPTION,
            INVALID_MENTORSHIP_REQUEST_ID, INVALID_MENTORSHIP_REQUEST_ID);

    protected static final User USER_1 = new User();
    protected static final User USER_2 = new User();

    protected static final Long VALID_USER_ID_1 = 1L;
    protected static final Long VALID_USER_ID_2 = 2L;

    protected static final MentorshipRequestDto VALID_MENTORSHIP_REQUEST_DTO
            = new MentorshipRequestDto(VALID_MENTORSHIP_REQUEST_DESCRIPTION, VALID_USER_ID_1, VALID_USER_ID_2);

    protected static final MentorshipRequest VALID_MENTORSHIP_REQUEST
            = new MentorshipRequest(VALID_MENTORSHIP_REQUEST_ID, VALID_MENTORSHIP_REQUEST_DESCRIPTION, USER_1, USER_2,
            RequestStatus.PENDING, null, LocalDateTime.now(), LocalDateTime.now());

    protected static final RejectionDto VALID_REJECTION_DTO = new RejectionDto("I don't want");

    protected static final MentorshipRequestFilterDto EMPTY_MENTORSHIP_REQUEST_FILTER_DTO
            = new MentorshipRequestFilterDto(null, null, null, null);

    protected static final List<MentorshipRequest> MENTORSHIP_REQUESTS = List.of(VALID_MENTORSHIP_REQUEST);

    protected static final MentorshipRequestFilter DESCRIPTION_MENTORSHIP_REQUEST_FILTER
            = new DescriptionMentorshipRequestFilter();

    protected static final MentorshipRequestFilter RECEIVER_MENTORSHIP_REQUEST_FILTER
            = new ReceiverMentorshipRequestFilter();

    protected static final MentorshipRequestFilter REQUESTER_MENTORSHIP_REQUEST_FILTER
            = new RequesterMentorshipRequestFilter();

    protected static final MentorshipRequestFilter STATUS_MENTORSHIP_REQUEST_FILTER
            = new StatusMentorshipRequestFilter();

    protected static final Iterator<MentorshipRequestFilter> MENTORSHIP_REQUEST_FILTER_ITERATOR
            = List.of(DESCRIPTION_MENTORSHIP_REQUEST_FILTER, RECEIVER_MENTORSHIP_REQUEST_FILTER,
            REQUESTER_MENTORSHIP_REQUEST_FILTER, STATUS_MENTORSHIP_REQUEST_FILTER).iterator();
}
