package school.faang.user_service.service.cloud;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceImplTest {
    @InjectMocks
    S3ServiceImpl s3Service;
    @Mock
    private AmazonS3 s3Client;

    private byte[] getImageBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.RED);
        graphics.fill(new Rectangle2D.Double(0, 0, 50, 50));
        byte[] bytes;
        try {
            ImageIO.write(image, "jpg", outputStream);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    @Test
    public void testUploadFileWithException() {
        ReflectionTestUtils.setField(s3Service, "bucketName", "bucket-name" );
        when(s3Client.putObject(anyString(), anyString(), any(ByteArrayInputStream.class), any(ObjectMetadata.class)))
                .thenThrow(AmazonServiceException.class);
        var exception = assertThrows(RuntimeException.class,
                () -> s3Service.uploadFile(new ByteArrayInputStream(getImageBytes()), "file"));
        assertEquals("Error uploading a file to Amazon S3", exception.getMessage());
        verify(s3Client, times(1)).putObject(anyString(), anyString(), any(ByteArrayInputStream.class), any(ObjectMetadata.class));
    }
}
