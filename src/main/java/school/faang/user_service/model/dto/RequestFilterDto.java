package school.faang.user_service.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.model.entity.RequestStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFilterDto {
    @NotBlank(message = "Description must not be null or empty")
    private String description;

    @Min(message = "Requester ID must be greater than zero", value = 1)
    private Long requesterId;

    @Min(message = "Receiver ID must be greater than zero", value = 1)
    private Long receiverId;

    private RequestStatus status;

}
