package school.faang.user_service.service.mentorship.filter;

import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface RequestFilter {

    boolean isApplicable(RequestFilterDto filters);

    Stream<MentorshipRequest> apply(MentorshipRequest request, RequestFilterDto filters);
}
