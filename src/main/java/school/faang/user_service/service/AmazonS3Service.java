package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;


@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 amazonS3;

    @Value("${services.s3.bucket-name}")
    private String bucketName;


    public String uploadFile(byte[] image, MultipartFile file, long userId, String imageSize) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(image);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(inputStream.available());

        String key = "u" + userId + "_" + imageSize + "_" + file.getOriginalFilename();
        amazonS3.putObject(bucketName, key, inputStream, metadata);

        return amazonS3.getUrl(bucketName, key).toString();
    }
}