package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    private final RestTemplate restTemplate;


    public S3Service(AmazonS3 amazonS3, RestTemplate restTemplate) {
        this.amazonS3 = amazonS3;
        this.restTemplate = restTemplate;
    }

    public void saveSvgToS3(String dicebearUrl, String bucketName, String fileName) {
        try {
            // Get SVG content from the URL
            byte[] svgBytes = restTemplate.getForObject(dicebearUrl, byte[].class);

            // Convert byte array to input stream
            ByteArrayInputStream inputStream = new ByteArrayInputStream(svgBytes);

            // Set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(svgBytes.length);
            metadata.setContentType("image/svg+xml"); // Set content type explicitly for SVG

            // Put the object into the bucket
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }
}