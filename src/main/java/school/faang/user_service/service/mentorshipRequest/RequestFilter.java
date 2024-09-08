package school.faang.user_service.service.mentorshipRequest;

import school.faang.user_service.dto.mentorshipRequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

public interface RequestFilter {
    boolean isApplicable(RequestFilterDto filter);
    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequestStream, RequestFilterDto filter);
}
