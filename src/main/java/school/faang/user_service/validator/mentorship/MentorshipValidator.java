package school.faang.user_service.validator.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipValidator{

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    public boolean isAllowedToMakeRequest(long menteeId, long mentorId){

        LocalDateTime threeMonthsAgo=LocalDateTime.now().minus(3, ChronoUnit.MONTHS);
        Optional<MentorshipRequest> latestRequest=mentorshipRequestRepository
                .findLatestRequest(menteeId, mentorId);

        if(latestRequest.isPresent()){
            LocalDateTime requestedAt=latestRequest.get().getCreatedAt();
            return requestedAt.isBefore(threeMonthsAgo);
        }else{
            log.warn("Allowed to make request to mentor" + mentorId );
            return true;
        }
    }

    public void checkIfUserExists(long userId){

        userRepository.findById(userId)
                .orElseThrow(()->{
                    log.warn("No user with such id "+userId);
                    return new EntityNotFoundException("No user with such id "+userId);

                });
    }
}
