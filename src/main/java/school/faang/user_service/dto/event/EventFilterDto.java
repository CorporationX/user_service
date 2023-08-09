package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EventFilterDto {
    @Min(value = 0, message = "Id should be a positive value")
    private Long id;
    @NotBlank(message = "Title cannot be blank")
    private String titlePattern;
    private LocalDateTime laterThanStartDate;
    private LocalDateTime earlierThanEndDate;
    private Long ownerId;
    @NotBlank(message = "Description cannot be blank")
    private String descriptionPattern;
    private List<SkillDto> relatedSkills;
    private String locationPattern;
    private int lessThanMaxAttendees;
}
