package school.faang.user_service.service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class RequestFilterByDescription implements RequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filterDto) {
        return filterDto.getDescription() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> requests, RequestFilterDto filterDto) {
        return requests.filter(request -> request.getDescription().contains(filterDto.getDescription()));
    }
}