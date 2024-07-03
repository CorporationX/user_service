package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

public interface MentorshipRequestFilter {
    boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto);

    boolean filter(MentorshipRequest mentorshipRequest,
                   MentorshipRequestFilterDto mentorshipRequestFilterDto);
}
