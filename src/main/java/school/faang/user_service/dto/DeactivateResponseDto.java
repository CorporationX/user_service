package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeactivateResponseDto {
    private String message;
    private long userId;
}
