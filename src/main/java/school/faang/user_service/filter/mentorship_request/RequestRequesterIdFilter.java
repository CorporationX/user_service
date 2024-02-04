package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDro;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class RequestRequesterIdFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDro filters) {
        return filters.getRequesterIdPattern() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDro filters) {
        return requests.filter(request -> {
            String requesterId = String.valueOf(request.getRequester().getId());
            return requesterId.contains(String.valueOf(filters.getRequesterIdPattern()));
        });
    }
}
