package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long id) {
        User user = mentorshipRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User was not found"));

        List<User> mentees = user.getMentees();
        return mentees == null ?
                new ArrayList<>() : userMapper.toDto(mentees);
    }

    public List<UserDto> getMentors(long id) {
        User user = mentorshipRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User was not found"));

        List<User> mentors = user.getMentors();
        return mentors == null ?
                new ArrayList<>() : userMapper.toDto(mentors);
    }
}
