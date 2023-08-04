package school.faang.user_service.dto.mentorshiprequest;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestFilterDto {

    @Size(max = 255, message = "Description can't be longer than 255")
    private String description;

    private Long requesterId;

    private Long receiverId;

    private RequestStatus status;
}

