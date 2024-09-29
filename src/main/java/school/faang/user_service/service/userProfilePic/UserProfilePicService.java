package school.faang.user_service.service.userProfilePic;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3ServiceImpl;
import school.faang.user_service.service.user.UserService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.imgscalr.Scalr;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class UserProfilePicService {
    private final UserRepository userRepository;
    private final UserService userService;
    @Value("${services.s3.bucketName}")
    private String bucketName;
    private final S3ServiceImpl s3Service;
    @Value("${reduce.max-pixel-size.large-image}")
    private int largeImage;
    @Value("${reduce.max-pixel-size.small-image}")
    private int smallImage;

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

        if (width > largeImage || height > largeImage) {

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

    public InputStream getBigImageFromProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return s3Service.downloadFile(user.getUserProfilePic().getFileId());
    }

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
