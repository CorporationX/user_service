package school.faang.user_service.requestformentoring.helper.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.requestformentoring.helper.exeptions.MentorshipRequestNotFoundException;
import school.faang.user_service.requestformentoring.helper.exeptions.TemporaryDataIsIncorrect;
import school.faang.user_service.requestformentoring.helper.exeptions.UserNotFoundException;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class ValidationDb {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    public void checkingAvailabilityUsersDb(long requesterId) {
        userRepository.findUserById(requesterId).orElseThrow(UserNotFoundException::new);
    }

    public void checksLastRequestMentoring(Long requesterId, Long receiverId, int months) {
        LocalDateTime dateLastRequest =
                mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).get().getUpdatedAt();
        if (!LocalDateTime.now().isAfter(dateLastRequest.plusMonths(months))) {
            throw new TemporaryDataIsIncorrect();
        }
    }

    public void checkAvailabilityMentorshipRequestDb(Long id) {
        mentorshipRequestRepository.findMentorshipRequestById(id)
                .orElseThrow(MentorshipRequestNotFoundException::new);
    }

}
