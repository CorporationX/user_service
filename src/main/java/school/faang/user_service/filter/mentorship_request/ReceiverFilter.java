package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class ReceiverFilter implements RequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filterDto) {
        return filterDto.receiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, MentorshipRequestFilterDto filters) {
        return requests.filter(request -> Objects.equals(request.getReceiver().getId(), filters.receiverId()));
    }
}
