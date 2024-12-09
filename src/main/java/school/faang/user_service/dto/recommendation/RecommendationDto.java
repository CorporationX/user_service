package school.faang.user_service.dto.recommendation;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillOfferDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {

    @NotNull(message = "ID cannot be null")
    private Long id;

    @NotNull(message = "Author ID cannot be null")
    private Long authorId;

    @NotNull(message = "Receiver ID cannot be null")
    private Long receiverId;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 255, message = "Content must not exceed 255 characters")
    private String content;

    @NotEmpty(message = "Skill offers cannot be empty")
    private List<SkillOfferDto> skillOffers;

    @NotNull(message = "The creation time cannot be empty")
    private LocalDateTime createdAt;
}
