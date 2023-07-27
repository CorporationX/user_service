package school.faang.user_service.filter.mentorship;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

@Component
public class MentorshipRequestReceiverFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filter) {
        return Objects.nonNull(filter.getReceiverId());
    }

    @Override
    public void apply(List<MentorshipRequest> requests, MentorshipRequestFilterDto filter) {
        requests.removeIf(request -> !Objects.equals(request.getReceiver().getId(), filter.getReceiverId()));
    }
}
