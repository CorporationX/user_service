package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
public class RequestFilterDto {
    @NotBlank
    private String description;
    @NotBlank
    private String requesterName;
    @NotBlank
    private String receiverName;
    @NotBlank
    private RequestStatus status;
}
