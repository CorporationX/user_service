package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectionDto {

    @NotNull
    @Positive
    Long id;

    @NotNull
    @Positive
    Long requesterId;

    @NotNull
    @Positive
    Long receiverId;

    @Size(min = 20, max = 4096, message = "description should be more then 19 and less or equal to 4096 symbols")
    String rejectionReason;
}
