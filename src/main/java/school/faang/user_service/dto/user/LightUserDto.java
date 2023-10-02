package school.faang.user_service.dto.user;

import lombok.Builder;

import java.util.List;

@Builder
public record LightUserDto(Long id,
                           String username,
                           String email,
                           List<Long>followerIds){}
