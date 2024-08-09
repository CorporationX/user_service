package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserValidator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


@Service
public class S3Service {
    @Value("${services.s3.bucket-name}")
    String bucketName;

    private static final int LARGE_IMAGE_SIZE = 1080;
    private static final int SMALL_IMAGE_SIZE = 170;


    private final AmazonS3 s3Client;
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final StringHelper stringHelper;

    @Autowired
    public S3Service(AmazonS3 s3Client, UserRepository userRepository, UserValidator userValidator, StringHelper stringHelper) {
        this.s3Client = s3Client;
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.stringHelper = stringHelper;
    }

    public String uploadAvatar(Long userId, MultipartFile file) throws IOException {
        User user = userValidator.findUserById(userId).get();
        UserProfilePic userProfilePic = new UserProfilePic();

        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        BufferedImage largeImage = resizeImage(originalImage, LARGE_IMAGE_SIZE);
        BufferedImage smallImage = resizeImage(originalImage, SMALL_IMAGE_SIZE);


        String largeAvatarKey = stringHelper.createAvatarKey(userId, "large");
        String smallAvatarKey = stringHelper.createAvatarKey(userId, "small");


        userProfilePic.setFileId(largeAvatarKey);
        userProfilePic.setSmallFileId(smallAvatarKey);

        user.setUserProfilePic(userProfilePic);

        convertImageToObjectMetaDataAndUpload(largeImage, largeAvatarKey);
        convertImageToObjectMetaDataAndUpload(smallImage, smallAvatarKey);

        userRepository.save(user);
        return "file uploaded";
    }

    public byte[] downloadAvatar(Long userId) {
        S3Object s3Object = s3Client.getObject(bucketName, stringHelper.createAvatarKey(userId, "large"));
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.getMessage();
        }
        return null;
    }


    public String deleteAvatar(Long userId) {
        if (userValidator.findUserById(userId).isEmpty()) {
            return "User not found!";
        }

        s3Client.deleteObject(bucketName, "avatars/" + userId + "/large.jpg");
        s3Client.deleteObject(bucketName, "avatars/" + userId + "/small.jpg");
        return "Avatar removed";
    }


    private BufferedImage resizeImage(BufferedImage originalImage, int targetSize) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int newWidth;
        int newHeight;

        newWidth = (width > height) ? targetSize : (targetSize * width) / height;
        newHeight = (width > height) ? (targetSize * height) / width : targetSize;

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    private void convertImageToObjectMetaDataAndUpload(BufferedImage image, String key) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        byte[] buffer = os.toByteArray();
        InputStream is = new ByteArrayInputStream(buffer);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(buffer.length);
        s3Client.putObject(new PutObjectRequest(bucketName, key, is, meta));
    }
}

