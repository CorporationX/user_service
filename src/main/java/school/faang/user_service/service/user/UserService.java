package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("There is no user with that id"));
    }

    public UserDto getUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no user with this ID"));
        log.info("Received a request to get the user with ID: {}", id);
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsers(List<Long> ids) {
        log.info("Received a request to get the users with the following ids: {}", ids);
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
