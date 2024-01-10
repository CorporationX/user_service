package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(Long userId) {
        User user = getMentorById(userId);
        List<User> mentees = user.getMentees();
        if (mentees == null) {
            throw new IllegalArgumentException(MessageFormat.format("User with id {0} has not mentees ",userId));
        }
        return mentees.stream()
                .map(userMapper::toDto)
                .toList();
    }

    private User getMentorById(Long userId) {
        return mentorshipRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException(MessageFormat.format("User with id {0} has not found",userId)));
    }
}
