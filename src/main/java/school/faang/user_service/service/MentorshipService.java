package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("This mentor with id: " + userId + " is not in the database"));
        return user.getMentees().stream().map((mentee) -> userMapper.toDto(mentee)).toList();
    }

    public List<UserDto> getMentors(long userId) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("This mentee with id: " + userId + " is not in the database"));
        return user.getMentors().stream().map((mentor) -> userMapper.toDto(mentor)).toList();
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor with id: " + mentorId + " is not in the database"));

        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new IllegalArgumentException("Mentee with id: " + menteeId + " is not in the database"));
        mentor.getMentees().remove(mentee);
        mentorshipRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentor = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentor with id: " + mentorId + " is not in the database"));

        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new IllegalArgumentException("Mentee with id: " + menteeId + " is not in the database"));
        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentee);
    }

}
