package school.faang.user_service.service.amazon;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DiceBearConnect;
import school.faang.user_service.repository.amazon.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Service
@RequiredArgsConstructor
public class AvatarService implements AvatarRepository {
    private final AmazonS3 amazonS3;
    @Value("${services.s3.bucket-name}")
    private String bucketName;

    @Async("avatar")
    public void saveToAmazonS3(UserProfilePic userProfilePic) {
        byte[] imageData = new byte[0];

        try {
            imageData = convertUrlToByte(userProfilePic.getFileId());
        } catch (IOException e) {
            throw new DiceBearConnect("Can't get image: " + e.getMessage());
        }

        uploadFile(userProfilePic.getFileId(), imageData);
    }

    @Override
    public void uploadFile(String nameFile, byte[] data) {
        try {
            amazonS3.putObject(bucketName, nameFile, new ByteArrayInputStream(data), null);
        } catch (AmazonServiceException e) {
            new DiceBearConnect("Can't upload file: " + e.getMessage());
        }
    }

    @Override
    public BufferedImage getFileAmazonS3(String fileName) {
        S3Object s3Object = amazonS3.getObject(bucketName, fileName);
        S3ObjectInputStream objectContent = s3Object.getObjectContent();

        byte[] data = new byte[0];
        try {
            data = IOUtils.toByteArray(objectContent);
        } catch (IOException e) {
            throw new RuntimeException("Wrong file: " + e.getMessage());
        }

        BufferedImage image = null;
        try {
            image = biteToImage(data);
        } catch (IOException e) {
            throw new RuntimeException("Wrong file saved in amazon s3: " + e.getMessage());
        }

        return image;
    }

    @Override
    public void deleteFile(String objectKey) {

    }

    private byte[] convertUrlToByte(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        try (InputStream inputStream = connection.getInputStream()) {
            int contentLength = connection.getContentLength();
            byte[] buffer = new byte[contentLength];
            int bytesRead = inputStream.read(buffer);
            if (bytesRead != contentLength) {
                throw new IOException("Failed to read full content from URL");
            }
            return buffer;
        }
    }

    private BufferedImage biteToImage(byte[] imageData) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        return ImageIO.read(inputStream);
    }
}
