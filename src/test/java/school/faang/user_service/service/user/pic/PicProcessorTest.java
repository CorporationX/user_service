package school.faang.user_service.service.user.pic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PicProcessorTest {

    @InjectMocks
    private PicProcessor picProcessor;

    @Mock
    private MultipartFile multipartFile;

    @Test
    public void testGetPicBaosByLength() throws IOException {
        when(multipartFile.getInputStream()).thenThrow(new IOException("Test message"));
        assertThrows(RuntimeException.class, () -> picProcessor.getPicBaosByLength(multipartFile, 1));
    }
}