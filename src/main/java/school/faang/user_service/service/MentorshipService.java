package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;

    public void stopUserMentorship(Long userId){
        var user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));


    }
}
