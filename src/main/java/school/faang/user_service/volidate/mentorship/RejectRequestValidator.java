package school.faang.user_service.volidate.mentorship;

import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

public class RejectRequestValidator {
    public void validator(MentorshipRequest request) throws Exception {
        if (request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new Exception();
        }
    }
}
