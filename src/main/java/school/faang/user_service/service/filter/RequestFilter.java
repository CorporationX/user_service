package school.faang.user_service.service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface RequestFilter {
    boolean isApplicable(RequestFilterDto filters);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters);
}
