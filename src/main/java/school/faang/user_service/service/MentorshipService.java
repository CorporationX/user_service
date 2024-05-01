package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentees(long userId) {
        User user = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("This mentor with id: " + userId + " is not in the database"));
        return user.getMentees().stream().map((mente)->userMapper.toDto(mente)).toList();
    }
}
