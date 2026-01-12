package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestReceiverIdFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filter) {
        return filter.receiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests,
                                           MentorshipRequestFilterDto filter) {
        return mentorshipRequests
                .filter(request -> filter.receiverId().equals(request.getReceiver().getId()));
    }
}
