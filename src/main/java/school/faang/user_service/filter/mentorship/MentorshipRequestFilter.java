package school.faang.user_service.filter.mentorship;

import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

public interface MentorshipRequestFilter {

    boolean isApplicable(RequestFilterDto filterDto);

    void apply(List<MentorshipRequest> mentorshipRequests, RequestFilterDto filterDto);
}
