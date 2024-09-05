package school.faang.user_service.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.goal.mentorship.MentorshipRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final GoalRepository goalRepository;

    public void stopMentorship(@NonNull Long id) {
        log.info("Stop mentorship with id {}", id);
        mentorshipRepository.deleteByMentorId(id);
        goalRepository.updateMentorIdByMentorId(id, null);
        log.info("Mentorship stopped with id {}", id);
    }
}
