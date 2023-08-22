package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Информация о пользователе")
public class UserDto {
    @Min(1L)
    @Max(Long.MAX_VALUE)
    @Schema(description = "Идентификатор пользователя")
    private Long id;

    @Schema(description = "Имя")
    private String username;

    @Email
    @Schema(description = "Email")
    private String email;
    @Schema(description = "Телефон")
    private String phone;
    @Schema(description = "Информация о пользователе")
    @JsonIgnore
    private String aboutMe;
    @JsonIgnore
    @Schema(description = "Страна")
    private String country;
    @JsonIgnore
    @Schema(description = "Город")
    private String city;
    @JsonIgnore
    @Schema(description = "Опыт")
    private Integer experience;
}