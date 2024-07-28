package school.faang.user_service.validation.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class mentorshipRequestValidator {
    public static void validateSelfRequest(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            throw new IllegalArgumentException("Cannot request from yourself");
        }
    }

    public static void validateDescription(MentorshipRequest mentorshipRequest) {
        if (mentorshipRequest.getDescription()
                .length() < 4) {
            throw new IllegalArgumentException("Mentorship description is too short, it should be at least 4 characters");
        }
    }

    public static void validateRequestUsers(Optional<User> user) {
        if (user.isEmpty()) {
            throw new NoSuchElementException("One or both users not found");
        }
    }

    public static void validateLastRequestDate(Optional<MentorshipRequest> latestRequest , int monthsCooldown) {
        LocalDateTime cooldownThreshold = LocalDateTime.now().minusMonths(monthsCooldown);
        if (latestRequest.isPresent() && latestRequest.get()
                .getCreatedAt()
                .isAfter(cooldownThreshold)) {
            throw new IllegalArgumentException("A mentorship request has already been made too early");
        }
    }

    public static void validateRequestAccepted(MentorshipRequest request) {
        if (request.getStatus() == RequestStatus.ACCEPTED) {
            throw new IllegalArgumentException("Mentorship request already accepted");
        }
    }
    public static void validateRequestRejected(MentorshipRequest request) {
        if (request.getStatus() == RequestStatus.REJECTED) {
            throw new IllegalArgumentException("Mentorship request already rejected");
        }
    }

    public static void validateRequestsCount(List<MentorshipRequestDto> response) {
        if (response.isEmpty()) {
            throw new NoSuchElementException("No mentorship requests found");
        }
    }
    public static void validateMentorshipExistance(boolean mentorshipExists) {
        if (mentorshipExists) {
            throw new IllegalArgumentException("Mentorship already exists");
        }
    }
}
