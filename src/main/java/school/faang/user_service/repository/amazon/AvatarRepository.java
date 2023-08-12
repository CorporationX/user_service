package school.faang.user_service.repository.amazon;

import com.amazonaws.SdkClientException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.awt.image.BufferedImage;

@Repository
public interface AvatarRepository {
    @Retryable(maxAttempts = 3, value = {SdkClientException.class})
    void uploadFile(String nameFile, byte[] data);

    BufferedImage getFileAmazonS3(String objectKey);

    void deleteFile(String objectKey);
}
