package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorshipRequestDto {

    private long id;

    @NotNull(message = "description shouldn't be null")
    private String description;

    private long requesterId;

    private long receiverId;

    private RequestStatus status;
}
