package school.faang.user_service.volidate.mentorship;

import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

public class AcceptRequestValidator {
    public void validate(MentorshipRequest request) throws Exception {
        if (request.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new Exception("Already ACCEPTED");
        }
    }
}
