package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

public interface MentorshipRequestFilter {

    boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto);

    List<MentorshipRequest> apply(List<MentorshipRequest> mentorshipRequests, MentorshipRequestFilterDto mentorshipRequestFilterDto);
}
