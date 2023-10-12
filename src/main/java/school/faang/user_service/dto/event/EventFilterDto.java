package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.ZonedDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class EventFilterDto {
    private Long id;
    private String titlePattern;
    private ZonedDateTime laterThanStartDate;
    private ZonedDateTime earlierThanEndDate;
    private Long ownerId;
    private String descriptionPattern;
    private List<SkillDto> relatedSkills;
    private String locationPattern;
    private int lessThanMaxAttendees;
}
