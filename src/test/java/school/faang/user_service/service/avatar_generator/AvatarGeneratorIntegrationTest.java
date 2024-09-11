package school.faang.user_service.service.avatar_generator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AvatarGeneratorIntegrationTest {

    @Autowired
    private AvatarGenerator avatarGenerator;

    @Test
    public void testGenerateAvatarLinkS3() {
        String s3Url = avatarGenerator.GenerateAvatarLinkS3();

        assertNotNull(s3Url, "The S3 URL should not be null");
        assertTrue(s3Url.contains("s3.amazonaws.com"), "The URL should be an S3 URL");
    }
}

