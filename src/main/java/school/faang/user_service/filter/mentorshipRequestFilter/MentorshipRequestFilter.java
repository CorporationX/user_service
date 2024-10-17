package school.faang.user_service.filter.mentorshipRequestFilter;

import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.model.filter_dto.MentorshipRequestFilterDto;

import java.util.List;
import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(MentorshipRequestFilterDto mentorshipRequestFilterDto);

    Stream<MentorshipRequest> apply(List<MentorshipRequest> mentorshipRequestList,
                                    MentorshipRequestFilterDto mentorshipRequestFilterDto);
}
