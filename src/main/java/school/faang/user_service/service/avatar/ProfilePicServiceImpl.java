package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.UserProfilePicDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.avatar.PictureMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.cloud.S3Service;
import school.faang.user_service.service.user.UserService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@PropertySource(value = "classpath:s3.properties")
public class ProfilePicServiceImpl implements ProfilePicService {
    @Value("${randomAvatar.url}")
    private String url;
    @Value("${smallSize}")
    private int smallSize;
    @Value("${largeSize}")
    private int largeSize;
    @Value("${bucketName}")
    private String bucketName;
    private final S3Service s3Service;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PictureMapper pictureMapper;

    private InputStream compressPic(InputStream inputStream, int size) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BufferedImage scaledImage = Thumbnails.of(inputStream).size(size, size).asBufferedImage();
            ImageIO.write(scaledImage, "jpg", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = outputStream.toByteArray();

        return new ByteArrayInputStream(bytes);
    }

    @Override
    @Transactional
    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 3))
    public void generateAndSetPic(UserDto user) {
        byte[] image = restTemplate.getForObject(url + user.getUsername(), byte[].class);
        if (image == null || image.length == 0) {
            throw new DataValidationException("Failed to get the generated image");
        }
        String nameForSmallPic = "small" + user.getUsername() + LocalDateTime.now() + ".jpg";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");

        InputStream inputStream = new ByteArrayInputStream(image);
        s3Service.uploadFile(bucketName, nameForSmallPic, compressPic(inputStream, smallSize));
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setSmallFileId(nameForSmallPic);
        user.setUserProfilePic(userProfilePic);
    }

    @Override
    @Transactional
    public UserProfilePicDto saveProfilePic(long userId, MultipartFile file){
        User user = userService.findUserById(userId);
        String nameForSmallPic = "small" + file.getName() + LocalDateTime.now();
        String nameForLargePic = "large" + file.getName() + LocalDateTime.now();
        try {
            s3Service.uploadFile(bucketName, nameForSmallPic, compressPic(file.getInputStream(), smallSize));
            s3Service.uploadFile(bucketName, nameForLargePic, compressPic(file.getInputStream(), largeSize));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UserProfilePic userProfilePic = new UserProfilePic(nameForLargePic, nameForSmallPic);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return pictureMapper.toDto(userProfilePic);
    }

    @Override
    @Transactional
    public InputStreamResource getProfilePic(long userId) {
        User user = userService.findUserById(userId);
        S3Object s3Object = s3Service.getFile(bucketName, user.getUserProfilePic().getFileId());

        return new InputStreamResource(s3Object.getObjectContent());
    }

    @Override
    @Transactional
    public UserProfilePicDto deleteProfilePic(long userId) {
        User user = userService.findUserById(userId);
        s3Service.deleteFile(bucketName, user.getUserProfilePic().getFileId());
        s3Service.deleteFile(bucketName, user.getUserProfilePic().getSmallFileId());
        UserProfilePicDto deletedProfilePicDto = pictureMapper.toDto(user.getUserProfilePic());
        user.getUserProfilePic().setSmallFileId(null);
        user.getUserProfilePic().setFileId(null);
        userRepository.save(user);

        return deletedProfilePicDto;
    }
}
