package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserProfilePicMapper;
import school.faang.user_service.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

import static school.faang.user_service.exception.MessageError.GENERATION_EXCEPTION;

@Service
@RequiredArgsConstructor
public class UserProfilePicService {

    private final UserService userService;
    private final AmazonS3 s3Client;
    private final UserRepository userRepository;
    private final UserProfilePicMapper userProfilePicMapper;
    private final RestTemplate restTemplate;
    private final UserContext userContext;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.smallSize}")
    private int smallSize;

    @Value("${services.s3.largeSize}")
    private int largeSize;

    @Value("${defaultAvatar.url}")
    private String url;

    public void putDefaultPicWhileCreating() {
        String defaultPic = getRandomAvatar();
        byte[] image = restTemplate.getForObject(defaultPic, byte[].class);
        if (image == null) {
            throw new DataValidationException(GENERATION_EXCEPTION.getMessage());
        }
        MultipartFile file = new MockMultipartFile("default_pic", "default_pic.jpg", "image/jpg", image);
        saveUserProfilePic(file);
    }

    private InputStream compressPic(InputStream inputStream, int size) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            BufferedImage image = Thumbnails.of(inputStream).size(size, size).asBufferedImage();
            ImageIO.write(image, "jpg", outputStream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = outputStream.toByteArray();

        return new ByteArrayInputStream(bytes);
    }

    public UserProfilePicDto saveUserProfilePic(MultipartFile file) {
        Long id = userContext.getUserId();
        User user = userService.findUserById(id);

        String smallPic = String.format("small%s%s", file.getName(), LocalDateTime.now());
        String largePic = String.format("large%s%s", file.getName(), LocalDateTime.now());

        try {
            s3Client.putObject(bucketName, smallPic, compressPic(file.getInputStream(), smallSize), null);
            s3Client.putObject(bucketName, largePic, compressPic(file.getInputStream(), largeSize), null);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

        UserProfilePic userProfilePic = new UserProfilePic(largePic, smallPic);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
        return userProfilePicMapper.toDto(userProfilePic);
    }

    public InputStream getUserProfilePic(Long userId) {
        User user = userService.findUserById(userId);
        S3Object s3Object = s3Client.getObject(bucketName, user.getUserProfilePic().getFileId());
        return s3Object.getObjectContent();
    }

    public UserProfilePicDto deleteUserProfilePic() {
        Long id = userContext.getUserId();
        User user = userService.findUserById(id);
        s3Client.deleteObject(bucketName, user.getUserProfilePic().getFileId());
        s3Client.deleteObject(bucketName, user.getUserProfilePic().getSmallFileId());

        user.setUserProfilePic(null);

        UserProfilePic deletedUserProfilePic = user.getUserProfilePic();

        userRepository.save(user);
        return userProfilePicMapper.toDto(deletedUserProfilePic);
    }

    private String getRandomAvatar() {
        Map<Integer, String> anySeeds = Map.of(
                0, "Boo",
                1, "Missy",
                2, "Abby");
        Random random = new Random();

        Integer max = Collections.max(anySeeds.keySet());
        int randomKey = random.nextInt(max + 1);
        String seed = anySeeds.get(randomKey);

        return String.format("%s%s", url, seed);
    }
}
