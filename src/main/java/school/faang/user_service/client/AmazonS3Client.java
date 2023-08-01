package school.faang.user_service.client;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AmazonS3Client {

    private final AmazonS3 amazonS3Client;



    @Value("${services.s3.accessKey}") private static String accessKey = "user";
    @Value("${services.s3.secretKey}") private static String secretKey = "password";

    private static AWSCredentials credentials = new BasicAWSCredentials(
            accessKey,
            secretKey
    );

    public static AmazonS3 getClient(){
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }
}
