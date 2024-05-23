package school.faang.user_service.filter.mentorship_request;

import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface MentorshipRequestFilter {

    boolean isApplicable(MentorshipRequestFilterDto filters);

    Stream<MentorshipRequest> apply(Supplier<Stream<MentorshipRequest>> requests, MentorshipRequestFilterDto filters);
}
