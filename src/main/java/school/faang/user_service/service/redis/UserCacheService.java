package school.faang.user_service.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.redis.UserCache;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.redis.UserCacheRepository;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService {

    private final UserCacheRepository userCacheRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final AvatarService avatarService;

    public void save(long userId) {
        log.info("Saving users into cache...");
        UserDto userDto = userService.getUser(userId);
        UserCache userCache = userMapper.toUserCache(userDto);
        userCache.setAvatar(getUserAvatar(userId));
        userCacheRepository.save(userCache);
        log.info("Users were saved into cache");
    }

    private byte[] getUserAvatar(long userId) {
        return avatarService.getSmallUserAvatar(userId);
    }
}
