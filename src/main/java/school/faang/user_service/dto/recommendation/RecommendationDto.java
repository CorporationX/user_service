package school.faang.user_service.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RecommendationDto {
    @Positive(message="id should be positive")
    private Long id;
    @NotBlank(message="content can't be empty")
    private String content;
    @NotNull
    @Positive(message="authorId should be positive")
    private Long authorId;
    @NotNull
    @Positive(message="receiverId should be positive")
    private Long receiverId;
    private List<@Valid SkillOfferDto> skillOffers;
    //@NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}