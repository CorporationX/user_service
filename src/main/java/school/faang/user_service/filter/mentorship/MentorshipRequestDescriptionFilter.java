package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestDescriptionFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.getDescription() != null;
    }

    @Override
    public Stream<MentorshipRequest> filter(Stream<MentorshipRequest> mentorshipRequestStream,
                                            MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestStream.filter(mentorshipRequest -> mentorshipRequest.getDescription().toLowerCase()
                .contains(mentorshipRequestFilterDto.getDescription()));
    }
}
