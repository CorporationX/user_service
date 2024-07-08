package school.faang.user_service.service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipDescriptionFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getDescriptionPattern() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        return requests.filter(req -> req.getDescription().toLowerCase().contains(filters.getDescriptionPattern().toLowerCase()));
    }
}
