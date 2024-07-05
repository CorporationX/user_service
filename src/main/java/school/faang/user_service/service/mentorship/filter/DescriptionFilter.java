package school.faang.user_service.service.mentorship.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
class DescriptionFilter implements RequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getDescription() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(MentorshipRequest request, RequestFilterDto filters) {
        return Stream.of(request).filter(r -> r.getDescription().equals(filters.getDescription()));
    }
}
