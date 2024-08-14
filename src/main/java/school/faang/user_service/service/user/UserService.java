package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<User> getAllUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds)
                .stream()
                .toList();
    }

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User with ID = " + id + " does not exist"));
        return userMapper.toDto(user);
    }

    public List<UserDto> saveUsers(List<User> users) {
        if(users == null){
            log.error("List<User> is empty. There aren't users for save");
            throw new DataValidationException("There aren't users for save");
        }
        List<User> savedUsers = userRepository.saveAll(users);
        log.info("{} users have been saved to DB", savedUsers.size());
        return userMapper.toDtoList(savedUsers);
    }
}
