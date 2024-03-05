package school.faang.user_service.controller.mentorship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MentorshipService {
    @Autowired
    MentorshipRepository mentorshipRepository;
    @Autowired
    UserRepository userRepository;
    public List<User> getMentees(long userId){
        Optional<User> mentorOptional = userRepository.findById(userId);
        return mentorOptional.isEmpty() ? Collections.emptyList() : mentorOptional.get().getMentees();
    }
}
