package school.faang.user_service.filter.mentorshiprequest;

import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(RequestFilterDto dto);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requestStream, RequestFilterDto dto);
}
