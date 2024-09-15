package school.faang.user_service.filter.mentorshipRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MentorshipRequestStatusFilter implements MentorshipRequestFilter{
    @Override
    public boolean isApplicable (MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(List<MentorshipRequest> mentorshipRequestList,
                                           MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestList.stream().filter(mentorshipRequest ->
                mentorshipRequest.getStatus().equals(mentorshipRequestFilterDto.getStatus()));
    }
}
