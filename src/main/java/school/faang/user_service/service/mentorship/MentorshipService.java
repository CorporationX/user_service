package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Autowired
    public MentorshipService(MentorshipRepository mentorshipRepository, UserMapper userMapper) {
        this.mentorshipRepository = mentorshipRepository;
        this.userMapper = userMapper;
    }

    public List<UserDTO> getMentees(long mentorId) {
        List<User> mentees = mentorshipRepository.findMenteesByMentorId(mentorId);
        return userMapper.toDTOs(mentees);
    }

    public List<UserDTO> getMentors(long menteeId) {
        List<User> mentees = mentorshipRepository.findMentorsByMenteeId(menteeId);
        return userMapper.toDTOs(mentees);
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
