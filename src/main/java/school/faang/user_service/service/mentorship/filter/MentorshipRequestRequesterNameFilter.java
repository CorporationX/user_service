package school.faang.user_service.service.mentorship.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestRequesterNameFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getRequesterNamePattern() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filter) {
        return requests.filter(request -> request.getRequester().getUsername().contains(filter.getRequesterNamePattern()));
    }
}
