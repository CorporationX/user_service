package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestDto{

    @NotNull
    private long id;

    @NotBlank(message = "Description cannot be blank")
    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Mentee cannot be blank")
    private long requesterId;

    @NotNull(message = "Mentor cannot be blank")
    private long receiverId;

    private RequestStatus status;

    private LocalDateTime createdAt;

}
