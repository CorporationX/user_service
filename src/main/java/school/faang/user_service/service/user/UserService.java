package school.faang.user_service.service.user;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filters.UserFilter;
import school.faang.user_service.service.s3.S3CompatibleService;
import school.faang.user_service.service.avatar_api.AvatarApiService;
import school.faang.user_service.service.s3.S3ServiceImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CountryRepository countryRepository;
    private final List<UserFilter> userFilters;
    private final AvatarApiService avatarApiService;
    private final S3CompatibleService s3CompatibleService;
    private final S3ServiceImpl s3Service;

    private final String DEFAULT_AVATAR_FILENAME = "default_profile";

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${image-limitations.max-pixel-size.large-image}")
    private int largeImage;

    @Value("${image-limitations.max-pixel-size.small-image}")
    private int smallImage;

    @Transactional(readOnly = true)
    public List<User> findPremiumUser(UserFilterDto filterDto) {
        Stream<User> premiumUsers = userRepository.findPremiumUsers();
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .reduce(premiumUsers, (stream, filter) -> filter.apply(stream, filterDto), (stream, filter) -> stream)
                .toList();
    }

    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User does not exist"));
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toDto(users);
    }

    public User registerNewUser(User newUser) {
        validateUsername(newUser.getUsername());
        validateEmail(newUser.getEmail());
        validatePhone(newUser.getPhone());
        validateCountry(newUser.getCountry().getId());

        generateAndSaveDefaultAvatar(newUser);
        User created = userRepository.save(newUser);
        log.info("Successfully registered a new user (ID={})", created.getId());
        return created;
    }

    @Transactional
    public void addProfileImage(Long userId, MultipartFile file) throws IOException {
        validateSizeImage(file);
        User user = userRepository.findById(userId).orElseThrow();

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        String filePath = String.format("user_%d/profile", userId);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());

        String largeImagePath = processAndUploadImage(originalImage, metadata, filePath, largeImage, userId);
        String smallImagePath = processAndUploadImage(originalImage, metadata, filePath + "_small", smallImage, userId);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(largeImagePath);
        userProfilePic.setSmallFileId(smallImagePath);

        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
    }

    public Resource getBigImageFromProfile(Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow();
        byte[] bigImageBytes = s3Service.downloadFile(user.getUserProfilePic().getFileId());

        return new ByteArrayResource(bigImageBytes);
    }

    public Resource getSmallImageFromProfile(Long userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow();
        byte[] smallImageBytes = s3Service.downloadFile(user.getUserProfilePic().getSmallFileId());

        return new ByteArrayResource(smallImageBytes);
    }

    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getUserProfilePic().getFileId().contains(DEFAULT_AVATAR_FILENAME)) {
            throw new RuntimeException("Can't delete default avatar!");
        }
        String large = user.getUserProfilePic().getFileId();
        String compressed = user.getUserProfilePic().getSmallFileId();


        s3Service.deleteFile(large);
        s3Service.deleteFile(compressed);

        user.setUserProfilePic(null);
        userRepository.save(user);
    }

    private void generateAndSaveDefaultAvatar(User toCreate) {
        byte[] defaultAvatarData = avatarApiService.generateDefaultAvatar(toCreate.getUsername());
        String fileKey = String.format("%s/%s", UUID.randomUUID(), DEFAULT_AVATAR_FILENAME);
        s3CompatibleService.uploadFile(defaultAvatarData, fileKey, "image/svg+xml");
        setAvatarKeyForUser(toCreate, fileKey);
    }

    private void setAvatarKeyForUser(User toUpdate, String fileKey) {
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(fileKey);
        profilePic.setSmallFileId(fileKey);
        toUpdate.setUserProfilePic(profilePic);
    }

    private void validateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalStateException(String.format("Username %s is already in use", username));
        }
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException(String.format("Email %s is already in use", email));
        }
    }

    private void validatePhone(String phone) {
        if (userRepository.existsByPhone(phone)) {
            throw new IllegalStateException(String.format("Phone %s is already in use", phone));
        }
    }

    private void validateCountry(Long countryId) {
        if (!countryRepository.existsById(countryId)) {
            throw new IllegalStateException(String.format("Country with ID %d does not exist", countryId));
        }
    }

    public void banUser(Long userId) {
        User user = getUserById(userId);
        user.setBanned(true);
        log.info("User with ID: " + userId + " banned.");
        userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new DataValidationException("User with ID: " + userId + " not found.")
        );
    }

    private String processAndUploadImage(BufferedImage originalImage,
                                         ObjectMetadata metadata,
                                         String filePath,
                                         int maxSize,
                                         Long userId) throws IOException {
        BufferedImage finalImage = originalImage;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        if (width > maxSize || height > maxSize) {
            double scaleFactor = Math.min((double) maxSize / width, (double) maxSize / height);
            int targetWidth = (int) (width * scaleFactor);
            int targetHeight = (int) (height * scaleFactor);
            finalImage = resizeImage(originalImage, targetWidth, targetHeight);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(finalImage, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();

        s3Service.uploadFile(imageBytes, filePath, userId, metadata.getContentType());

        return filePath;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage,
                Scalr.Method.AUTOMATIC,
                Scalr.Mode.AUTOMATIC,
                targetWidth,
                targetHeight,
                Scalr.OP_ANTIALIAS);
    }

    private void validateSizeImage(MultipartFile file) {
        int maxSizeFile = 5 * 1024 * 1024;
        if (file.getSize() > maxSizeFile) {
            throw new IllegalArgumentException("File exceeds the maximum size of 5 MB");
        }
    }
}

