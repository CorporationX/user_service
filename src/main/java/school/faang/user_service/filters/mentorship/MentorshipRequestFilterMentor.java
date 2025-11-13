package school.faang.user_service.filters.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.user.MentorshipRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class MentorshipRequestFilterMentor implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.receiverId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(
            Stream<MentorshipRequest> mentorshipRequest,
            MentorshipRequestFilterDto mentorshipRequestFilterDto) {

        return mentorshipRequest.filter((receiver) -> Objects.equals(
                mentorshipRequestFilterDto.receiverId(),
                receiver.getReceiver().getId()));
    }
}