package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto getUser(long userId) {
        Optional<User> user = userRepository.findById(userId);

        return user.map(UserDto::toDto).orElse(null);

    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return StreamSupport.stream(userRepository.findAllById(ids).spliterator(), false)
                .map(UserDto::toDto)
                .toList();
    }
}
