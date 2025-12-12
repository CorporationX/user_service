package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

/**
 * DTO для передачи данных при создании нового навыка пользователя.
 * Используется на уровне контроллера для валидации входных данных.
 */
@Data
public class CreateSkillDto {

    /**
     * Название навыка.
     * <p>
     * Не может быть пустым. Максимальная длина — 100 символов.
     */
    @NotBlank(message = "Skill name cannot be empty")
    @Size(max = 100, message = "Skill name cannot exceed 100 characters")
    @Getter
    public String title;
}