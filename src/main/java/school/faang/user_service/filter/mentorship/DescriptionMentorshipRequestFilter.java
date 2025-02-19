package school.faang.user_service.filter.mentorship;

import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

@Component
public class DescriptionMentorshipRequestFilter implements MentorshipRequestFilter {
  @Override
  public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
    String description = mentorshipRequestFilterDto.getDescriptionPattern();
    return description != null && !description.isEmpty();
  }

  @Override
  public Stream<MentorshipRequest> apply(
      Stream<MentorshipRequest> mentorshipRequests,
      MentorshipRequestFilterDto mentorshipRequestFilterDto) {
    return mentorshipRequests.filter(
        mentorshipRequest ->
            mentorshipRequest
                .getDescription()
                .contains(mentorshipRequestFilterDto.getDescriptionPattern()));
  }
}
