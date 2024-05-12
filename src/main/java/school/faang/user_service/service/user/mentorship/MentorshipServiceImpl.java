package school.faang.user_service.service.user.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    @Override
    @Transactional
    public void deleteMentorFromMentee(User mentor) {
        List<User> mentees = mentor.getMentees();
        mentees.forEach(mentee -> mentee.getMentors().remove(mentor));
    }
}
