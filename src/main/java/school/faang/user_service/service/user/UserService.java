package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.S3Config;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MessageError;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.S3Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Value("${dicebear.pic-base-url}")
    private String large_avatar;

    @Value("${dicebear.pic-base-url-small}")
    private String small_avatar;

    private final S3Service s3Service;
    private final S3Config s3Config;
    private final String bucketName;

    public UserDto getUser(long userId) {
        return userMapper.toDto(getUserEntityById(userId));
    }

    public List<UserDto> getUsersByIds(List<Long> userIds) {
        return userMapper.toDto(getUsersEntityByIds(userIds));
    }

    public User getUserEntityById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(MessageError.USER_NOT_FOUND_EXCEPTION));
    }

    public List<User> getUsersEntityByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    public UserDto create(UserDto userDto) {

        checkUserAlreadyExists(userDto);

        User user = userMapper.toEntity(userDto);
        user.setUserProfilePic(getRandomAvatar());
        user.setActive(true);

        User createdUser = userRepository.save(user);
        String fileNameSmallAva = "small_" + user.getId() + ".svg";
        String fileNameLargeAva = "large_" + user.getId() + ".svg";

        s3Service.
                saveSvgToS3(user.getUserProfilePic().getSmallFileId(),
                        bucketName,
                        fileNameSmallAva);
        s3Service.
                saveSvgToS3(user.getUserProfilePic().getFileId(),
                        bucketName,
                        fileNameLargeAva);
        return userMapper.toDto(createdUser);

    }


    private UserProfilePic getRandomAvatar() {

        UUID seed = UUID.randomUUID();
        return UserProfilePic.builder().
                smallFileId(small_avatar + seed).
                fileId(large_avatar + seed).build();

    }

    private void checkUserAlreadyExists(UserDto userDto) {

        boolean exists = userRepository.findById(userDto.getId()).isPresent();

        if (exists) {
            log.debug("User with id " + userDto.getId() + " exists");
            throw new DataValidationException("User with id " + userDto.getId() + " exists");
        }
    }
}
