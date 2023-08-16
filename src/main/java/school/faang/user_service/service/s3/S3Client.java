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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

import java.io.ByteArrayInputStream;

@Component
@RequiredArgsConstructor
public class S3Client {

    private final String AWS_ACCESS_KEY = "AKIAZ3KSEE34R2FW4VM7";
    private final String AWS_SECRET_KEY = "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";
    private final String BUCKET_NAME = "corpx-unicorn-dicebear-bucket";
    private final String PROFILE_PICTURE_EXTENSION = ".svg";

    private final BasicAWSCredentials CREDENTIALS = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);

    private final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(CREDENTIALS))
            .withRegion(Regions.EU_NORTH_1)
            .build();

    public void uploadPicture(User user, byte[] data) {
        System.out.format("Uploading %s's profile picture to S3 bucket %s...\n", user.getUsername(), BUCKET_NAME);
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            s3.putObject(BUCKET_NAME, user.getId() + PROFILE_PICTURE_EXTENSION, new ByteArrayInputStream(data), metadata);
            System.out.println("Done!");
        } catch (AmazonServiceException e) {
            System.out.println("Invalid request. Please, try again later.");
        }
    }

    public String getPictureURLById(Long id) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(BUCKET_NAME, id + PROFILE_PICTURE_EXTENSION).withMethod(HttpMethod.GET);
        return s3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
}