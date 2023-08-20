package school.faang.user_service.repository.amazon;

import com.amazonaws.SdkClientException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import java.awt.image.BufferedImage;

@Repository
public interface AvatarRepository extends JpaRepository<String, Long> {
    @Retryable(maxAttempts = 3, value = {SdkClientException.class})
    void uploadFile(String objectKey, byte[] data);

    BufferedImage getFileAmazonS3(String objectKey);

    void deleteFile(String objectKey);
}
