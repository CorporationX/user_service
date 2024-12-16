package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.event.SearchAppearanceEvent;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SearchAppearanceEventPublisher searchAppearanceEventPublisher;

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

    public List<Long> searchUsers(Long searchingUserId) {
        log.info("Received a request to get the users from the following user id: {}", searchingUserId);
        List<Long> userIds = List.of(1L, 2L, 3L);

        userIds.forEach(userId -> {
            SearchAppearanceEvent event = new SearchAppearanceEvent(userId, searchingUserId, LocalDateTime.now());
            searchAppearanceEventPublisher.publishSearchAppearanceEvent(event);
        });

        return userIds;
    }
}
