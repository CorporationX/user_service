package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RejectRequestDto {
    @NotNull(message = "Id is required!")
    private Long id;

    @NotNull(message = "Reason must be not null!")
    @NotBlank(message = "Reason must be not blank!")
    private String rejectReason;
}
