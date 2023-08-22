package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserDto getUserById(long id) {
        return userMapper.toDto(getUser(id));
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->  new EntityNotFoundException("User with id " + id + " not found"));
    }

    public void validateUsers(Long... userIds) {
        for (Long userId : userIds) {
            if (!userRepository.existsById(userId)) {
                throw new EntityNotFoundException("User with id " + userId + " not found.");
            }
        }
    }
}
