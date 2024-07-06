package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestRequesterFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.getRequesterId() != null;
    }

    @Override
    public Stream<MentorshipRequest> filter(Stream<MentorshipRequest> mentorshipRequestStream,
                                            MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestStream.filter(mentorshipRequest ->
                mentorshipRequest.getRequester().getId() == mentorshipRequestFilterDto.getRequesterId());
    }
}
