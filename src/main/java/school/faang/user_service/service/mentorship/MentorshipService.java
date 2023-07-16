package school.faang.user_service.service.mentorship;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long mentorId) {
        User user = validateUserId(mentorId);
        return user.getMentees().stream()
                .map(userMapper::userToUserDto)
                .toList();
    }

    private User validateUserId(long mentorId) {
        return mentorshipRepository.findUserById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Invalid user id"));
    }
}

