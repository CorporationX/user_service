package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDto {
    @Positive(message = "ID must be positive")
    private Long id;

    @Positive(message = "AuthorID must be positive")
    @NotNull(message = "AuthorID cannot be null")
    private Long authorId;

    @Positive(message = "ReceiverID must be positive")
    @NotNull(message = "ReceiverID cannot be empty")
    private Long receiverId;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    private List<SkillOfferDto> skillOffers;
}
