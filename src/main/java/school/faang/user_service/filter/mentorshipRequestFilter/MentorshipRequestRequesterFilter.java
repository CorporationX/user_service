package school.faang.user_service.filter.mentorshipRequestFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.filter_dto.MentorshipRequestFilterDto;
import school.faang.user_service.model.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MentorshipRequestRequesterFilter implements school.faang.user_service.filter.mentorshipRequestFilter.MentorshipRequestFilter {
    @Override
    public boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestFilterDto.getRequesterId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(List<MentorshipRequest> mentorshipRequestList,
                                           MentorshipRequestFilterDto mentorshipRequestFilterDto) {
        return mentorshipRequestList.stream().filter(mentorshipRequest ->
                mentorshipRequest.getRequester().getId().equals(mentorshipRequestFilterDto.getRequesterId()));
    }
}
