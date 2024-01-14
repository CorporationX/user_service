package school.faang.user_service.mentorship.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipDescriptionFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getDescriptionFilter() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto filterDto) {
        return mentorshipRequests
                .filter(mentorshipRequest -> mentorshipRequest.getDescription()
                        .contains(filterDto.getDescriptionFilter()));
    }
}
