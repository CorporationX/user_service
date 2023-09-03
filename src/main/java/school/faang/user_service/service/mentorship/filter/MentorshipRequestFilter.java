package school.faang.user_service.service.mentorship.filter;

import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(RequestFilterDto filters);
    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filter);
}
