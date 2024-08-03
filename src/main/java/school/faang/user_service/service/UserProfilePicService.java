package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.mapper.UserProfilePicMapper;
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
public class UserProfilePicService {

    private final UserService userService;
    private final AmazonS3 s3Client;
    private final UserRepository userRepository;
    private final UserProfilePicMapper userProfilePicMapper;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.smallSize}")
    private int smallSize;

    @Value("${services.s3.largeSize}")
    private int largeSize;

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

    public UserProfilePicDto saveUserProfilePic(Long id, MultipartFile file) {
        User user = userService.findUserById(id);

        String smallPic = String.format("small%s%d", file.getName(), LocalDateTime.now());
        String largePic = String.format("large%s%d", file.getName(), LocalDateTime.now());

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

    public UserProfilePicDto deleteUserProfilePic(Long userId) {
        User user = userService.findUserById(userId);
        s3Client.deleteObject(bucketName, user.getUserProfilePic().getFileId());
        s3Client.deleteObject(bucketName, user.getUserProfilePic().getSmallFileId());

        UserProfilePic deletedUserProfilePic = user.getUserProfilePic();

        deletedUserProfilePic.setFileId(null);
        user.getUserProfilePic().setSmallFileId(null);
        
        userRepository.save(user);
        return userProfilePicMapper.toDto(deletedUserProfilePic);
    }
}
