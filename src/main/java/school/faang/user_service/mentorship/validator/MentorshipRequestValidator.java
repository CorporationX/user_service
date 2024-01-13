package school.faang.user_service.mentorship.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mentorship.dto.MentorshipRequestDto;
import school.faang.user_service.mentorship.exception.MentorshipRequestException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;


@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {

    private MentorshipRequestRepository mentorshipRequestRepository;
    private UserRepository userRepository;

    public void mainMentorshipRequestValidation(MentorshipRequestDto mentorshipRequestDto) {
        Optional<User> requester = userRepository.findById(mentorshipRequestDto.getRequester());
        Optional<User> receiver = userRepository.findById(mentorshipRequestDto.getReceiver());

        if (requester.isEmpty() || receiver.isEmpty()) {
            throw new MentorshipRequestException("Requester or receiver don't exist in the database");
        }

        long requesterId = requester.get().getId();
        long receiverId = receiver.get().getId();

        if (requesterId == receiverId) {
            throw new MentorshipRequestException("Requester and receiver the same user");
        }

        Optional<MentorshipRequest> latestRequest = mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId);
        if (latestRequest.isPresent()) {
            if (DAYS.between(latestRequest.get().getCreatedAt(), LocalDateTime.now()) < 90) {
                throw new MentorshipRequestException("You can't apply for a mentorship more than once every 90 days.");
            }
        }
    }
}
