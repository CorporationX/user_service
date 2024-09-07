package school.faang.user_service.dto.event.filters;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.Skill;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDto {
    private String titlePattern;

    private String descriptionPattern;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateBeforePattern;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateAfterPattern;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateBeforePattern;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateAfterPattern;

    private String locationPattern;

    private int maxAttendeesLowerPattern;

    private int maxAttendeesLargerPattern;

    private List<Skill> relatedAllSkillsPattern;

    private List<Skill> relatedAnySkillsPattern;

    private String typePattern;

    private String statusPattern;
}
