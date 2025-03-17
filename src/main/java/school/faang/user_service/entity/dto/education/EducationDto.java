package school.faang.user_service.entity.dto.education;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EducationDto {
    @NotNull(message = "Id не может быть NULL")
    private long id;

    @NotNull(message = "Год начала обучения не может быть null")
    private Integer yearFrom;

    @NotNull(message = "Год окончания обучения не может быть null")
    private Integer yearTo;

    @NotBlank(message = "Учреждение не может быть пустым")
    private String institution;

    @NotBlank(message = "Уровень образования не может быть пустым")
    private String educationLevel;

    @NotBlank(message = "Специализация не может быть пустой")
    private String specialization;
}
