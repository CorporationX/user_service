package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.model.dto.UserWithoutFollowersDto;
import school.faang.user_service.model.dto.cache.RedisUserDto;
import school.faang.user_service.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.service.impl.RedisUserServiceImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisUserServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private RedisConnection redisConnection;


    @InjectMocks
    private RedisUserServiceImpl redisUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(redisUserService, "userTtlInSeconds", 86400L);
    }

    @Test
    void testGetUser_FromRedis() {
        Long userId = 1L;
        String key = "user:" + userId;

        Map<Object, Object> redisData = new HashMap<>();
        redisData.put("userId", "1");
        redisData.put("username", "testUser");
        redisData.put("fileId", "file123");
        redisData.put("smallFileId", "smallFile123");

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.hasKey(key)).thenReturn(true);
        when(hashOperations.entries(key)).thenReturn(redisData);

        RedisUserDto result = redisUserService.getUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("testUser", result.getUsername());
        verify(redisTemplate).opsForHash();
        verify(hashOperations).entries(key);
    }

    @Test
    void testGetUser_FromDatabase() {
        Long userId = 2L;
        String key = "user:" + userId;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.hasKey(key)).thenReturn(false);

        UserWithoutFollowersDto dbUser = new UserWithoutFollowersDto(userId, "dbUser", "file456", "smallFile456");
        when(userRepository.findUserWithoutFollowers(userId)).thenReturn(Optional.of(dbUser));

        // Настройка Redis транзакции
        doAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            return callback.doInRedis(redisConnection);
        }).when(redisTemplate).execute(any(RedisCallback.class));

        doNothing().when(redisConnection).multi();
        when(redisConnection.exec()).thenReturn(Collections.emptyList());

        RedisUserDto result = redisUserService.getUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("dbUser", result.getUsername());

        verify(userRepository).findUserWithoutFollowers(userId);
        verify(redisConnection, times(1)).multi();
        verify(redisConnection, times(1)).exec();
    }

    @Test
    void testGetUser_UserNotFound() {
        Long userId = 3L;
        String key = "user:" + userId;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.hasKey(key)).thenReturn(false);
        when(userRepository.findUserWithoutFollowers(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> redisUserService.getUser(userId));

        verify(userRepository).findUserWithoutFollowers(userId);
    }

    @Test
    void testSaveUser() {
        Long userId = 4L;
        String key = "user:" + userId;

        // Настройка данных из базы
        UserWithoutFollowersDto dbUser = new UserWithoutFollowersDto(userId, "saveUser", "file789", "smallFile789");
        when(userRepository.findUserWithoutFollowers(userId)).thenReturn(Optional.of(dbUser));

        // Настройка Mock для RedisTemplate и операций
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        // Настройка транзакций Redis
        doAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            callback.doInRedis(redisConnection); // Выполняем Redis транзакцию
            return null;
        }).when(redisTemplate).execute(any(RedisCallback.class));

        // Настройка поведения RedisConnection
        doNothing().when(redisConnection).multi();
        when(redisConnection.exec()).thenReturn(Collections.emptyList());

        // Настройка вызова expire
        doAnswer(invocation -> {
            String actualKey = invocation.getArgument(0);
            long actualTtl = invocation.getArgument(1);
            TimeUnit actualTimeUnit = invocation.getArgument(2);

            assertEquals(key, actualKey); // Проверяем ключ
            assertEquals(86400L, actualTtl); // Проверяем TTL
            assertEquals(TimeUnit.SECONDS, actualTimeUnit); // Проверяем TimeUnit
            return true;
        }).when(redisTemplate).expire(anyString(), anyLong(), any());

        // Выполнение тестируемого метода
        RedisUserDto result = redisUserService.saveUser(userId);

        // Проверки результата
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("saveUser", result.getUsername());

        // Проверка взаимодействия с моками
        verify(userRepository).findUserWithoutFollowers(userId);
        verify(redisConnection, times(1)).multi();
        verify(redisConnection, times(1)).exec();
        verify(hashOperations, atLeastOnce()).put(anyString(), anyString(), any());
        verify(redisTemplate, times(1)).expire(anyString(), anyLong(), any());
    }

    @Test
    void testSaveUser_UserNotFound() {
        Long userId = 5L;

        when(userRepository.findUserWithoutFollowers(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> redisUserService.saveUser(userId));

        verify(userRepository).findUserWithoutFollowers(userId);
        verify(redisTemplate, never()).execute(any(RedisCallback.class));
    }
}
