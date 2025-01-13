package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.Objects;

@Component
public class RequesterMentorshipRequestFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.getRequesterId() != null;
    }

    @Override
    public List<MentorshipRequest> apply(List<MentorshipRequest> mentorshipRequests, MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequests.stream()
                .filter(mentorshipRequest
                        -> Objects.equals(mentorshipRequest.getRequester().getId(), mentorshipRequestFilterDto.getRequesterId()))
                .toList();
    }
}
