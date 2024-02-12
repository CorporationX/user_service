package school.faang.user_service.config.cloud;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Bean
    public AmazonS3 amazons3() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 clientAmazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint,
                        Regions.DEFAULT_REGION.getName()))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withPathStyleAccessEnabled(true)
                .build();

        if (!clientAmazonS3.doesBucketExistV2(bucketName)) {
            clientAmazonS3.createBucket(bucketName);
        }
        return clientAmazonS3;
    }
}