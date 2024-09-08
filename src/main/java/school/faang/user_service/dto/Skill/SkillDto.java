package school.faang.user_service.dto.Skill;

import lombok.Data;

@Data
public class SkillDto {
    private String title;
    private long userId;

    public SkillDto(String title, long userId) {
        this.title = title;
        this.userId = userId;
    }
}
