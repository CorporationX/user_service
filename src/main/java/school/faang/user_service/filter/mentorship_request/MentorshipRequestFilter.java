package school.faang.user_service.filter.mentorship_request;

import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(MentorshipRequestFilterDto filter);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests,
                                    MentorshipRequestFilterDto filter);
}
