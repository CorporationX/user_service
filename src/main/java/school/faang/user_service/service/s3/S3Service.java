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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Optional;


@Service
public class S3Service {
    @Value("${services.s3.bucket-name}")
     String bucketName;


    private final AmazonS3 s3Client;
    private final UserRepository userRepository;

    @Autowired
    public S3Service(AmazonS3 s3Client, UserRepository userRepository) {
        this.s3Client = s3Client;
        this.userRepository = userRepository;
    }

    public String uploadAvatar(Long userId, MultipartFile file) throws IOException {
        User user = findUserById(userId).get();
        UserProfilePic userProfilePic = new UserProfilePic();

        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        BufferedImage largeImage = resizeImage(originalImage, 1080);
        BufferedImage smallImage = resizeImage(originalImage, 170);

        String largeAvatarKey = "avatars/" + userId + "/large.jpg";
        String smallAvatarKey = "avatars/" + userId + "/small.jpg";

        userProfilePic.setFileId(largeAvatarKey);
        userProfilePic.setSmallFileId(smallAvatarKey);

        user.setUserProfilePic(userProfilePic);

        convertImageToObjectMetaDataAndUpload(largeImage, largeAvatarKey);
        convertImageToObjectMetaDataAndUpload(smallImage, smallAvatarKey);

        userRepository.save(user);
        return "file uploaded";
    }

    public byte[] downloadAvatar(Long userId) {
        S3Object s3Object = s3Client.getObject(bucketName, "avatars/" + userId + "/large.jpg");
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.getMessage();
        }
        return null;
    }



    public String deleteAvatar(Long userId) {
        if (findUserById(userId).isPresent()) {
            s3Client.deleteObject(bucketName, "avatars/" + userId + "/large.jpg");
            s3Client.deleteObject(bucketName, "avatars/" + userId + "/small.jpg");
            return "Avatar removed";
        } else {
            return "User not found!";
        }
    }


    private BufferedImage resizeImage(BufferedImage originalImage, int targetSize) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        int newWidth;
        int newHeight;

        if (width > height) {
            newWidth = targetSize;
            newHeight = (newWidth * height) / width;
        } else {
            newHeight = targetSize;
            newWidth = (newHeight * width) / height;
        }

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

    private Optional<User> findUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return userOptional;
    }
}

