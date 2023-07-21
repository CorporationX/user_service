package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final UserRepository userRepository;

    public void validate(MentorshipRequest mentorshipRequest) {
        Long requesterId = mentorshipRequest.getRequester().getId();
        Long receiverId = mentorshipRequest.getReceiver().getId();
        LocalDateTime threeMonthBeforeNow = LocalDateTime.now().minusMonths(3);

        if(mentorshipRequest.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description field must be filled");
        }
        if(!userRepository.existsById(requesterId)) {
            throw new IndexOutOfBoundsException("Requester must be registered");
        }
        if(!userRepository.existsById(receiverId)) {
            throw new IndexOutOfBoundsException("Receiver must be registered");
        }
        if(requesterId == receiverId) {
            throw new IllegalArgumentException("A requester cannot be a receiver fo itself");
        }
        if(mentorshipRequest.getUpdatedAt() == mentorshipRequest.getCreatedAt()) {
            if(mentorshipRequest.getCreatedAt().isAfter(threeMonthBeforeNow)) {
                throw new RuntimeException("The user can only make a request once every three months");
            }
        } else {
            if(mentorshipRequest.getUpdatedAt().isAfter(threeMonthBeforeNow)) {
                throw new RuntimeException("The user can only make a request once every three months");
            }
        }
    }
}
