package school.faang.user_service.dto.mentorshipRequest;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestFilterDto {
    private String description;

    @Min(value = 1, message = "Id can not be lower than 1")
    private Long requesterId;

    @Min(value = 1, message = "Id can not be lower than 1")
    private Long receiverId;

    private String status;
}

