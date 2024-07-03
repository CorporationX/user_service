package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

@Component
public class MentorshipRequestReceiverFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.getReceiverId() != null;
    }

    @Override
    public boolean filter(MentorshipRequest mentorshipRequest,
                          MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequest.getReceiver().getId() == mentorshipRequestFilterDto.getReceiverId();
    }
}
