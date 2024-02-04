package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDro;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class RequestReceiverFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDro filters) {
        return filters.getReceiverPattern() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDro filters) {
        return requests.filter(request -> request.getReceiver().getUsername().contains(filters.getReceiverPattern()));
    }
}
