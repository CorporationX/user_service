package school.faang.user_service.listener;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;

@Component
public class MentorshipRequestActivity implements Activity{
    private final Long rating = 3L;
    @Override
    public Long getUserId(Object object) {
        MentorshipRequest mentorshipRequest = (MentorshipRequest) object;
        return mentorshipRequest.getReceiver().getId();
    }

    @Override
    public Long getRating(Object object) {
        return rating;
    }

    @Override
    public Class getEntityClass() {
        return MentorshipRequest.class;
    }
}
