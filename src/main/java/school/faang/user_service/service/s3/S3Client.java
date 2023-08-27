package school.faang.user_service.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

import java.io.ByteArrayInputStream;

@Data
@Component
@RequiredArgsConstructor
public class S3Client {

    @Autowired
    private final Environment environment;

    private final String BUCKET_NAME = environment.getProperty("aws.bucketName");
    private final String AWS_ACCESS_KEY = environment.getProperty("aws.accessKey");
    private final String AWS_SECRET_KEY = environment.getProperty("aws.secretKey");

    private BasicAWSCredentials CREDENTIALS = new BasicAWSCredentials(AWS_SECRET_KEY, AWS_ACCESS_KEY);

    private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(CREDENTIALS))
            .withRegion(Regions.EU_NORTH_1)
            .build();

    public void upload(User user, byte[] data, String extension) {
        System.out.format("Uploading %s's profile picture to S3 bucket %s...\n", user.getUsername(), BUCKET_NAME);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            s3.putObject(BUCKET_NAME, user.getId() + extension, new ByteArrayInputStream(data), metadata);
            System.out.println("Done!");
        } catch (AmazonServiceException e) {
            System.out.println("Invalid request. Please, try again later.");
        }
    }

    public String getURLById(Long id, String extension) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(BUCKET_NAME, id + extension).withMethod(HttpMethod.GET);
        return s3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
}