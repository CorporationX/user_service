package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.dto.UserWithoutFollowersDto;
import school.faang.user_service.model.dto.cache.RedisUserDto;
import school.faang.user_service.model.dto.cache.UserFields;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.RedisTransactional;
import school.faang.user_service.service.RedisUserService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisUserServiceImpl implements RedisUserService, RedisTransactional {
    private static final String KEY_PREFIX = "user:";

    @Value("${redis.feed.ttl.user:86400}")
    private long userTtlInSeconds;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    public RedisUserServiceImpl(@Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                                UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    @Override
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public RedisUserDto getUser(Long userId) {
        String key = createUserKey(userId);
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            log.warn("User with ID {} not found in Redis, fetching from database", userId);
            return saveUser(userId);
        }

        Map<Object, Object> redisData = redisTemplate.opsForHash().entries(key);
        HashMap<String, Object> redisDataStrKey = redisData.entrySet().stream()
                .collect(HashMap::new, (m, e) -> m.put(e.getKey().toString(), e.getValue()), Map::putAll);
        return convertMapToUserDto(redisDataStrKey);
    }

    @Override
    @Retryable(retryFor = RuntimeException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public RedisUserDto saveUser(Long userId) {
        UserWithoutFollowersDto userWithoutFollowersDto = userRepository.findUserWithoutFollowers(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id = %d not found", userId)));
        RedisUserDto redisUserDto = new RedisUserDto(
                userWithoutFollowersDto.getUserId(),
                userWithoutFollowersDto.getUsername(),
                userWithoutFollowersDto.getFileId(),
                userWithoutFollowersDto.getSmallFileId());
        String key = createUserKey(redisUserDto.getUserId());
        Map<String, Object> userMap = convertUserDtoToMap(redisUserDto);
        executeRedisTransaction(() -> {
            userMap.entrySet().stream()
                    .filter(entry -> entry.getValue() != null)
                    .forEach(entry -> redisTemplate.opsForHash().put(key, entry.getKey(), entry.getValue()));
            redisTemplate.expire(key, userTtlInSeconds, TimeUnit.SECONDS);
        });
        return redisUserDto;
    }

    private String createUserKey(Long userId) {
        return KEY_PREFIX + userId;
    }

    private Map<String, Object> convertUserDtoToMap(RedisUserDto userDto) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(UserFields.USER_ID, userDto.getUserId().toString());
        userMap.put(UserFields.USERNAME, userDto.getUsername());
        userMap.put(UserFields.FILE_ID, userDto.getFileId());
        userMap.put(UserFields.SMALL_FILE_ID, userDto.getSmallFileId());
        return userMap;
    }

    private RedisUserDto convertMapToUserDto(Map<String, Object> userMap) {
        RedisUserDto userDto = new RedisUserDto();
        userDto.setUserId(Long.valueOf(userMap.get(UserFields.USER_ID).toString()));
        userDto.setUsername((String) userMap.get(UserFields.USERNAME));
        userDto.setFileId((String) userMap.get(UserFields.FILE_ID));
        userDto.setSmallFileId((String) userMap.get(UserFields.SMALL_FILE_ID));
        return userDto;
    }
}

