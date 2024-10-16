package school.faang.user_service.model.filter_dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@NotNull
public class UserFilterDto {
    private String namePattern;
    private String phone;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private Integer experience;
}
