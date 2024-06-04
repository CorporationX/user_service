package school.faang.user_service.service.cloud;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@PropertySource(ignoreResourceNotFound = true, value = "classpath:s3.properties")
public class S3ServiceImpl implements S3Service{
    private final AmazonS3 s3Client;
    @Value("${bucket-name}")
    private String bucketName;

    public void uploadFile(@NonNull InputStream inputStream, String fileName){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");

        s3Client.putObject(bucketName, fileName, inputStream, metadata);
    }
}
