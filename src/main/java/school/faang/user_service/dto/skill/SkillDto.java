package school.faang.user_service.dto.skill;

import lombok.Data;

@Data
public class SkillDto {
    private Long id;
    private String title;

    public SkillDto(String title) {
        this.title = title;
    }
}