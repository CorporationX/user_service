package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final static int MONTHS_COOLDOWN = 3;

    public MentorshipRequest requestMentorship(MentorshipRequest mentorshipRequest) {
        long requesterId = mentorshipRequest.getRequester()
                .getId();
        long receiverId = mentorshipRequest.getReceiver()
                .getId();

//        Redundant tho, because the same check is done when checking if both users exists, but its more clear error
        if (requesterId == receiverId) {
            throw new IllegalArgumentException("Cannot request from yourself");
        }

        if(mentorshipRequest.getDescription().length() < 4) {
            throw new IllegalArgumentException("Mentorship description is too short, it should be at least 4 characters");
        }

//        Ensure both users exist
        Iterable<User> users = userRepository.findAllById(List.of(requesterId, receiverId));
        if (((Collection<User>) users).size() != 2) {
            throw new IllegalArgumentException("One or both users not found");
        }

//        Check if USER made request in last 3 months, correct me if it should check for requests of requester responder pair
        LocalDateTime cooldownThreshold = LocalDateTime.now().minusMonths(MONTHS_COOLDOWN);
        Optional<MentorshipRequest> latestRequest = mentorshipRequestRepository.findLatestRequestByRequester(requesterId);

        if (latestRequest.isPresent() && latestRequest.get().getCreatedAt().isAfter(cooldownThreshold)) {
            throw new IllegalArgumentException("A mentorship request has already been made within the last " + MONTHS_COOLDOWN + " months");
        }

        return mentorshipRequestRepository.save(mentorshipRequest);
    }

}
