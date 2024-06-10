package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {
    private final GoalService goalService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDTO> getMentees(long mentorId) {
        UserDTO mentor = getUserDTOById(mentorId);
        return mentor.getMenteeIds().stream()
                .map(this::getUserDTOById)
                .toList();
    }

    @Override
    public void stopMentorship(long mentorId) {
        List<UserDTO> mentees = getMentees(mentorId);
        mentees.forEach(mentee -> {
            reassignMentor(mentee, mentorId);
            reassignMentorsOfGoals(mentee, mentorId);
            updateMentee(mentee.getId());
        });
    }

    @Override
    public List<UserDTO> getMentors(long userId) {
        User user = getUserById(userId);
        return user.getMentors().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = getUserById(mentorId);
        User mentee = getUserById(menteeId);
        if (!mentor.getMentees().contains(mentee)) {
            throw new EntityNotFoundException("Данный пользователь не является менти у указанного ментора");
        }
        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    @Override
    public void deleteMentor(long menteeId, long mentorId) {
        User mentor = getUserById(mentorId);
        User mentee = getUserById(menteeId);
        if (!mentee.getMentors().contains(mentor)) {
            throw new EntityNotFoundException("Данный пользователь не является ментором для указанного менти");
        }
        mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }

    @Override
    public void assignMentor(long menteeId, long mentorId) {
        User mentee = getUserById(menteeId);
        User mentor = getUserById(mentorId);
        mentee.getMentors().add(mentor);
        mentor.getMentees().add(mentee);
        userRepository.save(mentee);
        userRepository.save(mentor);
    }

    private UserDTO getUserDTOById(long userId) {
        return userMapper.toDTO(getUserById(userId));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId + " не найден"));
    }

    private void reassignMentor(UserDTO mentee, long mentorId) {
        List<Long> mentorIds = mentee.getMentorIds().stream()
                .filter(idOfMentor -> idOfMentor != mentorId)
                .toList();
        mentee.setMentorIds(mentorIds);
    }

    private void reassignMentorsOfGoals(UserDTO mentee, long mentorId) {
        List<Long> goalIds = goalService.findGoalsByUserId(mentee.getId()).stream()
                .map(goalDto -> {
                    if (goalDto.getMentorId() == mentorId) {
                        goalDto.setMentorId(mentee.getId());
                    }
                    return goalDto.getId();
                })
                .toList();
        mentee.setGoalIds(goalIds);
    }

    private void updateMentee(long userId) {
        User updatedUser = getUserById(userId);
        updatedUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(updatedUser);
    }
}
