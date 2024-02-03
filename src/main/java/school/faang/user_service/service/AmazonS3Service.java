package school.faang.user_service.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.util.ImageService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 amazonS3;
    private final ImageService imageService;

    @Value("${services.s3.bucket-name}")
    private String bucketName;

    public void uploadProfilePic(MultipartFile file, long userId) {
        byte[] big = imageService.resize(file, true);
        uploadFile(big, file, userId, "big");

        byte[] small = imageService.resize(file, false);
        uploadFile(small, file, userId, "small");
    }

    public void uploadFile(byte[] image, MultipartFile file, long userId, String imageSize) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(image);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(inputStream.available());

        String key = "u" + userId + "_" + imageSize + "_" + file.getOriginalFilename();
        amazonS3.putObject(bucketName, key, inputStream, metadata);
    }
}
