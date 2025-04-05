package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class RequestDescriptionFilter implements RequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getDescription() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filters) {
        return requests.filter(request -> request.getDescription().contains(filters.getDescription()));
    }
}
