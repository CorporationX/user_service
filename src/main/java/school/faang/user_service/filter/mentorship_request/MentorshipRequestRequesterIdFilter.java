package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestRequesterIdFilter implements MentorshipRequestFilter{
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getRequesterId()!=null;
    }

    @Override
    public void apply(Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto filters) {
        mentorshipRequests.filter(request -> request.getRequester().getId()==filters.getRequesterId());
    }
}
