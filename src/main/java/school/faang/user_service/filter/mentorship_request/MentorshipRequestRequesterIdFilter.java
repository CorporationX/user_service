package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestRequesterIdFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filter) {
        return filter.requesterId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests,
                                           MentorshipRequestFilterDto filter) {
        return mentorshipRequests
                .filter(request -> filter.requesterId().equals(request.getRequester().getId()));
    }
}
