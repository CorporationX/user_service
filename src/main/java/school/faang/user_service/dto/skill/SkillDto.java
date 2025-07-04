package school.faang.user_service.dto.skill;

import school.faang.user_service.dto.user.UserDto;

import java.util.List;

public record SkillDto(
        Long id,
        String title,
        List<UserDto> guarantors
) {
}