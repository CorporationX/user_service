package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.ReadUserDto;
import school.faang.user_service.dto.user.WriteUserDto;
import school.faang.user_service.mapper.user.UserToReadUserDtoMapper;
import school.faang.user_service.mapper.user.WriteUserDtoToUserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final WriteUserDtoToUserMapper writeUserDtoToUserMapper;
    private final UserToReadUserDtoMapper userToReadUserDtoMapper;
    private final AvatarService avatarService;

    @Transactional
    public ReadUserDto createUser(WriteUserDto writeUserDto) {
        return Optional.of(writeUserDto)
                .map(writeUserDtoToUserMapper::map)
                .map(userRepository::save)
                .map(user -> {
                    avatarService.uploadAvatar(user);
                    return user;
                })
                .map(userToReadUserDtoMapper::map)
                .orElseThrow();
    }
}