package school.faang.user_service.service.mentorship.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class ReceiverFilter implements RequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.receiverIdFilter() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        return requests.filter(request -> Objects.equals(request.getReceiver().getId(), filters.receiverIdFilter()));
    }
}
