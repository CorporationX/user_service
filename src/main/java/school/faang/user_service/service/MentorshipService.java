package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final MentorshipMapper mentorshipMapper;
    private final UserRepository userRepository;
    private final GoalService goalService;

    @Transactional(readOnly = true)
    public List<MentorshipUserDto> getMentees(long userId) {
        return getUser(userId).getMentees().stream()
                .map(mentorshipMapper::toMentorshipUserDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MentorshipUserDto> getMentors(long userId) {
        return getUser(userId).getMentors().stream()
                .map(mentorshipMapper::toMentorshipUserDto)
                .toList();
    }

    @Transactional
    public void deleteMentorshipRelations(long mentorId, long menteeId) {
        User mentor = getUser(mentorId);
        User mentee = getUser(menteeId);
        if (!mentor.getMentees().contains(mentee)) {
            throw new EntityNotFoundException(
                    String.format("Mentor with id: %s haven't mentee with id: %s", mentorId, menteeId));
        }
        mentor.getMentees().remove(mentee);
        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentor);
        mentorshipRepository.save(mentee);
    }

    private User getUser(long userId) {
        return mentorshipRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found user with id: " + userId));
    }

    @Transactional
    public void deactivateMentorship(long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<User> mentees = user.getMentees();

//        Seems like clean code, same abstraction level operaions
        mentees.forEach(mentee -> {
            goalService.setUserGoalsToSelf(mentee);
            removeMentorFromMentee(mentee, userId);
        });

    }

    private void removeMentorFromMentee(User mentee, long mentorId) {
        List<User> userMentors = mentee.getMentors();
        userMentors.removeIf(mentor -> mentor.getId() == mentorId);
        mentee.setMentors(userMentors);
    }
}
