package school.faang.user_service.config.amazon;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
//@ConfigurationProperties(prefix = "aws")
public class S3Config {
    @Value("${services.s3.accessKey}")
    private String accessKey;
    @Value("${services.s3.secretKey}")
    private String secretKey;
//    @Value("${aws.region}")
//    private String region;
    @Value("${services.s3.bucketName}")
    private String bucketName;
    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Bean
    public AmazonS3 s3Client(){
        if (accessKey == null || secretKey == null || region == null) {
            throw new IllegalArgumentException("Учетные данные AWS и регион должны быть установлены");
        }
        log.info("Создание клиента AmazonS3 с ключем: {}", accessKey);
//        log.info("Создание клиента AmazonS3 с регионом: {}", region);
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                .withCredentials(
                        new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
        if (!amazonS3.doesBucketExistV2(bucketName)){
            amazonS3.createBucket(bucketName);
        }
        return amazonS3;
    }
}
