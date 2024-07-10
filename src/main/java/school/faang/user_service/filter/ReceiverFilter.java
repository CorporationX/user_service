package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class ReceiverFilter implements MentorshipRequestFilter{
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getReceiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        return requests.filter(mentorshipRequest -> mentorshipRequest.getReceiver().getId() == (filters.getReceiverId()));
    }
}
