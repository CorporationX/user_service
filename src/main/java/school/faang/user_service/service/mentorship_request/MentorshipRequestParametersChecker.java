package school.faang.user_service.service.mentorship_request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.BadRequestException;
import school.faang.user_service.exception.UserNotFoundException;
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
        boolean isExist = mentorshipRequestRepository.existAcceptedRequest(requesterId, receiverId);
        if (isExist) {
            log.error("Mentorship request from user with id {} to user with id {} was accepted before",
                    requesterId, receiverId);
            throw new BadRequestException(REQUEST_IS_ACCEPTED_BEFORE, requesterId, receiverId);
        }
    }

    public void checkRequestParams(long requesterId, long receiverId, String description) {
        validateDescription(description);
        checkSameUserIds(requesterId, receiverId);
        checkExistUserId(requesterId);
        checkExistUserId(receiverId);
        checkLatestMentorshipRequest(requesterId, receiverId);
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            log.error("Description is null or empty");
            throw new BadRequestException(EMPTY_DESCRIPTION);
        }
    }

    private void checkSameUserIds(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            log.error(REQUEST_TO_HIMSELF);
            throw new BadRequestException(REQUEST_TO_HIMSELF);
        }
    }

    private void checkExistUserId(long id) {
        if (!userRepository.existsById(id)) {
            log.error("User with id {} not found", id);
            throw new UserNotFoundException(USER_NOT_FOUND, id);
        }
    }

    private void checkLatestMentorshipRequest(long requesterId, long receiverId) {
        MentorshipRequest latestMentorshipRequest = mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId)
                .orElse(null);
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        if (latestMentorshipRequest != null && latestMentorshipRequest.getCreatedAt().isAfter(threeMonthsAgo)) {
            log.error(ONCE_EVERY_THREE_MONTHS);
            throw new BadRequestException(ONCE_EVERY_THREE_MONTHS);
        }
    }
}
