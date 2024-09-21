package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.httpResponse.HttpResponseData;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.image.RemoteImageService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.user.UserValidator;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RemoteImageService remoteImageService;
    private final S3Service s3Service;
    private final UserValidator userValidator;

    @Transactional
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with this id does not exist in the database"));
    }

    @Transactional
    public UserDto registerUser(UserRegistrationDto userRegistrationDto) {
        User user = userMapper.toEntity(userRegistrationDto);
        userValidator.validateUserConstrains(user);

        log.info("Trying save new user {}", user);
        userRepository.save(user);
        log.info("User {} saved", user);

        setUserDefaultAvatar(user);
        return userMapper.toDto(user);
    }

    private void setUserDefaultAvatar(User user) {
        String key;

        try {
            key = assignUserRemoteRandomPicture(user);
        } catch (Exception e) {
            log.info("Couldn't apply remote picture to user profile. Trying to apply default picture from cloud");

            if (s3Service.isDefaultPictureExistsOnCloud()) {
                log.info("Found default picture on cloud");

                key = s3Service.getDefaultPictureName();
            } else {
                log.info("Couldn't found default picture on cloud");
                return;
            }
        }

        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(key)
                .build());

        log.info("Finish assignment random picture for user {}", user);
    }

    private String assignUserRemoteRandomPicture(User user) {
        log.info("Trying to assign random picture for user {}", user);

        String pictureKey;

        try {
            HttpResponseData pictureHttpContent = remoteImageService.getUserProfileImageFromRemoteService();

            String s3Folder = user.getUsername() + user.getId() + "profilePic";
            pictureKey = s3Service.uploadHttpData(pictureHttpContent, s3Folder);

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return pictureKey;
    }

}
