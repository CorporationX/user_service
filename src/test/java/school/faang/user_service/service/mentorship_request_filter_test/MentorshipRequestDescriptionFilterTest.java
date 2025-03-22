package school.faang.user_service.service.mentorship_request_filter_test;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;

import java.util.stream.Stream;

public class MentorshipRequestDescriptionFilterTest implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto requestFilterDto) {
        return true;
    }

    @Override
    public Stream<MentorshipRequest> apply(
            Stream<MentorshipRequest> mentorshipRequests,
            RequestFilterDto requestFilterDto) {
        return mentorshipRequests
                .filter(mentorshipRequest -> mentorshipRequest
                        .getDescription()
                        .equalsIgnoreCase("test"));
    }
}
