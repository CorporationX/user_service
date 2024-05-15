package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import static school.faang.user_service.exception.MessageForGoalInvitationService.NO_USER_IN_DB;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    public UserDto getUser(long userId) {
        System.out.println(userRepository.findById(userId));
        return userMapper.toDto(userRepository.findById(userId).orElseThrow(() -> new DataValidationException(NO_USER_IN_DB.getMessage())));
    }
}
