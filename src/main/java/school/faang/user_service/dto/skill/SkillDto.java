package school.faang.user_service.dto.skill;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SkillDto {
    private final Long id;
    private final String title;
}
