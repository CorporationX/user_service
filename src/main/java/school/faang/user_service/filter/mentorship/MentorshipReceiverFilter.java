package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Component
public class MentorshipReceiverFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getReceiverFilter() != null;
    }

    @Override
    public void apply(List<MentorshipRequest> mentorshipRequests, RequestFilterDto filterDto) {
        mentorshipRequests.removeIf(mentorshipRequest -> !(mentorshipRequest.getReceiver().getId() == filterDto.getReceiverFilter()));
    }
}
