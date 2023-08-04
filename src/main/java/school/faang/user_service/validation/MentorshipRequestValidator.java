package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void validateRequest(MentorshipRequest mentorshipRequest) {
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

        Optional<MentorshipRequest> optionalLatestRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);

        if(optionalLatestRequest.isPresent()) {
            MentorshipRequest latestRequest = optionalLatestRequest.get();
            if(latestRequest.getUpdatedAt().isAfter(threeMonthBeforeNow)) {
                throw new RuntimeException("Request can only be made once every 3 months");
            }
        }
    }

    public void validateAcceptRequest(Long requestId) {
        Optional<MentorshipRequest> requestOpt = mentorshipRequestRepository.findById(requestId);
        if(!requestOpt.isPresent()) {
            throw new NullPointerException("Request must exist");
        } else {
            MentorshipRequest request = requestOpt.get();
            User requester = request.getRequester();
            User receiver = request.getReceiver();

            if(requester.getMentors().contains(receiver)) {
                throw new IllegalArgumentException(receiver.getUsername() + "is already the requester mentor");
            }
        }


    }
}
