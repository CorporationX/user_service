package school.faang.user_service.dto.mentorshipRequest;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFilterDto {
    private String description;

    @Min(value = 0, message = "Id can not be lower than 0")
    private Long requesterId;

    @Min(value = 0, message = "Id can not be lower than 0")
    private Long receiverId;

    private String status;
}

