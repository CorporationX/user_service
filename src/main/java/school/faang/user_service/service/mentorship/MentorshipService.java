package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.UserNotFound;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getMentees(long mentorId) {
        User user = mentorshipRepository.findById(mentorId)
                .orElseThrow(() -> new UserNotFound("user not found in database"));

        return user.getMentees().stream().map(userMapper::toDto).toList();
    }
}
