package school.faang.user_service.service.s3.helper;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.Test;
import school.faang.user_service.service.HelperAmazonS3Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelperAmazonS3ServiceForSvgTest {

    private final HelperAmazonS3Service helperAmazonS3Service = new HelperAmazonS3ServiceForSvg();
    private final byte[] picture = {1, 2, 3};

    @Test
    void getMetadata() {
        ObjectMetadata correctMetadata = new ObjectMetadata();
        correctMetadata.setContentLength(picture.length);
        correctMetadata.setContentType("image/svg");
        correctMetadata.setContentEncoding("utf-8");

        ObjectMetadata result = helperAmazonS3Service.getMetadata(picture);

        assertEquals(correctMetadata.getContentLength(), result.getContentLength());
        assertEquals(correctMetadata.getContentType(), result.getContentType());
        assertEquals(correctMetadata.getContentEncoding(), result.getContentEncoding());
    }

    @Test
    void getKey() {
        String name = "Robert";
        String[] keyArgs = {"Hello", "World"};
        String correctStartKey = "Robert/Hello:World";
        String correctEndKey = "-image.svg";

        String result = helperAmazonS3Service.getKey(picture, name, keyArgs);

        System.out.println(result);
        assertTrue(result.startsWith(correctStartKey));
        assertTrue(result.endsWith(correctEndKey));
    }
}