package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

public class MentorshipRequestStatusFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getStatusPattern() != null;
    }

    @Override
    public boolean apply(MentorshipRequest entity, RequestFilterDto filterDto) {
        var requestStatus = filterDto.getStatusPattern();

        var result = entity.getStatus().equals(requestStatus);
        return result;

//        return entity.getStatus().equals(requestStatus);
    }
}