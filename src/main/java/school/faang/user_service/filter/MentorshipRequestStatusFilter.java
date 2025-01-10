package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
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
    public void apply(Stream<MentorshipRequest> requests, MentorshipRequestFilterDto filters) {
        requests.filter(request -> Objects.equals(request.getStatus(), filters.getStatus()));
    }
}
