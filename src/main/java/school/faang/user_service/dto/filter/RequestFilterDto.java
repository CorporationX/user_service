package school.faang.user_service.dto.filter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFilterDto {

    private Long receiverId;
    private Long requesterId;

    @NotBlank
    private String description;

    @NotNull
    private RequestStatus requestStatus;
}
