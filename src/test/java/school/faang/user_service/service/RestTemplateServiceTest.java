package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestTemplateServiceTest {

    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private RestTemplateService restTemplateService;

    @Test
    @DisplayName("Проверка, что при вызове метода getImageBytes, возвращается байтовый массив")
    public void getImageBytesTest() {
        byte[] expectedBytes = new byte[1];
        when(restTemplate.getForObject("url", byte[].class)).thenReturn(expectedBytes);
        byte[] actualBytes = restTemplateService.getImageBytes("url");
        Assertions.assertEquals(expectedBytes, actualBytes);
    }
}
