package school.faang.user_service.config.minio;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.config.properties.S3Properties;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials awsCredentials =
                new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey());

        AmazonS3 clientAmazonS3 =
                AmazonS3ClientBuilder.standard()
                        .withEndpointConfiguration(
                                new AwsClientBuilder.EndpointConfiguration(
                                        s3Properties.getEndpoint(), "us-east-1"))
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                        .withPathStyleAccessEnabled(true)
                        .build();

        if (!clientAmazonS3.doesBucketExistV2(s3Properties.getBucketName())) {
            clientAmazonS3.createBucket(s3Properties.getBucketName());
        }

        return clientAmazonS3;
    }
}
