package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {

    boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests, MentorshipRequestFilterDto mentorshipRequestFilterDto);
}
