package school.faang.user_service.service.avatar;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.avatar.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.avatar.PictureMapper;
import school.faang.user_service.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfilePicServiceImpl implements ProfilePicService {
    @Value("${randomAvatar.url}")
    private String url;
    private final UserRepository userRepository;
    private final AmazonS3 s3Client;
    private final PictureMapper pictureMapper;
    @Value("${services.s3.bucket-name}")
    private String bucketName;
    @Value("${services.s3.smallSize}")
    private int smallSize;
    @Value("${services.s3.largeSize}")
    private int largeSize;
    private final RestTemplate restTemplate;

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

    private User getUser(long userId) {
        return userRepository.findById(userId).
                orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " was not found"));
    }

    @Override
    @Transactional
    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 3))
    public void generateAndSetPic(User user) {
        byte[] image = restTemplate.getForObject(url + user.getUsername(), byte[].class);
        if (image == null || image.length == 0) {
            throw new DataValidationException("Failed to get the generated image");
        }
        String nameForSmallPic = "small" + user.getUsername() + LocalDateTime.now() + ".jpg";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");

        InputStream inputStream = new ByteArrayInputStream(image);
        s3Client.putObject(bucketName, nameForSmallPic, compressPic(inputStream, smallSize), metadata);
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setSmallFileId(nameForSmallPic);
        user.setUserProfilePic(userProfilePic);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public UserProfilePicDto saveProfilePic(long userId, MultipartFile file) {
        User user = getUser(userId);

        String nameForSmallPic = "small" + file.getName() + LocalDateTime.now();
        String nameForLargePic = "large" + file.getName() + LocalDateTime.now();

        s3Client.putObject(bucketName, nameForSmallPic, compressPic(file, smallSize), null);
        s3Client.putObject(bucketName, nameForLargePic, compressPic(file, largeSize), null);

        UserProfilePic userProfilePic = new UserProfilePic(nameForLargePic, nameForSmallPic);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return pictureMapper.toDto(userProfilePic);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public InputStreamResource getProfilePic(long userId) {
        User user = getUser(userId);
        S3Object s3Object = s3Client.getObject(bucketName, user.getUserProfilePic().getFileId());

        return new InputStreamResource(s3Object.getObjectContent());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public UserProfilePicDto deleteProfilePic(long userId) {
        User user = getUser(userId);
        s3Client.deleteObject(bucketName, user.getUserProfilePic().getFileId());
        s3Client.deleteObject(bucketName, user.getUserProfilePic().getSmallFileId());
        UserProfilePicDto deletedProfilePicDto = pictureMapper.toDto(user.getUserProfilePic());
        user.getUserProfilePic().setSmallFileId(null);
        user.getUserProfilePic().setFileId(null);
        userRepository.save(user);

        return deletedProfilePicDto;
    }


}
