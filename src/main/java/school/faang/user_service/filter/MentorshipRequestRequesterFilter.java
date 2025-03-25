package school.faang.user_service.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.UserService;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MentorshipRequestRequesterFilter implements MentorshipRequestFilter {
    private final UserService userService;

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.requesterUsername() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests,
                                           MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequests.filter(
                mentorshipRequest -> userService.getUniqueIdByUsername(mentorshipRequestFilterDto.requesterUsername())
                        == mentorshipRequest.getRequester().getId());
    }
}