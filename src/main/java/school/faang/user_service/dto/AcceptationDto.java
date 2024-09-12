package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AcceptationDto {
    @NotNull(message = "Request id can't be null")
    private Long requestId;
    private Long requesterId;
    private Long receiverId;
    private String status;
}
