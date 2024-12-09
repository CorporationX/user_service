package school.faang.user_service.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Сущность пользователя")
public class UserDto {

    @Schema(description = "Идентификатор", example = "12")
    private long id;

    @Schema(description = "Имя пользователя", example = "Иван")
    @NotNull(message = "The user name mustn't be null")
    @NotBlank(message = "The user name mustn't be blank")
    private String username;

    @Schema(description = "Активный ли пользователь", example = "True")
    private boolean active;

    @Schema(description = "Описание пользователя", example = "хобби")
    private String aboutMe;

    private String country;

    @Schema(description = "Опыт пользователя", example = "123")
    private Integer experience;

    @Schema(description = "Время создания", example = "2023-03-15T10:30:45.123")
    private LocalDateTime createdAt;

    @Schema(description = "Подписки", example = "[101, 102, 103, 104, 105]")
    private List<Long> followersIds;

    @Schema(description = "Подписчики", example = "[101, 102, 103, 104, 105]")
    private List<Long> followeesIds;

    @Schema(description = "Менти", example = "[101, 102, 103, 104, 105]")
    private List<Long> menteesIds;

    @Schema(description = "Менторы", example = "[101, 102, 103, 104, 105]")
    private List<Long> mentorsIds;

    @Schema(description = "Цели", example = "[101, 102, 103, 104, 105]")
    private List<Long> goalsIds;

    @Schema(description = "Навыки", example = "[101, 102, 103, 104, 105]")
    private List<Long> skillsIds;
}
