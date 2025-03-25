package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestStatusFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.status() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests,
                                           MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequests.filter(
                mentorshipRequest -> mentorshipRequestFilterDto.status()
                        .equals(mentorshipRequest.getStatus()));
    }
}
