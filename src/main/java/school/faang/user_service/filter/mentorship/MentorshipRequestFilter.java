package school.faang.user_service.filter.mentorship;

import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(RequestFilterDto filter);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> entities, RequestFilterDto filterDto);
}
