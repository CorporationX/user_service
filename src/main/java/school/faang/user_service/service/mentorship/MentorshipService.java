package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getMentees(long userId) {
        User user = getUserById(userId);
        return user == null ? Collections.emptyList() :
                user.getMentees()
                        .stream()
                        .map(userMapper::toDto)
                        .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMentors(long userId) {
        User user = getUserById(userId);
        return user == null ? Collections.emptyList() :
                user.getMentors()
                        .stream()
                        .map(userMapper::toDto)
                        .collect(Collectors.toList());
    }

    // Утилитный private метод
    private User getUserById(long id) {
        return mentorshipRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
