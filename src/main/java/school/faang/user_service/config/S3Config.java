package school.faang.user_service.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${aws.accessKey}")
    private String ACCESS_KEY;

    @Value("${aws.secretKey}")
    private String SECRET_KEY;

    @Value("${aws.region}")
    private String REGION;

    @Value("${services.s3.bucket-name}")
    private String BUCKET_NAME;

    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials awsCredentials =
                new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(REGION)
                .build();

        if (!s3Client.doesBucketExistV2(BUCKET_NAME)) {
            s3Client.createBucket(BUCKET_NAME);
        }

        return s3Client;
    }
}
