package school.faang.user_service.service.s3_minio_service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;

@Service
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3;
    private final RestTemplate restTemplate;

    public S3Service(AmazonS3 amazonS3, RestTemplate restTemplate) {
        this.amazonS3 = amazonS3;
        this.restTemplate = restTemplate;

    }

    public void saveSvgToS3(String dicebearUrl, String bucketName, String fileName) {

        try {

            byte[] svgBytes = restTemplate.getForObject( dicebearUrl, byte[].class );
            ByteArrayInputStream inputStream = new ByteArrayInputStream( svgBytes );

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength( svgBytes.length );
            metadata.setContentType( "image/svg+xml" );

            amazonS3.putObject( new PutObjectRequest( bucketName, fileName, inputStream, metadata ) );
        } catch (Exception e) {
            log.error( e.getMessage() );
        }
    }

}