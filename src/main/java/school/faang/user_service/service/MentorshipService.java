package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void cancelMentoring(User user, List<Goal> goals) {
        user.getMentees().stream().map(User::getMentors).forEach(list -> list.remove(user));
        goals.stream().filter(goal -> goal.getMentor().getId() == user.getId()).forEach(goal -> goal.setMentor(null));
    }

    public List<UserDto> getMentees(Long userId) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        return userMapper.toUserListDto(user.getMentees());
    }

    public List<UserDto> getMentors(Long userId) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

        return userMapper.toUserListDto(user.getMentors());
    }

    public void deleteMentee(Long menteeId, Long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor with id " + mentorId + " not found"));
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new IllegalArgumentException("Mentee with id " + menteeId + " not found"));
        mentor.getMentees().remove(mentee);
        userRepository.save(mentor);
    }

    public void deleteMentor(Long menteeId, Long mentorId) {
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor with id " + mentorId + " not found"));
        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new IllegalArgumentException("Mentee with id " + menteeId + " not found"));
        mentee.getMentors().remove(mentor);
        userRepository.save(mentee);
    }
}
