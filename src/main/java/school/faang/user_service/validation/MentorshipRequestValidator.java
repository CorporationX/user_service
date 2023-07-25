package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.Optional;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {

    private final UserService userService;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void validate(MentorshipRequest mentorshipRequest) {
        Long receiverId = mentorshipRequest.getReceiver().getId();
        Long requesterId = mentorshipRequest.getRequester().getId();

        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("You can not send request to yourself");
        }
        if (!userService.isUserExist(receiverId)) {
            throw new IllegalArgumentException("Invalid request. Mentor with id: " +  requesterId + " not found");
        }
        if (!userService.isUserExist(requesterId)) {
            throw new IllegalArgumentException("Invalid request. Mentee with id: " + " not found");
        }

        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        Optional<MentorshipRequest> existingRequest = mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId);

        if (existingRequest.isPresent() && existingRequest.get().getCreatedAt().isAfter(threeMonthsAgo)) {
            throw new IllegalArgumentException("You can only send a mentorship request once every 3 months");
        }
    }
}
