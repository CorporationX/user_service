package school.faang.user_service.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

    // Поля для хранения значений ключей доступа и конечной точки Amazon S3
    @Value("${services.s3.accessKey}")
    private String accessKey;
    @Value("${services.s3.secretKey}")
    private String secretKey;
    @Value("${services.s3.endpoint}")
    private String endpoint;

    // Метод конфигурации Spring, создающий и настраивающий экземпляр Amazon S3
    @Bean
    public AmazonS3 createAmazonS3(){
        // Создание объекта с учетными данными AWS, используя ключ доступа и секретный ключ
        AWSCredentials credentials = new BasicAWSCredentials(
                accessKey,
                secretKey
        );

        // Создание клиента Amazon S3 с использованием стандартного строителя
        AmazonS3 client = AmazonS3ClientBuilder
                .standard()
                // Настройка конечной точки S3
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                // Установка учетных данных для клиента Amazon S3
                .withCredentials(new AWSStaticCredentialsProvider(credentials) )
                .build();

        return client; // Возвращение созданного клиента Amazon S3
    }

}
