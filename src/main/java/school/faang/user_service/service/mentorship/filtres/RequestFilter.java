package school.faang.user_service.service.mentorship.filtres;

import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

public interface RequestFilter {
    boolean isApplicable(RequestFilterDto filter);

    Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto filter);
}
