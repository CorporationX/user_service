package school.faang.user_service.service.registration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.AvatarService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final AvatarService avatarService;

    public UserDto register(UserDto userDto) {
        User user = userRepository.save(mapper.dtoUserToUser(userDto));
        avatarService.createDefaultAvatarForUser(user.getId());
        log.info("User with id = %d registered".formatted(user.getId()));
        return mapper.userToUserDto(user);
    }
}
