package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.function.Supplier;
import java.util.stream.Stream;

@Component
public class MentorshipRequestReceiverFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filters) {
        return filters.getReceiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Supplier<Stream<MentorshipRequest>> requests, MentorshipRequestFilterDto filters) {
        return requests.get().filter(request -> filters.getReceiverId().equals(request.getReceiver().getId()));
    }
}
