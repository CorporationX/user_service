package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.mentorship.MentorshipRepository;


@Service
@RequiredArgsConstructor
public class MentorshipService {
    private MentorshipRepository mentorshipRepository;


}
