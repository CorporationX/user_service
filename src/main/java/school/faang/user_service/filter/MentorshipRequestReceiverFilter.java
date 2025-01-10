package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestReceiverFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filters) {
        return filters.getReceiverId() != null;
    }

    @Override
    public void apply(Stream<MentorshipRequest> requests, MentorshipRequestFilterDto filters) {
        requests.filter(request -> request.getReceiver().getId() == filters.getReceiverId());
    }
}
