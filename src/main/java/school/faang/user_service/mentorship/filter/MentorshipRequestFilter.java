package school.faang.user_service.mentorship.filter;

import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {

    boolean isApplicable(RequestFilterDto filterDto);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto filterDto);
}
