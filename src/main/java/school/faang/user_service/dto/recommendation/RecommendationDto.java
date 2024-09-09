package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private Long id;

    @NotNull(message = "AuthorId cannot be null")
    private Long authorId;

    @NotNull(message = "ReceiverId cannot be empty")
    private Long receiverId;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    private List<SkillOfferDto> skillOffers;
}
