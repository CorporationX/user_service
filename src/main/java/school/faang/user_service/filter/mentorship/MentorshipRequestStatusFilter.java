package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

@Component
public class MentorshipRequestStatusFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.getStatus() != null;
    }

    @Override
    public boolean filter(MentorshipRequest mentorshipRequest,
                          MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequest.getStatus() == mentorshipRequestFilterDto.getStatus();
    }
}
