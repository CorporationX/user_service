package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class CreatedAtFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getCreatedAt() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        return requests.filter(request -> request.getCreatedAt().equals(filters.getCreatedAt()));
    }
}
