package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.redis.RedisTtlProperties;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.user.UserRedisMapperImpl;
import school.faang.user_service.model.user.UserRedisModel;
import school.faang.user_service.repository.user.UserRedisRepository;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRedisServiceTest {
    @Mock
    private UserRedisRepository userRedisRepository;
    @Spy
    private UserRedisMapperImpl userRedisMapper;
    @Mock
    private RedisTtlProperties redisTtlProperties;
    @Captor
    private ArgumentCaptor<UserRedisModel> userRedisModelCaptor;
    @InjectMocks
    private UserRedisService userRedisService;
    private User user;
    private static final long TTL = 30L;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(15L);
    }

    @Test
    void testSaveUser_successfully() {
        assertDoesNotThrow(() -> userRedisService.saveUser(user, TTL));

        verify(userRedisRepository).save(userRedisModelCaptor.capture());

        UserRedisModel capturedModel = userRedisModelCaptor.getValue();
        assertNotNull(capturedModel);
        assertEquals(RedisKeyUtil.getSmallKeyById(user.getId()), capturedModel.getKey());
        assertEquals(user.getId(), capturedModel.getId());
    }

    @Test
    void testGetUserById_presentInRedis() {
        String expectedKey = RedisKeyUtil.getSmallKeyById(user.getId());
        UserRedisModel redisModel = new UserRedisModel();
        redisModel.setKey(expectedKey);

        when(userRedisRepository.findById(expectedKey)).thenReturn(Optional.of(redisModel));
        when(userRedisMapper.toUserEntity(redisModel)).thenReturn(user);

        Optional<User> result = userRedisService.getUserFromRedisById(user.getId());

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRedisRepository).findById(expectedKey);
        verify(userRedisMapper).toUserEntity(redisModel);
    }

    @Test
    void testGetUserById_notPresentInRedis() {
        String expectedKey = RedisKeyUtil.getSmallKeyById(user.getId());

        when(userRedisRepository.findById(expectedKey)).thenReturn(Optional.empty());

        Optional<User> result = userRedisService.getUserFromRedisById(user.getId());

        assertTrue(result.isEmpty());
        verify(userRedisRepository).findById(expectedKey);
    }

    @Test
    void testUpdatePromotedUser_saveUpdatedUser() {
        assertDoesNotThrow(() -> userRedisService.updatePromotedUser(user));

        verify(userRedisRepository).save(userRedisModelCaptor.capture());

        UserRedisModel capturedModel = userRedisModelCaptor.getValue();
        assertNotNull(capturedModel);
        assertEquals(user.getId(), capturedModel.getId());
    }
}
