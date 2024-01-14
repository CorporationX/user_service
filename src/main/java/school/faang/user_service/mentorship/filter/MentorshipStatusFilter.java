package school.faang.user_service.mentorship.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Component
public class MentorshipStatusFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getStatusFilter() != null;
    }

    @Override
    public void apply(List<MentorshipRequest> mentorshipRequests, RequestFilterDto filterDto) {
        mentorshipRequests.removeIf(mentorshipRequest -> !mentorshipRequest.getStatus().equals(filterDto.getStatusFilter()));
    }
}
