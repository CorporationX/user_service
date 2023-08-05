package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long id) {
        User user = findUserByIdMentorshipRep(id);
        List<User> mentees = user.getMentees();
        return isNullListUsers(mentees);
    }

    public List<UserDto> getMentors(long id) {
        User user = findUserByIdMentorshipRep(id);
        List<User> mentors = user.getMentors();
        return isNullListUsers(mentors);
    }

    public void deleteMentee(long menteeId, long mentorId) {
        User mentee = findUserById(menteeId);
        User mentor = findUserById(mentorId);

        mentor.getMentees().remove(mentee);
        mentorshipRepository.save(mentor);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = findUserById(menteeId);
        User mentor = findUserById(mentorId);

        mentee.getMentors().remove(mentor);
        mentorshipRepository.save(mentee);
    }

    private User findUserById(long id) {
        return mentorshipRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User was not found"));
    }

    private User findUserByIdMentorshipRep(long id) {
        return mentorshipRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User was not found"));
    }

    private List<UserDto> isNullListUsers(List<User> users) {
        return users == null ?
                new ArrayList<>() : userMapper.toDto(users);
    }
}
