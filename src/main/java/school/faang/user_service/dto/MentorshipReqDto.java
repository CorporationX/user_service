package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MentorshipReqDto {

    @NotNull(message = "Requester ID must not be null")
    private Long requesterId;

    @NotNull(message = "Receiver ID must not be null")
    private Long receiverId;

    @NotBlank(message = "Description is required")
    @Size(min = 5, message = "Description must be at least 5 characters long")
    private String description;

}
