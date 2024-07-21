package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${services.diceBear.avatar_url.full}")
    private String fullAvatarUrl;

    @Value("${services.diceBear.avatar_url.small}")
    private String smallAvatarUrl;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        setDefaultUserAvatar(user);
        return userMapper.toDto(userRepository.save(user));
    }

    private void setDefaultUserAvatar(User user) {
        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(fullAvatarUrl + user.hashCode())
                .smallFileId(smallAvatarUrl + user.hashCode())
                .build());
    }

}
