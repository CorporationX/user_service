package school.faang.user_service.volidate.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MentorshipRequestValidator {
    public void validate(User requester, User receiver) throws Exception {
        if (requester == receiver) {
            throw new Exception("request was not create, your mentor is you");
        }

        int n = requester.getPremium().getEndDate().isAfter(LocalDateTime.now()) ? 5 : 3;

        List<MentorshipRequest> sentRequests = requester.getSentMentorshipRequests();
        if (sentRequests != null && sentRequests.size() >= n) {

            MentorshipRequest thirdLatestRequest = sentRequests.get(sentRequests.size() - n);
            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

            if (!thirdLatestRequest.getCreatedAt().isBefore(oneMonthAgo)) {
                throw new Exception("request was not created, too many requests in this month");
            }
        }
    }
}
