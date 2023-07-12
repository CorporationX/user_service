package school.faang.user_service.dto;

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
    private Long requesterId;

    @NotNull
    private Long receiverId;
}
