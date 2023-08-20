package school.faang.user_service.filter.mentorship_request;

import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(RequestFilterDto filters);
    void apply(Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto filters);
}
