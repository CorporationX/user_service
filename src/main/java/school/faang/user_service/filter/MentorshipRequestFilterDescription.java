package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class MentorshipRequestFilterDescription implements MentorshipRequestFilter {
    @Override
    public boolean isApplyable(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getDescription() != null
                && !requestFilterDto.getDescription().isBlank();
    }

    @Override
    public Stream<MentorshipRequest> filter(Stream<MentorshipRequest> requests, RequestFilterDto filter) {
        return  requests.filter(request ->
                request.getDescription().contains(filter.getDescription()));
    }
}
