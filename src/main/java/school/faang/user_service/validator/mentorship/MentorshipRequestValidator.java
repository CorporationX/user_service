package school.faang.user_service.validator.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final UserService userService;
    private static final int REQUEST_WITH_PREMIUM = 5;
    private static final int REQUEST_WITHOUT_PREMIUM = 3;

    public void requestValidate(MentorshipRequestDto requestDto) {
        if (requestDto.getRequesterId() == requestDto.getReceiverId()) {
            throw new DataValidationException("request was not create, your mentor is you");
        }

        User requester = userService.findUserById(requestDto.getRequesterId());
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

    public void acceptRequestValidator(MentorshipRequestDto mentorshipRequestDto, RequestStatus currentStatus) {
        if (currentStatus.equals(RequestStatus.ACCEPTED)) {
            throw new DataValidationException("Already ACCEPTED");
        }

        UserDto requester = userService.getUser(mentorshipRequestDto.getRequesterId());
        List<Long> mentorIds = userService.getMentorIds(requester.getId());
        if (!mentorIds.isEmpty() &&
                mentorIds.contains(mentorshipRequestDto.getReceiverId())) {
            throw new DataValidationException("Already working");
        }
    }

    public void rejectRequestValidator(MentorshipRequestDto mentorshipRequestDto, RequestStatus currentStatus) {
        if (currentStatus.equals(RequestStatus.REJECTED)) {
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
