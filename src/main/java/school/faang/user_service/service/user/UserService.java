package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserValidator;

import java.util.UUID;
import school.faang.user_service.handler.exception.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    @Value("${dicebear.avatar}")
    private String avatarUrl;
    @Value("${dicebear.small_avatar}")
    private String smallAvatarUrl;

    public UserDto create(UserDto userDto) {
        userValidator.validatePassword(userDto);
        User user = userMapper.toEntity(userDto);
        user.setUserProfilePic(getRandomAvatar());
        user.setActive(true);
        User createdUser = userRepository.save(user);
        return userMapper.toDto(createdUser);
    }

    private UserProfilePic getRandomAvatar() {
        UUID uuid = UUID.randomUUID();
        return UserProfilePic.builder()
                .fileId(avatarUrl + uuid)
                .smallFileId(smallAvatarUrl + uuid)
                .build();
        }
        public UserDto getUserById (Long userId){
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("User with id: %s not found", userId)));
            return userMapper.toDtoUser(user);
        }
        public List<UserDto> getUsersByIds (List < Long > ids) {
        return userMapper.toDtoUser(userRepository.findAllById(ids));
    }
}
