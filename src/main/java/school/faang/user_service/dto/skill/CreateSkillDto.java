package school.faang.user_service.dto.skill;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CreateSkillDto {
    private final String title;
}
