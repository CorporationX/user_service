package school.faang.user_service.dto.mentorshiprequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorshipRequestDto {

    @NotBlank(message = "Description shouldn't be empty")
    private String description;

    @NotNull(message = "Id can't be null")
    private Long requesterId;

    @NotNull(message = "Id can't be null")
    private Long receiverId;
}
