package school.faang.user_service.filter.mentorship_request;

import school.faang.user_service.dto.RequestFilterDro;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(RequestFilterDro filters);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDro filters);
}
