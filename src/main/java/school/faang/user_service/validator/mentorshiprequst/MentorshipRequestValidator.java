package school.faang.user_service.validator.mentorshiprequst;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private static final long MONTHS_REQUEST = 3;
    private final UserService userService;

    public void validateReceiverNoEqualsRequester(MentorshipRequestDto menReqDto) {
        if (menReqDto.getRequesterId() == menReqDto.getReceiverId()) {
            throw new IllegalArgumentException("The user cannot designate himself as a requester.");
        }
    }

    public void validateAvailabilityUsersDB(MentorshipRequestDto menReqDto) {
        List<Long> usersId = Arrays.asList(menReqDto.getRequesterId(), menReqDto.getReceiverId());
        List<User> users = userService.getUsersById(usersId);

        if (users.size() < 2) {
            throw new EntityNotFoundException("The user ID is not correct");
        }
    }

    public void validateDataCreateRequest(MentorshipRequest menReq) {
        if (LocalDate.now().isAfter(ChronoLocalDate.from(menReq.getCreatedAt().plusMonths(MONTHS_REQUEST)))) {
            throw new IllegalArgumentException(
                    "It should pass " + MONTHS_REQUEST + " one month since the last application for mentoring");
        }
    }

    public void validateMentorsContainsReceiver(MentorshipRequest mentorshipRequest) {
        if (mentorshipRequest.getReceiver().getMentors().contains(mentorshipRequest.getRequester())) {
            throw new IllegalArgumentException("The user is already your mentor");
        }
    }
}
