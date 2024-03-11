package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<User> getMentees(Long idMentor) {
        User user = getUser(idMentor);
        if (user == null) {
            return Collections.emptyList();
        }
        return user.getMentees();
    }

    public List<User> getMentors(Long userId) {
        User user = getUser(userId);
        if (user == null) {
            return Collections.emptyList();
        }
        return user.getMentors();
    }

    private User getUser(Long userId) {
        return mentorshipRepository.findById(userId).orElse(null);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentee = getMentee(menteeId);
        User mentor = getMentor(mentorId);

        mentor.getMentees().remove(mentee);
        mentorshipRepository.save(mentor);

    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = getMentee(menteeId);
        User mentor = getMentor(mentorId);

        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentee);
    }

    private User getMentor(long mentorId) {
        return mentorshipRepository.findById(mentorId).orElseThrow(() -> new IllegalArgumentException("The sent Mentor_id is invalid"));
    }

    private User getMentee(long menteeId) {
        return mentorshipRepository.findById(menteeId).orElseThrow(() -> new IllegalArgumentException("The sent Mentee_id is invalid"));
    }
}
