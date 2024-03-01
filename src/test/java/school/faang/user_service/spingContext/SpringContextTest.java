package school.faang.user_service.spingContext;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SpringContextTest {
    @MockBean
    private AmazonS3 clientAmazonS3;

    @Test
    void contextLoads() {
    }
}
