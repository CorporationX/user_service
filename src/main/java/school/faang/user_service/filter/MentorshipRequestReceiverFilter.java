package school.faang.user_service.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.UserService;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestReceiverFilter implements MentorshipRequestFilter {
    private final UserService userService;

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.receiverUsername() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests,
                                           MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequests.filter(
                mentorshipRequest -> userService.getUniqueIdByUsername(mentorshipRequestFilterDto.receiverUsername())
                        == mentorshipRequest.getReceiver().getId());
    }
}
