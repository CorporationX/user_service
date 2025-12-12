package school.faang.user_service.dto.skill;

import java.util.List;
import school.faang.user_service.dto.user.UserDto;

public record SkillDto(Long id, String title, List<UserDto> guarantors) { }
