package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.MenteeResponseDto;
import school.faang.user_service.entity.user.User;
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

    @Transactional
    public void deleteMentee(long menteeId, long mentorId) {
        log.info("Trying to delete mentee {} for mentor {}", menteeId, mentorId);
        List<User> mentees = userRepository.findById(mentorId)
                .map(User::getMentees)
                .orElseThrow(() -> new EntityNotFoundException("Mentor not found"));

        boolean isMenteeDeleted = mentees.removeIf(user -> user.getId() == menteeId);
        if (isMenteeDeleted) {
            deleteMentor(menteeId, mentorId);
        }
    }

    @Transactional
    public void deleteMentor(long menteeId, long mentorId) {
        log.info("Trying to delete mentor {} for mentee {}", mentorId, menteeId);
        List<User> mentors = userRepository.findById(menteeId)
                .map(User::getMentors)
                .orElseThrow(() -> new EntityNotFoundException("Mentee not found"));

        boolean isMentorDeleted = mentors.removeIf(user -> user.getId() == mentorId);
        if (isMentorDeleted) {
            deleteMentee(menteeId, mentorId);
        }
    }

    @Transactional
    public List<MenteeResponseDto> getMentees (long mentorId){
        log.info("Trying to get mentees of user: {}", mentorId);
        return userMapper.toMenteeResponseList(
                userRepository.findById(mentorId)
                        .map(User::getMentees)
                        .orElseThrow(() -> new EntityNotFoundException("Mentor not found")));
    }

    @Transactional
    public List<MenteeResponseDto> getMentors(long menteeId) {
        log.info("Trying to get mentors of user: {}", menteeId);
        return userMapper.toMenteeResponseList(
                userRepository.findById(menteeId)
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