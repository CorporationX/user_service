package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.time.LocalDateTime;

@Builder
@Data
public class MentorshipRequestDto{

    @NotNull
    private long id;

    @NotBlank(message = "Description cannot be blank")
    @NotNull(message = "Description cannot be null")
    private String description;

    @NotNull(message = "Mentee cannot be blank")
    private User requester;

    @NotNull(message = "Mentor cannot be blank")
    private User receiver;

    @NotNull
    private RequestStatus status;

    @NotNull
    private LocalDateTime createdAt;

}
