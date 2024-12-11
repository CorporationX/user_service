package school.faang.user_service.service;


import school.faang.user_service.model.dto.cache.RedisUserDto;

public interface RedisUserService {
    RedisUserDto getUser(Long userId);
    RedisUserDto saveUser(Long userId);
}
