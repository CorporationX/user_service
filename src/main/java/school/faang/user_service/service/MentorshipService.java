package school.faang.user_service.service;

import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;

@Service
public class MentorshipService {
    public User stopMentorship(User user) {
        user.getMentees()
                .forEach(mentee -> mentee.setMentors(
                        mentee.getMentors()
                                .stream()
                                .peek(mentor -> mentorsFilterAndPeek(user, mentor))
                                .toList()
                ));
        return user;
    }

    private static void mentorsFilterAndPeek(User user, User mentor) {
        if (mentor.getId() == user.getId()) {
            mentor.setId(user.getId());
        }
    }
}
