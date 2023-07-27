package school.faang.user_service.filter.mentorship;

import java.util.List;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

@Component
public interface MentorshipRequestFilter {
    boolean isApplicable(MentorshipRequestFilterDto filter);

    void apply(List<MentorshipRequest> requests, MentorshipRequestFilterDto filter);
}
