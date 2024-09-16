package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long mentorId) {
        if (!mentorshipRepository.existsById(mentorId)) {
            throw new IllegalArgumentException("Mentor does not exist");
        }
        List<User> mentees = mentorshipRepository.findMenteesByMentorId(mentorId);
        return userMapper.toDtos(mentees);
    }

    public List<UserDto> getMentors(long menteeId) {
        if (!mentorshipRepository.existsById(menteeId)) {
            throw new IllegalArgumentException("Mentee does not exist");
        }
        List<User> mentors = mentorshipRepository.findMentorsByMenteeId(menteeId);
        return userMapper.toDtos(mentors);
    }

    @Transactional
    public void deleteMenteeOfMentor(long mentorId, long menteeId) {
        if (!mentorshipRepository.existsById(mentorId) || !mentorshipRepository.existsById(menteeId)) {
            throw new IllegalArgumentException("Mentor or mentee does not exist");
        }
        mentorshipRepository.deleteMenteeOfMentor(mentorId, menteeId);
    }

    @Transactional
    public void deleteMentorOfMentee(long mentorId, long menteeId) {
        if (!mentorshipRepository.existsById(mentorId) || !mentorshipRepository.existsById(menteeId)) {
            throw new IllegalArgumentException("Mentor or mentee does not exist");
        }
        mentorshipRepository.deleteMentorOfMentee(mentorId, menteeId);
    }


}
