package school.faang.user_service.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AmazonS3Config {

    private final AmazonCredentials amazonCredentials;

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials credentials =
                new BasicAWSCredentials(amazonCredentials.getAccessKey(), amazonCredentials.getSecretKey());

        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(amazonCredentials.getRegion())
                .build();

        if (!amazonS3.doesBucketExistV2(amazonCredentials.getBucketName())) {
            amazonS3.createBucket(amazonCredentials.getBucketName());
        }
        return amazonS3;
    }
}
