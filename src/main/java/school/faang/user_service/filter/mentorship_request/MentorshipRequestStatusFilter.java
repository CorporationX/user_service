package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestStatusFilter implements MentorshipRequestFilter{
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getStatus()!=null;
    }

    @Override
    public void apply(Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto filters) {
        mentorshipRequests.filter(request -> request.getStatus()==filters.getStatus());
    }
}
