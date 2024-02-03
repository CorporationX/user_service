package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.util.ImageService;

import java.io.InputStream;


@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 amazonS3;
    private final ImageService imageService;

    @Value("${services.s3.bucket-name}")
    private String bucketName;

    public void uploadProfilePic(MultipartFile file, String folder, long userId) {

        InputStream bigImage = imageService.resizeImage(file, true);
        InputStream smallImage = imageService.resizeImage(file, false);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
//        objectMetadata.setContentLength(file.getSize());
        String key = "User/" + userId + "/" + folder + "/" + file.getOriginalFilename();

        try {
            PutObjectRequest requestBigImage = new PutObjectRequest(bucketName, key + " big image", bigImage, objectMetadata);
            amazonS3.putObject(requestBigImage);
            PutObjectRequest requestSmallImage = new PutObjectRequest(bucketName, key + " small image", smallImage, objectMetadata);
            amazonS3.putObject(requestSmallImage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
