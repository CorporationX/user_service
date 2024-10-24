package school.faang.user_service.service.mentorship.filter;

import school.faang.user_service.dto.mentorship.request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface RequestFilter {
    boolean isApplicable(RequestFilterDto filters);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters);
}
