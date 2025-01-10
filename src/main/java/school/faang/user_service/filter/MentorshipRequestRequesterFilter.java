package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestRequesterFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filters) {
        return filters.getRequesterId() != null;
    }

    @Override
    public void apply(Stream<MentorshipRequest> requests, MentorshipRequestFilterDto filters) {
        requests.filter(request -> request.getRequester().getId() == filters.getRequesterId());
    }
}
