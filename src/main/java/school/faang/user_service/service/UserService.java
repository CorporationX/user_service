package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    public UserDto getUserById(Long id){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new EntityNotFoundException("User not found");
        }
        return userMapper.toUserDto(user.get());
    }
}
