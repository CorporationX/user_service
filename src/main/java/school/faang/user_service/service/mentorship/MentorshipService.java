package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    public void removeUserFromListHisMentees(User user) {
        List<User> mentees = user.getMentees();

        mentees.forEach(mentee ->
                mentee.getMentors().remove(user));
    }
}
