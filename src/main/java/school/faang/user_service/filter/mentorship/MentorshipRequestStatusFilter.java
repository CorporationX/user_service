package school.faang.user_service.filter.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestStatusFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getStatusPattern() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> entities, RequestFilterDto filterDto) {
        var statusFilter = filterDto.getStatusPattern();

        return entities.filter(entity -> entity.getStatus().equals(statusFilter));
    }
}