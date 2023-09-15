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
import school.faang.user_service.exception.DiceBearApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final AmazonS3 clientAmazonS3;
    @Value("${services.s3.bucket_name_for_avatars}")
    private String bucketName;

    @Async("avatarImageThreadPoolExecutor")
    public void saveToAmazonS3(UserProfilePic userProfilePic) {
        byte[] imageData = new byte[0];

        try {
            imageData = convertUrlToByte(userProfilePic.getFileId());
        } catch (IOException e) {
            throw new DiceBearApiException("Can't get image: " + e.getMessage());
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
            //TODO Can't upload file: The specified bucket is not valid.
            //clientAmazonS3.putObject(request);
        } catch (AmazonServiceException e) {
           throw new DiceBearApiException("Can't upload file: " + e.getMessage());
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
        return new byte[connection.getContentLength()];
    }
}
