package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataGettingException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.amazonS3.S3Service;
import school.faang.user_service.service.user.image.AvatarGeneratorService;
import school.faang.user_service.service.user.image.BufferedImagesHolder;
import school.faang.user_service.service.user.image.ImageProcessor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static school.faang.user_service.exception.message.ExceptionMessage.*;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    public static final String BIG_AVATAR_PICTURE_NAME = "bigPicture";

    public static final String SMALL_AVATAR_PICTURE_NAME = "smallPicture";

    public static final String FOLDER_PREFIX = "user";

    private S3Service s3Service;

    private AvatarGeneratorService avatarGeneratorService;

    private ImageProcessor imageProcessor;

    private UserRepository userRepository;

    private UserMapper userMapper;

    private CountryRepository countryRepository;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        Long userId = userDto.getId();
        if (userId != null && userRepository.existsById(userId)) {
            log.warn("Registered attempt to create user by id of existing user.");
            throw new DataValidationException(REPEATED_USER_CREATION_EXCEPTION.getMessage());
        }

        User userToBeCreated = userMapper.toEntity(userDto);
        setCountryByCountryId(userDto.getCountryId(), userToBeCreated);
        User createdUser = userRepository.save(userToBeCreated);

        log.info("User created successfully.");

        BufferedImage avatar = avatarGeneratorService.getRandomAvatar();
        return uploadUserAvatar(createdUser.getId(), avatar);
    }

    public UserDto getUser(long userId) {
        return userMapper.toDto(getUserEntity(userId));
    }

    public User getUserEntity(long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new DataValidationException(NO_SUCH_USER_EXCEPTION.getMessage()));
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserDto uploadUserAvatar(Long userId, BufferedImage uploadedImage) {
        User user = getUserEntity(userId);

        BufferedImagesHolder scaledImages = imageProcessor.scaleImage(uploadedImage);
        log.info("Received avatar image was successfully scaled.");

        String fileId = uploadFile(userId, imageProcessor.getImageOS(scaledImages.getBigPicture()), BIG_AVATAR_PICTURE_NAME);
        String smallFileId = uploadFile(userId, imageProcessor.getImageOS(scaledImages.getSmallPicture()), SMALL_AVATAR_PICTURE_NAME);

        log.info("Scaled images of user avatar were uploaded on cloud.");

        if (user.getUserProfilePic() != null) {
            deleteUserAvatar(userId);
        }

        user.setUserProfilePic(UserProfilePic.builder()
                .fileId(fileId)
                .smallFileId(smallFileId)
                .build());

        User updatedUser = userRepository.save(user);

        log.info("Keys of uploaded images were saved in database.");
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public byte[] downloadUserAvatar(Long userId) {
        User user = getUserEntity(userId);
        UserProfilePic userProfilePictures = user.getUserProfilePic();

        if (userProfilePictures == null) {
            log.warn("Can't download user's avatar, cause user doesn't have it.");
            throw new DataGettingException(USER_AVATAR_ABSENCE_EXCEPTION.getMessage());
        }

        try (InputStream userAvatarIS = s3Service.downloadFile(userProfilePictures.getSmallFileId())) {
            byte[] imageInBytesArray = userAvatarIS.readAllBytes();

            log.info("User's avatar image was downloaded successfully.");
            return imageInBytesArray;

        } catch (IOException e) {
            log.error(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
            throw new RuntimeException(FILE_PROCESSING_EXCEPTION.getMessage() + e.getMessage());
        }
    }

    @Transactional
    public void deleteUserAvatar(Long userId) {
        User user = getUserEntity(userId);
        if (user.getUserProfilePic() == null) {
            log.warn("Can't delete user's avatar, cause user doesn't have it.");

            throw new DataGettingException(USER_AVATAR_ABSENCE_EXCEPTION.getMessage());
        }

        s3Service.deleteFile(user.getUserProfilePic().getFileId());
        s3Service.deleteFile(user.getUserProfilePic().getSmallFileId());

        user.setUserProfilePic(null);

        log.info("User's avatar images were deleted successfully.");
    }

    @Transactional
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    private User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(NO_SUCH_USER_EXCEPTION.getMessage() + "UserId = " + userId);
                    return new DataGettingException(NO_SUCH_USER_EXCEPTION.getMessage());
                });
    }

    private String uploadFile(Long userId, ByteArrayOutputStream outputStream, String fileName) {
        String key = String.format("%s/%d%s", FOLDER_PREFIX + userId, System.currentTimeMillis(), fileName);
        s3Service.uploadFile(outputStream, key);
        return key;
    }

    private void setCountryByCountryId(Long countryId, User userToBeCreated) {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new DataGettingException(NO_SUCH_COUNTRY_EXCEPTION.getMessage()));
        userToBeCreated.setCountry(country);
    }
}
