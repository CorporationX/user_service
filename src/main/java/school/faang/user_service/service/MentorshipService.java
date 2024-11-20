package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final MentorshipRepository mentorshipRepository;
    private final GoalRepository goalRepository;

    public void deleteMentee(long menteeId, long mentorId) {
        List<User> mentees = userRepository.findById(mentorId)
                .map(User::getMentees)
                .orElseThrow(() -> new EntityNotFoundException("Mentor not found"));

        boolean isMenteeDeleted = mentees.removeIf(user -> user.getId() == menteeId);
        if (isMenteeDeleted) {
            deleteMentor(menteeId, mentorId);
        }
    }


    public void deleteMentor(long menteeId, long mentorId) {
        List<User> mentors = userRepository.findById(menteeId)
                .map(User::getMentors)
                .orElseThrow(() -> new EntityNotFoundException("Mentee not found"));

        boolean isMentorDeleted = mentors.removeIf(user -> user.getId() == mentorId);
        if (isMentorDeleted) {
            deleteMentee(menteeId, mentorId);
        }

        public List<UserDto> getMentees ( long userId){
            return userMapper.toDto(
                    userRepository.findById(userId)
                            .map(User::getMentees)
                            .orElseThrow(() -> new EntityNotFoundException("Mentor not found")));
        }
    }

    public List<UserDto> getMentors(long userId) {
        return userMapper.toDto(
                userRepository.findById(userId)
                        .map(User::getMentors)
                        .orElseThrow(() -> new EntityNotFoundException("Mentee not found")));
    }


    public void stopMentorship(User mentor) {
        if (mentor == null) {
            return;
        }
        mentor.getMentees().forEach(mentee -> {
            mentee.getSettingGoals().stream()
                    .filter(goal -> goal.getMentor().equals(mentor))
                    .forEach(goal -> {
                        goal.setMentor(mentee);
                        goalRepository.save(goal);
                    });

            mentee.removeMentor(mentor);
            mentorshipRepository.save(mentee);
        });
        log.info("Mentorship with {} is stopped", mentor.getId());
    }
}