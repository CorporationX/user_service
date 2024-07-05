package school.faang.user_service.service.mentorship.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
class StatusFilter implements RequestFilter{

    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(MentorshipRequest request, RequestFilterDto filters) {
        return Stream.of(request).filter(r -> r.getStatus().equals(filters.getStatus()));
    }
}
