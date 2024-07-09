package school.faang.user_service.service.mentorship_request_filter;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.Objects;
import java.util.List;
@Component
@Data
public class MentorshipRequestRequesterFilter implements MentorshipRequestFilter {

    @Override
    public boolean isApplecable(RequestFilterDto filters) {
        return filters.getRequesterId() != null;
    }

    @Override
    public List<MentorshipRequest> apply(List<MentorshipRequest> requests, RequestFilterDto filters) {
        return requests.stream()
                .filter(request -> Objects.equals(request.getRequester().getId(), filters.getRequesterId()))
                .toList();
    }
}
