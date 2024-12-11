package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.dto.cache.RedisUserDto;
import school.faang.user_service.service.RedisUserService;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisUserController {
    private final RedisUserService redisUserService;

    @GetMapping("/{userId}")
    public RedisUserDto getUser(@PathVariable long userId) {
        return redisUserService.getUser(userId);
    }
}
