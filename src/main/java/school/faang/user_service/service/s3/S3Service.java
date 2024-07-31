package school.faang.user_service.service.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.ExceptionMessages;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.FindException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private static final int MAX_IMAGE_LARGE_PHOTO = 1080;
    private static final int MAX_IMAGE_SMALL_PHOTO = 170;
    private final AmazonS3 s3Client;
    private final MultipartFileCopyUtil multipartFileCopyUtil;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public UserProfilePic uploadProfile(MultipartFile multipartFile, String folder) throws IOException {

        MultipartFile largeImage = multipartFileCopyUtil
                .compressionMultipartFile(multipartFile, MAX_IMAGE_LARGE_PHOTO);
        MultipartFile smallImage = multipartFileCopyUtil
                .compressionMultipartFile(multipartFile, MAX_IMAGE_SMALL_PHOTO);

        ObjectMetadata objectMetadataOne = multipartFileCopyUtil.collectMetadata(largeImage);
        ObjectMetadata objectMetadataTwo = multipartFileCopyUtil.collectMetadata(smallImage);

        String keyOne = String.format("Origin%s%d%s",
                folder, System.currentTimeMillis(), largeImage.getOriginalFilename());
        String keyTwo = String.format("Small%s%d%s",
                folder, System.currentTimeMillis(), smallImage.getOriginalFilename());

        sendingRequestToTheCloud(bucketName, keyOne, largeImage, objectMetadataOne);
        sendingRequestToTheCloud(bucketName, keyTwo, smallImage, objectMetadataTwo);

        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(keyOne);
        userProfilePic.setSmallFileId(keyTwo);

        return userProfilePic;
    }

    public InputStream downloadingByteImage(String key) {
        try {
            return s3Client.getObject(bucketName, key).getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SdkClientException(e);
        }
    }

    public void deleteImage(UserProfilePic userProfilePic) {
        s3Client.deleteObject(bucketName, userProfilePic.getFileId());
        s3Client.deleteObject(bucketName, userProfilePic.getSmallFileId());
    }

    private void sendingRequestToTheCloud(String bucketName,
                                          String key,
                                          MultipartFile multipartFile,
                                          ObjectMetadata objectMetadata) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, multipartFile.getInputStream(), objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            log.error(ExceptionMessages.CLOUD_SENDING, e);
            throw new FindException(ExceptionMessages.CLOUD_SENDING, e);
        }
    }
}