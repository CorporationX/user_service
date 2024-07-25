package school.faang.user_service.dto;

import jakarta.validation.constraints.Max;
import lombok.*;

@Data
@Builder
public class RequestFilterDto {
    private Long id;
    private String status;
    private Long requesterId;
    private Long receiverId;
    @Max(value = 255, message = "Длина фильтра не может быть больше 255 символов")
    private String descriptionPattern;
}
