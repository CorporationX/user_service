package school.faang.user_service.filter.mentorship;

import java.util.stream.Stream;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

public interface MentorshipRequestFilter {

  boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto);

  Stream<MentorshipRequest> apply(
      Stream<MentorshipRequest> mentorshipRequests,
      MentorshipRequestFilterDto mentorshipRequestFilterDto);
}
