package school.faang.user_service.service.amazon;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DiceBearConnect;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final AmazonS3 clientAmazonS3;
    @Value("${services.s3.bucket-name-for-avatars}")
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

    public void uploadFile(String nameFile, byte[] data) {
        checkBucketName(bucketName);
        try {
            PutObjectRequest request = new PutObjectRequest(
                    bucketName,
                    nameFile,
                    new ByteArrayInputStream(data),
                    addMetadata(data));

            clientAmazonS3.putObject(request);
        } catch (AmazonServiceException e) {
            new DiceBearConnect("Can't upload file: " + e.getMessage());
        }
    }

    private ObjectMetadata addMetadata(byte[] data) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        metadata.setContentType("image/png");
        return metadata;
    }

    private void checkBucketName(String bucketName) {
        if (!clientAmazonS3.doesBucketExistV2(bucketName)) {
            clientAmazonS3.createBucket(bucketName);
        }
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
}
