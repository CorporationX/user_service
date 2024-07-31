package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserServiceMapper;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserServiceMapper userServiceMapper;

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()->new EntityNotFoundException("This user is not found"));
        return userServiceMapper.toDto(user);
    }
}
