package school.faang.user_service.filter.mentorship_request;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDro;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;

import java.util.stream.Stream;

@Component
public class RequestDescriptionFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDro filters) {
        return filters.getDescriptionPattern() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDro filters) {
        return requests.filter(request -> request.getDescription().contains(filters.getDescriptionPattern()));
    }
}
