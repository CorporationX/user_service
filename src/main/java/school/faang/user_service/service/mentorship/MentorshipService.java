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
        List<User> mentees = mentorshipRepository.findMenteesByMentorId(mentorId);
        return userMapper.toDtos(mentees);
    }

    public List<UserDto> getMentors(long menteeId) {
        List<User> mentors = mentorshipRepository.findMentorsByMenteeId(menteeId);
        return userMapper.toDtos(mentors);
    }

    @Transactional
    public void deleteMenteeOfMentor(long mentorId, long menteeId) {
        mentorshipRepository.deleteMenteeOfMentor(mentorId, menteeId);
    }

    @Transactional
    public void deleteMentorOfMentee(long mentorId, long menteeId) {
        mentorshipRepository.deleteMentorOfMentee(mentorId, menteeId);
    }
}
