package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.ReadUserDto;
import school.faang.user_service.dto.user.WriteUserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.user.UserToReadUserDtoMapper;
import school.faang.user_service.mapper.user.WriteUserDtoToUserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WriteUserDtoToUserMapper writeUserDtoToUserMapper;
    private final UserToReadUserDtoMapper userToReadUserDtoMapper;
    private final AvatarService avatarService;

    public List<UserDto> getUsersDtoByIds(List<Long> ids) {
        return userMapper.usersToUserDTOs(userRepository.findAllById(ids));
    }

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
