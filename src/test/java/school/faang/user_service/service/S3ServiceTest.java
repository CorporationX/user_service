package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private S3Service s3Service;


    @Test
    void testSaveSvgToS3_Success() {

        String dicebearUrl = "https://example.com/svg";
        String bucketName = "testBucket";
        String fileName = "example.svg";
        byte[] svgBytes = "<svg>...</svg>".getBytes();

        when( restTemplate.getForObject( dicebearUrl, byte[].class ) ).thenReturn( svgBytes );

        s3Service.saveSvgToS3( dicebearUrl, bucketName, fileName );
        verify( restTemplate ).getForObject( dicebearUrl, byte[].class );
        verify( amazonS3 ).putObject( any( PutObjectRequest.class ) );
    }

    @Test
    void testSaveSvgToS3_Error() {

        String dicebearUrl = "https://example.com/svg";
        String bucketName = "testBucket";
        String fileName = "example.svg";

        when( restTemplate.getForObject( dicebearUrl, byte[].class ) ).thenThrow( new RuntimeException( "Error downloading SVG" ) );

        s3Service.saveSvgToS3( dicebearUrl, bucketName, fileName );
        verify( restTemplate ).getForObject( dicebearUrl, byte[].class );
        verify( amazonS3, never() ).putObject( any( PutObjectRequest.class ) );
    }
}