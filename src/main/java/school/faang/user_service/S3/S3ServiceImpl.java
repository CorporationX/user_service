package school.faang.user_service.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;

@Service
public class S3ServiceImpl implements S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);

    private final AmazonS3 amazonS3Client;
    private final String bucketName;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3Client, @Value("${services.s3.bucketName}") String bucketName) {
        this.amazonS3Client = amazonS3Client;
        this.bucketName = bucketName;
    }

    @Override
    public User uploadFile(MultipartFile file, String folder) throws IOException {
        logger.info("Uploading file to S3 in folder: {}", folder);

        try {
            String key = folder + "/" + file.getOriginalFilename();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), null);
            PutObjectResult putObjectResult = amazonS3Client.putObject(putObjectRequest);

            logger.info("File uploaded successfully to S3. Key: {}", key);

            // Создаем объект User и устанавливаем в него информацию о загруженном файле
            User user = new User();
            user.setUserProfilePic(new UserProfilePic(key));
            return user;
        } catch (Exception e) {
            logger.error("Error uploading file to S3", e);
            throw new IOException("Error occurred while uploading file to Amazon S3", e);
        }
    }

    @Override
    public void deleteFile(String folder, String key) throws IOException {
        try {
            amazonS3Client.deleteObject(bucketName, key);
            logger.info("File deleted successfully from S3. Key: {}", key);
        } catch (Exception e) {
            logger.error("Error deleting file from S3", e);
            throw new IOException("Error occurred while deleting file from Amazon S3", e);
        }
    }

    @Override
    public InputStream downloadFile(String folder, String key) throws IOException {
        try {
            InputStream inputStream = amazonS3Client.getObject(bucketName, key).getObjectContent();
            logger.info("File downloaded successfully from S3. Key: {}", key);
            return inputStream;
        } catch (Exception e) {
            logger.error("Error downloading file from S3", e);
            throw new IOException("Error occurred while downloading file from Amazon S3", e);
        }
    }
}

