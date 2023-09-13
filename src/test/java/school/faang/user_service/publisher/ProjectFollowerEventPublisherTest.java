package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.redis.ProjectFollowerEventDto;

import static org.mockito.Mockito.verify;

public class ProjectFollowerEventPublisherTest {
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private ProjectFollowerEventPublisher projectFollowerEventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectFollowerEventPublisher = new ProjectFollowerEventPublisher(redisTemplate, objectMapper);
    }

    @Test
    public void testPublishMessage() throws JsonProcessingException {
        ProjectFollowerEventDto projectFollowerEventDto = ProjectFollowerEventDto.builder()
                .projectId(1L)
                .subscriberId(1L)
                .ownerId(2L)
                .build();

        Mockito.when(objectMapper.writeValueAsString(projectFollowerEventDto)).thenReturn("JSON_STRING");
        projectFollowerEventPublisher.publishMessage(projectFollowerEventDto);
        verify(redisTemplate).convertAndSend(projectFollowerEventPublisher.getChannel(), "JSON_STRING");
    }
}
