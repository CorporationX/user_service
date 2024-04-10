package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Value("${dicebear.pic-base-url}")
    private String large_avatar;

    @Value("${dicebear.pic-base-url-small}")
    private String small_avatar;

    public UserDto getUser(long userId) {
        User user = userRepository.findById( userId ).orElseThrow( () -> new NoSuchElementException( "User not found!" ) );
        return userMapper.toDto( user );
    }

    public UserDto create(UserDto userDto) {
        User user = userMapper.toEntity( userDto );
        user.setUserProfilePic( getRandomAvatar() );
        user.setActive( true );
        User createdUser = userRepository.save( user );
        return userMapper.toDto( createdUser );

    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userMapper.toDto( userRepository.findAllById( ids ) );
    }

    private UserProfilePic getRandomAvatar() {
        UUID seed = UUID.randomUUID();
        return UserProfilePic.builder().
                smallFileId( small_avatar ).
                fileId( large_avatar ).build();
    }
}
