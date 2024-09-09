package school.faang.user_service.service.mentorship_request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.EMPTY_DESCRIPTION;
import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.ONCE_EVERY_THREE_MONTHS;
import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.REQUEST_IS_ACCEPTED_BEFORE;
import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.REQUEST_TO_HIMSELF;
import static school.faang.user_service.service.mentorship_request.error_messages.MentorshipRequestErrorMessages.USER_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipRequestParametersChecker {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    public void checkExistAcceptedRequest(long requesterId, long receiverId) {
        log.info("Check exist accepted request");
        if (mentorshipRequestRepository.existAcceptedRequest(requesterId, receiverId)) {
            log.error("Mentorship request from user with id {} to user with id {} was accepted before",
                    requesterId, receiverId);
            throw new IllegalArgumentException(
                    String.format(REQUEST_IS_ACCEPTED_BEFORE, requesterId, receiverId)
            );
        }
        log.info("Request mentorship wasn't accepted before");
    }

    public void checkRequestParams(long requesterId, long receiverId, String description) {
        validateDescription(description);
        checkSameUserIds(requesterId, receiverId);
        checkExistUserId(requesterId);
        checkExistUserId(receiverId);
        checkLatestMentorshipRequest(requesterId, receiverId);
    }

    private void validateDescription(String description) {
        log.info("Validate description");
        if (description == null || description.isBlank()) {
            log.error("Description is null or empty");
            throw new IllegalArgumentException(EMPTY_DESCRIPTION);
        }
        log.info("Validation of description is successful");
    }

    private void checkSameUserIds(long requesterId, long receiverId) {
        log.info("Validate request to himself");
        if (requesterId == receiverId) {
            log.error(REQUEST_TO_HIMSELF);
            throw new RuntimeException(REQUEST_TO_HIMSELF);
        }
        log.info("User ids are different");
    }

    private void checkExistUserId(long id) {
        log.info("Check exist user with id {}", id);
        if (!userRepository.existsById(id)) {
            log.error("User with id {} not found", id);
            throw new RuntimeException(String.format(USER_NOT_FOUND, id));
        }
        log.info("User with id {} has been found", id);
    }

    private void checkLatestMentorshipRequest(long requesterId, long receiverId) {
        log.info("Check latest mentorship request was before 3 months ago");
        MentorshipRequest latestMentorshipRequest = mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId)
                .orElse(null);
        if (latestMentorshipRequest == null) {
            log.info("This is first request from user with id {} to user with id {}",
                    requesterId, receiverId);
        } else {
            log.info("Latest request from user with id {} to user with id {} was {}",
                    requesterId, receiverId, latestMentorshipRequest.getCreatedAt());
        }
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        if (latestMentorshipRequest != null && latestMentorshipRequest.getCreatedAt().isAfter(threeMonthsAgo)) {
            log.error(ONCE_EVERY_THREE_MONTHS);
            throw new RuntimeException(ONCE_EVERY_THREE_MONTHS);
        }
        log.info("Latest mentorship request was before 3 months ago");
    }
}
