package school.faang.user_service.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.userProfilePic.AvatarFromAwsDto;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {
    @Value("${aws.bucket-name}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    public String uploadFile(byte[] resizedFile, MultipartFile file, long userId, String size) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(resizedFile);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(inputStream.available());

        String objectKey = "u" + userId + "_" + size + "_" + file.getOriginalFilename();
        try {
            amazonS3.putObject(bucketName, objectKey, inputStream, metadata);
        } catch (AmazonServiceException ase) {
            log.error("Amazon S3 couldn't process operation", ase);
            throw ase;
        } catch (SdkClientException sce) {
            log.error("Amazon S3 couldn't be contacted for a response", sce);
            throw sce;
        }
        return objectKey;
    }

    public AvatarFromAwsDto receiveFile(String objectKey) {
        byte[] imageBytes;
        S3Object s3Object;

        try {
            s3Object = amazonS3.getObject(bucketName, objectKey);
        } catch (AmazonServiceException ase) {
            log.error("Amazon S3 couldn't process operation", ase);
            throw ase;
        } catch (SdkClientException sce) {
            log.error("Amazon S3 couldn't be contacted for a response", sce);
            throw sce;
        }

        try (InputStream in = s3Object.getObjectContent()) {
            BufferedImage imageFromAWS = ImageIO.read(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageFromAWS, "png", baos);
            imageBytes = baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new AvatarFromAwsDto(imageBytes, s3Object.getObjectMetadata().getContentType());
    }

    public void deleteObject(String objectKey) {
        try {
            amazonS3.deleteObject(bucketName, objectKey);
        } catch (AmazonServiceException ase) {
            log.error("Amazon S3 couldn't process operation", ase);
            throw ase;
        } catch (SdkClientException sce) {
            log.error("Amazon S3 couldn't be contacted for a response", sce);
            throw sce;
        }
    }
}
