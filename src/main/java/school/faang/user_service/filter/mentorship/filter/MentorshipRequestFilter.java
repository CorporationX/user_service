package school.faang.user_service.filter.mentorship.filter;

import lombok.Data;
import school.faang.user_service.dto_mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public interface MentorshipRequestFilter {
    boolean isApplicable(RequestFilterDto filters);
    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters);
}
