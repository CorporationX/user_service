package school.faang.user_service.dto.recomendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecommendationRequestDto {
    @NotBlank(message = "Message can't be blank or empty")
    @Length(max = 4096, message = "Maximum number of characters 4096 chairs")
    private String message;
    @NotEmpty(message = "Skills can't be empty")
    private List<Long> skills;
    @NotNull(message = "Requester Id can't be null")
    private Long requesterId;
    @NotNull(message = "Receiver Id can't be null")
    private Long receiverId;
}