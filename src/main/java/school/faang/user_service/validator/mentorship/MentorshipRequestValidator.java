package school.faang.user_service.validator.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.DataNotFoundException;
import school.faang.user_service.exception.mentorship.DataValidationException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;


@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {

    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void validateUserData(User receiver, User requester) {
        if (requester.getId() == receiver.getId()) {
            throw new DataNotFoundException("Requester and receiver the same user");
        }
        Optional<MentorshipRequest> latestRequest = mentorshipRequestRepository
                .findLatestRequest(requester.getId(), receiver.getId());
        if (latestRequest.isPresent()) {
            if (DAYS.between(latestRequest.get().getCreatedAt(), LocalDateTime.now()) < 90) {
                throw new DataValidationException("You can't apply for a mentorship more than once every 90 days.");
            }
        }
    }

    public void commonCheck(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getId() == null || mentorshipRequestDto.getRequester() == null
                || mentorshipRequestDto.getReceiver() == null || mentorshipRequestDto.getDescription() == null) {
            throw new DataValidationException("Incorrect data in mentorshipRequestDto");
        }
    }
}
