package school.faang.user_service.service.mentorship_request_filter;

import lombok.Data;
import lombok.ToString;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
public interface MentorshipRequestFilter {

    boolean isApplecable(RequestFilterDto filters);

    List<MentorshipRequest> apply(List<MentorshipRequest> requests, RequestFilterDto filters);
}
