package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class MentorshipRequestStatusFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplicable(MentorshipRequestFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, MentorshipRequestFilterDto filters) {
        return requests.filter(request -> Objects.equals(request.getStatus(), filters.getStatus()));
    }
}
