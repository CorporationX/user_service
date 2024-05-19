package school.faang.user_service.service.mentorship;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@AllArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    @Override
    public Boolean requestMentorship(Long userId, Long mentorId, String description) {
        if (!userRepository.existsById(userId) || !userRepository.existsById(mentorId))
            return false;
        Optional<MentorshipRequest> optional = mentorshipRequestRepository.findLatestRequest(userId, mentorId);
        boolean is1 = optional.isEmpty() && !userId.equals(mentorId);
        boolean is2 = optional.isPresent() && moreThanThreeMonths(optional.get());
        if (is1 || is2) {
            mentorshipRequestRepository.create(userId, mentorId, description);
            return true;
        }
        return false;
    }

    private boolean moreThanThreeMonths(MentorshipRequest mentorshipRequest){
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeRequest = mentorshipRequest.getCreatedAt();
        int compareResult = timeRequest.plusMonths(3).compareTo(timeNow);
        return compareResult <= 0;
    }
}