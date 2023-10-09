package school.faang.user_service.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.util.ResizeImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfilePicService {
    @Value("${services.s3.bucket-name}")
    private String bucketName;
    private final AmazonS3 s3Client;
    private final ResizeImage image;

    public UserProfilePic upload(MultipartFile multipartFile) {
        File largeImage = null;
        File smallImage = null;
        try {
            byte[] fileBytes = multipartFile.getBytes();
            String fileName = getFileName(multipartFile);
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(fileBytes));

            largeImage = resizeImage(originalImage, 1080, fileName, multipartFile);
            smallImage = resizeImage(originalImage, 170, fileName, multipartFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return UserProfilePic.builder()
                .fileId(largeImage.getName())
                .smallFileId(smallImage.getName())
                .build();
    }

    public void deleteAvatar(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (AmazonClientException e) {
            log.error(e.getMessage(), e);
        }
    }

    private File resizeImage(BufferedImage originalImage, int maxSize, String fileName, MultipartFile multipartFile) {
        BufferedImage largeImage = image.resizeImage(originalImage, maxSize);
        File imageFile = new File(fileName);
        try {
            ImageIO.write(largeImage, "png", imageFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        putFile(multipartFile, imageFile, fileName);
        return imageFile;
    }

    private void putFile(MultipartFile multipartFile, File file, String fileName) {
        long fileSize = multipartFile.getSize();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(multipartFile.getContentType());
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file));
    }

    private String getFileName(MultipartFile file) {
        return System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }
}
