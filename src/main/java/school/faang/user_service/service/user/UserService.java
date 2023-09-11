package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataValidationException("User was not found"));
    }

    public boolean areOwnedSkills(long userId, List<Long> skillIds) {
        if (skillIds.isEmpty()) {
            return true;
        }
        return userRepository.countOwnedSkills(userId, skillIds) == skillIds.size();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
  
    public UserDto getUser(long id) {
        return userMapper.toDto(findUserById(id));
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = StreamSupport.stream(userRepository.findAllById(ids).spliterator(), false).toList();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public void banAuthors(List<Long> bannerId) {
        userRepository.findAllById(bannerId)
                .forEach(user -> user.setBanned(true));
    }
}
