package school.faang.user_service.util.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.util.filter.Filter;

import java.util.stream.Stream;

@Component
public class ReceiverFilter implements Filter<RequestFilterDto, MentorshipRequest> {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getReceiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        return requests.filter(mentorshipRequest -> mentorshipRequest.getReceiver().getId() == filters.getReceiverId());
    }
}
