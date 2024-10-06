package school.faang.user_service.service.user;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filters.UserFilter;
import school.faang.user_service.service.s3.S3ServiceImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    @Value("${services.s3.bucketName}")
    private String bucketName;
    private final S3ServiceImpl s3Service;
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

    @Transactional(readOnly = true)
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User does not exist"));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return userMapper.toDto(users);
    }

    @Transactional
    public void addProfileImage(Long userId, MultipartFile file) throws IOException {
        validateSizeImage(file);
        User user = userRepository.findById(userId).orElseThrow();

        String filePath = String.format("user_%d/profile", userId);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        UserProfilePic userProfilePic = new UserProfilePic();

        if (width <= largeImage && height <= largeImage) {
            InputStream ImageStream = bufferedImageToInputStream(originalImage, "jpg");
            s3Service.uploadFile(ImageStream, metadata, filePath, userId);
            userProfilePic.setFileId(filePath);
        } else {
            double scaleFactor = width > height ?
                    (double) largeImage / width :
                    (double) largeImage / height;

            int targetWidth = (int) (width * scaleFactor);
            int targetHeight = (int) (height * scaleFactor);

            BufferedImage maxLargeImage = resizeImage(originalImage, targetWidth, targetHeight);
            InputStream largeImageStream = bufferedImageToInputStream(maxLargeImage, "jpg");

            s3Service.uploadFile(largeImageStream, metadata, filePath, userId);
            userProfilePic.setFileId(filePath);
        }

        if (width > smallImage || height > smallImage) {

            double scaleFactor = width > height ?
                    (double) smallImage / width :
                    (double) smallImage / height;

            int targetWidth = (int) (width * scaleFactor);
            int targetHeight = (int) (height * scaleFactor);

            BufferedImage maxSmallImage = resizeImage(originalImage, targetWidth, targetHeight);
            InputStream smallImageStream = bufferedImageToInputStream(maxSmallImage, "jpg");
            String filePathSmallImage = filePath + "_small";

            s3Service.uploadFile(smallImageStream, metadata, filePathSmallImage, userId);
            userProfilePic.setSmallFileId(filePathSmallImage);
        }
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public InputStream getBigImageFromProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return s3Service.downloadFile(user.getUserProfilePic().getFileId());
    }

    @Transactional(readOnly = true)
    public InputStream getSmallImageFromProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return s3Service.downloadFile(user.getUserProfilePic().getSmallFileId());
    }

    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        String large = user.getUserProfilePic().getFileId();
        String compressed = user.getUserProfilePic().getSmallFileId();

        s3Service.deleteFile(large);
        s3Service.deleteFile(compressed);

        user.setUserProfilePic(null);
        userRepository.save(user);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(originalImage,
                Scalr.Method.AUTOMATIC,
                Scalr.Mode.AUTOMATIC,
                targetWidth,
                targetHeight,
                Scalr.OP_ANTIALIAS);
    }

    private InputStream bufferedImageToInputStream(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, format, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    private void validateSizeImage(MultipartFile file) {
        int maxSizeFile = 5 * 1024 * 1024;
        if (file.getSize() > maxSizeFile) {
            throw new IllegalArgumentException("File exceeds the maximum size of 5 MB");
        }
    }
}