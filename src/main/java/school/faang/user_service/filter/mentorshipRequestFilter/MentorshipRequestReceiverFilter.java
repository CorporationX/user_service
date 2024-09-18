package school.faang.user_service.filter.mentorshipRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MentorshipRequestReceiverFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.getReceiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(List<MentorshipRequest> mentorshipRequestList,
                                           MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestList.stream().filter(mentorshipRequest ->
                mentorshipRequest.getReceiver().getId() == mentorshipRequestFilterDto.getReceiverId());
    }
}
