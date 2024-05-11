package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

public class MentorshipRequestRequesterIdFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getRequesterIdPattern() != null;
    }

    @Override
    public boolean apply(MentorshipRequest entity, RequestFilterDto filterDto) {
        var requesterIdFilter = filterDto.getRequesterIdPattern();

        var result = entity.getRequester().getId() == requesterIdFilter;
        return result;


//        return entity.getRequester().getId() == requesterIdFilter;
    }
}
