package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecommendationRequestDto {

    @NotBlank(message = "Message must not be blank")
    private String message;

    @NotNull(message = "ReceiverId must not be blank")
    private Long receiverId;

    private List<Long> skillIds;
}
