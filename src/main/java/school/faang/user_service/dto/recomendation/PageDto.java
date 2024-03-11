package school.faang.user_service.dto.recomendation;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {
    @Min(value = 0, message = "page не может быть меньше 0")
    private Integer page = 0;
    @Min(value = 1, message = "pageSize должно быть > 0")
    private Integer pageSize = 10;
}
