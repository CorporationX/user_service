package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequest requestMentorship(MentorshipRequest mentorshipRequest);

    List<MentorshipRequest> getRequests(RequestFilterDto filter);

    MentorshipRequest acceptRequest(long id);

    MentorshipRequest rejectRequest(long id, RejectionDto rejection);
}
