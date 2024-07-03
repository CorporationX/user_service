package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

@Component
public class MentorshipRequestDescriptionFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.getDescription() != null;
    }

    @Override
    public boolean filter(MentorshipRequest mentorshipRequest,
                          MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequest.getDescription().toLowerCase()
                .contains(mentorshipRequestFilterDto.getDescription().toLowerCase());
    }
}
