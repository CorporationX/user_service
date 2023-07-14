package school.faang.user_service.dto.mentorshipRequest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestDto {

    @NotNull
    private String description;

    @NotNull
    @Min(value = 0, message = "Id can't be lower than 0")
    private Long requesterId;

    @NotNull
    @Min(value = 0, message = "Id can't be lower than 0")
    private Long receiverId;
}
