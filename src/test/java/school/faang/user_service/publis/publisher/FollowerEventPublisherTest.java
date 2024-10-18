package school.faang.user_service.publis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.follower.FollowerEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.follower.FollowerEventMapper;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FollowerEventPublisherTest {
    @Mock
    private RedisProperties redisProperties;
    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private FollowerEventMapper followerEventMapper;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    private final User follower = new User();
    private final Long followeeId = 1L;
    private final FollowerEventDto followerEventDto = new FollowerEventDto();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFollowerEventPublisher() throws JsonProcessingException {
        String channelName = "followerEventChannel";
        String message = "{\"followerId\":1,\"followeeId\":2}";

        when(redisProperties.getFollowerEventChannelName()).thenReturn(channelName);
        when(followerEventMapper.toEventDto(follower, followeeId)).thenReturn(followerEventDto);
        when(objectMapper.writeValueAsString(followerEventDto)).thenReturn(message);

        followerEventPublisher.publishFollower(follower, followeeId);

        verify(redisTemplate).convertAndSend(channelName, message);
        verify(followerEventMapper).toEventDto(follower, followeeId);
        verify(objectMapper).writeValueAsString(followerEventDto);
    }

    @Test
    void testFollowerEventPublisher_FailFormat() throws JsonProcessingException {
        when(followerEventMapper.toEventDto(follower, followeeId)).thenReturn(followerEventDto);
        when(objectMapper.writeValueAsString(followerEventDto)).
                thenThrow(new JsonProcessingException("ERROR") {});

        assertThrows(RuntimeException.class, () -> followerEventPublisher.publishFollower(follower, followeeId));

        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }
}
