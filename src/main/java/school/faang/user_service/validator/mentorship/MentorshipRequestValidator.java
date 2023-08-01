package school.faang.user_service.validator.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MentorshipRequestValidator {
    private static final int REQUEST_WITH_PREMIUM = 5;
    private static final int REQUEST_WITHOUT_PREMIUM = 3;

    public void requestValidate(User requester, User receiver) {
        if (requester.getId() == receiver.getId()) {
            throw new DataValidationException("request was not create, your mentor is you");
        }

        int numberOfRequestsInMonth = getRequestsInMonth(requester);

        List<MentorshipRequest> sentRequests = requester.getSentMentorshipRequests();
        if (sentRequests.size() >= numberOfRequestsInMonth) {

            MentorshipRequest thirdLatestRequest = sentRequests.get(sentRequests.size() - numberOfRequestsInMonth);
            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

            if (!thirdLatestRequest.getCreatedAt().isBefore(oneMonthAgo)) {
                throw new DataValidationException("request was not created, too many requests in this month");
            }
        }
    }

    public void acceptRequestValidator(MentorshipRequest request) {
        User requester = request.getRequester();
        User receiver = request.getReceiver();

        if (request.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new DataValidationException("Already ACCEPTED");
        }
        if (requester.getMentors().contains(receiver)) {
            throw new DataValidationException("Already working");
        }
    }

    public void rejectRequestValidator(MentorshipRequest request) {
        if (request.getStatus().equals(RequestStatus.REJECTED)) {
            throw new DataValidationException("Already REJECTED");
        }
    }

    private int getRequestsInMonth(User requester) {
        if (requester.getPremium() != null) {
            return requester.getPremium()
                    .getEndDate()
                    .isAfter(LocalDateTime.now()) ? REQUEST_WITH_PREMIUM : REQUEST_WITHOUT_PREMIUM;
        } else {
            return REQUEST_WITHOUT_PREMIUM;
        }
    }
}
